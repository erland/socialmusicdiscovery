/*
 *  Copyright 2010-2011, Social Music Discovery project
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of Social Music Discovery project nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL SOCIAL MUSIC DISCOVERY PROJECT BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.socialmusicdiscovery.server.plugins.mediaimport.filesystem.tagreader;

import org.socialmusicdiscovery.server.plugins.mediaimport.TagData;
import org.socialmusicdiscovery.server.plugins.mediaimport.TrackData;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

public class FlacTagReaderTest {
    @Test
    public void testFlacSimpleFlac() throws IOException {
        String filename = BaseTestCase.getTestResourceDiretory() + "org/socialmusicdiscovery/server/plugins/mediaimport/filesystem/testfile1.flac";
        TrackData data = new FlacTagReader(null).getTrackData(new File(filename));
        assert data != null;
        assert data.getFile().equals(filename);
        assert data.getUrl().startsWith("file:/");
        assert data.getUrl().endsWith("testfile1.flac");
        assert data.getFormat().equals("flc");
        assert data.getSmdID().equals("fa5a51e838417f9ba57185973fd3ff8-000001a5");
        assert data.getTags() != null;
        assert data.getTags().size() == 8;
        boolean foundAlbumArtist = false;
        boolean foundArtist = false;
        for (TagData tagData : data.getTags()) {
            if (tagData.getName().equals("ARTIST") && tagData.getValue().equals("EPMD")) {
                foundArtist = true;
            } else if (tagData.getName().equals("ALBUMARTIST") && tagData.getValue().equals("Kenny \"Dope\" Gonzalez")) {
                foundAlbumArtist = true;
            }
        }
        assert foundArtist;
        assert foundAlbumArtist;
    }

    @Test
    public void testFlacMultipleCustomTags() throws IOException {
        String filename = BaseTestCase.getTestResourceDiretory() + "org/socialmusicdiscovery/server/plugins/mediaimport/filesystem/testfile2.flac";
        TrackData data = new FlacTagReader(null).getTrackData(new File(filename));
        assert data != null;
        assert data.getFile().equals(filename);
        assert data.getUrl().startsWith("file:/");
        assert data.getUrl().endsWith("testfile2.flac");
        assert data.getFormat().equals("flc");
        assert data.getSmdID().equals("e8143bb472c39b208a1b8ccb5b6666ad-000001b3");
        assert data.getTags() != null;
        assert data.getTags().size() == 9;
        boolean foundAlbumArtist = false;
        boolean foundArtist = false;
        int foundStyle = 0;
        for (TagData tagData : data.getTags()) {
            if (tagData.getName().equals("ARTIST")) {
                foundArtist = true;
            } else if (tagData.getName().equals("ALBUMARTIST") && tagData.getValue().equals("Model 500")) {
                foundAlbumArtist = true;
            } else if (tagData.getName().equals("STYLE") && tagData.getValue().equals("Techno")) {
                foundStyle++;
            } else if (tagData.getName().equals("STYLE") && tagData.getValue().equals("Drum n Bass")) {
                foundStyle++;
            } else if (tagData.getName().equals("STYLE") && tagData.getValue().equals("Electro")) {
                foundStyle++;
            }
        }
        assert !foundArtist;
        assert foundAlbumArtist;
        assert foundStyle == 3;
    }

    @Test
    public void testFlacMultipleSeparatedCustomTags() throws IOException {
        String filename = BaseTestCase.getTestResourceDiretory() + "org/socialmusicdiscovery/server/plugins/mediaimport/filesystem/testfile2separated.flac";
        TrackData data = new FlacTagReader(";").getTrackData(new File(filename));
        assert data != null;
        assert data.getFile().equals(filename);
        assert data.getUrl().startsWith("file:/");
        assert data.getUrl().endsWith("testfile2separated.flac");
        assert data.getFormat().equals("flc");
        assert data.getSmdID().equals("e8143bb472c39b208a1b8ccb5b6666ad-000001b3");
        assert data.getTags() != null;
        assert data.getTags().size() == 9;
        boolean foundAlbumArtist = false;
        boolean foundArtist = false;
        int foundStyle = 0;
        for (TagData tagData : data.getTags()) {
            if (tagData.getName().equals("ARTIST")) {
                foundArtist = true;
            } else if (tagData.getName().equals("ALBUMARTIST") && tagData.getValue().equals("Model 500")) {
                foundAlbumArtist = true;
            } else if (tagData.getName().equals("STYLE") && tagData.getValue().equals("Techno")) {
                foundStyle++;
            } else if (tagData.getName().equals("STYLE") && tagData.getValue().equals("Drum n Bass")) {
                foundStyle++;
            } else if (tagData.getName().equals("STYLE") && tagData.getValue().equals("Electro")) {
                foundStyle++;
            }
        }
        assert !foundArtist;
        assert foundAlbumArtist;
        assert foundStyle == 3;
    }
}
