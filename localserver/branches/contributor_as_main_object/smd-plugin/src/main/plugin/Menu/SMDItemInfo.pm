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

package Plugins::SocialMusicDiscovery::Menu::SMDItemInfo;

=head1 NAME

Plugins::SocialMusicDiscovery::Menu::SMDItemInfo

=head1 DESCRIPTION

Context menu implementation for SMD items

=cut


use strict;

use base qw(Slim::Menu::Base);

use Scalar::Util qw(blessed);

use Slim::Utils::Log;
use Slim::Utils::Prefs;
use Slim::Utils::Strings qw(cstring);
use Data::Dumper;

my $serverPrefs = preferences('server');
my $prefs = preferences('plugin.socialmusicdiscovery');
my $log = logger('plugin.socialmusicdiscovery');
my $xmlBrowserImplementation;

sub init {
        my $class = shift;
	my $xmlBrowserImpl = shift;
        $class->SUPER::init();

	$xmlBrowserImplementation = $xmlBrowserImpl;
        
        Slim::Control::Request::addDispatch(
                [ 'smditeminfo', 'items', '_index', '_quantity' ],
                [ 0, 1, 1, \&cliQuery ]
        );
        
        Slim::Control::Request::addDispatch(
                [ 'smditeminfo', 'playlist', '_method' ],
                [ 1, 1, 1, \&cliQuery ]
        );
}

sub name {
	return 'SOCIALMUSICDISCOVERY_ITEM_INFO';
}

##
# Register all the information providers that we provide.
# This order is defined at http://wiki.slimdevices.com/index.php/UserInterfaceHierarchy
#
sub registerDefaultInfoProviders {
        my $class = shift;
        
        $class->SUPER::registerDefaultInfoProviders();

        $class->registerInfoProvider( addsmditem => (
                menuMode  => 1,
                after    => 'top',
                func      => \&addSMDItemEnd,
        ) );

        $class->registerInfoProvider( addsmditemnext => (
                menuMode  => 1,
                after    => 'addsmditem',
                func      => \&addSMDItemNext,
        ) );


        $class->registerInfoProvider( playitem => (
                menuMode  => 1,
                after    => 'addsmditemnext',
                func      => \&playSMDItem,
        ) );

        $class->registerInfoProvider( relatedsmditems => (
                after    => 'playitem',
                func      => \&relatedSMDItems,
        ) );
}

sub menu {
        my ( $class, $client, $path, $audio_url, $tags ) = @_;
        $tags ||= {};

        # If we don't have an ordering, generate one.
        # This will be triggered every time a change is made to the
        # registered information providers, but only then. After
        # that, we will have our ordering and only need to step
        # through it.
        my $infoOrdering = $class->getInfoOrdering;

        
        # $remoteMeta is an empty set right now. adding to allow for parallel construction with trackinfo
        my $remoteMeta = {};

        # Function to add menu items
        my $addItem = sub {
                my ( $ref, $items ) = @_;
                
                if ( defined $ref->{func} ) {
                        
                        my $item = eval { $ref->{func}->( $client, $path, $audio_url, $remoteMeta, $tags ) };
                        if ( $@ ) {
                                $log->error( 'SMDIteminfo menu item "' . $ref->{name} . '" failed: ' . $@ );
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
                                $log->error( 'SMDInfoinfo menu item "' . $ref->{name} . '" failed: not an arrayref or hashref' );
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
                type  => 'opml',
                items => $items,
                menuComplete => 1,
        };
}

sub playSMDItem {
        my ( $client, $path, $audio_url, $remoteMeta, $tags) = @_;

        my $items = [];
        my $jive;

        return $items if !blessed($client);
        
        my $play_string   = cstring($client, 'PLAY');

	my $actions;
	if(defined($audio_url)) {
		my $track = Slim::Schema->objectForUrl({
		        'url' => $audio_url,
		});
		$actions = {
		        go => {
		                player => 0,
		                cmd => [ 'playlistcontrol' ],
		                params => {
		                        track_id => $track->id,
		                        cmd => 'load',
		                },
		                nextWindow => 'nowPlaying',
		        },
		};
	}else {
		$actions = {
		        go => {
		                player => 0,
		                cmd => [ 'smdplaylistcontrol' ],
		                params => {
		                        path => $path,
		                        cmd => 'load',
		                },
		                nextWindow => 'nowPlaying',
		        },
		};
	}
        
        $actions->{play} = $actions->{go};

        $jive->{actions} = $actions;
        $jive->{style} = 'itemplay';

        push @{$items}, {
                type => 'text',
                name => $play_string,
                jive => $jive, 
        };
        
        return $items;
}

sub addSMDItemEnd {
        my ( $client, $path, $audio_url, $remoteMeta, $tags ) = @_;
        my $add_string   = cstring($client, 'ADD_TO_END');
        my $cmd = 'add';
        addSMDItem( $client, $path, $audio_url, $remoteMeta, $tags, $add_string, $cmd ); 
}


sub addSMDItemNext {
        my ( $client, $path, $audio_url, $remoteMeta, $tags ) = @_;
        my $add_string   = cstring($client, 'PLAY_NEXT');
        my $cmd = 'insert';
        addSMDItem( $client, $path, $audio_url, $remoteMeta, $tags, $add_string, $cmd ); 
}

sub addSMDItem {
        my ( $client, $path, $audio_url, $remoteMeta, $tags, $add_string, $cmd ) = @_;

        my $items = [];
        my $jive;
        
        return $items if !blessed($client);

        my $actions;
	if(defined($audio_url)) {
		my $track = Slim::Schema->objectForUrl({
			'url' => $audio_url,
		});
		$actions = {
		        go => {
		                player => 0,
		                cmd => [ 'playlistcontrol' ],
		                params => {
		                        track_id => $track->id,
		                        cmd => $cmd,
		                },
		                nextWindow => 'parent',
		        },
		};
	}else {
		$actions = {
		        go => {
		                player => 0,
		                cmd => [ 'smdplaylistcontrol' ],
		                params => {
		                        path => $path,
		                        cmd => $cmd,
		                },
		                nextWindow => 'parent',
		        },
		};
	}
        $actions->{play} = $actions->{go};
        $actions->{add}  = $actions->{go};

        $jive->{actions} = $actions;

        push @{$items}, {
                type => 'text',
                name => $add_string,
                jive => $jive, 
        };
        return $items;
}

sub relatedSMDItems {
        my ( $client, $path, $audio_url, $remoteMeta, $tags) = @_;

        my $items = [];
        my $jive;

        return $items if !blessed($client);

	$log->debug("Creating related to menu for: $path");

	my $item = undef;        
	if($path =~ /.*\/([^\/]+)$/) {
		$item = "/".$1;
	}

	return $items if(!defined($item));

	$item =~ s/:/=/g;

	$log->debug("Using item: $item");

        my $actions = {
                allAvailableActionsDefined => 1,
                items => {
                        command     => ['smdbrowsecontext', 'items'],
                        fixedParams => { mode => 'smdcontext', path => $item },
                },
        };


        push @{$items}, {
                type => 'link',
		url => 'blabla',
                name => cstring($client, 'PLUGIN_SOCIALMUSICDISCOVERY_RELATED_TO'),
                itemActions => $actions, 
        };
        
        return $items;
}

sub cliQuery {
        $log->debug('cliQuery');
        my $request = shift;
        
        # WebUI or newWindow param from SP side results in no
        # _index _quantity args being sent, but XML Browser actually needs them, so they need to be hacked in
        # here and the tagged params mistakenly put in _index and _quantity need to be re-added
        # to the $request params
        my $index      = $request->getParam('_index');
        my $quantity   = $request->getParam('_quantity');
        if ( $index =~ /:/ ) {
                $request->addParam(split (/:/, $index));
                $index = 0;
                $request->addParam('_index', $index);
        }
        if ( $quantity =~ /:/ ) {
                $request->addParam(split(/:/, $quantity));
                $quantity = 200;
                $request->addParam('_quantity', $quantity);
        }
        
        my $client         = $request->client;
        my $path           = $request->getParam('path');
        my $audio_url      = $request->getParam('audio_url');
        my $menuMode       = $request->getParam('menu') || 0;
        

        my $tags = {
                menuMode      => $menuMode,
        };

        unless ( $path ) {
                $request->setStatusBadParams();
                return;
        }
        
        my $feed = Plugins::SocialMusicDiscovery::Menu::SMDItemInfo->menu( $client, $path, $audio_url, $tags );
        
	no strict 'refs';
        &{"${xmlBrowserImplementation}::cliQuery"}( 'smditeminfo', $feed, $request );
	use strict 'refs';
}

1;

