#   Copyright 2010, Erland Isaksson (erland at isaksson.info)
#   All rights reserved.
#
#   Redistribution and use in source and binary forms, with or without
#   modification, are permitted provided that the following conditions are met:
#       * Redistributions of source code must retain the above copyright
#         notice, this list of conditions and the following disclaimer.
#       * Redistributions in binary form must reproduce the above copyright
#         notice, this list of conditions and the following disclaimer in the
#         documentation and/or other materials provided with the distribution.
#       * Neither the name of Logitech nor the
#         names of its contributors may be used to endorse or promote products
#         derived from this software without specific prior written permission.
#
#   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
#   ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
#   WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
#   DISCLAIMED. IN NO EVENT SHALL LOGITECH, INC BE LIABLE FOR ANY
#   DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
#   (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
#   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
#   ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
#   (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
#   SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

use strict;
use warnings;
                   
package Plugins::DuplicateDetector::Plugin;

use base qw(Slim::Plugin::Base);

use Slim::Utils::Prefs;

use Slim::Utils::Misc;
use Slim::Utils::OSDetect;
use Slim::Utils::Strings qw(string);
use File::Spec::Functions qw(:ALL);
use DBI qw(:sql_types);

use Plugins::DuplicateDetector::Settings;

use Data::Dumper;

my $prefs = preferences('plugin.duplicatedetector');
my $serverPrefs = preferences('server');
my $log = Slim::Utils::Log->addLogCategory({
	'category'     => 'plugin.duplicatedetector',
	'defaultLevel' => 'WARN',
	'description'  => 'PLUGIN_DUPLICATEDETECTOR',
});

my $PLUGINVERSION = undef;

my $driver;
my $totalTracks = 0;
my $currentTrack = 0;
my $currentTrackFile = "";
my $inProgress = 0;
my $tracks = undef;
my $noOfBytes = undef;

$prefs->migrate(1, sub {
        $prefs->set('noofbytes',      10000 );
        1;
});

sub getDisplayName()
{
	return string('PLUGIN_DUPLICATEDETECTOR'); 
}

sub initPlugin
{
	my $class = shift;
	$class->SUPER::initPlugin(@_);
	$PLUGINVERSION = Slim::Utils::PluginManager->dataForPlugin($class)->{'version'};
	Plugins::DuplicateDetector::Settings->new($class);
	${Slim::Music::Info::suffixes}{'binfile'} = 'binfile';
	${Slim::Music::Info::types}{'binfile'} = 'application/octet-stream';
	initDatabase();
	createIndex();
}

sub initDatabase {
	$driver = $serverPrefs->get('dbsource');
	$driver =~ s/dbi:(.*?):(.*)$/$1/;
    
	if(UNIVERSAL::can("Slim::Schema","sourceInformation")) {
		my ($source,$username,$password);
		($driver,$source,$username,$password) = Slim::Schema->sourceInformation;
	}

	$log->debug("Checking if customscan_track_attributes database table exists\n");
	my $dbh = Slim::Schema->storage->dbh();;
	my $st = $dbh->table_info();
	my $tblexists;
	while (my ( $qual, $owner, $table, $type ) = $st->fetchrow_array()) {
		if($table eq "duplicatedetector_tracks") {
			$tblexists=1;
		}
	}
	if($tblexists) {
		eval { $dbh->do("select audiosize from duplicatedetector_tracks limit 1;") };
		if ($@) {
			$dbh->do("DROP TABLE IF EXISTS duplicatedetector_tracks");
			$tblexists=undef;
		};
	}
	unless ($tblexists) {
		$log->warn("Duplicate Detector: Creating database tables\n");
		executeSQLFile("dbcreate.sql");
	}
}

sub dropIndex {
	my $dbh = Slim::Schema->storage->dbh();;
	eval { $dbh->do("alter table duplicatedetector_tracks drop index smdidIndex;") };
}

sub createIndex {
	my $dbh = Slim::Schema->storage->dbh();;
	if($driver eq 'mysql') {
		my $sth = $dbh->prepare("show index from duplicatedetector_tracks;");
		eval {
			$log->debug("Checking if indexes is needed for duplicatedetector_tracks");
			$sth->execute();
			my $keyname;
			$sth->bind_col( 3, \$keyname );
			my $foundSmdid = 0;
			while( $sth->fetch() ) {
				if($keyname eq "smdidIndex") {
					$foundSmdid = 1;
				}
			}
			if(!$foundSmdid) {
				$log->warn("No smdidIndex index found in duplicatedetector_tracks, creating index...");
				eval { $dbh->do("create index smdidIndex on duplicatedetector_tracks (smdid,audiosize);") };
				if ($@) {
					$log->warn("Couldn't add index: $@\n");
				}
			}
		};
		if( $@ ) {
		    $log->warn("Database error: $DBI::errstr");
		}
	}else {
		executeSQLFile("dbindex.sql");
	}
}

sub executeSQLFile {
	my $file  = shift;

	my $sqlFile;
	for my $plugindir (Slim::Utils::OSDetect::dirsFor('Plugins')) {
		opendir(DIR, catdir($plugindir,"DuplicateDetector")) || next;
		$sqlFile = catdir($plugindir,"DuplicateDetector", "SQL", $driver, $file);
		closedir(DIR);
	}

	$log->info("Executing SQL file $sqlFile\n");

	open(my $fh, $sqlFile) or do {
		$log->error("Couldn't open: $sqlFile : $!\n");
		return;
	};

	my $dbh = Slim::Schema->storage->dbh();;

	my $statement   = '';
	my $inStatement = 0;

	for my $line (<$fh>) {
		chomp $line;

		# skip and strip comments & empty lines
		$line =~ s/\s*--.*?$//o;
		$line =~ s/^\s*//o;

		next if $line =~ /^--/;
		next if $line =~ /^\s*$/;

		if ($line =~ /^\s*(?:CREATE|SET|INSERT|UPDATE|DELETE|DROP|SELECT|ALTER|DROP)\s+/oi) {
			$inStatement = 1;
		}

		if ($line =~ /;/ && $inStatement) {

			$statement .= $line;

			$log->debug("Executing SQL statement: [$statement]\n");

			eval { $dbh->do($statement) };

			if ($@) {
				$log->error("Couldn't execute SQL statement: [$statement] : [$@]\n");
			}

			$statement   = '';
			$inStatement = 0;
			next;
		}

		$statement .= $line if $inStatement;
	}

	commit($dbh);

	close $fh;
}

sub initScan {
	my $dbh = Slim::Schema->storage->dbh();
	my $sth = $dbh->prepare("DROP TABLE IF EXISTS duplicatedetector_tracks");
	$sth->execute();
	$sth->finish();
	commit($dbh);
	initDatabase();
	$log->info("Getting tracks...");
	$tracks = Slim::Schema->resultset('Track');
	$totalTracks = $tracks->count;
	$currentTrack = 0;
	$currentTrackFile = "";
	$noOfBytes = $prefs->get('noofbytes');
	$log->info("Got $totalTracks tracks");
	Slim::Utils::Scheduler::add_task(\&scanTrack);
	return 0;
}

sub scanTrack {
	my $track = undef;
	if(defined($tracks) && $inProgress) {
		$track = $tracks->next;
		$currentTrack++;
		# Skip non audio tracks and tracks with url longer than max number of characters
		while(defined($track) && !$track->audio) {
			$track = $tracks->next;
			$currentTrack++;
		}
	}
	if(defined($track)) {
		$currentTrackFile = Slim::Utils::Misc::pathFromFileURL($track->url);
		$log->debug("Scanning track $currentTrack: ".$currentTrackFile);

		my $data = undef;
		my ($start, $end) = Slim::Music::Info::isFragment($track->url);
		my $cuesheetAudioSize = undef;
		if (defined $start || defined $end) {
			my $md5size = $noOfBytes;
			if($md5size>$track->audio_size) {
				$md5size = $track->audio_size;
			}
			my $md5_offset = $track->audio_offset + ($track->audio_size / 2) - ($md5size/2);
			$log->debug("Detecting $currentTrackFile, using MD5 size=$md5size, offset=$md5_offset");
			$data = Audio::Scan->scan( $currentTrackFile , { md5_size => $md5size, md5_offset => $md5_offset });
			$cuesheetAudioSize = $track->audio_size;
		}else {
			$log->debug("Detecting $currentTrackFile, using MD5 size=$noOfBytes, offset=(default)");
			$data = Audio::Scan->scan( $currentTrackFile , { md5_size => $noOfBytes);
		}

		my $checksum = $data->{info}->{audio_md5};
		if( !$checksum) {
			$log->error("Unable to calculate checksum for: $currentTrackFile");
		}
		my $size = $cuesheetAudioSize;
		if(!$size) {
			$size = $data->{info}->{audio_size};
		}
		if(!$size) {
			$log->error("Unable to get audio size for: $currentTrackFile");
		}
		storeChecksum($track->url,$checksum,$size);
		return 1;

	}
	$tracks = undef;
	if($inProgress) {
		$log->info("Finished scanning");
	}else {
		$log->info("Aborted scanning");
	}
	createIndex();
	$inProgress = 0;
	return 0;
}

sub storeChecksum {
	my $url = shift;
	my $checksum = shift;
	my $size = shift;
	
	my $dbh = Slim::Schema->storage->dbh();

	my $sth = $dbh->prepare( "INSERT INTO duplicatedetector_tracks (url,smdid,audiosize) values (?,?,?)" );
	eval {
		$sth->bind_param(1, $url , SQL_VARCHAR);
		$sth->bind_param(2, $checksum , SQL_VARCHAR);
		$sth->bind_param(3, $size , SQL_INTEGER);
		$sth->execute();
		commit($dbh);
	};
}

sub webPages {

	my %pages = (
		"DuplicateDetector/index\.(?:htm|xml)"     => \&webIndex,
		"DuplicateDetector/checksumduplicates\.(?:binfile)"     => \&webChecksumDuplicates,
		"DuplicateDetector/incorrectduplicates\.(?:binfile)"     => \&webIncorrectDuplicates,
		"DuplicateDetector/duplicates\.(?:binfile)"     => \&webDuplicates,
	);

	for my $page (keys %pages) {
		Slim::Web::Pages->addPageFunction($page, $pages{$page});
	}
	Slim::Web::Pages->addPageLinks("plugins", { 'PLUGIN_DUPLICATEDETECTOR' => 'plugins/DuplicateDetector/index.html' });
}

sub webIndex {
	my ($client, $params) = @_;

	if($params->{'start'} && !$inProgress) {
		$inProgress = 1;
		Slim::Utils::Scheduler::add_task(\&initScan);
	}elsif($params->{'stop'}) {
		$inProgress = 0;
	}
	my $dbh = Slim::Schema->storage->dbh();
	my $sth = $dbh->prepare("SELECT ifnull(sum(cnt),0) from (SELECT count(*) as cnt FROM duplicatedetector_tracks GROUP BY smdid HAVING count(*)>1) duplicates");
	my $checksumduplicates = 0;
	$sth->execute();
	$sth->bind_col(1, \$checksumduplicates);
	$sth->fetch();
	$sth->finish();

	$sth = $dbh->prepare("SELECT ifnull(sum(cnt),0) from (SELECT count(*) as cnt FROM duplicatedetector_tracks GROUP BY smdid,audiosize HAVING count(*)>1) duplicates");
	my $duplicates = 0;
	$sth->execute();
	$sth->bind_col(1, \$duplicates);
	$sth->fetch();
	$sth->finish();

	if(defined($noOfBytes)) {
		$params->{'pluginDuplicateDetectorNoOfBytes'} = $noOfBytes;
	}

	$params->{'pluginDuplicateDetectorIncorrectDuplicates'} = $checksumduplicates-$duplicates;
	$params->{'pluginDuplicateDetectorChecksumDuplicates'} = $checksumduplicates;
	$params->{'pluginDuplicateDetectorDuplicates'} = $duplicates;
	$params->{'pluginDuplicateDetectorScanning'} = $inProgress;
	$params->{'pluginDuplicateDetectorCurrent'} = $currentTrack;
	$params->{'pluginDuplicateDetectorCurrentFile'} = $currentTrackFile;
	$params->{'pluginDuplicateDetectorTotal'} = $totalTracks;
	return Slim::Web::HTTP::filltemplatefile('plugins/DuplicateDetector/index.html', $params);
}

sub webIncorrectDuplicates {
	my ($client, $params, $prepareResponseForSending, $httpClient, $response) = @_;

	my $dbh = Slim::Schema->storage->dbh();
	my $sth = $dbh->prepare("SELECT url,smdid,audiosize from duplicatedetector_tracks ddt where smdid is null or audiosize is null or exists (SELECT * FROM duplicatedetector_tracks where ddt.smdid=smdid and ddt.audiosize!=audiosize) order by smdid,url");
	my $url;
	my $smdid;
	my $audiosize;
	$sth->execute();
	$sth->bind_col(1, \$url);
	$sth->bind_col(2, \$smdid);
	$sth->bind_col(3, \$audiosize);
	my $duplicateList = "";
	my $last = "";
	while($sth->fetch()) {
		if(!defined($smdid)) {
			$smdid="";
		}
		if($last ne $smdid) {
			$duplicateList.="\n";
		}
		$last = $smdid;
		$duplicateList .= ($smdid ne""?$smdid:"NOCHECKSUM").(defined($audiosize)?sprintf("-%08x",$audiosize):"-NOSIZE")." ".Slim::Utils::Misc::pathFromFileURL($url)."\n";
	}
	$sth->finish();
	$response->header("Content-Disposition","attachment; filename=\"incorrectduplicates.txt\"");
	return \$duplicateList;
}

sub webChecksumDuplicates {
	my ($client, $params, $prepareResponseForSending, $httpClient, $response) = @_;

	my $dbh = Slim::Schema->storage->dbh();
	my $sth = $dbh->prepare("SELECT url,smdid,audiosize from duplicatedetector_tracks ddt where smdid is null or audiosize is null or exists (SELECT * FROM duplicatedetector_tracks where ddt.smdid=smdid GROUP BY smdid HAVING count(*)>1) order by smdid,url");
	my $url;
	my $smdid;
	my $audiosize;
	$sth->execute();
	$sth->bind_col(1, \$url);
	$sth->bind_col(2, \$smdid);
	$sth->bind_col(3, \$audiosize);
	my $duplicateList = "";
	my $last = "";
	while($sth->fetch()) {
		if(!defined($smdid)) {
			$smdid="";
		}
		if($last ne $smdid) {
			$duplicateList.="\n";
		}
		$last = $smdid;
		$duplicateList .= ($smdid ne""?$smdid:"NOCHECKSUM").(defined($audiosize)?sprintf("-%08x",$audiosize):"-NOSIZE")." ".Slim::Utils::Misc::pathFromFileURL($url)."\n";
	}
	$sth->finish();
	$response->header("Content-Disposition","attachment; filename=\"checksumduplicates.txt\"");
	return \$duplicateList;
}

sub webDuplicates {
	my ($client, $params, $prepareResponseForSending, $httpClient, $response) = @_;

	my $dbh = Slim::Schema->storage->dbh();
	my $sth = $dbh->prepare("SELECT url,smdid,audiosize from duplicatedetector_tracks ddt where smdid is null or audiosize is null or exists (SELECT * FROM duplicatedetector_tracks where ddt.smdid=smdid and ddt.audiosize=audiosize GROUP BY smdid HAVING count(*)>1) order by smdid,audiosize,url");
	my $url;
	my $smdid;
	my $audiosize;
	$sth->execute();
	$sth->bind_col(1, \$url);
	$sth->bind_col(2, \$smdid);
	$sth->bind_col(3, \$audiosize);
	my $duplicateList = "";
	my $lastsmdid = "";
	my $lastaudiosize = 0;
	while($sth->fetch()) {
		if(!defined($audiosize)) {
			$audiosize=0;
		}
		if(!defined($smdid)) {
			$smdid="";
		}
		if($lastsmdid ne $smdid || $lastaudiosize != $audiosize) {
			$duplicateList.="\n";
		}
		$lastsmdid = $smdid;
		$lastaudiosize = $audiosize;
		$duplicateList .= ($smdid ne ""?$smdid:"NOCHECKSUM").($audiosize!=0?sprintf("-%08x",$audiosize):"-NOSIZE")." ".Slim::Utils::Misc::pathFromFileURL($url)."\n";
	}
	$sth->finish();
	$response->header("Content-Disposition","attachment; filename=\"duplicates.txt\"");
	return \$duplicateList;
}

sub commit {
	my $dbh = shift;
	if (!$dbh->{'AutoCommit'}) {
		$dbh->commit();
	}
}

*escape   = \&URI::Escape::uri_escape_utf8;

1;

__END__
