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

use strict;
use warnings;
                   
package Plugins::SocialMusicDiscovery::Importer;

use Slim::Utils::Prefs;
use Slim::Utils::Misc;
use Slim::Utils::Log;
use Slim::Music::Import;
use LWP::UserAgent;
use HTTP::Request;

my $prefs = preferences('plugin.socialmusicdiscovery');
my $log = logger('plugin.socialmusicdiscovery');

# The number of seconds between status checks
my $STATUS_CHECK_INTERVAL = 2;

=head1 NAME

Plugins::SocialMusicDiscovery::Import::FullImport

=head1 DESCRIPTION

Import module which triggers a Squeezebox Server import into SMD and monitors the progress 
and reports it to the Squeezebox Server scanning progress interface.

=cut

=head2 init( )

Initialize the importer menu and configure it to run after Squeezebox Server scanning

=cut
sub init {
	Slim::Control::Request::subscribe(sub {
		if($prefs->get('autoimport')) {
			my $lastImport = $prefs->get('lastImportTime') || 0;
			if($lastImport eq Slim::Music::Import->lastScanTime) {
				$log->debug("Ignoring, no changes since last import");
				return;
			}
			Plugins::SocialMusicDiscovery::Importer::startImport();
		}
	},[['rescan'],['done']]);
}

=head2 startFullImport( )

Trigers an full import and monitors the complete import process in the background, this function is safe
to call also from within the man SBS process and is desiged so it won't cause audio disturbances 
if executed while music is playing.

=cut
sub startFullImport {
	startImport(1);
}

=head2 startImport( )

Trigers an import and monitors the complete import process in the background, this function is safe
to call also from within the man SBS process and is desiged so it won't cause audio disturbances 
if executed while music is playing.

=cut
sub startImport {
	my $dropPreviousContent = shift||0;

	if(Slim::Music::Import->stillScanning() eq 'PLUGIN_SOCIALMUSICDISCOVERY_SCAN_TYPE') {
		$log->warn("Abort currently running import before starting a new one");
		return;
	}

	# work round fact that when external scanning process completes the next call to isScanning resets scanning flag
	if (Slim::Music::Import->scanningProcess) {
		Slim::Music::Import->scanningProcess(undef);
	}

	Slim::Music::Import->setIsScanning('PLUGIN_SOCIALMUSICDISCOVERY_SCAN_TYPE');

	my $hostname = $prefs->get('hostname');
	my $port = $prefs->get('port');

	my $http = Slim::Networking::SimpleAsyncHTTP->new(\&_startImportReply, \&_smdServerError, {
		'progresses' => {},
		'items' => {},
	});
	my $parameters = "{}";
	if($dropPreviousContent) {
		$parameters="{squeezeboxserver.deletePrevious:true}";
	}
	$log->debug("Starting SMD import: http://$hostname:$port/mediaimportmodules/squeezeboxserver with: $parameters");
	$http->post("http://$hostname:$port/mediaimportmodules/squeezeboxserver",'Content-Type' => 'application/json', $parameters);
}


sub _startImportReply {
	my $http = shift;
	my $content = $http->content();

	my $jsonResult = JSON::XS::decode_json($content);

	# Startup monitoring if import was successfully started	
	if($jsonResult->{'success'}) {
		_checkStatus(undef, 
			$http->params()->{'progresses'},
			$http->params()->{'items'});
	}else {
		$log->warn("Import into SMD failed: Failed to start import");
		_endMonitoring();
	}
}

sub _checkStatus {
	my $client = shift; # Just here to make Slim::Utils::Timers happy
	my $progresses = shift; # Hash with Slim::Utils::Progress objects per import phase
	my $items = shift; # Hash with total number of items per import phase

	my $hostname = $prefs->get('hostname');
	my $port = $prefs->get('port');

	# If not aborted
	if(Slim::Music::Import->stillScanning eq 'PLUGIN_SOCIALMUSICDISCOVERY_SCAN_TYPE') {
		my $http = Slim::Networking::SimpleAsyncHTTP->new(\&_checkStatusReply, \&_smdServerError, {
			'progresses' => $progresses,
			'items' => $items,
		});
		$log->debug("Check status of import: http://$hostname:$port/mediaimportmodules/squeezeboxserver");
		$http->get("http://$hostname:$port/mediaimportmodules/squeezeboxserver");
	}else {
		my $http = Slim::Networking::SimpleAsyncHTTP->new(\&_abortReply, \&_smdServerError, {
			'progresses' => $progresses,
			'items' => $items,
		});
		$log->info("Aborting scanning...");
		$http->_createHTTPRequest( DELETE => "http://$hostname:$port/mediaimportmodules/squeezeboxserver");
	}

	return 0;
}

sub _endMonitoring {
	# Make sure timers have been removed and scanner status reset
	Slim::Utils::Timers::killTimers(0, \&_checkStatus);
	Slim::Music::Import->setIsScanning(0);
}

sub _checkStatusReply {
	my $http = shift;
	my $content = $http->content();

	my $jsonResult = JSON::XS::decode_json($content);

	my $progresses = $http->params()->{'progresses'};
	my $items = $http->params()->{'items'};
	if(!_handleStatus($progresses,$items,$jsonResult)) {
		# Setup a timer which will trigger a new status check a bit later
		Slim::Utils::Timers::setTimer(0, Time::HiRes::time() + $STATUS_CHECK_INTERVAL, \&_checkStatus, 
			$http->params()->{'progresses'},
			$http->params()->{'items'});
	}else {
		# Import finished or failed, lets end the monitoring
		_endMonitoring();
		$log->info("Finished importing into SMD");
	}
}

sub _abortReply {
	my $http = shift;

	$log->warn("Aborted scanning before it was completed");
	_endMonitoring();
	$log->info("Finished importing into SMD");
}

sub _smdServerError {
	my $http = shift;

	# Let's end the monitoring if an error occurs
	_endMonitoring();

	$log->warn("Import into SMD failed: Unable to contact SMD server");
}

sub _handleStatus {
	# Hash with Slim::Utils::Progress entries of current states in each phase in the import process
	my $progresses = shift; 

	# Hash with total items in each phase of current import process
	my $items = shift;

	# JSON status reply from SMD server 
	my $status = shift; 

	my $currentPhase = $status->{'currentPhaseNo'};
	my $totalPhase = $status->{'totalPhaseNo'};

	$log->debug("Processing phase $currentPhase/$totalPhase and item ".$status->{'currentNumber'}." of ".$status->{'totalNumber'});

	# If this is the first status in this phase
	if(!defined($progresses->{$currentPhase})) {

		# Mark any previous states as final		
		for(my $i=1;$i<$currentPhase;$i++) {
			if(defined($progresses->{$i})) {
				$progresses->{$i}->final();
				delete $progresses->{$i};
			}
		}

		# If import isn't finished or failed
		if($status->{'status'} ne 'Failed' && $status->{'status'} ne 'FinishedOk') {

			# Setup a new Slim::Utils::Progress entry for this phase
			$items->{$currentPhase} = $status->{'totalNumber'};
			$progresses->{$currentPhase} = Slim::Utils::Progress->new({
				'type' => 'importer',
				'name' => 'PLUGIN_SOCIALMUSICDISCOVERY_PHASE_'.$currentPhase.'_'.$totalPhase,
				'total' => $items->{$currentPhase},
				'bar'	=> 1,
			});
			$progresses->{$currentPhase}->update(undef,$status->{'currentNumber'});

			# Import not finished yet
			return 0;
		}else {
			# Abort monitoring and log the failure to the log
			if($status->{'status'} eq 'Failed') {
				$log->warn("Import into SMD failed: Failure during import process");
			}else {
				$prefs->set('lastImportTime',Slim::Music::Import->lastScanTime);
			}
			# Import aborted
			return 1;
		}

	# If import is still active (not finished or failed)
	}elsif($status->{'status'} ne 'Failed' && $status->{'status'} ne 'FinishedOk') {

		# Make sure we update total number if it has changed since previous check
		if($items->{$currentPhase} ne $status->{'totalNumber'}) {
			$items->{$currentPhase} = $status->{'totalNumber'};
			$progresses->{$currentPhase}->total($items->{$currentPhase});
		}
		# Update progress bar
		$progresses->{$currentPhase}->update(undef,$status->{'currentNumber'});

		# Import not finished yet
		return 0;

	# Else if import is failed or finished
	}else {

		# Mark current progress indicator as finished
		$progresses->{$currentPhase}->final();

		# Log failure to the log if the import was failed
		if($status->{'status'} eq 'Failed') {
			$log->warn("Import into SMD failed: Failure during import process");
		}else {
			$prefs->set('lastImportTime',Slim::Music::Import->lastScanTime);
		}
		# Import finished or failed
		return 1;
	}					
}


1;

__END__
