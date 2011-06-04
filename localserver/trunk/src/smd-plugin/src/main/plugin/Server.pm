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
package Plugins::SocialMusicDiscovery::Server;

use strict;
use warnings;

use Tie::Cache::LRU;
use File::Spec::Functions;
use JSON::XS::VersionOneAndTwo;

use Slim::Utils::Log;
use Slim::Utils::Prefs;

my $log = logger('plugin.socialmusicdiscovery');
my $prefs  = preferences('plugin.socialmusicdiscovery');
my $sprefs = preferences('server');

my $server;

sub start {
	my ($class, $plugin) = @_;

	if ($prefs->get('hostname') ne 'localhost') {
		$log->info("not starting smd-server as hostname is not set to localhost");
		return;
	}

	my $smdServerPath = Plugins::SocialMusicDiscovery::Plugin->jars(qr/^smd-server/);

	if ($smdServerPath) {

		$log->debug("smd-server binary: $smdServerPath");

	} else {

		$log->error("can't find smd-server binary");
		return;
	}

	my ($driver, $source, $username, $password) = Slim::Schema->sourceInformation;

	my $cacheDir  = preferences('server')->get('cachedir');
	my $serverLog = catdir(Slim::Utils::OSDetect::dirsFor('log'), 'smd-server.log');
	my $smdPort   = $prefs->get('port');
	my $database;

	if ($driver ne 'mysql') {

		$log->info("Using default database for smd-server as server is using SQLite");
		$database = "database.directory=$cacheDir";

	} elsif ($source !~ /port=9092/) {

		$log->info("Using default database for smd-server because an external MySQL server is used and we can't ensure we have permission to create a database");
		$database = "database.directory=$cacheDir";

	} else {

        $log->info("Using the bundled MySQL database for smd-server, a separate smd schema is configured");
	    $database = "database=mysql-sbs";
	}

	my @opts = (
		"-Dorg.socialmusicdiscovery.server.$database",
		"-Dorg.socialmusicdiscovery.server.daemon=true",
		"-Dorg.socialmusicdiscovery.server.port=$smdPort",
		"-Dorg.socialmusicdiscovery.server.stdout=$serverLog",
		"-Dorg.socialmusicdiscovery.server.stderr=$serverLog",
	);

	if ($sprefs->get('authorize')) {
		push @opts, "-Dsqueezeboxserver.username=" . $sprefs->get('username');
		push @opts, "-Dsqueezeboxserver.passwordhash=" . $sprefs->get('password');
	}

	my $cmdString = join(' ', ("java", @opts, "-jar $smdServerPath"));

	$log->info("Starting smd-server");

	$log->debug("cmdline: $cmdString");

	$server = Proc::Background->new({'die_upon_destroy' => 1}, $cmdString);

	if (!$class->running) {
		$log->error("Unable to launch smd-server");
	}
}

sub stop {
	my $class = shift;

	if ($class->running) {
		$log->info("stopping smd-server");
		$server->die;
	}
}

sub running {
	return $server && $server->alive;
}

sub uriBase {
	my $hostname = $prefs->get('hostname');
	my $port     = $prefs->get('port');

	return "http://$hostname:$port";
}

# small cache for browsing to avoid repeat requests to smd-server
tie my %responseCache, 'Tie::Cache::LRU', 10;

sub get {
	my ($class, $path, $cb, $ecb, $params) = @_;

	if ($responseCache{$path}) {
		$log->info("using cached response for: $path");
		$cb->($responseCache{$path});
		return;
	}

	my $url = $class->uriBase . $path;
	$ecb ||= sub {};

	$log->info("fetching from: $url");

	my $timeout = Time::HiRes::time() + ($params->{'timeout'} || 35);
	my $try;

	$try = sub {
		
		Slim::Networking::SimpleAsyncHTTP->new(

			sub {
				my $json = eval { from_json($_[0]->content) };
				if ($@) {
					$log->warn("$@");
					$ecb->("$@");
				} else {
					$responseCache{$path} = $json;
					$cb->($json);
				}
			},

			sub {
				my $time = Time::HiRes::time();
				if ($time < $timeout) {
					$log->debug("retrying fetch of: $url");
					Slim::Utils::Timers::setTimer(undef, $time + 5, $try);
				} else {
					$log->warn("error fetching $url: " . $_[1]);
					$ecb->($_[1]);
				}
			},

			$params,

		)->get($url);
	};

	$try->();
}

1;
