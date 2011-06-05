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
package Plugins::SocialMusicDiscovery::Plugin;

use strict;
use warnings;

use base qw(Slim::Plugin::Base);

use File::Spec::Functions;
use Slim::Utils::Log;
use Slim::Utils::Prefs;

if ( main::WEBUI ) {
	require Plugins::SocialMusicDiscovery::Settings;
}

use Plugins::SocialMusicDiscovery::Server;
use Plugins::SocialMusicDiscovery::Browse;
use Plugins::SocialMusicDiscovery::ContextMenu;
use Plugins::SocialMusicDiscovery::Scanner;

my $log = Slim::Utils::Log->addLogCategory({
	'category'     => 'plugin.socialmusicdiscovery',
	'defaultLevel' => 'WARN',
	'description'  => 'PLUGIN_SOCIALMUSICDISCOVERY',
});

my $prefs = preferences('plugin.socialmusicdiscovery');

$prefs->init({ hostname => 'localhost', port => '9998', replacemenu => 0 });

sub initPlugin {
	my $class = shift;

	my $self = $class->SUPER::initPlugin(@_);

	if ( main::WEBUI ) {
		Plugins::SocialMusicDiscovery::Settings->new;
	}

	Plugins::SocialMusicDiscovery::Server->start($class);
	Plugins::SocialMusicDiscovery::Browse->init;
	Plugins::SocialMusicDiscovery::ContextMenu->init;
	Plugins::SocialMusicDiscovery::Scanner::init();
}

sub shutdownPlugin {
	Plugins::SocialMusicDiscovery::Server->stop;
}

sub getDisplayName { 'PLUGIN_SOCIALMUSICDISCOVERY' }

my $jars;

sub jars {
	my ($class, $re) = @_;

	$jars || do {

		# FIXME - should we put all jars in one folder and add a download link for the frontend?
		my $basedir = $class->_pluginDataFor('basedir');
		my @dirs = ($basedir, catdir($basedir, 'HTML', 'EN', 'plugins', 'SocialMusicDiscovery', 'html'));
		
		for my $dir (@dirs) {
			for my $file (Slim::Utils::Misc::readDirectory($dir, 'jar')) {
				my $path = catdir($dir, $file);{ 
					if (-f $path && -r $path) {
						$jars->{ $file } = $path;
					}
				}
			}
		}
	};

	for my $key (keys %$jars) {
		if ($key =~ $re) {
			return ($key, $jars->{$key});
		}
	}
}

sub webPages {
	my $class = shift;

	return unless main::WEBUI;

	my ($name, $jarPath) = $class->jars(qr/^smd-frontend/);

	if ($name) {

		$log->debug("frontend binary: $name, adding page links");

		for my $page (qw(index fullwindow)) {
			
			Slim::Web::Pages->addPageFunction("SocialMusicDiscovery/$page\.(?:htm|xml)", 
				sub {
					my ($client, $params) = @_;
					$params->{'pluginSocialMusicDiscoverySmdFrontendJar'} = $name;
					return Slim::Web::HTTP::filltemplatefile("plugins/SocialMusicDiscovery/$page.html", $params);
				}
			);
		}

		Slim::Web::Pages->addPageLinks("plugins", { 'PLUGIN_SOCIALMUSICDISCOVERY' => 'plugins/SocialMusicDiscovery/index.html' });
	}
}

1;
