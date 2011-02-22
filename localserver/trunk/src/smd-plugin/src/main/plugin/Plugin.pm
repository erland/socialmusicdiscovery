#   Copyright 2010-2011, Social Music Discovery project
#   All rights reserved.
#
#   Redistribution and use in source and binary forms, with or without
#   modification, are permitted provided that the following conditions are met:
#       * Redistributions of source code must retain the above copyright
#         notice, this list of conditions and the following disclaimer.
#       * Redistributions in binary form must reproduce the above copyright
#         notice, this list of conditions and the following disclaimer in the
#         documentation and/or other materials provided with the distribution.
#       * Neither the name of Social Music Discovery project nor the
#         names of its contributors may be used to endorse or promote products
#         derived from this software without specific prior written permission.
#
#   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
#   ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
#   WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
#   DISCLAIMED. IN NO EVENT SHALL SOCIAL MUSIC DISCOVERY PROJECT BE LIABLE FOR ANY
#   DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
#   (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
#   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
#   ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
#   (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
#   SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

use strict;
use warnings;
                   
package Plugins::SocialMusicDiscovery::Plugin;

use base qw(Slim::Plugin::Base);

use Slim::Utils::Misc;
use Slim::Utils::Strings qw(string);
use DBI qw(:sql_types);
use File::Spec::Functions qw(:ALL);
use Proc::Background;
use Slim::Utils::OSDetect;

use Data::Dumper;

if ( main::WEBUI ) {
	require Plugins::SocialMusicDiscovery::Settings;
}
use Plugins::SocialMusicDiscovery::Scanner;
use Plugins::SocialMusicDiscovery::MenuAPI::BrowseLibrary;
use Plugins::SocialMusicDiscovery::Menu::SMDMenus;
use Plugins::SocialMusicDiscovery::MenuAPI::Buttons::XMLBrowser;

my $log = Slim::Utils::Log->addLogCategory({
	'category'     => 'plugin.socialmusicdiscovery',
	'defaultLevel' => 'WARN',
	'description'  => 'PLUGIN_SOCIALMUSICDISCOVERY',
});

my $PLUGINVERSION = undef;

my $NO_OF_BYTES_IN_SMDID = 200000;

my $driver;
my $tracks;
my $inProgress;

my $currentlyScannedTrackNo;
my $currentlyScannedTrackFile;
my $totalNumberOfTracks;

my $smdServer;
=head1 NAME

Plugins::SocialMusicDiscovery::Plugin

=head1 DESCRIPTION

Plugin that will scan tags in music files available in Squeezebox Server and provide 
them to external sources through a JSON interface. It will also provide search providers
and browse menu that works towards the Social Music Discovery server.

See documentation for the Plugins::SocialMusicDiscovery::Scanner module for more information about
supported JSON commands to retrieve tags.
=cut

# Get localized user friendly name of plugin
sub getDisplayName()
{
	return string('PLUGIN_SOCIALMUSICDISCOVERY'); 
}

# Initialize plugin, will be called by plugin framework
sub initPlugin
{
	my $class = shift;
	$class->SUPER::initPlugin(@_);

	# Store plugin version from install.xml file so we can show it in the user interface
	$PLUGINVERSION = Slim::Utils::PluginManager->dataForPlugin($class)->{'version'};

	if ( main::WEBUI ) {
		# Initialize settings module
		Plugins::SocialMusicDiscovery::Settings->new();
	}

	# Initialize scanner module so it's JSON commands are registered
	Plugins::SocialMusicDiscovery::Scanner::init();
	Plugins::SocialMusicDiscovery::MenuAPI::BrowseLibrary->init();
	Plugins::SocialMusicDiscovery::MenuAPI::Buttons::XMLBrowser->init();
	Plugins::SocialMusicDiscovery::Menu::SMDMenus->init();

    # Find location of smd-server binary in plugin directory
    my $smdServerPath = undef;
    for my $plugindir (Slim::Utils::OSDetect::dirsFor('Plugins')) {
        opendir(DIR, catdir($plugindir,"SocialMusicDiscovery")) || next;
        my @dircontents = Slim::Utils::Misc::readDirectory(catdir($plugindir,"SocialMusicDiscovery"),"jar");
        for my $file (@dircontents) {
            if($file =~ /^smd-server/) {
                $smdServerPath = catfile($plugindir,"SocialMusicDiscovery", $file);
            }
        }
    }

	if(defined($smdServerPath)) {
	    my $database = "-Dorg.socialmusicdiscovery.server.database=mysql-sbs";
	    my ($driver,$source,$username,$password) = Slim::Schema->sourceInformation;
	    if($driver ne 'mysql') {
	        $log->info("Using default database for smd-server because it isn't supporting SQLite");
	        $database = "-Dorg.socialmusicdiscovery.server.database.directory=".catdir(Slim::Utils::OSDetect::dirsFor('cache'));
	    }elsif($source !~ /port=9092/) {
	        $log->info("Using default database for smd-server because an external MySQL server is used and we can't ensure we have permission to create a database");
	        $database = "-Dorg.socialmusicdiscovery.server.database.directory=".catdir(Slim::Utils::OSDetect::dirsFor('cache'));
	    }else {
	        $log->info("Using the bundled MySQL database for smd-server, a separate smd schema is configured");
	    }

	    # Launch smd-server
        $log->info("Starting smd-server (".$smdServerPath.")");
        $smdServer = Proc::Background->new({'die_upon_destroy' => 1}, "java ".$database." -Dorg.socialmusicdiscovery.server.stdout=".catdir(Slim::Utils::OSDetect::dirsFor('log'),"smd-server.log")." -Dorg.socialmusicdiscovery.server.stderr=".catdir(Slim::Utils::OSDetect::dirsFor('log'),"smd-server.log")." -jar ".$smdServerPath);
        if(!$smdServer->alive) {
            $log->error("Unable to launch smd-server");
        }
    }else {
        $log->info("smd-server not started since binary isn't available");
    }
}

# Shutdown plugin, will be called by plugin framework
sub shutdownPlugin
{
    if($smdServer && $smdServer->alive) {
        $log->info("Stopping smd-server");
        $smdServer->die;
    }
}

# Register web interface pages, will be called by plugin framework
sub webPages {
	if ( main::WEBUI ) {

		my %pages = (
			"SocialMusicDiscovery/scanner\.(?:htm|xml)"     => \&webScanner,
			"SocialMusicDiscovery/index\.(?:htm|xml)"     => \&webIndex,
			"SocialMusicDiscovery/fullwindow\.(?:htm|xml)"     => \&webFullWindow,
		);

		for my $page (keys %pages) {
			Slim::Web::Pages->addPageFunction($page, $pages{$page});
		}
		Slim::Web::Pages->addPageLinks("plugins", { 'PLUGIN_SOCIALMUSICDISCOVERY' => 'plugins/SocialMusicDiscovery/index.html' });
		Slim::Web::Pages->addPageLinks("plugins", { 'PLUGIN_SOCIALMUSICDISCOVERY_SCANNER' => 'plugins/SocialMusicDiscovery/scanner.html' });
	}
}

# Page handler for the scanner status page of the plugin
sub webScanner {
	my ($client, $params) = @_;

	if($params->{'start'}) {
		Plugins::SocialMusicDiscovery::Scanner::initScan();
	}elsif($params->{'stop'}) {
		Plugins::SocialMusicDiscovery::Scanner::abortScan();
	}

	my ($totalNumberOfTracks, $currentlyScannedTrackNo, $currentlyScannedTrackFile, $inProgress) = Plugins::SocialMusicDiscovery::Scanner::getScanInformation();

	$params->{'pluginSocialMusicDiscoveryScanning'} = $inProgress;
	$params->{'pluginSocialMusicDiscoveryCurrent'} = $currentlyScannedTrackNo;
	$params->{'pluginSocialMusicDiscoveryCurrentFile'} = $currentlyScannedTrackFile;
	$params->{'pluginSocialMusicDiscoveryTotal'} = $totalNumberOfTracks;
	return Slim::Web::HTTP::filltemplatefile('plugins/SocialMusicDiscovery/scanner.html', $params);
}

# Page handler for the main main plugin user interface
sub webIndex
{
    my ($client, $params) = @_;

	# Find name of smd-frontend jar file (if it exists)
    for my $plugindir (Slim::Utils::OSDetect::dirsFor('Plugins')) {
        opendir(DIR, catdir($plugindir,"SocialMusicDiscovery")) || next;
        my @dircontents = Slim::Utils::Misc::readDirectory(catdir($plugindir,"SocialMusicDiscovery","HTML","EN","plugins","SocialMusicDiscovery","html"),"jar");
        for my $file (@dircontents) {
            if($file =~ /^smd-frontend/) {
				$params->{'pluginSocialMusicDiscoverySmdFrontendJar'} = $file;
				last;
            }
        }
    }

    return Slim::Web::HTTP::filltemplatefile('plugins/SocialMusicDiscovery/index.html', $params);
}

sub webFullWindow
{
    my ($client, $params) = @_;

    # Find name of smd-frontend jar file (if it exists)
    for my $plugindir (Slim::Utils::OSDetect::dirsFor('Plugins')) {
        opendir(DIR, catdir($plugindir,"SocialMusicDiscovery")) || next;
        my @dircontents = Slim::Utils::Misc::readDirectory(catdir($plugindir,"SocialMusicDiscovery","HTML","EN","plugins","SocialMusicDiscovery","html"),"jar");
        for my $file (@dircontents) {
            if($file =~ /^smd-frontend/) {
                $params->{'pluginSocialMusicDiscoverySmdFrontendJar'} = $file;
                last;
            }
        }
    }

    return Slim::Web::HTTP::filltemplatefile('plugins/SocialMusicDiscovery/fullwindow.html', $params);
}


1;

__END__
