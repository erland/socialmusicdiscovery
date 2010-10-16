#   Copyright 2010, Social Music Discovery project
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
#   DISCLAIMED. IN NO EVENT SHALL LOGITECH, INC BE LIABLE FOR ANY
#   DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
#   (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
#   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
#   ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
#   (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
#   SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

use strict;
use warnings;
                   
package Plugins::SocialMusicDiscovery::Scanner;

use base qw(Slim::Plugin::Base);

use Slim::Utils::Prefs;

use Slim::Utils::Misc;
use Slim::Utils::OSDetect;
use Slim::Utils::Strings qw(string);
use File::Spec::Functions qw(:ALL);
use DBI qw(:sql_types);

use Data::Dumper;

my $prefs = preferences('plugin.socialmusicdiscovery');
my $serverPrefs = preferences('server');
my $log = Slim::Utils::Log->addLogCategory({
	'category'     => 'plugin.socialmusicdiscovery',
	'defaultLevel' => 'WARN',
	'description'  => 'PLUGIN_SOCIALMUSICDISCOVERY',
});

my $NO_OF_BYTES_IN_SMDID = 10000;

my $tracks;
my $inProgress;

my $currentlyScannedTrackNo;
my $currentlyScannedTrackFile;
my $totalNumberOfTracks;

=head1 NAME

Plugins::SocialMusicDiscovery::Scanner

=head1 DESCRIPTION

Will scan tags in music files available in Squeezebox Server and provide 
them to external sources through a JSON interface. It supports two operation modes, 
it can either scan tags on demand when they are requested through the JSON command or
it can manually pre-scan tags in the tracks and read it from a database table later when
requested through JSON.

To use it from an external application, you can post the JSON command to:
http://localhost:9000/jsonrpc.js

The command that read and scan the 20 first tracks looks like this:
  {"id":1,"method":"slim.request","params":[ "-", ["socialmusicdiscovery", "tracks","offset:0","size:20" ]]}

The command that read the 20 first pre-scanned tracks looks like this:
  {"id":1,"method":"slim.request","params":[ "-", ["socialmusicdiscovery", "scannedtracks","offset:0","size:20" ]]}

The "offset" and "size" parameters are optional but it's recommended to use them. Their purpose is to 
make it possible to get the tags in chunks.

  First requesting:
    offset:0, size:20 (to get the first 20 tracks)

  And then request:
    offset:20, size:20 (to get the next 20 tracks)

The JSON result reported back have the structure:
  {
  	"count":"45",
  	"offset":"20",
  	"size":"2",
  	"tracks":[{
  			"url":"file:///mnt/flacmusic/Absolute%20Love%202%20-%20Disc%201/01%20I%27LL%20BE%20MISSING%20YOU.flac",
  			"smdID":"849e907c5ed1d1035f3bcaeb2a60eac9-00001302-0215aa57",
			"file":"/mnt/flacmusic/Absolute Love 2 - Disc 1/01 I'LL BE MISSING YOU.flac"
  			"tags":[{
  					"name":"ALBUM",
  					"value":"Absolute Love 2"
  				},{
  					"name":"ARTIST",
  					"value":"Puff Daddy & Faith Evans"
  				},{
  					"name":"SBSALBUMID",
  					"value":"1"
  				}
  			]
  		},{
  			"url":""url":"file:///mnt/flacmusic/Ace%20Of%20Base%20-%20Flowers/01%20Life%20Is%20A%20Flower.flac",
  			"smdID":"681a0f25a2a31776be927c08f91d2131-00001257-01bfe2eb",
			"file":"/mnt/flacmusic/Ace Of Base - Flowers/01 Life Is A Flower.flac"
  			"tags":[{
  					"name":"ALBUM",
  					"value":"Flowers"
  				},{
  					"name":"ARTIST",
  					"value":"Ace of Base",
  					"sortValue":"Ace of Base"
  				},{
  					"name":"SBSALBUMID",
  					"value":"2"
  				}
  			]
  		}
  	]
  }

=cut

# Mapping between sort tags and their corresponding real tag, note that
# the real tag is the named which has been mapped through the MAPPED_TAGS mapping
my $SORT_TAGS = {
	'ALBUMSORT' => 'ALBUM',
	'ARTISTSORT' => 'ARTIST',
	'COMPOSERSORT' => 'COMPOSER',
	'CONDUCTORSORT' => 'CONDUCTOR',
	'BANDSORT' => 'BAND',
	'ALBUMARTISTSORT' => 'ALBUMARTIST',
	'TRACKARTISTSORT' => 'TRACKARTIST',
	'PERFORMERSORT' => 'PERFORMER',
	'TITLESORT' => 'TITLE',
	'GENRESORT' => 'GENRE',
	'MOODSORT' => 'MOOD',
	'STYLESORT' => 'STYLE',
	'WORKSORT' => 'WORK',
	'PARTSORT' => 'PART',
	'MOVEMENTSORT' => 'MOVEMENT',
        'TSOA' => 'ALBUM',
        'YTSA' => 'ALBUM',
        'TSOP' => 'ARTIST',
        'YTSP' => 'ARTIST',      # non-standard iTunes tag
        'TSOT' => 'TITLE',
        'TSOT' => 'TITLE',
        'YTST' => 'TITLE',       # non-standard iTunes tag
        'TST ' => 'TITLE',     # broken iTunes tag
        'TSO2' => 'ALBUMARTIST',
        'YTS2' => 'ALBUMARTIST', # non-standard iTunes tag
        'TSOC' => 'COMPOSER',
        'YTSC' => 'COMPOSER',    # non-standard iTunes tag
	'MUSICBRAINZ_ALBUMARTISTSORTNAME' => 'ALBUMARTIST',
        'XSOP' => 'ARTISTSORT',
};

# List of tags that should be scanned and which name they should be
# represented with. The tags in this list is scanned plus the sort related tags 
# in the SORT_TAGS map
my $MAPPED_TAGS = {
	'ALBUM' => 'ALBUM',
	'DISC'	=> 'DISC',
	'DISCNUMBER' => 'DISC',

	'ARTIST' => 'ARTIST',
	'COMPOSER' => 'COMPOSER',
	'CONDUCTOR' => 'CONDUCTOR',
	'BAND' => 'BAND',
	'ALBUMARTIST' => 'ALBUMARTIST',
	'ALBUM ARTIST' => 'ALBUMARTIST',
	'TRACKARTIST' => 'TRACKARTIST',
	'PERFORMER' => 'PERFORMER',

	'TITLE' => 'TITLE',
	'TRACKNUM' => 'TRACKNUM',

	'GENRE' => 'GENRE',

	'DATE'	=> 'YEAR',
	'YEAR'	=> 'YEAR',

	'MOOD' => 'MOOD',
	'STYLE' => 'STYLE',

	'WORK' => 'WORK',
	'PART' => 'PART',
	'MOVEMENT' => 'PART',

	'MUSICBRAINZ_TRM_ID' => 'MUSICBRAINZ_TRM_ID',
	'MUSICBRAINZ_ID' => 'MUSICBRAINZ_ID',
	'MUSICBRAINZ_ALBUM_ID' => 'MUSICBRAINZ_ALBUM_ID',
	'MUSICBRAINZ_ARTIST_ID' => 'MUSICBRAINZ_ARTIST_ID',
	'MUSICBRAINZ_ALBUMARTIST_ID' => 'MUSICBRAINZ_ALBUMARTIST_ID',

	'MUSICBRAINZ ALBUM ARTIST' => 'ALBUMARTIST',
	'MUSICBRAINZ_ALBUM_TYPE' => 'ALBUMTYPE',
	
        'COMM' => 'COMMENT',
        'TALB' => 'ALBUM',
        'TBPM' => 'BPM',
        'TCOM' => 'COMPOSER',
        'TCMP' => 'COMPILATION',
        'YTCP' => 'COMPILATION', # non-standard v2.3 frame
        'TCON' => 'GENRE',
        'TYER' => 'YEAR',
        'TDRC' => 'YEAR',
        'TDOR' => 'YEAR',
        'XDOR' => 'YEAR',
        'TIT2' => 'TITLE',
        'TPE1' => 'ARTIST',
        'TPE2' => 'BAND',
        'TPE3' => 'CONDUCTOR',
        'TPOS' => 'PART',
        'TRCK' => 'TRACKNUM',
        'YRVA' => 'RVAD',
        'UFID' => 'MUSICBRAINZ_ID',
        'USLT' => 'LYRICS',
};

=head1 PUBLIC FUNCTIONS

=head2 init ()

Initialize the scanner module, this will register the JSON handlers in Squeezebox Server to make
them avaialble for any JSON client

=cut
sub init {
	Slim::Control::Request::addDispatch(['socialmusicdiscovery','scannedtracks'], [0, 1, 1, \&getDatabaseTagsAsJSON]);
	Slim::Control::Request::addDispatch(['socialmusicdiscovery','tracks'], [0, 1, 1, \&getDirectTagsAsJSON]);
}

=head2 initDatabase ()

Initialize the database by creating or upgrading necessary tables and index, this method can
be called externally if you like to do this initialization in advance but else it will be performed
on request when needed.

=cut
sub initDatabase {
	$log->debug("Checking if socialmusicdiscovery_tracks database table exists\n");
	my $dbh = Slim::Schema->storage->dbh();;
	my $st = $dbh->table_info();
	my $tblexists;
	while (my ( $qual, $owner, $table, $type ) = $st->fetchrow_array()) {
		if($table eq "socialmusicdiscovery_tracks") {
			$tblexists=1;
		}
	}
	unless ($tblexists) {
		$log->info("Social Music Discovery: Creating database table...");
		_executeSQLFile("dbcreate.sql");
		_executeSQLFile("dbindex.sql");
	}
}

=head2 isScanningInProgress ()

Checks if a background scanning is currently in progress

=cut
sub isScanningInProgress {
	return $inProgress;
}

=head2 abortScan ()

Abort an existing backgroiund scanning process

=cut
sub abortScan {
	$inProgress = 0;
}

=head2 getScanInformation ()

Get information about currently scanned track and totally number of tracks that is about to be scanned.

Should be called as:
  my ($total, $currentNo, $currentFile, $inProgress) = getScanInformation();

=cut
sub getScanInformation {
	my $count = $totalNumberOfTracks;
	if(!isScanningInProgress()) {
		initDatabase();
		my $dbh = Slim::Schema->storage->dbh();
		my $sthCount = $dbh->prepare( "SELECT count(*) FROM socialmusicdiscovery_tracks" );

		$sthCount->execute() or do {
			$log->error("Unable to get number of scanned tracks");
			return;
		};
		$sthCount->bind_col(1,\$count);
		$sthCount->fetch();
	}
	return ($count, $currentlyScannedTrackNo, $currentlyScannedTrackFile, isScanningInProgress());
}

=head2 initScan () 

This method initiate a backround scanning of all tracks in the database, it will automatically
create the necessary database tables if they don't already exist.

=cut
sub initScan {
	if($inProgress) {
		return 0;
	}
	$inProgress = 1;
	# Make sure database tables are available
	initDatabase();
	# Remove previously scanned tracks
	# TODO: Add support for incremental scanning
	_executeSQLFile("dbclear.sql");

	$log->info("Start scanning...");
	$tracks = Slim::Schema->resultset('Track');
	$totalNumberOfTracks = $tracks->count;
	$currentlyScannedTrackNo = 0;
	$currentlyScannedTrackFile = "";
	$log->info("Got $totalNumberOfTracks tracks to scan");
	Slim::Utils::Scheduler::add_task(\&_scanTrack);
	return 1;
}

=head2 readTags ( $track )

Scans a track and returns the smdID plus an array of the scanned tags.

Should be used as follows:
  my ($smdID, $tags) = readTags($track);

=cut
sub readTags {
	my $track = shift;

	my $file = Slim::Utils::Misc::pathFromFileURL($track->url);

	my $data = undef;
	my @tags = ();
	my $SMDIDsize = $NO_OF_BYTES_IN_SMDID;
	my $SMDIDoffset = undef;

	my ($start, $end) = Slim::Music::Info::isFragment($track->url);
	my $cuesheetAudioSize = undef;

	my $size = undef;
	my $offset = undef;
	# If cuesheet
	if (defined $start || defined $end) {
		# Make sure we don't calculate an audio outside the specific file segement defined by cuesheet
		if($SMDIDsize>$track->audio_size) {
 			$SMDIDsize = $track->audio_size;
		}
		# Make sure we scann a section in the middle of the file segment
		$SMDIDoffset = $track->audio_offset + ($track->audio_size / 2) - ($SMDIDsize/2);
		$log->debug("Scanning $file represented by cuesheet, using SMDID size=$SMDIDsize, offset=$SMDIDoffset");
		$data = Audio::Scan->scan( $file , { md5_size => $SMDIDsize, md5_offset => $SMDIDoffset });
		$size = $track->audio_size;
		$offset = $track->audio_offset;
	}else {
		$log->debug("Scanning $file, using SMDID size=$SMDIDsize, offset=(default)");
		$data = Audio::Scan->scan( $file , { md5_size => $SMDIDsize});
		$size = $data->{info}->{audio_size};
		$offset = $data->{info}->{audio_offset};
	}

	# Calculate SMDID
	my $smdID = $data->{info}->{audio_md5};
        if( !defined($smdID)) {
		$log->error("Unable to calculate checksum for: $file, using path as fallback");
		$smdID = Digest::MD5::md5_hex($track->url);
        }
        if(!$size) {
		$log->error("Unable to get audio size for: $file, using 0 as fallback");
		$size = 0;
	}
        if(!defined($offset)) {
		$log->error("Unable to get audio offset for: $file, using 0 as fallback");
		$offset = 0;
	}
	$smdID .= "-".sprintf("%08x-%08x", $offset,$size);

	my $SBStags = Slim::Formats->readTags($track->url);
	

	my $sortTags = {};
	my $normalTags = {};
	for my $tag (keys %$SBStags) {
		if($SORT_TAGS->{uc($tag)}) {
			my @arrayValues = _splitTag($SBStags->{$tag});
			$sortTags->{$SORT_TAGS->{uc($tag)}} = \@arrayValues;
		}elsif($MAPPED_TAGS->{uc($tag)}){
			my @arrayValue = _splitTag($SBStags->{$tag});
			$normalTags->{$MAPPED_TAGS->{uc($tag)}} = \@arrayValue;
		}
	}
	for my $tag (keys %$normalTags) {
		my $values = $normalTags->{$tag};
		my $sortValues = undef;
		if(defined($sortTags->{$tag})) {
			$sortValues = $sortTags->{$tag};
		}
		my $i = 0;
		for my $value (@$values) {
			#strip spaces in beginning/end
			$value =~ s/^\s*//;
			$value =~ s/\s*$//;

			if($value ne '') {
				my $sortValue = undef;
				if(defined($sortValues) && scalar(@$sortValues)>$i) {
					$sortValue = $sortValues->[$i];
					 #strip spaces in beginning/end
					$sortValue =~ s/^\s*//;
					$sortValue =~ s/\s*$//;
					if($sortValue eq '') {
						$sortValue = undef;
					}
				}

				my $item = {
					'name' => $MAPPED_TAGS->{$tag},
					'value' => $value,
					'sortvalue' => $sortValue,
				};
				push @tags, $item;
			}
			$i++;
		}
	}
	if(defined($track->albumid)) {
		my $item = {
			'name' => 'SBSALBUMID',
			'value' => $track->albumid,
			'sortvalue' => undef,
		};
		push @tags, $item;
	}
	return ($smdID,\@tags);
}

=head2 storeTags ($track,$smdID,\@tags)

Store the provided tags for a track together with its smdID in the database. The necessary database
tables will be created unless they already exists.

=cut
sub storeTags {
	my $url = shift;
	my $smdID = shift;
	my $tags = shift;
	
	initDatabase();

	my $dbh = Slim::Schema->storage->dbh();

	my $sthTrack = $dbh->prepare( "INSERT INTO socialmusicdiscovery_tracks (smdid,url) values (?,?)" );
	my $sthTags = $dbh->prepare( "INSERT INTO socialmusicdiscovery_tags (smdid,name,value,sortvalue) values (?, ?, ?, ?)" );
	eval {
		$sthTrack->bind_param(1, $smdID, SQL_VARCHAR);
		$sthTrack->bind_param(2, $url, SQL_VARCHAR);
		$sthTrack->execute();
	};
	for my $tagItem (@$tags) {
		eval {
			$sthTags->bind_param(1, $smdID , SQL_VARCHAR);
			$sthTags->bind_param(2, $tagItem->{'name'} , SQL_VARCHAR);
			$sthTags->bind_param(3, $tagItem->{'value'} , SQL_VARCHAR);
			$sthTags->bind_param(4, $tagItem->{'sortvalue'} , SQL_VARCHAR);
			$sthTags->execute();
		};
	}
	$sthTrack->finish();
	$sthTags->finish();
	_commit($dbh);
}

=head2 getDatabaseTagsAsJSON ($request)

Command handler that returns the requested tags stored in the database. This method will return empty
result unless a scanning with initScan method has been performed previously.

The request can handle the followinng parameters:
  - offset: The offset in the total track list to return as the first item
  - size: The number of items to return

You will know that you have got all tracks if the "count" in the result is equal or less than offset+size.
If the request doesn't contain both "offset" and "size" parameter the complete list of tracks will be returned.

=cut
sub getDatabaseTagsAsJSON {
	$log->debug("Entering jsonHandlerTable");
	my $request = shift;

	initDatabase();

  	my $offset = $request->getParam('offset');
  	my $size = $request->getParam('size');

	my $dbh = Slim::Schema->storage->dbh();
	my $sth;
	if(defined($size) && defined($offset)) {
		$sth = $dbh->prepare( "SELECT url,smdid FROM socialmusicdiscovery_tracks ORDER BY url LIMIT ?,?" );
		$sth->bind_param(1, $offset,SQL_INTEGER);
		$sth->bind_param(2, $size,SQL_INTEGER);
	}else {
		$sth = $dbh->prepare( "SELECT url,smdid FROM socialmusicdiscovery_tracks ORDER BY url" );
	}
	my $sthCount = $dbh->prepare( "SELECT count(*) FROM socialmusicdiscovery_tracks" );
	my $sthTags = $dbh->prepare( "SELECT name,value,sortvalue FROM socialmusicdiscovery_tags WHERE smdid=? ORDER BY name" );

	my $count;
	$sthCount->execute() or do {
		$log->error("Error retrieving tracks");
		$request->setBadDispatch();
		return;
	};
	$sthCount->bind_col(1,\$count);
	$sthCount->fetch();
	$sthCount->finish();

	my $url;
	my $smdID;
	
	$sth->execute() or do {
		$log->error("Error retrieving tracks");
		$request->setBadDispatch();
		return;
	};
	$sth->bind_col(1,\$url);
	$sth->bind_col(2,\$smdID);

	my $result = {
		'count' => $count,
	};
	if(defined($offset)) {
		$result->{'offset'} = $offset;
	}
	if(defined($size)) {
		$result->{'size'} = $size;
	}

	my @tracks = ();
	while ($sth->fetch()) {
		my $file = Slim::Utils::Misc::pathFromFileURL($url);
		my $item = {
			'smdID' => $smdID,
			'url' => $url,
			'file' => $file,
		};

		my $tagName;
		my $tagValue;
		my $tagSortValue;
		$sthTags->bind_param(1, $smdID,SQL_VARCHAR);
		$sthTags->execute() or do {
			$log->error("Error retrieving tags");
			$request->setBadDispatch();
			return;
		};
		$sthTags->bind_col(1,\$tagName);
		$sthTags->bind_col(2,\$tagValue);
		$sthTags->bind_col(3,\$tagSortValue);

		my @tags = ();
		while($sthTags->fetch()) {
			my $tag = {
				'name' => $tagName,
				'value' => $tagValue,
			};
			if(defined($tagSortValue)) {
				$tag->{'sortValue'} = $tagSortValue;
			}
			push @tags,$tag;
		}
		$item->{'tags'} = \@tags;
		push @tracks,$item;
	}
	$sthTags->finish();
	$sth->finish();
	$result->{'tracks'} = \@tracks;
	$request->setRawResults($result);

	$log->debug("Exit jsonHandlerTable");
	$request->setStatusDone();
}

=head2 getDirectTagsAsJSON ($request)

Command handler that returns the requested tags by reading the database directly. This method will return empty
result unless a scanning with initScan method has been performed previously.

The request can handle the followinng parameters:
  - offset: The offset in the total track list to return as the first item
  - size: The number of items to return

You will know that you have got all tracks if the "count" in the result is equal or less than offset+size.
If the request doesn't contain both "offset" and "size" parameter the complete list of tracks will be returned.

=cut
sub getDirectTagsAsJSON {
	$log->debug("Entering jsonHandlerDirect");
	my $request = shift;

  	my $offset = $request->getParam('offset');
  	my $size = $request->getParam('size');

	my $dbh = Slim::Schema->storage->dbh();

	my $iterator;
	$iterator = Slim::Schema->rs('Track')->search({ 'audio' => 1, 'remote' => 0 },{'order_by' => 'url'});
	my $count = $iterator->count;
	if(defined($size) && defined($offset)) {
		$iterator = Slim::Schema->rs('Track')->search({ 'audio' => 1, 'remote' => 0 },{'order_by' => 'url'})->slice($offset,$offset+$size);
	}

	my $url;

	my $result = {
		'count' => $count,
	};
	if(defined($offset)) {
		$result->{'offset'} = $offset;
	}

	my @tracks = ();
	my $track = $iterator->next;
	while ($track) {
		my $file = Slim::Utils::Misc::pathFromFileURL($track->url);
		my ($smdID,$fileTags) = readTags($track);
		my $item = {
			'smdID' => $smdID,
			'url' => $track->url,
			'file' => $file,
		};

		my @tags = ();
		for my $tagItem (@$fileTags) {
			my $tag = {
				'name' => $tagItem->{'name'},
				'value' => $tagItem->{'value'},
			};
			if(defined($tagItem->{'sortvalue'})) {
				$tag->{'sortValue'} = $tagItem->{'sortvalue'};
			}
			push @tags,$tag;
		}
		$item->{'tags'} = \@tags;
		push @tracks,$item;
		$track = $iterator->next;
	}
	$result->{'tracks'} = \@tracks;
	$request->setRawResults($result);

	$log->debug("Exit jsonHandlerDirect");
	$request->setStatusDone();
}


# Scan a specific track and store its tags in the database
sub _scanTrack {
	my $track = undef;
	if(defined($tracks) && $inProgress) {
		$track = $tracks->next;
		$currentlyScannedTrackNo++;
		# Skip non audio tracks
		while(defined($track) && !$track->audio) {
			$track = $tracks->next;
			$currentlyScannedTrackNo++;
		}
	}
	if(defined($track)) {
		$currentlyScannedTrackFile = Slim::Utils::Misc::pathFromFileURL($track->url);
		my ($smdID,$tags) = readTags($track);
		storeTags($track->url,$smdID,$tags);
		return 1;

	}
	$tracks = undef;
	if($inProgress) {
		$log->info("Finished scanning");
	}else {
		$log->info("Aborted scanning");
	}
	$inProgress = 0;
	return 0;
}

# Slits a tag into multiple values using the separator character configured
# in Squeezebox Server settings
sub _splitTag {
        my $value = shift;

        my @arrayValues = ();
        if(ref($value) eq 'ARRAY') {
                for my $v (@$value) {
                        my @subArrayValues = Slim::Music::Info::splitTag($v);
                        if(scalar(@subArrayValues)>0) {
                                push @arrayValues,@subArrayValues;
                        }
                }
        }else {
                @arrayValues = Slim::Music::Info::splitTag($value);
        }
        return @arrayValues;
}

# Get the type of database engine that is currently used
sub _getDatabaseEngine {
	my $driver = $serverPrefs->get('dbsource');
	$driver =~ s/dbi:(.*?):(.*)$/$1/;
    
	if(UNIVERSAL::can("Slim::Schema","sourceInformation")) {
		my ($source,$username,$password);
		($driver,$source,$username,$password) = Slim::Schema->sourceInformation;
	}
	return $driver;
}

# Executes a SQL file which does modifications to the database. The SQL file will be picked
# from either the SQL/mysql directory or the SQL/SQLite directory depending on which database
# engine that is used in Squeezebox Server
sub _executeSQLFile {
	my $file  = shift;

	my $driver = _getDatabaseEngine();
	my $sqlFile;
	for my $plugindir (Slim::Utils::OSDetect::dirsFor('Plugins')) {
		opendir(DIR, catdir($plugindir,"SocialMusicDiscovery")) || next;
		$sqlFile = catdir($plugindir,"SocialMusicDiscovery", "SQL", $driver, $file);
		closedir(DIR);
	}

	$log->info("Executing SQL file $sqlFile...");

	open(my $fh, $sqlFile) or do {
		$log->error("Couldn't open: $sqlFile : $!\n");
		return;
	};

	my $dbh = Slim::Schema->storage->dbh();;

	my $statement   = '';
	my $inStatement = 0;

	for my $line (<$fh>) {
		chomp $line;

		# ignore comments & empty lines
		next if $line =~ /^--/;
		next if $line =~ /^\s*$/;

		if ($line =~ /^\s*(?:CREATE|SET|INSERT|UPDATE|DELETE|DROP|SELECT|ALTER|DROP|TRUNCATE)\s+/oi) {
			$inStatement = 1;
		}

		if ($line =~ /;/ && $inStatement) {

			$statement .= $line;

			$log->debug("Executing SQL statement: [$statement]");

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

	_commit($dbh);

	close $fh;
}

# Commit database changes unless automatic commit is enabled
sub _commit {
	my $dbh = shift;
	if (!$dbh->{'AutoCommit'}) {
		$dbh->commit();
	}
}

1;

__END__
