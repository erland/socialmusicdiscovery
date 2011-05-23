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

package org.socialmusicdiscovery.server.support.format;

import org.socialmusicdiscovery.server.business.model.core.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class TitleFormatTest {
    @BeforeMethod
    public void setUpMethod(Method m) {
        System.out.println("Executing " + getClass().getSimpleName() + "." + m.getName() + "...");
    }

    @Test
    public void testRelease() throws Exception {
        TitleFormat parser = new TitleFormat("%object.name");

        ReleaseEntity release = new ReleaseEntity();
        release.setName("Release 1");

        assert parser.format(release).equals("Release 1");
    }

    @Test
    public void testString() throws Exception {
        TitleFormat parser = new TitleFormat("%object");

        assert parser.format("Test String").equals("Test String");
    }

    @Test
    public void testSeparatorCharacters() throws Exception {
        TitleFormat parser = new TitleFormat("%obj1|| and some separators like -_.&#:/()=; and ||[%obj3,%obj2]|| and ||(%obj3|%obj2)|| and ||(%obj4|%obj5|%obj1)");

        Map<String, Object> objects = new HashMap<String, Object>();
        objects.put("obj1", "Object1");
        objects.put("obj2", "Object2");
        objects.put("obj3", "Object3");
        System.out.println(parser.format(objects));
        assert parser.format(objects).equals("Object1 and some separators like -_.&#:/()=; and Object2 and Object3 and Object1");
    }

    @Test
    public void testWork() throws Exception {
        TitleFormat parser = new TitleFormat("%parentwork.name||[%parentwork.name,: ]||%work.name");

        WorkEntity work = new WorkEntity();
        work.setName("Work 1");
        WorkEntity part = new WorkEntity();
        part.setName("Part 1");
        part.setParent(work);

        Map<String, Object> objects = new HashMap<String, Object>();

        objects.put("work", work);
        assert parser.format(objects).equals("Work 1");

        objects.put("work", part);
        objects.put("parentwork", work);
        assert parser.format(objects).equals("Work 1: Part 1");
    }

    @Test
    public void testTrack() throws Exception {
        TitleFormat parser = new TitleFormat("(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name");

        TrackEntity track1 = new TrackEntity();
        track1.setNumber(5);
        track1.setRecording(new RecordingEntity());
        track1.getRecording().getWorks().add(new WorkEntity());
        track1.getRecording().getWorks().iterator().next().setName("Work 2");
        assert parser.format(track1).equals("5. Work 2");

        TrackEntity track2 = new TrackEntity();
        track2.setNumber(9);
        track2.setMedium(new MediumEntity());
        track2.getMedium().setNumber(1);
        track2.setRecording(new RecordingEntity());
        track2.getRecording().getWorks().add(new WorkEntity());
        track2.getRecording().getWorks().iterator().next().setName("Part 1");
        track2.getRecording().getWorks().iterator().next().setParent(new WorkEntity());
        track2.getRecording().getWorks().iterator().next().getParent().setName("Work 3");
        assert parser.format(track2).equals("1-9. Work 3: Part 1");

        TrackEntity track3 = new TrackEntity();
        track3.setNumber(13);
        track3.setMedium(new MediumEntity());
        track3.getMedium().setName("A");
        track3.setRecording(new RecordingEntity());
        track3.getRecording().getWorks().add(new WorkEntity());
        track3.getRecording().getWorks().iterator().next().setName("Part 1");
        track3.getRecording().getWorks().iterator().next().setParent(new WorkEntity());
        track3.getRecording().getWorks().iterator().next().getParent().setName("Work 4");
        assert parser.format(track3).equals("A-13. Work 4: Part 1");
    }
}
