package Slim76Compat::Menu::BrowseLibraryLite;

# Emulate a minimal set of Slim::Menu::BrowseLibrary from 7.6 to allow xmlbrowser nodes
# to be added to MY_MUSIC on 7.5 using the same API as 7.6.  
# Each interface is implemented as a simple wrapper to the included xmlbrowser

use strict;
use warnings;

use Slim::Utils::Log;
use Slim::Utils::Prefs;

sub registerNodeFilter {}
sub deregisterNodeFilter {}
sub deregisterNode {}

sub registerNode {
	my ($class, $node) = @_;
	
	return unless $node->{'id'};
	
	if (!$node->{'id'} || ref $node->{'feed'} ne 'CODE') {
		logBacktrace('Invalid node specification');
		return 0;
	}

	$class->_registerWeb($node);
	$class->_registerMode($node);
	$class->_registerJive($node);
}

sub _registerWeb {
	my ($class, $node) = @_;
	
	my $title = $node->{'name'};
	my $url   = 'plugins/' . $node->{'id'} . '/index.html';
	
	Slim::Web::Pages->addPageLinks('browse', { $title => $url });
	Slim::Web::Pages->addPageLinks("icons", { $title => $node->{'icon'} });
	
	if ($node->{'condition'}) {
		Slim::Web::Pages->addPageCondition($title, $node->{'condition'});
	}

	require Slim76Compat::Web::XMLBrowser;

	Slim::Web::Pages->addPageFunction($url, sub {
		my $client = $_[0];
		
		Slim76Compat::Web::XMLBrowser->handleWebIndex( {
			client  => $client,
			feed    => $node->{'feed'},
			type    => $node->{'type'},
			title   => $title,
			timeout => 35,
			args    => \@_,
		} );
	} );
}

sub _registerMode {
	my ($class, $node) = @_;

	my $mode = $node->{'id'};
	my $name = $node->{'name'};

	require Slim76Compat::Buttons::XMLBrowser;

	# this may get called multiple times, but no consequences of doing this at present
	Slim76Compat::Buttons::XMLBrowser::init();

	Slim::Buttons::Common::addMode($mode, $name, sub {
		my ($client, $method) = @_;

		if ($method eq 'pop') {
			Slim::Buttons::Common::popMode($client);
			return;
		}

		my $title = (uc($name) eq $name) ? $client->string( $name ) : $name;
	
		my %params = (
			header   => $name,
			modeName => $name,
			url      => $node->{'feed'},
			type     => $node->{'type'},
			title    => $title,
			timeout  => 35,
		);

		Slim::Buttons::Common::pushMode($client, 'xmlbrowser76compat', \%params);

		$client->modeParam( handledTransition => 1 );
	});

	my %params = (
		'useMode'   => $mode,
		'header'    => $name,
		'condition' => $node->{'condition'},
	);
	
	Slim::Buttons::Home::addMenuOption($name, \%params);
	Slim::Buttons::Home::addSubMenu('BROWSE_MUSIC', $name, \%params);
}

sub _registerJive {
	my ($class, $node) = @_;

	require Slim76Compat::Control::XMLBrowser;

	my $cliQuery = sub {
		my $request = shift;
		Slim76Compat::Control::XMLBrowser::cliQuery( $node->{'id'}, $node->{'feed'}, $request );
	};
	
	Slim::Control::Request::addDispatch([ $node->{'id'}, 'items', '_index', '_quantity' ], [ 0, 1, 1, $cliQuery ]);
	Slim::Control::Request::addDispatch([ $node->{'id'}, 'playlist', '_method' ], [ 1, 1, 1, $cliQuery ]);

	my $menu = {
		text      => $node->{'name'},
		id        => $node->{'id'},
		node      => 'myMusic',
		weight    => $node->{'weight'},
		iconStyle => $node->{'iconStyle'},
		actions   => {
			go => {
				cmd    => [ $node->{'id'}, 'items'],
				params => {
					menu => 1
				}
			},
		},
	};

	Slim::Control::Jive::registerPluginMenu([ $menu ]);
}

1;
