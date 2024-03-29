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
package Plugins::SocialMusicDiscovery::Settings;

use strict;
use base qw(Slim::Web::Settings);

use Slim::Utils::Prefs;
use Plugins::SocialMusicDiscovery::Importer;

my $prefs = preferences('plugin.socialmusicdiscovery');

$prefs->migrate(2, sub {
	if(!defined($prefs->get('autoimport'))) {
		$prefs->set('autoimport',1);
	}
	1;
});
$prefs->setValidate({ 'validator' => 'intlimit', 'low' =>    1, 'high' => 65535 }, 'port'  );

sub name {
	return Slim::Web::HTTP::CSRF->protectName('SOCIALMUSICDISCOVERY');
}

sub page {
	return Slim::Web::HTTP::CSRF->protectURI('plugins/SocialMusicDiscovery/settings/basic.html');
}

sub prefs {
	return ($prefs, qw(hostname port replacemenu simulatedData autoimport));
}

sub beforeRender {
	my ($class, $paramRef) = @_;
	$paramRef->{'show_replacemenu'} = !Plugins::SocialMusicDiscovery::Browse->compat;
}

sub handler {
	my ($class, $client, $paramRef) = @_;

	if($paramRef->{'full_import'}) {
		Plugins::SocialMusicDiscovery::Importer::startFullImport();
	}elsif($paramRef->{'incremental_import'}) {
		Plugins::SocialMusicDiscovery::Importer::startImport();
	}

	return $class->SUPER::handler($client, $paramRef);
}

1;
