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
use Data::Dumper;

my $serverPrefs = preferences('server');
my $prefs = preferences('plugin.socialmusicdiscovery');
my $log = logger('plugin.socialmusicdiscovery');
my $browseLibraryImplementation;
my $separateJumpCommand;

my %nodeFilters;

my %modeMap = (
	'smd' => \&_smd,
	'smdcontext' => \&_smd,
);

my %modeUrl = (
	'smd' => "/library",
	'smdcontext' => "/context",
);

my %modeBrowseCmd = (
	'smd' => "browselibrary",
	'smdcontext' => "smdbrowsecontext",
);

sub init {
	my $class = shift;
	my $blImplementation = shift;
	my $separateJump = shift || 0;
	
	$browseLibraryImplementation = $blImplementation;
	$separateJumpCommand = $separateJump;

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
		$browseLibraryImplementation->registerNode($_);
	}
	Slim::Control::Request::addDispatch(
		[ "smdplaylistcontrol"],
	    [ 1, 0, 1, \&cliPlaylistControl ]
	);
	Slim::Control::Request::addDispatch(
		[ "smdbrowsecontext", 'items', '_index', '_quantity' ],
	    [ 0, 1, 1, \&cliBrowseContext ]
	);
}

sub cliBrowseContext {
 	my $request = shift;
	Slim::Control::XMLBrowser::cliQuery( "smdbrowsecontext", \&_contextMenu, $request );
};

my @topLevelArgs = qw();

sub _contextMenu {
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
			
			my $func = $modeMap{$params->{'mode'}};
			
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

sub cliPlaylistControl {
	my $request = shift;

	# check this is the correct command.
	if ($request->isNotCommand([['smdplaylistcontrol']])) {
		$request->setStatusBadDispatch();
		return;
	}

	# get the parameters
	my $client              = $request->client();
	my $cmd                 = $request->getParam('cmd');
	my $jumpIndex           = $request->getParam('play_index');
	my $path                = $request->getParam('path');
	my $audio_url                = $request->getParam('audio_url');

	if ($request->paramUndefinedOrNotOneOf($cmd, ['load', 'insert', 'add', 'delete'])) {
		$request->setStatusBadParams();
		return;
	}

	if(defined($audio_url) && $audio_url ne "") {
		$log->debug("Getting track: ".$audio_url);
		my $track = Slim::Schema->objectForUrl({
		        'url' => $audio_url,
		});
		$log->info("Playing track: ".$track->id);
		Slim::Control::Request::executeRequest(
			$client, ['playlistcontrol', 'cmd:'.$cmd, 'track_id:'.$track->id]
		);
		$request->setStatusDone();
		return;
	}

	if (!defined($path)) {
		$request->setStatusBadParams();
		return;
	}

	$path =~ s/^\///;
	my @pathElements = split(/\//,$path);

	# Remove any item that doesn't contain ":", this is not needed for play command
	# Only use the items after the first "." item
	my @newItemList = ();
	my $start = 0;
	foreach my $part (@pathElements) {
		if($part =~ /=/) {
			if($start) {
				$part =~ s/=/:/g;
				push @newItemList,$part;
			}
		}else {
			$start = 1;
		}
	}
	@pathElements = @newItemList;

	if (scalar(@pathElements)==0) {
		$request->setStatusBadParams();
		return;
	}

	main::INFOLOG && $log->is_info && $log->info("smdplaylistcontrol ",join(", ", @pathElements));
	
	my $http = Slim::Networking::SimpleAsyncHTTP->new(\&_playReply, \&_playError, {
		cmd => $cmd,
		request => $request,
                client => $client, 
                jumpIndex => $jumpIndex,
        });

	my $hostname = $prefs->get('hostname');
	my $port = $prefs->get('port');
	my $url = "http://".$hostname.":".$port."/browse/Track?criteria=".join("&criteria=",@pathElements);

	$log->info("Getting data using: ".$url);
	$http->get($url);
}

sub _playReply {
	my $http = shift;
	my $params = $http->params();
	
	my $content = $http->content();
	my $jsonResult = JSON::XS::decode_json($content);
	main::DEBUGLOG && $log->is_debug && $log->debug("Reply: ".Dumper($jsonResult));
	my @trackIdList = ();
	foreach (@{$jsonResult->{'items'}}) {
		my $playableElements = $_->{'item'}->{'playableElements'};
		if(scalar(@$playableElements)>0) {
			my $playableElement = shift @$playableElements;
			$log->debug("Using: ".$playableElement->{'uri'});
			my $track = Slim::Schema->objectForUrl({
		                'url' => $playableElement->{'uri'},
		        });
			if(defined($track)) {
				push @trackIdList, $track->id; 
			}
		}
	}

	if(scalar(@trackIdList)>0) {
		$log->info("Playing tracks: ".join(",",@trackIdList));
		if(defined($params->{'jumpIndex'})) {
			Slim::Control::Request::executeRequest(
				$params->{'client'}, ['playlistcontrol', 'cmd:'.$params->{'cmd'}, 'track_id:'.join(",",@trackIdList), 'play_index:'.$params->{'jumpIndex'}]
			);
			
			# Separate jump command is required in 7.5 since it doesn't support the "play_index" parameter
			if($separateJumpCommand && $params->{'cmd'} eq 'load') {
				Slim::Control::Request::executeRequest(
					$params->{'client'}, ['playlist', 'jump', $params->{'jumpIndex'}]
				);
			}
		}else {
			Slim::Control::Request::executeRequest(
				$params->{'client'}, ['playlistcontrol', 'cmd:'.$params->{'cmd'}, 'track_id:'.join(",",@trackIdList)]
			);
		}
	}else {
		$log->error("Error executing smdplaylistcontrol command, no playable elements found");
	}

	$params->{'request'}->setStatusDone();
}

sub _playError {
	my $http = shift;
	my $params = $http->params();

	$log->error("Error executing smdplaylistcontrol command, unable to communicate with server");
	$params->{'request'}->setStatusDone();
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
		$path =~ s/=/:/g;
	}
	my $userInterfaceIdiom = "Logitech";
	if(defined($params->{'userInterfaceIdiom'})) {
		$userInterfaceIdiom = $params->{'userInterfaceIdiom'};
	}

	main::INFOLOG && $log->is_info && $log->info("$path ($index, $quantity): tags ->", join(', ', @$criterias));
	
	my $http = Slim::Networking::SimpleAsyncHTTP->new(\&_genericReply, \&_genericError, {
		path => $path,
		args => $args,
		criterias => $criterias,
		userInterfaceIdiom => $userInterfaceIdiom,
                client => $client, 
                callback => $callback, 
                resultsFunc => $resultsFunc, 
        });

	my $url = "http://".$hostname.":".$port."/browse".$modeUrl{$params->{'mode'}}.$path;
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
	my ($result, $extraitems) = $params->{'resultsFunc'}->($params->{'path'}, $params->{'userInterfaceIdiom'}, $jsonResult, $params->{'args'}, $params->{'criterias'});
	$result->{'offset'} = $jsonResult->{'offset'};
	$result->{'count'} = $jsonResult->{'size'};
	$result->{'total'} = $jsonResult->{'totalSize'};
	main::DEBUGLOG && $log->is_debug && $log->debug("Returning ".Dumper($result));
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
		\&_renderBrowseMenu,
	);
}

sub _renderBrowseMenu {
	my $path = shift;
	my $userInterfaceIdiom = shift;
	my $results = shift;
	my $args = shift;
	my $searchTags = shift;

	my @empty = ();
	my $items = \@empty;
	my $i = $args->{'index'} || $args->{'params'}->{'_index'} || 0;
	my $playall = 0;
	my $playable = 0;
	foreach (@{$results->{'items'}}) {
		my $id = $_->{'id'};
		$id =~ s/:/=/g;
		$path =~ s/:/=/g;
		my $item = {
			'id' => $path."/".$id,
			'parent_id' => $path,
			'name' => $_->{'name'},
			'passthrough' => [ { searchTags => [@$searchTags, "path:" . $path."/".$id] } ],
		};
		if($_->{'leaf'}) {
			if($_->{'playable'}) {
				$playable = 1;
				$playall = 1;
				$item->{'type'} = "audio";
				$item->{'playall'} = 1;
				$item->{'play_index'} = $i++;
				my $playableElements = $_->{'item'}->{'playableElements'};
				if(scalar(@$playableElements)>0) {
					my $playableElement = shift @$playableElements;
					$item->{'audio_url'} = $playableElement->{'uri'};
					$item->{'favorites_url'} = $playableElement->{'uri'};
				}else {
					$item->{'audio_url'} = 'smd:object='.$id;
					$item->{'favorites_url'} = 'smd:object='.$id;
				}
			}else {
				$item->{'type'} = "text";
			}
		}else {
			$item->{'url'} = $modeMap{$args->{'params'}->{'mode'}};
			$item->{'playlist'} = $modeMap{$args->{'params'}->{'mode'}};
			if($_->{'playable'}) {
				$playable = 1;
				if($userInterfaceIdiom eq 'iPeng') {
					$item->{'type'} = "link";
				}else {
					$item->{'type'} = "playlist";
				}
				$item->{'favorites_url'} = 'smd:object='.$id;
			}else {
				$item->{'type'} = "link";
			}
		}
		push @$items,$item;
	}
	my $extra;
	
	my $params = _tagsToParams($searchTags);
	my %actions = (
		allAvailableActionsDefined => 1,
		commonVariables	=> ['path' => 'id','audio_url' => 'audio_url'],
		info => {
			command     => ['smditeminfo', 'items'],
		},
		items => {
			command     => [$modeBrowseCmd{$args->{'params'}->{'mode'}}, 'items'],
			fixedParams => {
				mode       => $args->{'params'}->{'mode'},
				%{&_tagsToParams($searchTags)},
			},
		},
	);
	if($playable) {
		$actions{'play'} = {
				command     => ['smdplaylistcontrol'],
				fixedParams => {cmd => 'load', %$params},
			};
		$actions{'add'} = {
				command     => ['smdplaylistcontrol'],
				fixedParams => {cmd => 'add', %$params},
			};
		$actions{'insert'} = {
				command     => ['smdplaylistcontrol'],
				fixedParams => {cmd => 'insert', %$params},
			};
		if($playall) {
			$actions{'playall'} = {
					command     => ['smdplaylistcontrol'],
					fixedParams => {cmd => 'load', %$params},
					variables => ['path' => 'parent_id', 'play_index' => 'play_index'],
				};
			$actions{'addall'} = {
					command     => ['smdplaylistcontrol'],
					fixedParams => {cmd => 'add', %$params},
					variables => ['path' => 'parent_id', 'play_index' => 'play_index'],
				};
		}else {
			$actions{'playall'} = $actions{'play'};
			$actions{'addall'} = $actions{'add'};
		}
	}

	return {items => $items, actions => \%actions, sorted => 1}, $extra;
}


1;
