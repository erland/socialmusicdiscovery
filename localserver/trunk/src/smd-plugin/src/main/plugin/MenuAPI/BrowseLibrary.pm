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

package Plugins::SocialMusicDiscovery::MenuAPI::BrowseLibrary;

=head1 NAME

Slim::MenuAPI::BrowseLibrary

=head1 SYNOPSIS

	use Plugins::SocialMusicDiscovery::MenuAPI::BrowseLibrary;
	
	Slim::MenuAPI::BrowseLibrary->registerNode({
		type         => 'link',
		name         => 'MYMUSIC_MENU_ITEM_TITLE',
		params       => {mode => 'myNewMode'},
		feed         => \&myFeed,
		icon         => 'html/images/someimage.png',
		homeMenuText => 'HOMEMENU_MENU_ITEM_TITLE',
		condition    => sub {my ($client, $nodeId) = @_; return 1;}
		id           => 'myNewModeId',
		weight       => 30,
	});
	
	Plugins::SocialMusicDiscovery::MenuAPI::BrowseLibrary->deregisterNode('someNodeId');
	
	Plugins::SocialMusicDiscovery::MenuAPI::BrowseLibrary->registerNodeFilter(\&nodeFilter);
	
	Plugins::SocialMusicDiscovery::MenuAPI::BrowseLibrary->deregisterNodeFilter(\&nodeFilter);

=head1 DESCRIPTION

Register or deregister menu items for the My Music menu.

Register or deregister filter functions used to determine if a menu
item should be included in the My Music menu, possibly for a specific client.

This version is inspired on the 31633 version of the "onebrowser" branch in Squeezebox Server.

=head2 registerNode()

The new menu item is specified using a HASH-ref as follows (mandatory items marked with *):

=over

=item C<type>*

C<link> | C<search>

=item C<id>*

Unique identifier for the menu item

=item C<name>*

Unique string name for the menu item title when used in the My Music menu

=item C<homeMenuText>

Unique string name for the menu item title when used in the Home menu

=item C<feed>*

function to a function that is invoked in the manner of an XMLBrowser function feed

=item C<icon>

Icon to be used with menu item

=item C<condition>

function to determine dynamically whether this menu item should be shown in the menu

=item C<weight>

Hint as to relative position of item in menu

=item C<params>

HASH-ref containing:

=over

=item C<mode>

This will default to the value of the C<id> of the menu item.
If one of C<artists, albums, genres, years, tracks, playlists, playlistTracks, bmf>
is used then it will override the default method from BrowseLibrary - use with caution.

=item C<sort track_id artist_id genre_id album_id playlist_id year folder_id>

When browsing to a deeper level in the menu hierarchy,
then any of these values (and only these values)
will be passed in the C<params> value of the I<args> HASH-ref passed as the third parameter
to the C<feed> function as part of the (re)navigation to the sub-menu(s).

Any search-input string will also be so passed as the C<search> value.

=back

All values of this C<params> HASH will be passed in the C<params> value
of the I<args> HASH-ref passed as the third parameter to the C<feed> function
when it is invoked at the top level.

=back

Note that both C<id> and C<name> should be unique 
and should not be one of the standard IDs or name strings used by BrowseLibrary.
That means that if, for example, one wants to replace the B<Artists> menu item,
one cannot use C<BROWSE_BY_ARTIST> as the C<name> string;
one must supply one's own string with a unique name,
but quite possibly pointing to the same localized strings.

=head2 deregisterNode()

Remove a previously registered menu item specified by its C<id>.

I<Caution:> will not restore any default BrowseLibrary handlers that had been overridden
using a C<params =E<gt> mode> value of one of the default handlers.

=head2 registerNodeFilter()

Register a function to be called when a menu is being displayed to determine whether that 
menu item should be included.

Passed the Slim::Player::Client for which the menu is being built, if it is a client-specific menu,
and the C<id> of the menu item.

Multiple filter functions can be registered.

If the condition associated with a menu item itself (if any),
or any of the registered filter functions,
return false then the menu item will not be included;
otherwise it will be included.

=head2 deregisterNodeFilter()

Deregister a menu-item filter.
If this method is going to be called then both registerNodeFilter() & deregisterNodeFilter()
should be passed a reference to a real sub (not an anonymous one).

=cut


use strict;
use Slim::Utils::Log;
use Slim::Utils::Prefs;
use Slim::Utils::Strings qw(cstring);

my $log = logger('database.info');

#my %pluginData = (
#	icon => 'html/images/browselibrary.png',
#);
#
#sub _pluginDataFor {
#	my $class = shift;
#	my $key   = shift;
#
#	my $pluginData = $class->pluginData() if $class->can('pluginData');
#
#	if ($pluginData && ref($pluginData) && $pluginData->{$key}) {
#		return $pluginData->{$key};
#	}
#	
#	if ($pluginData{$key}) {
#		return $pluginData{$key};
#	}
#
#	return __PACKAGE__->SUPER::_pluginDataFor($key);
#}
#
#my @submenus = (
#	['Albums', 'browsealbums', 'BROWSE_BY_ALBUM', \&_albums, {
#		icon => 'html/images/albums.png',
#	}],
#	['Artists', 'browseartists', 'BROWSE_BY_ARTIST', \&_artists, {
#		icon => 'html/images/artists.png',
#	}],
#);
#
#sub _initSubmenu {
#	my ($class, %args) = @_;
#	$args{'weight'} ||= $class->weight() + 1;
#	$args{'is_app'} ||= 0;
#	$class->SUPER::initPlugin(%args);
#}
#
#sub _initSubmenus {
#	my $class = shift;
#	my $base  = __PACKAGE__;
#	
#	foreach my $menu (@submenus) {
#
#		my $packageName = $base . '::' . $menu->[0];
#		my $pkg = "{
#			package $packageName;
#			use base '$base';
#			my \$pluginData;
#			
#			sub init {
#				my (\$class, \$feed, \$data) = \@_;
#				\$pluginData = \$data;
#				\$class->SUPER::_initSubmenu(feed => \$feed, tag => '$menu->[1]');
#			}
#		
#			sub getDisplayName {'$menu->[2]'}	
#			sub pluginData {\$pluginData}	
#		}";
#		
#		eval $pkg;
#		
#		$packageName->init($menu->[3], $menu->[4]);
#	}
#}

use constant BROWSELIBRARY => 'smdbrowselibrary';

my $_initialized = 0;
my $_pendingChanges = 0;
my %nodes;
my @addedNodes;
my @deletedNodes;

my %browseLibraryModeMap = (
);

my %nodeFilters;

sub getFeedForMode {
	my ($class, $mode) = @_;

	return $browseLibraryModeMap{$mode};
}

sub setFeedForMode {
	my ($class, $mode, $feed) = @_;

	$browseLibraryModeMap{$mode} = $feed;
}

sub registerNodeFilter {
	my ($class, $filter) = @_;
	
	if (!ref $filter eq 'CODE') {
		$log->error("Invalid filter: must be a CODE ref");
		return;
	}
	
	$nodeFilters{$filter} = 1;
}

sub deregisterNodeFilter {
	my ($class, $filter) = @_;
	
	delete $nodeFilters{$filter};
}

sub registerNode {
	my ($class, $node) = @_;
	
	return unless $node->{'id'};
	
	if (!$node->{'id'} || ref $node->{'feed'} ne 'CODE') {
		logBacktrace('Invalid node specification');
		return 0;
	}
	
	if ($nodes{$node->{'id'}}) {
		logBacktrace('Duplicate node id: ', $node->{'id'});
		return 0;
	}
	
	$node->{'params'}->{'mode'} ||= $node->{'id'};
	$nodes{$node->{'id'}} = $node;
	$browseLibraryModeMap{$node->{'params'}->{'mode'}} = $node->{'feed'};
	
	$class->_scheduleMenuChanges($node, undef);
	
	return 1;
}

sub deregisterNode {
	my ($class, $id) = @_;
	
	if (my $node = delete $nodes{$id}) {
		if ($browseLibraryModeMap{$node->{'params'}->{'mode'}} == $node->{'feed'}) {
			delete $browseLibraryModeMap{$node->{'params'}->{'mode'}};
		}
		$class->_scheduleMenuChanges(undef, $node);
	}
}


sub init {
	my $class = shift;
	
	main::DEBUGLOG && $log->is_debug && $log->debug('init');
	
	{
		no strict 'refs';
		*{$class.'::'.'feed'}     = sub { \&_topLevel; };
		*{$class.'::'.'tag'}      = sub { BROWSELIBRARY };
		*{$class.'::'.'modeName'} = sub { BROWSELIBRARY };
		*{$class.'::'.'menu'}     = sub { undef };
		*{$class.'::'.'weight'}   = sub { 15 };
		*{$class.'::'.'type'}     = sub { 'link' };
	}
	
	$class->_initCLI();
	
#	$class->_initSubmenus();
	
    $class->_initModes();
    
    Slim::Control::Request::subscribe(\&_libraryChanged, [['library'], ['changed']]);
    Slim::Control::Request::subscribe(\&_registerJiveMenu, [['client'], ['new']]);
    
    $_initialized = 1;
}

sub _registerJiveMenu {
	my $request = shift;
	my $client = $request->client();
	
	my $menu = getJiveMenu($client,'myMusic','album',\&Slim::Control::Jive::refreshPluginMenus);
	Slim::Control::Jive::registerPluginMenu($menu,undef,$client);
}

sub cliQuery {
 	my $request = shift;
	Plugins::SocialMusicDiscovery::MenuAPI::Control::XMLBrowser::cliQuery( BROWSELIBRARY, \&_topLevel, $request );
};

sub _initCLI {
	my ( $class ) = @_;
	
	# CLI support
	Slim::Control::Request::addDispatch(
		[ BROWSELIBRARY, 'items', '_index', '_quantity' ],
	    [ 0, 1, 1, \&cliQuery ]
	);
	
	Slim::Control::Request::addDispatch(
		[ BROWSELIBRARY, 'playlist', '_method' ],
		[ 1, 1, 1, \&cliQuery ]
	);
}

sub _addMode {
	my ($class, $node) = @_;
	
	Slim::Buttons::Home::addSubMenu('BROWSE_MUSIC', $node->{'name'}, {
		useMode   => $class->modeName(),
		header    => $node->{'name'},
		condition => sub {return _conditionWrapper(shift, $node->{'id'}, $node->{'condition'});},
		title     => '{' . $node->{'name'} . '}',
		%{$node->{'params'}},
	});
	
	if ($node->{'homeMenuText'}) {
		Slim::Buttons::Home::addMenuOption($node->{'name'}, {
			useMode   => $class->modeName(),
			header    => $node->{'homeMenuText'},
			title     => '{' . $node->{'homeMenuText'} . '}',
			%{$node->{'params'}},
			
		});
	}
}

sub _initModes {
	my $class = shift;
	
	Slim::Buttons::Common::addMode($class->modeName(), {}, sub { $class->setMode(@_) });
	
	foreach my $node (@{_getNodeList()}) {
		$class->_addMode($node);
	}
}

my $jiveUpdateCallback = undef;

sub _libraryChanged {
	if ($jiveUpdateCallback) {
		$jiveUpdateCallback->();
	}
}

sub _scheduleMenuChanges {
	my $class = shift;
	
	my ($add, $del) = @_;
	
	return if !$_initialized;
	
	push @addedNodes, $add if $add;
	push @deletedNodes, $del if $del;
	
	return if $_pendingChanges;
	
	Slim::Utils::Timers::setTimer($class, Time::HiRes::time() + 1, \&_handleMenuChanges);

	$_pendingChanges = 1;
}

sub _handleMenuChanges {
	my $class = shift;
	# do deleted first, then added
	
	foreach my $node (@deletedNodes) {

		Slim::Buttons::Home::delSubMenu('BROWSE_MUSIC', $node->{'name'});
		if ($node->{'homeMenuText'}) {
			Slim::Buttons::Home::delMenuOption($node->{'name'});
		}
	}
	
	foreach my $node (@addedNodes) {
		$class->_addMode($node);
	}
	
	@addedNodes = ();
	@deletedNodes = ();
	$_pendingChanges = 0;

	_libraryChanged();
}

sub _conditionWrapper {
	my ($client, $id, $baseCondition) = @_;
	
	if ($baseCondition && !$baseCondition->($client, $id)) {
		return 0;
	}
	
	foreach my $filter (keys %nodeFilters) {
		my $status;
		
		eval {
			$status = $_->($client, $id)
		};
		
		if ($@) {
			$log->warn("Couldn't call menu-filter", Slim::Utils::PerlRunTime::realNameForCodeRef($_), ": $@");
			# Assume true
			next;
		}
		
		if (!$status) {
			return 0;
		}
	}
	
	return 1;
}

sub _getNodeList {
	my ($albumsSort) = @_;
	$albumsSort ||= 'album';
	
	my @specials = ();
	
	return [@specials, values %nodes];
}

sub getJiveMenu {
	my ($client, $baseNode, $albumSort, $updateCallback) = @_;
	
	$jiveUpdateCallback = $updateCallback if $updateCallback;
	
	my @myMusicMenu;
	
	foreach my $node (@{_getNodeList($albumSort)}) {
		if (!_conditionWrapper($client, $node->{'id'}, $node->{'condition'})) {
			next;
		}
		
		my %menu = (
			text => cstring($client, $node->{'name'}),
			id   => $node->{'id'},
			node => $baseNode,
			weight => $node->{'weight'},
			actions => {
				go => {
					cmd    => [BROWSELIBRARY, 'items'],
					params => {
						menu => 1,
						%{$node->{'params'}},
					},
					
				},
			}
		);
		
		if ($node->{'homeMenuText'}) {
			$menu{'homeMenuText'} = cstring($client, $node->{'homeMenuText'});
		}
		
		push @myMusicMenu, \%menu;
	}
	
	return \@myMusicMenu;
}

sub setMode {
	my ( $class, $client, $method ) = @_;

	if ($method eq 'pop') {

		Slim::Buttons::Common::popMode($client);
		return;
	}

	my $name  = $class->getDisplayName();
	my $title = (uc($name) eq $name) ? cstring($client,  $name ) : $name;
	
	my %params = (
		header   => $name,
		modeName => $name,
		url      => $class->feed( $client ),
		title    => $title,
		timeout  => 35,
		%{$client->modeParams()},
	);
	Slim::Buttons::Common::pushMode( $client, 'smdxmlbrowser', \%params );
	
	# we'll handle the push in a callback
	$client->modeParam( handledTransition => 1 );
}

my @topLevelArgs = qw();

sub _topLevel {
	my ($client, $callback, $args) = @_;
	my $params = $args->{'params'};
	
	if ($params) {
		my %args;

		if ($params->{'query'} && $params->{'query'} =~ /C<$1>=(.*)/) {
			$params->{$1} = $2;
		}

		my @searchTags;
		for (@topLevelArgs) {
			push (@searchTags, $_ . ':' . $params->{$_}) if $params->{$_};
		}
		$args{'searchTags'}   = \@searchTags if scalar @searchTags;
		$args{'sort'}         = 'sort:' . $params->{'sort'} if $params->{'sort'};
		$args{'search'}       = $params->{'search'} if $params->{'search'};
		$args{'wantMetadata'} = $params->{'wantMetadata'} if $params->{'wantMetadata'};
		
		if ($params->{'mode'}) {
			my %entryParams;
			for (@topLevelArgs, qw(sort search mode)) {
				$entryParams{$_} = $params->{$_} if $params->{$_};
			}
			main::INFOLOG && $log->is_info && $log->info('params=>', join('&', map {$_ . '=' . $entryParams{$_}} keys(%entryParams)));
			
			my $func = $browseLibraryModeMap{$params->{'mode'}};
			
			if (ref $func ne 'CODE') {
				$log->error('No feed method for mode: ', $params->{'mode'});
				return;
			}
			
			&$func($client,
				sub {
					my $opml = shift;
					$opml->{'query'} = \%entryParams;
					$callback->($opml, @_);
				},
				$args, \%args);
			return;
		}
	}
	
	$log->error("Routing failure: node mode param");
}

sub getDisplayName () {
	return 'MY_MUSIC';
}

sub playerMenu {'PLUGINS'}


1;
