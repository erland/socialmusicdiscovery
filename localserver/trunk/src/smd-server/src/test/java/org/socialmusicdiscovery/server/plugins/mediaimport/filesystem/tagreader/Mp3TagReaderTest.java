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

import junit.framework.Assert;
import org.socialmusicdiscovery.server.plugins.mediaimport.TagData;
import org.socialmusicdiscovery.server.plugins.mediaimport.TrackData;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

public class Mp3TagReaderTest {
    @Test
    public void testSimple() throws IOException {
        String filename = BaseTestCase.getTestResourceDiretory() + "org/socialmusicdiscovery/server/plugins/mediaimport/filesystem/testfile1.mp3";
        TrackData data = new Mp3TagReader().getTrackData(new File(filename));
        assert data != null;
        assert data.getFile().equals(filename);
        assert data.getUrl().startsWith("file:/");
        assert data.getUrl().endsWith("testfile1.mp3");
        assert data.getFormat().equals("mp3");
        assert data.getSmdID().equals("ab2dbe29c46c1668b89cf4bd17e34a59-000004e4");
        assert data.getTags() != null;
        assert data.getTags().size() == 8;
        boolean foundAlbumArtist = false;
        boolean foundArtist = false;
        for (TagData tagData : data.getTags()) {
            if (tagData.getName().equals("ARTIST") && tagData.getValue().equals("EPMD")) {
                foundArtist = true;
            } else if (tagData.getName().equals("BAND") && tagData.getValue().equals("Kenny \"Dope\" Gonzalez")) {
                foundAlbumArtist = true;
            }
        }
        Assert.assertTrue(foundArtist);
        Assert.assertTrue(foundAlbumArtist);
    }

    @Test(enabled = false)
    public void testSimplev1() throws IOException {
        String filename = BaseTestCase.getTestResourceDiretory() + "org/socialmusicdiscovery/server/plugins/mediaimport/filesystem/testfile1v1.mp3";
        TrackData data = new Mp3TagReader().getTrackData(new File(filename));
        assert data != null;
        assert data.getFile().equals(filename);
        assert data.getUrl().startsWith("file:/");
        assert data.getUrl().endsWith("testfile1v1.mp3");
        assert data.getFormat().equals("mp3");
        assert data.getSmdID().equals("ab2dbe29c46c1668b89cf4bd17e34a59-000004e4");
        assert data.getTags() != null;
        assert data.getTags().size() == 8;
        boolean foundAlbumArtist = false;
        boolean foundArtist = false;
        for (TagData tagData : data.getTags()) {
            if (tagData.getName().equals("ARTIST") && tagData.getValue().equals("EPMD")) {
                foundArtist = true;
            } else if (tagData.getName().equals("BAND") && tagData.getValue().equals("Kenny \"Dope\" Gonzalez")) {
                foundAlbumArtist = true;
            }
        }
        Assert.assertTrue(foundArtist);
        Assert.assertTrue(foundAlbumArtist);
    }

    @Test
    public void testSimplev2() throws IOException {
        String filename = BaseTestCase.getTestResourceDiretory() + "org/socialmusicdiscovery/server/plugins/mediaimport/filesystem/testfile1v2.mp3";
        TrackData data = new Mp3TagReader().getTrackData(new File(filename));
        assert data != null;
        assert data.getFile().equals(filename);
        assert data.getUrl().startsWith("file:/");
        assert data.getUrl().endsWith("testfile1v2.mp3");
        assert data.getFormat().equals("mp3");
        assert data.getSmdID().equals("ab2dbe29c46c1668b89cf4bd17e34a59-000004e4");
        assert data.getTags() != null;
        assert data.getTags().size() == 8;
        boolean foundAlbumArtist = false;
        boolean foundArtist = false;
        for (TagData tagData : data.getTags()) {
            if (tagData.getName().equals("ARTIST") && tagData.getValue().equals("EPMD")) {
                foundArtist = true;
            } else if (tagData.getName().equals("BAND") && tagData.getValue().equals("Kenny \"Dope\" Gonzalez")) {
                foundAlbumArtist = true;
            }
        }
        Assert.assertTrue(foundArtist);
        Assert.assertTrue(foundAlbumArtist);
    }

    @Test
    public void testMultipleCustomTagsv2() throws IOException {
        String filename = BaseTestCase.getTestResourceDiretory() + "org/socialmusicdiscovery/server/plugins/mediaimport/filesystem/testfile2v2.mp3";
        TrackData data = new Mp3TagReader().getTrackData(new File(filename));
        assert data != null;
        assert data.getFile().equals(filename);
        assert data.getUrl().startsWith("file:/");
        assert data.getUrl().endsWith("testfile2v2.mp3");
        assert data.getFormat().equals("mp3");
        assert data.getSmdID().equals("b3ee93a0e9c037d22a2220d994f8a1a4-000004e4");
        assert data.getTags() != null;
        assert data.getTags().size() == 9;
        boolean foundAlbumArtist = false;
        boolean foundArtist = false;
        int foundStyle = 0;
        for (TagData tagData : data.getTags()) {
            if (tagData.getName().equals("ARTIST")) {
                foundArtist = true;
            } else if (tagData.getName().equals("BAND") && tagData.getValue().equals("Model 500")) {
                foundAlbumArtist = true;
            } else if (tagData.getName().equals("STYLE") && tagData.getValue().equals("Techno")) {
                foundStyle++;
            } else if (tagData.getName().equals("STYLE") && tagData.getValue().equals("Drum n Bass")) {
                foundStyle++;
            } else if (tagData.getName().equals("STYLE") && tagData.getValue().equals("Electro")) {
                foundStyle++;
            }
        }
        Assert.assertFalse(foundArtist);
        Assert.assertTrue(foundAlbumArtist);
        Assert.assertEquals(foundStyle, 3);
    }
}
