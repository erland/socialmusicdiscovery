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
use Scalar::Util qw(blessed);

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
		if (UNIVERSAL::can("Slim::Music::Import","hasAborted")) {
			require Slim::Music::Import;
			if(Slim::Music::Import::hasAborted()) {
				$log->info("Scanning or import aborted");
				return;
			}
		}
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

	# work round fact that when external scanning process completes on 7.6 the next call to isScanning resets scanning flag
	if (Slim::Music::Import->scanningProcess) {
		Slim::Music::Import->scanningProcess(undef);
	}

	Slim::Music::Import->setIsScanning('PLUGIN_SOCIALMUSICDISCOVERY_SCAN_TYPE');

	my $parameters = $dropPreviousContent ? "{squeezeboxserver.deletePrevious:true}" : "{}";

	$log->debug("Starting SMD import: /mediaimportmodules/squeezeboxserver with: $parameters");

	Plugins::SocialMusicDiscovery::Server->post(
		"/mediaimportmodules/squeezeboxserver",
		sub { _startImportReply($_[0], { progresses => {}, items => {} }) }, 
		\&_smdServerError,
		{ timeout => 35 },
		'Content-Type', 'application/json',
		$parameters
	);
}

sub _startImportReply {
	my $jsonResult = shift;
	my $state = shift;

	# Startup monitoring if import was successfully started	
	if($jsonResult->{'success'}) {
		_checkStatus(undef, $state);
	}else {
		$log->warn("Import into SMD failed: Failed to start import");
		_endMonitoring();
	}
}

sub _checkStatus {
	my $obj = shift;
	my $state = shift;

	# If not aborted
	if(_stillScanning() eq 'PLUGIN_SOCIALMUSICDISCOVERY_SCAN_TYPE') {
		$log->debug("checking import status");
		Plugins::SocialMusicDiscovery::Server->get(
			"/mediaimportmodules/squeezeboxserver",
			sub { _checkStatusReply($_[0], $state) },
			\&_smdServerError,
			{ timeout => 35, nocache => 1 },
		);
	}else {
		$log->info("Aborting scanning...");
		Plugins::SocialMusicDiscovery::Server->request(
			'DELETE',
			"/mediaimportmodules/squeezeboxserver",
			\&_abortReply,
			\&_smdServerError,
			{ timeout => 35 },
		);
	}

	return 0;
}

sub _stillScanning {
	# We can't use Slim::Music::Import->stillScanning to check this
	# because it will set to false in 7.5 as soon as scanner process dies
	my $scanRS   = Slim::Schema->single('MetaInformation', { 'name' => 'isScanning' });
	my $scanning = blessed($scanRS) ? $scanRS->value : 0;
	return $scanning;
}

sub _endMonitoring {
	# Make sure timers have been removed and scanner status reset
	Slim::Utils::Timers::killTimers(undef, \&_checkStatus);
	Slim::Music::Import->setIsScanning(0);
}

sub _checkStatusReply {
	my $jsonResult = shift;
	my $state = shift;

	if(!_handleStatus($jsonResult, $state)) {
		# Setup a timer which will trigger a new status check a bit later
		Slim::Utils::Timers::setTimer(undef, Time::HiRes::time() + $STATUS_CHECK_INTERVAL, \&_checkStatus, $state);
	}else {
		# Import finished or failed, lets end the monitoring
		_endMonitoring();
		$log->info("Finished importing into SMD");
	}
}

sub _abortReply {
	$log->warn("Aborted scanning before it was completed");
	_endMonitoring();
	$log->info("Finished importing into SMD");
}

sub _smdServerError {
	# Let's end the monitoring if an error occurs
	_endMonitoring();

	$log->warn("Import into SMD failed: Unable to contact SMD server");
}

sub _handleStatus {
	# JSON status reply from SMD server 
	my $status = shift; 

	my $state = shift;

	# Hash with Slim::Utils::Progress entries of current states in each phase in the import process
	my $progresses = $state->{'progresses'}; 

	# Hash with total items in each phase of current import process
	my $items = $state->{'items'};

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
