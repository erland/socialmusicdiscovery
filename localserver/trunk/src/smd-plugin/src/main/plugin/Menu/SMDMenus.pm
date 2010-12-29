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
my $log = logger('database.info');

my %nodeFilters;

sub init {
	my $class = shift;
	
	main::DEBUGLOG && $log->is_debug && $log->debug('init');
	
	Plugins::SocialMusicDiscovery::MenuAPI::BrowseLibrary->setFeedForMode('tracks',\&_tracks);

	my @topLevel = (
		{
			type         => 'link',
			name         => 'PLUGIN_SOCIALMUSICDISCOVERY_BROWSE_BY_ARTIST',
			params       => {mode => 'artists'},
			feed         => \&_artists,
			icon         => 'html/images/artists.png',
			homeMenuText => 'PLUGIN_SOCIALMUSICDISCOVERY_BROWSE_ARTISTS',
			condition    => \&Slim::Schema::hasLibrary,
			id           => 'SMDArtists',
			weight       => 81,
		},
		{
			type         => 'link',
			name         => 'PLUGIN_SOCIALMUSICDISCOVERY_BROWSE_BY_RELEASE',
			params       => {mode => 'releases'},
			feed         => \&_releases,
			icon         => 'html/images/albums.png',
			homeMenuText => 'PLUGIN_SOCIALMUSICDISCOVERY_BROWSE_RELEASES',
			condition    => \&Slim::Schema::hasLibrary,
			id           => 'SMDReleases',
			weight       => 82,
		},
		{
			type         => 'link',
			name         => 'PLUGIN_SOCIALMUSICDISCOVERY_BROWSE_BY_CLASSIFICATION',
			params       => {mode => 'classifications'},
			feed         => \&_classifications,
			icon         => 'html/images/genres.png',
			homeMenuText => 'PLUGIN_SOCIALMUSICDISCOVERY_BROWSE_CLASSIFICATIONS',
			condition    => \&Slim::Schema::hasLibrary,
			id           => 'SMDClassifications',
			weight       => 83,
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
		$query,
		$criterias,
		$resultsFunc
	) = @_;
	
	my $hostname = $prefs->get('hostname');
	my $port = $prefs->get('port');

	my $index = $args->{'index'} || $args->{'params'}->{'_index'} || 0;
	my $quantity = $args->{'quantity'} || $args->{'params'}->{'_quantity'} || 10000000; #Not sure why this is needed, might be some change in the "onebrowser" branch requiring it
	
	my $params = $args->{'params'};
	for my $key (keys %$params) {
		foreach (qw(Artist Release Classification Track)) {
			if($_ eq $key) {
				push @$criterias,$key.':'.$params->{$key};
				last;
			}
		}
	}

	main::INFOLOG && $log->is_info && $log->info("$query ($index, $quantity): tags ->", join(', ', @$criterias));
	
	my $http = Slim::Networking::SimpleAsyncHTTP->new(\&_genericReply, \&_genericError, {
                client => $client, 
                callback => $callback, 
                resultsFunc => $resultsFunc, 
        });

	my $url = "http://".$hostname.":".$port."/browse/".$query."?";
	if(defined($criterias) && scalar(@$criterias)>0) {
		$url .= "&criteria=".join('&criteria=', @$criterias);
	}
	if($index>0 || $quantity<1000000) {
		$url .= "&offset=".$index."&size=".$quantity;
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
	my ($result, $extraitems) = $params->{'resultsFunc'}->($jsonResult);
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

sub _classifications {
	my ($client, $callback, $args, $pt) = @_;
	my @searchTags = $pt->{'searchTags'} ? @{$pt->{'searchTags'}} : ();

	_generic($client, $callback, $args, 'Classification', [@searchTags],
		sub {
			my $results = shift;
			my @empty = ();
			my $items = \@empty;
			foreach (@{$results->{'items'}}) {
				my $item = {
					'id' => $_->{'item'}->{'id'},
					'name' => $_->{'item'}->{'name'},
					'type' => 'playlist',
					'playlist' => \&_tracks,
					'url' => \&_artists,
					'passthrough' => [ { searchTags => [@searchTags, "Genre:" . $_->{'item'}->{'id'}] } ],
					'favorites_url' => 'smd:classification='.$_->{'item'}->{'id'},
				};
				push @$items,$item;
			}
			
			my %actions = (
				allAvailableActionsDefined => 1,
				commonVariables	=> ['Classification' => 'id'],
#				info => {
#					command     => ['genreinfo', 'items'],
#				},
				items => {
					command     => [Plugins::SocialMusicDiscovery::MenuAPI::BrowseLibrary::BROWSELIBRARY, 'items'],
					fixedParams => {mode => 'artists'},
				},
#				play => {
#					command     => ['playlistcontrol'],
#					fixedParams => {cmd => 'load'},
#				},
#				add => {
#					command     => ['playlistcontrol'],
#					fixedParams => {cmd => 'add'},
#				},
#				insert => {
#					command     => ['playlistcontrol'],
#					fixedParams => {cmd => 'insert'},
#				},
			);
#			$actions{'playall'} = $actions{'play'};
#			$actions{'addall'} = $actions{'add'};
			
			return {items => $items, actions => \%actions, sorted => 1}, undef;
		},
	);
}

sub _artists {
	my ($client, $callback, $args, $pt) = @_;
	my @searchTags = $pt->{'searchTags'} ? @{$pt->{'searchTags'}} : ();
	my $search     = $pt->{'search'};

	if (!$search && !scalar @searchTags && $args->{'search'}) {
		$search = $args->{'search'};
	}
	
	my @ptSearchTags;
	if ($serverPrefs->get('noGenreFilter')) {
		@ptSearchTags = grep {$_ !~ /^Classification:/} @searchTags;
	} else {
		@ptSearchTags = @searchTags;
	}

	_generic($client, $callback, $args, 'Artist', 
		[@searchTags],
		sub {
			my $results = shift;
			my @empty = ();
			my $items = \@empty;
			foreach (@{$results->{'items'}}) {
				my $item = {
					'id' => $_->{'item'}->{'id'},
					'name' => $_->{'item'}->{'name'},
					'type' => 'playlist',
					'playlist' => \&_tracks,
					'url' => \&_releases,
					'passthrough' => [ { searchTags => [@ptSearchTags, "Artist:" . $_->{'item'}->{'id'}] } ],
					'favorites_url' => 'smd:artist='.$_->{'item'}->{'id'},
				};
				push @$items,$item;
			}
			my $extra;
			
			my $params = _tagsToParams(\@ptSearchTags);
			my %actions = (
				allAvailableActionsDefined => 1,
				commonVariables	=> ['Artist' => 'id'],
#				info => {
#					command     => ['artistinfo', 'items'],
#				},
				items => {
					command     => [Plugins::SocialMusicDiscovery::MenuAPI::BrowseLibrary::BROWSELIBRARY, 'items'],
					fixedParams => {
						mode       => 'releases',
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

sub _releases {
	my ($client, $callback, $args, $pt) = @_;
	my @searchTags = $pt->{'searchTags'} ? @{$pt->{'searchTags'}} : ();
	my $search     = $pt->{'search'};

	if (!$search && !scalar @searchTags && $args->{'search'}) {
		$search = $args->{'search'};
	}
	
	my @ptSearchTags;
	if ($serverPrefs->get('noGenreFilter')) {
		@ptSearchTags = grep {$_ !~ /^Classification:/} @searchTags;
	} else {
		@ptSearchTags = @searchTags;
	}

	_generic($client, $callback, $args, 'Release', 
		[@searchTags],
		sub {
			my $results = shift;
			my @empty = ();
			my $items = \@empty;
			foreach (@{$results->{'items'}}) {
				my $item = {
					'id' => $_->{'item'}->{'id'},
					'name' => $_->{'item'}->{'name'},
					'type' => 'playlist',
					'playlist' => \&_tracks,
					'url' => \&_tracks,
					'passthrough' => [ { searchTags => [@ptSearchTags, "Release:" . $_->{'item'}->{'id'}] } ],
					'favorites_url' => 'smd:release='.$_->{'item'}->{'id'},
				};
				push @$items,$item;
			}
			my $extra;
			
			my $params = _tagsToParams(\@ptSearchTags);
			my %actions = (
				allAvailableActionsDefined => 1,
				commonVariables	=> ['Release' => 'id'],
#				info => {
#					command     => ['albuminfo', 'items'],
#				},
				items => {
					command     => [Plugins::SocialMusicDiscovery::MenuAPI::BrowseLibrary::BROWSELIBRARY, 'items'],
					fixedParams => {
						mode       => 'tracks',
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
#	my $sort       = $pt->{'sort'} || 'sort:tracknum';
	my $search     = $pt->{'search'};
	my $offset     = $args->{'index'} || 0;
	
	if (!$search && !scalar @searchTags && $args->{'search'}) {
		$search = $args->{'search'};
	}

	_generic($client, $callback, $args, 'Track',
		[@searchTags],
		sub {
			my $results = shift;
			my @empty = ();
			my $items = \@empty;
			foreach (@{$results->{'items'}}) {
				my $tracknum = $_->{'item'}->{'number'} ? $_->{'item'}->{'number'} . '. ' : '';
				if(defined($_->{'item'}->{'medium'}->{'number'})) {
					$tracknum = $_->{'item'}->{'medium'}->{'number'}."-".$tracknum;
				}elsif(defined($_->{'item'}->{'medium'}->{'name'})) {
					$tracknum = $_->{'item'}->{'medium'}->{'name'}."-".$tracknum;
				}
				my $tracktitle = $_->{'item'}->{'recording'}->{'name'} ? $_->{'item'}->{'recording'}->{'name'} : undef;
				if(!defined($tracktitle) && defined($_->{'item'}->{'recording'}->{'work'}->{'name'})) {
					$tracktitle = $_->{'item'}->{'recording'}->{'work'}->{'name'};
					if(defined($_->{'item'}->{'recording'}->{'work'}->{'parent'})) {
						$tracktitle = $_->{'item'}->{'recording'}->{'work'}->{'parent'}->{'name'}.': '.$tracktitle;
					}
				}
				my $item = {
					'id' => $_->{'item'}->{'id'},
					'name' => $tracknum.$tracktitle,
					'type' => 'audio',
					'playlist' => \&_tracks,
					'playall'       => 1,
					'play_index'    => $offset++,
				};
				push @$items,$item;
			}
			
			my %actions = (
				commonVariables	=> ['Track' => 'id'],
				allAvailableActionsDefined => 1,
				
#				info => {
#					command     => ['trackinfo', 'items'],
#				},
#				play => {
#					command     => ['playlistcontrol'],
#					fixedParams => {cmd => 'load'},
#				},
#				add => {
#					command     => ['playlistcontrol'],
#					fixedParams => {cmd => 'add'},
#				},
#				insert => {
#					command     => ['playlistcontrol'],
#					fixedParams => {cmd => 'insert'},
#				},
			);
#			$actions{'items'} = $actions{'info'};	# XXX, not sure about this, probably harmless but unnecessary

#			$actions{'playall'} = {
#				command     => ['playlistcontrol'],
#				fixedParams => {cmd => 'load', %{&_tagsToParams([@searchTags, $sort])}},
#				variables	=> [play_index => 'play_index'],
#			};
#			$actions{'addall'} = {
#				command     => ['playlistcontrol'],
#				variables	=> [],
#				fixedParams => {cmd => 'add', %{&_tagsToParams([@searchTags, $sort])}},
#			};
			
			return {items => $items, actions => \%actions, sorted => 0, albumData => undef, cover => undef}, undef;
		},
	);
}

1;
