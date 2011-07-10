#!/usr/bin/perl -w

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
use File::Path;
use File::Basename;

=head1 NAME

generate_library.pl

=head1 DESCRIPTION

Will take a csv file produced from DiscogsLargeDatabaseSampleCreator Java Unit test case found here:
  http://socialmusicdiscovery.googlecode.com/svn/localserver/trunk/src/smd-server/src/test/java/org/socialmusicdiscovery/server/database/sampledata/DiscogsLargeDatabaseSampleCreator.java

Based on this, it will produce small random FLAC files tagged according to this data.

Each row in the csv file represent one file and it can look as this:
  PATH=/music/Profound Sounds Vol. 1_95b083ae-b005-4b9b-a7d6-f3f7e88ea683/2.Anjua (Sneaky 3).mp3|SMDID=5c3dc9832f894ecd8f3ac3be4dfd36b9|ALBUM=Profound Sounds Vol. 1|GENRE=Electronic|STYLE=Techno|STYLE=Tech House|ALBUMARTIST=Josh Wink|TITLE=Anjua (Sneaky 3)|TRACKNUM=2|ARTIST=Karl Axel Bissler

Which will produce a file:
  ./music/Profound Sounds Vol. 1_95b083ae-b005-4b9b-a7d6-f3f7e88ea683/2.Anjua (Sneaky 3).flac

With the following tags:
  ALBUM=Profound Sounds Vol. 1
  GENRE=Electronic
  STYLE=Techno
  STYLE=Tech House
  ALBUMARTIST=Josh Wink
  TITLE=Anjua (Sneaky 3)
  TRACKNUM=2
  ARTIST=Karl Axel Bissler

The PATH and SMDID fields are ignored when creating the tags.

=cut

if (!@ARGV) {
	print "You need to specify a csv file as input\n";
	exit;
}

if (! -x "/bin/dd") {
	print "Can't find dd binary at /bin/dd\n";
	exit;
}

if (! -x "/usr/bin/flac") {
	print "Can't find flac binary at /usr/bin/flac\n";
	exit;
}

if (! -x "/usr/bin/sox") {
	print "Can't find sox binary at /usr/bin/sox\n";
	exit;
}

my $input = $ARGV[0];

if (! -e $input) {
	print "Can't find input file: $input\n";
	exit;
}
print "Processing $input\n";

open FILE, "$input" or die $!;
my @files = <FILE>;
foreach my $line (@files) {
	$line =~ s/[\r\n]//;
	if(defined($line) && $line ne "") {
		my @tags = split(/\|/,$line);
		my $file = shift @tags;
		$file =~ s/^PATH=//;
		$file =~ s/[^\.]+$/flac/;
		$file = ".$file";
		my $tagString = "";
		foreach my $tag (@tags) {
			if($tag !~ /^SMDID=/) {
				$tag =~ s/\"/\\"/g;
				$tagString .= " --tag=\"$tag\"";
			}
		}
		print "Creating: $file\n";
		my $dir = dirname($file);
		mkpath($dir);
		system("dd if=/dev/urandom bs=2 count=1 2>/dev/null|sox -t raw -c 1 -s -w -r 44100 - -t wav ./generated.wav");
		if( -e "./generated.wav" ) {
			$file =~ s/\"/\\\"/g;
			system("flac ./generated.wav -s -f -o \"$file\" $tagString") == 0 or die "Failed to generate: $file";
			system("rm -f ./generated.wav");
		}else {
			print "Failed to generate data for ".$file;
		}
	}
}
