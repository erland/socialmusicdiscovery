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

package Plugins::SocialMusicDiscovery::Menu::SMDMenus;

=head1 NAME

Plugins::SocialMusicDiscovery::Menu::SMDMenus

=head1 DESCRIPTION

Register or deregister menu items for the My Music menu.

Register or deregister filter functions used to determine if a menu
item should be included in the My Music menu, possibly for a specific client.

This version is inspired on the 31633 version of the "onebrowser" branch in Squeezebox Server.

=cut


use strict;
use Slim::Utils::Log;
use Slim::Utils::Prefs;
use Slim::Utils::Strings qw(cstring);
use Plugins::SocialMusicDiscovery::MenuAPI::BrowseLibrary;

my $serverPrefs = preferences('server');
my $prefs = preferences('plugin.socialmusicdiscovery');
my $log = logger('plugin.socialmusicdiscovery');

my %nodeFilters;

sub init {
	my $class = shift;
	
	main::DEBUGLOG && $log->is_debug && $log->debug('init');
	
	my @topLevel = (
		{
			type         => 'link',
			name         => 'PLUGIN_SOCIALMUSICDISCOVERY',
			params       => {mode => 'smd'},
			feed         => \&_smd,
			icon         => 'html/images/artists.png',
			homeMenuText => 'PLUGIN_SOCIALMUSICDISCOVERY',
			condition    => \&Slim::Schema::hasLibrary,
			id           => 'SMD',
			weight       => 81,
		},
	);
	
	foreach (@topLevel) {
		Plugins::SocialMusicDiscovery::MenuAPI::BrowseLibrary->registerNode($_);
	}
}

sub _generic {
	my ($client,
		$callback,
		$args,
		$criterias,
		$resultsFunc
	) = @_;
	
	my $hostname = $prefs->get('hostname');
	my $port = $prefs->get('port');

	my $index = $args->{'index'} || $args->{'params'}->{'_index'} || 0;
	my $quantity = $args->{'quantity'} || $args->{'params'}->{'_quantity'} || 10000000; #Not sure why this is needed, might be some change in the "onebrowser" branch requiring it
	
	my $params = $args->{'params'};
	my $path = "";
	if(defined($params->{'path'})) {
		$path = $params->{'path'};
	}

	main::INFOLOG && $log->is_info && $log->info("$path ($index, $quantity): tags ->", join(', ', @$criterias));
	
	my $http = Slim::Networking::SimpleAsyncHTTP->new(\&_genericReply, \&_genericError, {
		path => $path,
                client => $client, 
                callback => $callback, 
                resultsFunc => $resultsFunc, 
        });

	my $url = "http://".$hostname.":".$port."/browse/library".$path;
	if($index>0 || $quantity<1000000) {
		$url .= "?offset=".$index."&size=".$quantity;
	}
	$log->info("Getting data using: ".$url);
	$http->get($url);
}

sub _genericError {
	my $http = shift;
	my $params = $http->params();

	my $result = {};

	$log->error("GOT error!");

	$params->{'callback'}->($result);
}

sub _genericReply {
	my $http = shift;
	my $params = $http->params();

	my $content = $http->content();

	my $jsonResult = JSON::XS::decode_json($content);
	my ($result, $extraitems) = $params->{'resultsFunc'}->($params->{'path'}, $jsonResult);
	$result->{'offset'} = $jsonResult->{'offset'};
	$result->{'count'} = $jsonResult->{'size'};
	$result->{'total'} = $jsonResult->{'totalSize'};

	$params->{'callback'}->($result);
}

sub _tagsToParams {
	my $tags = shift;
	my %p;
	foreach (@$tags) {
		my ($k, $v) = /([^:]+):(.+)/;
		$p{$k} = $v;
	}
	return \%p;
}


sub _smd {
	my ($client, $callback, $args, $pt) = @_;
	my @searchTags = $pt->{'searchTags'} ? @{$pt->{'searchTags'}} : ();
	my $search     = $pt->{'search'};

	if (!$search && !scalar @searchTags && $args->{'search'}) {
		$search = $args->{'search'};
	}

	_generic($client, $callback, $args, 
		[@searchTags],
		sub {
			my $path = shift;
			my $results = shift;
			my @empty = ();
			my $items = \@empty;
			foreach (@{$results->{'items'}}) {
				my $item = {
					'id' => $path."/".$_->{'id'},
					'name' => $_->{'name'},
					'type' => 'playlist',
					'playlist' => \&_tracks,
					'url' => \&_smd,
					'passthrough' => [ { searchTags => [@searchTags, "path:" . $path."/".$_->{'id'}] } ],
					'favorites_url' => 'smd:object='.$_->{'id'},
				};
				push @$items,$item;
			}
			my $extra;
			
			my $params = _tagsToParams(\@searchTags);
			my %actions = (
				allAvailableActionsDefined => 1,
				commonVariables	=> ['path' => 'id'],
#				info => {
#					command     => ['artistinfo', 'items'],
#				},
				items => {
					command     => [Plugins::SocialMusicDiscovery::MenuAPI::BrowseLibrary::BROWSELIBRARY, 'items'],
					fixedParams => {
						mode       => 'smd',
						%{&_tagsToParams(\@searchTags)},
					},
				},
#				play => {
#					command     => ['playlistcontrol'],
#					fixedParams => {cmd => 'load', %$params},
#				},
#				add => {
#					command     => ['playlistcontrol'],
#					fixedParams => {cmd => 'add', %$params},
#				},
#				insert => {
#					command     => ['playlistcontrol'],
#					fixedParams => {cmd => 'insert', %$params},
#				},
			);
#			$actions{'playall'} = $actions{'play'};
#			$actions{'addall'} = $actions{'add'};

			return {items => $items, actions => \%actions, sorted => 1}, $extra;
		},
	);
}


sub _tracks {
	my ($client, $callback, $args, $pt) = @_;
	my @searchTags = $pt->{'searchTags'} ? @{$pt->{'searchTags'}} : ();
	my $search     = $pt->{'search'};

	if (!$search && !scalar @searchTags && $args->{'search'}) {
		$search = $args->{'search'};
	}

	#TODO: Implement this function, at the moment it's just a stupid placeholder
	my $result = {};
	$callback->($result);
}

1;
