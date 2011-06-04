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
package Plugins::SocialMusicDiscovery::Browse;

use strict;
use warnings;

use Tie::Cache::LRU;
use Tie::RegexpHash;

use Slim::Utils::Log;
use Slim::Utils::Prefs;

my $prefs  = preferences('plugin.socialmusicdiscovery');
my $sprefs = preferences('server');
my $log;

my $compat;
my $menuClass;

BEGIN {
	$log = logger('plugin.socialmusicdiscovery');

	$menuClass = qw(Slim::Menu::BrowseLibrary);
	eval "use $menuClass";

	if ($@) {
		$menuClass = qw(Slim76Compat::Menu::BrowseLibraryLite);
		eval "use $menuClass";

		if ($@) {
			$log->error("unable to load menu class $menuClass [$@]");
		} else {
			$compat = 1;
		}
	}

	if (!$@) {
		$log->info("using $menuClass");
	}
}

sub compat { $compat }

sub init {
	my $class = shift;

	$class->updateMenus;

	if (!$compat) {
		# update menus if pref is changed
		$prefs->setChange(sub { $class->updateMenus }, 'replacemenu');
		
		# hide built in browselibrary menus when pref replacemenu pref is set
		$menuClass->registerNodeFilter(sub { !$prefs->get('replacemenu') || $_[1] !~ /myMusic(Artists|Albums|Years|Genres)/ });
	}

	# create our own playlist command to allow playlist actions by smd object id
	Slim::Control::Request::addDispatch(['smdplcmd'], [1, 0, 1, \&plCommand]);
}

my @registeredMenus;

sub updateMenus {
	my $class = shift;

	while (my $oldNode = shift @registeredMenus) {

		$log->info("removing node: $oldNode");

		$menuClass->deregisterNode($oldNode);
	}

	if ($compat || !$prefs->get('replacemenu')) {

		# default case of only adding a single menu entry, only option available in 7.5 compatiblity mode
		my $key = 'PLUGIN_SOCIALMUSICDISCOVERY';

		$log->info("adding menu top level menu $key");

		$menuClass->registerNode({
			type         => 'link',
			name         => $key,
			params       => { mode => $key },
			feed         => sub { level($_[0], $_[1], $_[2], '/browse/library') }, 
			icon         => 'html/images/artists.png',
			iconStyle    => 'hm_myMusicArtists',
			homeMenuText => $key,
			condition    => \&Slim::Schema::hasLibrary,
			id           => $key,
			weight       => 51,
		});

		@registeredMenus = ( $key );

	} else {

		$log->info("replacing my music menus with smd menus");

		my $root = '/browse/library';

		tie my %iconMap, 'Tie::RegexpHash';
		%iconMap = (
			qr/artists/         => [ 'html/images/artists.png', 'hm_myMusicArtists' ],
			qr/releases/        => [ 'html/images/albums.png',  'hm_myMusicAlbums' ],
			qr/classifications/ => [ 'html/images/genres.png',  'hm_myMusicGenres' ],
		);
		
		Plugins::SocialMusicDiscovery::Server->get(
			$root,
			sub {
				my $json = shift;
				my $i = 0;
				
				for my $entry (@{$json->{'items'}}) {
					
					my $key  = 'SMD_TOPLEVEL_' . $i;
					my $name = $entry->{'name'};
					my $path = $root . "/" . $entry->{'id'};
					my $icons = $iconMap{ $entry->{'id'} } || [ 'html/images/artists.png', 'hm_myMusicArtists' ];
					
					$log->info("adding menu: $key -> $name $path");
					
					Slim::Utils::Strings::setString($key, $name);
					
					$menuClass->registerNode({
						type         => 'link',
						name         => $key,
						params       => { mode => $key },
						feed         => sub { level($_[0], $_[1], $_[2], $path) }, 
						icon         => $icons->[0],
						iconStyle    => $icons->[1],
						homeMenuText => $key,
						condition    => \&Slim::Schema::hasLibrary,
						id           => $key,
						weight       => ++$i,
					});

					push @registeredMenus, $key;
				}
			},
			undef,
			{ timeout => 35 }
		);
	}
}

sub level {
	my ($client, $callback, $args, $path, $flags) = @_;

	my $query = $path;

	my $index = $args->{'index'} || 0;
	my $quantity = $args->{'quantity'};

	if ($quantity) {
		$query .= "?offset=$index&size=$quantity";
	}

	$flags ||= {};
	$flags->{'ipeng'} ||= $args->{'params'}->{'userInterfaceIdiom'} && $args->{'params'}->{'userInterfaceIdiom'} =~ /iPeng/;

	if (!defined $flags->{'playalbum'}) {

		# FIXME: do we want this - makes the web interface only play the selected track?
		# FIXME: is this offset always correct - better to add something to the params?
		if (caller(10) =~ /Web/) {
			$flags->{'playalbum'} = 0;
		}

		if (!defined $flags->{'playalbum'} && $client) {
			$flags->{'playalbum'} = $sprefs->client($client)->get('playtrackalbum');
		}

		# if player pref for playtrack album is not set, get the old server pref.
		if (!defined $flags->{'playalbum'}) {
			$flags->{'playalbum'} = $sprefs->get('playtrackalbum') ? 1 : 0;
		}
	}

	$log->info("fetching next level from: $query path: $path");

	Plugins::SocialMusicDiscovery::Server->get(
		$query,
		sub {
			_createResponse($client, $callback, $_[0], $path, $flags);
		},
		sub {
			$callback->({ type => 'text', name => "Error", items => [ { type => 'text', name => "$_[0]" } ] });
		},
		{ timeout => 35 },
	);
}

sub _createResponse {
	my ($client, $callback, $json, $path, $flags) = @_;

	my $i = 0;
	my @menu;
	my $playAction; my $infoAction;

	for my $entry (@{$json->{'items'}}) {
		
		if ($entry->{'playable'}) {

			$playAction = 1;

			if ($entry->{'leaf'}) {
				
				my $playable = $entry->{'item'}->{'playableElements'}->[0]->{'uri'};

				my $menu = {
					name => $entry->{'name'},
					type => 'audio',
					uri  => $playable,
					url  => $playable,
				};
		
				if ($flags->{'playalbum'}) {
					$menu->{'id'}   = $i;
					$menu->{'path'} = $path;
				}

				push @menu, $menu;

			} else {

				my $entrypath = $path . "/" . $entry->{'id'};

				push @menu, {
					name => $entry->{'name'},
					type => $flags->{'ipeng'} ? 'opml' : 'playlist',
					url  => \&level,
					path => $entrypath,
					passthrough => [ $entrypath, $flags ],
				};
			}

		} elsif ($entry->{'leaf'}) {

			push @menu, {
				name => $entry->{'name'},
				type => 'text',
			};

		} else {

			$infoAction = 1;

			my $entrypath = $path . "/" . $entry->{'id'};

			push @menu, {
				name => $entry->{'name'},
				type => 'link',
				url  => \&level,
				path => $entrypath,
				passthrough => [ $entrypath, $flags ],
			};
		}
			
		$i++;
	}

	my $ret = {
		items  => \@menu,
		offset => $json->{'offset'},
		total  => $json->{'totalSize'},
	};

	if ($playAction || $infoAction) {

		my %actions = (
			commonVariables	=> [ index => 'id', path => 'path', uri => 'uri' ],
			info => { command => ['smdinfocmd', 'items'], fixedParams => { playable => $playAction || 0 } },
		);

		if ($playAction) {
			$actions{'play'}  = { command => ['smdplcmd'], fixedParams => { cmd => 'load' }, };
			$actions{'add'}   = { command => ['smdplcmd'], fixedParams => { cmd => 'add' },	};
			$actions{'insert'}= { command => ['smdplcmd'], fixedParams => { cmd => 'insert' }, };
			$actions{'playall'} = $actions{'play'};
			$actions{'addall'}  = $actions{'add'};
		}
		
		$ret->{'actions'} = \%actions;
	}

	$callback->($ret);
}

sub plCommand {
	my $request = shift;

	my $client = $request->client;
	my $cmd    = $request->getParam('cmd');
	my $uri    = $request->getParam('uri');
	my $path   = $request->getParam('path');
	my $index  = $request->getParam('index');

	if ($cmd && $uri && !(defined $index && defined $path)) {

		my $obj;

		if ($uri =~ /^file:\/\// && (my $track = Slim::Schema->objectForUrl({'url' => $uri }))) {
			$obj = $track;
		} else {
			$obj = $uri;
		}

		$log->info("${cmd}ing track from $uri");
		$client->execute(['playlist', "${cmd}tracks", 'listref', [ $obj ] ]);

	} elsif ($cmd && $path) {

		my @pathElements;

		# extract object elements containing ':'
		for my $obj (split(/\//, $path)) {
			if ($obj =~ /:/) {
				push @pathElements, $obj;
			}
		}

		my $query = "/browse/Track?criteria=" . join("&criteria=", @pathElements);

		$log->info("fetching playlist from: $query");

		Plugins::SocialMusicDiscovery::Server->get(
			$query,
			sub {
				my $json = shift;
				my @tracks;
				my @urls;

				for my $entry (@{$json->{'items'}}) {

					if ($entry->{'playable'}) {

						for my $item (@{$entry->{'item'}->{'playableElements'}}) {

							my $url = $item->{'uri'};

							if ($url =~ /^file:\/\// && (my $track = Slim::Schema->objectForUrl({'url' => $url }))) {
								push @tracks, $track;
							} else {
								push @tracks, $url;
							}
						}
					}
				}

				if (scalar @tracks) {
					$log->info("${cmd}ing " . scalar @tracks . " tracks" . ($index ? " starting at $index" : ""));
					if (!$compat) {
						$client->execute([ 'playlist', "${cmd}tracks", 'listref', \@tracks, undef, $index ]);
					} else {
						$client->execute([ 'playlist', "${cmd}tracks", 'listref', \@tracks ]);
						if ($cmd eq 'load' && $index) {
							$client->execute([ 'playlist', 'jump', $index ]);
						}
					}
				} else {
					$log->info("no playable urls found");
				}
			},
			sub {
				$log->warn("error getting playlist: " . $_[0]);
			},
			{ timeout => 35 },
		);									   
	}
}

1;
