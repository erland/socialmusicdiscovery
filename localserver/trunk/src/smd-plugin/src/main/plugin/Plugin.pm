#   Copyright 2010, Social Music Discovery project
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
#   DISCLAIMED. IN NO EVENT SHALL LOGITECH, INC BE LIABLE FOR ANY
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

use Data::Dumper;

use Plugins::SocialMusicDiscovery::Scanner;

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

	# Initialize scanner module so it's JSON commands are registered
	Plugins::SocialMusicDiscovery::Scanner::init();
}

# Register web interface pages, will be called by plugin framework
sub webPages {

	my %pages = (
		"SocialMusicDiscovery/index\.(?:htm|xml)"     => \&webIndex,
	);

	for my $page (keys %pages) {
		Slim::Web::Pages->addPageFunction($page, $pages{$page});
	}
	Slim::Web::Pages->addPageLinks("plugins", { 'PLUGIN_SOCIALMUSICDISCOVERY' => 'plugins/SocialMusicDiscovery/index.html' });
}

# Page handler for the main page of the plugin
sub webIndex {
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
	return Slim::Web::HTTP::filltemplatefile('plugins/SocialMusicDiscovery/index.html', $params);
}

1;

__END__
