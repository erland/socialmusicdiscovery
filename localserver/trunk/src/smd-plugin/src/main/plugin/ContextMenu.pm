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
package Plugins::SocialMusicDiscovery::ContextMenu;

use strict;
use warnings;

use base qw(Slim::Menu::Base);

use URI::Escape;

use Slim::Utils::Strings qw(cstring);
use Slim::Utils::Log;
use Slim::Utils::Prefs;

my $log    = logger('plugin.socialmusicdiscovery');
my $prefs  = preferences('plugin.socialmusicdiscovery');

sub init {
	my $class = shift;

	$class->SUPER::init;

	# create our own info commands
	# support 3 levels of context menu through 3 different commands, this allows us to cache each level separately
	Slim::Control::Request::addDispatch(['smdinfocmd0', 'items', '_index', '_quantity' ], [0, 1, 1, \&infoCommand]);
	Slim::Control::Request::addDispatch(['smdinfocmd1', 'items', '_index', '_quantity' ], [0, 1, 1, \&infoCommand]);
	Slim::Control::Request::addDispatch(['smdinfocmd2', 'items', '_index', '_quantity' ], [0, 1, 1, \&infoCommand]);
}

sub name {
	return 'PLUGIN_SOCIALMUSICDISCOVERY';
}

sub registerDefaultInfoProviders {
	my $class = shift;
	
	$class->SUPER::registerDefaultInfoProviders();

	$class->registerInfoProvider( addsmditem => (
		menuMode  => 1,
		after    => 'top',
		func      => \&addItemEnd,
	) );
	$class->registerInfoProvider( addsmditemnext => (
		menuMode  => 1,
		after    => 'addsmditem',
		func      => \&addItemNext,
	) );
	$class->registerInfoProvider( playsmditem => (
		menuMode  => 1,
		after    => 'addsmditemnext',
		func      => \&playItem,
	) );
	$class->registerInfoProvider( smdcontext => (
		after    => 'middle',
		func      => \&smdContext,
	) );
}

sub menu {
	my ($class, $client, $playpath, $path, $tags, $contextInfo) = @_;

	my $infoOrdering = $class->getInfoOrdering;
	
	# Function to add menu items
	my $addItem = sub {
		my ( $ref, $items ) = @_;
		
		if ( defined $ref->{func} ) {
			
			# nb functions are called with different params from normal SBS context menus
			my $item = eval { $ref->{func}->($client, $playpath, $path, $tags, $contextInfo) };
			if ( $@ ) {
				$log->error( 'smdinfo menu item "' . $ref->{name} . '" failed: ' . $@ );
				return;
			}
			
			return unless defined $item;
			
			# skip jive-only items for non-jive UIs
			return if $ref->{menuMode} && !$tags->{menuMode};
			
			if ( ref $item eq 'ARRAY' ) {
				if ( scalar @{$item} ) {
					push @{$items}, @{$item};
				}
			}
			elsif ( ref $item eq 'HASH' ) {
				return if $ref->{menuMode} && !$tags->{menuMode};
				if ( scalar keys %{$item} ) {
					push @{$items}, $item;
				}
			}
			else {
				$log->error('smdinfo menu item "' . $ref->{name} . '" failed: not an arrayref or hashref' );
			}				
		}
	};
	
	# Now run the order, which generates all the items we need
	my $items = [];
	
	for my $ref ( @{ $infoOrdering } ) {
		# Skip items with a defined parent, they are handled
		# as children below
		next if $ref->{parent};
		
		# Add the item
		$addItem->( $ref, $items );
		
		# Look for children of this item
		my @children = grep {
			$_->{parent} && $_->{parent} eq $ref->{name}
		} @{ $infoOrdering };
		
		if ( @children ) {
			my $subitems = $items->[-1]->{items} = [];
			
			for my $child ( @children ) {
				$addItem->( $child, $subitems );
			}
		}
	}

	return {
		name  => $contextInfo && $contextInfo->{'context'} && $contextInfo->{'context'}->{'name'},
		type  => 'opml',
		items => $items,
		menuComplete => 1,
	};
}

sub _entry {
	my ($cmd, $playpath, $path, $index) = @_;
	return {
		player => 0,
		cmd => [ 'smdplcmd' ],
		params => {
			cmd      => $cmd,
			playpath => $playpath,
			infopath => $path,
			index_cm => $index,
		},
		nextWindow => $cmd eq 'load' ? 'nowPlaying' :'parent',
	}
}

sub addItemEnd {
	my ($client, $playpath, $path, $tags, $contextInfo) = @_;

	return if !$tags->{'playable'};

	my $action = _entry('add', $playpath, $path, $tags->{'index'});

	return { 
		name => cstring($client, 'ADD'),
		type => 'text', 
		jive => {
			style => 'itemplay',
			actions => { 
				go   => $action,
				add  => $action,
				play => $action,
			},	 
		},
	};
}

sub addItemNext {
	my ($client, $playpath, $path, $tags, $contextInfo) = @_;

	return if !$tags->{'playable'};

	my $action = _entry('insert', $playpath, $path, $tags->{'index'});

	return { 
		name => cstring($client, 'PLAY_NEXT'),
		type => 'text', 
		jive => {
			style => 'itemplay',
			actions => { 
				go   => $action,
				add  => $action,
				play => $action,
			},	 
		},
	};
}

sub playItem {
	my ($client, $playpath, $path, $tags, $contextInfo) = @_;

	return if !$tags->{'playable'};

	my $action = _entry('load', $playpath, $path, $tags->{'index'});

	return { 
		name => cstring($client, 'PLAY'),
		type => 'text', 
		jive => {
			style => 'itemplay',
			actions => { 
				go   => $action,
				play => $action,
			},	 
		},
	};
}

sub smdContext {
	my ($client, $playpath, $path, $tags, $contextInfo) = @_;

	return if !$contextInfo;

	my $cmDepth = $tags->{'cmDepth'} + 1;

	if ($cmDepth > 2) {
		$log->warn("context menu depth exceeded");
		return undef;
	}

	my @menu;

	for my $item (@{$contextInfo->{'items'}}) {
		push @menu, {
			name => $item->{'name'},
			url  => \&Plugins::SocialMusicDiscovery::Browse::level, 
			passthrough => [ $path . "/" . $item->{'id'}, { cm_depth => $cmDepth } ],
		};
	}

	return \@menu;
}

# keep a small cache of feeds params to allow browsing into feeds
tie my %cachedFeed, 'Tie::Cache::LRU', 10;

sub infoCommand {
	my $request = shift;

	if ($request->isNotQuery([['smdinfocmd0', 'smdinfocmd1', 'smdinfocmd2']])) {
		$request->setStatusBadDispatch();
		return;
	}

	my $client = $request->client;
	my $infopath    = $request->getParam('infopath');
	my $playpath    = $request->getParam('playpath');
	my $menuMode    = $request->getParam('menu') || 0;
	my $menuContext = $request->getParam('context') || 'normal';
	my $playable    = $request->getParam('playable');
	my $index       = $request->getParam('index');

	my $connectionId = $request->connectionID || 0;

	my $command = $request->getRequest(0);
	my ($cmDepth) = $command =~ /smdinfocmd(\d+)/ ;

	my $cacheKey = "$cmDepth-$connectionId";

	$infopath = uri_unescape($infopath);

	my $tags = {
		menuMode    => $menuMode,
		menuContext => $menuContext,
		playable    => $playable,
		index       => $index,
		cmDepth     => $cmDepth,
	};

	if ($infopath) {
		
		$log->info("info request for: $infopath cache key: $cacheKey");
		
		my @pathElements = split(/\//, $infopath);
		
		# strip off non object items
		while (scalar @pathElements && $pathElements[0] !~ /:/) {
			shift @pathElements;
		}
		
		# use the last object as the key for the context menu
		my $contextPath = "/browse/context/" . join(',', $pathElements[-1] || '');

		$request->setStatusProcessing;
		
		my $contextCB = sub {
			my $contextInfo = shift;

			$cachedFeed{ $cacheKey } = [ $client, $playpath, $contextPath, $tags, $contextInfo ];

			my $feed = __PACKAGE__->menu($client, $playpath, $contextPath, $tags, $contextInfo);
			
			# call xmlbrowser using compat version if necessary
			if (!Plugins::SocialMusicDiscovery::Browse->compat) {
				Slim::Control::XMLBrowser::cliQuery($command, $feed, $request);
			} else {
				Slim76Compat::Control::XMLBrowser::cliQuery($command, $feed, $request);
			}
		};

		Plugins::SocialMusicDiscovery::Server->get(
			$contextPath,
			$contextCB,
			sub {
				$log->warn("can't get context menu for $contextPath");
				$contextCB->(undef);
			},
			{ timeout => 35 },
		);

		return;

	} else {

		if ( $cachedFeed{ $cacheKey } ) {

			$log->info("using cached feed key: $cacheKey");

			my $feed = __PACKAGE__->menu(@{ $cachedFeed{ $cacheKey } });

			# call xmlbrowser using compat version if necessary
			if (!Plugins::SocialMusicDiscovery::Browse->compat) {
				Slim::Control::XMLBrowser::cliQuery($command, $feed, $request);
			} else {
				Slim76Compat::Control::XMLBrowser::cliQuery($command, $feed, $request);
			}

			return;
		}
	}

	$log->warn("no feed");
	$request->setStatusBadParams();
}

1;
