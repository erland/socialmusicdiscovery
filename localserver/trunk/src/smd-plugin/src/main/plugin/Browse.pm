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
use URI::Escape;
use JSON::XS::VersionOneAndTwo;

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
	my ($client, $callback, $args, $path, $session) = @_;

	my $query = $path;

	my $index = $args->{'index'} || 0;
	my $quantity = $args->{'quantity'};

	if ($quantity) {
		$query .= "?offset=$index&size=$quantity";
	}

	$session ||= {};
	$session->{'ipeng'} ||= $args->{'params'}->{'userInterfaceIdiom'} && $args->{'params'}->{'userInterfaceIdiom'} =~ /iPeng/;
	$session->{'cm_depth'} ||= 0;

	if (!defined $session->{'playalbum'}) {

		# FIXME: do we want this - makes the web interface only play the selected track?
		# FIXME: is this offset always correct - better to add something to the params?
		if (caller(10) =~ /Web/) {
			$session->{'playalbum'} = 0;
		}

		if (!defined $session->{'playalbum'} && $client) {
			$session->{'playalbum'} = $sprefs->client($client)->get('playtrackalbum');
		}

		# if player pref for playtrack album is not set, get the old server pref.
		if (!defined $session->{'playalbum'}) {
			$session->{'playalbum'} = $sprefs->get('playtrackalbum') ? 1 : 0;
		}
	}

	$log->info("fetching next level from: $query path: $path");

	Plugins::SocialMusicDiscovery::Server->get(
		$query,
		sub {
			_createResponse($client, $callback, $_[0], $path, $session);
		},
		sub {
			$callback->({ type => 'text', name => "Error", items => [ { type => 'text', name => "$_[0]" } ] });
		},
		{ timeout => 35 },
	);
}

sub _createResponse {
	my ($client, $callback, $json, $path, $session) = @_;

	my $i = 0;
	my @menu;
	my $playAction; my $infoAction;

	my $playableBase = $json->{'playableBaseURL'};
	my $cm_depth = $session->{'cm_depth'} || 0;
	my $uriBase = Plugins::SocialMusicDiscovery::Server->uriBase;

	for my $entry (@{$json->{'items'}}) {
		
		my $menu; my $noArtwork;

		if ($entry->{'playable'}) {

			my $entrypath = $path . "/" . $entry->{'id'};

			$playAction = 1;

			if ($entry->{'leaf'}) {

				$menu = {
					name     => $entry->{'name'},
					type     => 'audio',
					url      => 'someurl', # needed to make button mode context menu appear
					infopath => uri_escape($entrypath),
					textkey  => $entry->{'sortKey'},
				};
		
				if ($session->{'playalbum'}) {
					$menu->{'ind'} = $i;
					$menu->{'playpath'} = uri_escape($playableBase);
				} else {
					$menu->{'ind'} = 'none'; # define so all commonVariables are set, otherwise CM not available on web interface
					$menu->{'playpath'} = uri_escape($playableBase . $entry->{'playable'});
				}

				# don't add artwork for track entries
				$noArtwork = 1;

			} else {

				$menu = {
					name     => $entry->{'name'},
					type     => $session->{'ipeng'} ? 'opml' : 'playlist',
					url      => \&level,
					infopath => uri_escape($entrypath),
					playpath => uri_escape($playableBase . $entry->{'playable'}),
					ind      => 'none', # define so all commonVariables are set, otherwise CM not available on web interface
					passthrough => [ $entrypath, $session ],
					textkey  => $entry->{'sortKey'},
					# following are used for favorites only
					favorites_url => $uriBase . $entrypath,
					favorites_type=> 'link',
					parser   => __PACKAGE__,
				};
			}

		} elsif ($entry->{'leaf'}) {

			$menu = {
				name => $entry->{'name'},
				type => 'text',
			};

		} else {

			$infoAction = 1;

			my $entrypath = $path . "/" . $entry->{'id'};

			$menu = {
				name     => $entry->{'name'},
				type     => 'link',
				url      => \&level,
				infopath => uri_escape($entrypath),
				passthrough => [ $entrypath, $session ],
				textkey  => $entry->{'sortKey'},
				# following are used for favorites only
				favorites_url => $uriBase . $entrypath,
				parser   => __PACKAGE__,
			};

			# special case - browse ImageFolder in slideshow
			if ($entry->{'type'} eq 'ImageFolder') {
				$menu->{'type'} = 'slideshow';
				$session->{'slideshow'} = 1;
			}

		}

		if (!$noArtwork && (my $image = $entry->{'image'})) {

			if ($session->{'slideshow'}) {
				$menu->{'type'} = 'slideshow';
				$menu->{'date'} = '';
				$menu->{'owner'} = '';
			}

			if ($image->{'providerId'} eq 'squeezeboxserver') {

			   $menu->{'artwork_track_id'} = $image->{'providerImageId'};
			   $menu->{'image'}            = "music/$image->{providerImageId}/cover";

		   } else {

			   $menu->{'image'} = $image->{'url'};
		   }
		}

		push @menu, $menu;
			
		$i++;
	}

	my $ret = {
		items  => \@menu,
		offset => $json->{'offset'},
		total  => $json->{'totalSize'},
	};

	if ($json->{'alphabetic'}) {
		$ret->{'sorted'} = 1;
	}

	if ($playAction || $infoAction) {

		my %actions = (
			commonVariables	=> [ ind => 'ind', playpath => 'playpath', infopath => 'infopath' ],
			info => { command => [ "smdinfocmd$cm_depth", 'items' ], fixedParams => { playable => $playAction || 0 } },
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

	if ($callback) {

		$callback->($ret);

	} else {

		return $ret;
	}
}

# this is called from xmlbrowser entries which have parser set to this module - used for favorite entries
sub parse {
    my ($class, $http) = @_;

    my $params = $http->params('params');
    my $url    = $params->{'url'};
	my $client = $params->{'client'};

	my ($path) = $url =~ /http:\/\/.*:\d+(\/.*)/;

	$log->info("parsing favorite for: $path");

	my $json   = eval { from_json($http->content) };

	if ($@) {
		$log->warn("$@");
	}

	return _createResponse($client, undef, $json, $path);
}

sub plCommand {
	my $request = shift;

	my $client   = $request->client;
	my $cmd      = $request->getParam('cmd');
	my $playpath = $request->getParam('playpath');
	my $ind      = $request->getParam('ind');
	my $ind_cm   = $request->getParam('ind_cm');

	# undef if set to 'none' as this is a workaround for web CM
	$ind = undef if defined $ind && $ind eq 'none';

	$playpath = uri_unescape($playpath);

	$log->info("fetching playable items from: $playpath");

	Plugins::SocialMusicDiscovery::Server->get(
		$playpath,
		sub {
			my $json = shift;
			my @tracks;
			my @urls;

			for my $item (@{$json->{'items'}}) {
				
				if (my $url = $item->{'uri'}) {

					if ($url =~ /^file:\/\// && (my $track = Slim::Schema->objectForUrl({'url' => $url }))) {
						push @tracks, $track;
					} else {
						push @tracks, $url;
					}
				}
			}

			if (defined $ind_cm) {
				$log->debug("context menu - ${cmd}ing single track at index: $ind_cm");
				@tracks = ( $tracks[$ind_cm] );
			}
			
			if (scalar @tracks) {
				$log->info("${cmd}ing " . scalar @tracks . " tracks" . ($ind ? " starting at $ind" : ""));
				if (!$compat) {
					$client->execute([ 'playlist', "${cmd}tracks", 'listref', \@tracks, undef, $ind ]);
				} else {
					$client->execute([ 'playlist', "${cmd}tracks", 'listref', \@tracks ]);
					if ($cmd eq 'load' && $ind) {
						$client->execute([ 'playlist', 'jump', $ind ]);
					}
				}
			} else {
				$log->info("no playable urls found");
			}
		},
		sub {
			$log->warn("error getting playable: " . $_[0]);
		},
		{ timeout => 35 },
	);									   
}

1;
