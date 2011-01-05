package org.socialmusicdiscovery.server.support.format;

import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class TitleFormatTest extends BaseTestCase {
    @BeforeTest
    public void setUp() {
        super.setUp();
    }

    @AfterTest
    public void tearDown() {
        super.tearDown();
    }

    @BeforeMethod
    public void setUpMethod(Method m) {
        System.out.println("Executing " + getClass().getSimpleName() + "." + m.getName() + "...");
        em.clear();
    }

    @AfterMethod
    public void tearDownMethod(Method m) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
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
        TitleFormat parser = new TitleFormat("(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.work.parent.name||[%object.recording.work.parent,: ]||%object.recording.work.name");

        TrackEntity track1 = new TrackEntity();
        track1.setNumber(5);
        track1.setRecording(new RecordingEntity());
        track1.getRecording().setWork(new WorkEntity());
        track1.getRecording().getWork().setName("Work 2");
        assert parser.format(track1).equals("5. Work 2");

        TrackEntity track2 = new TrackEntity();
        track2.setNumber(9);
        track2.setMedium(new MediumEntity());
        track2.getMedium().setNumber(1);
        track2.setRecording(new RecordingEntity());
        track2.getRecording().setWork(new WorkEntity());
        track2.getRecording().getWork().setName("Part 1");
        track2.getRecording().getWork().setParent(new WorkEntity());
        track2.getRecording().getWork().getParent().setName("Work 3");
        assert parser.format(track2).equals("1-9. Work 3: Part 1");

        TrackEntity track3 = new TrackEntity();
        track3.setNumber(13);
        track3.setMedium(new MediumEntity());
        track3.getMedium().setName("A");
        track3.setRecording(new RecordingEntity());
        track3.getRecording().setWork(new WorkEntity());
        track3.getRecording().getWork().setName("Part 1");
        track3.getRecording().getWork().setParent(new WorkEntity());
        track3.getRecording().getWork().getParent().setName("Work 4");
        assert parser.format(track3).equals("A-13. Work 4: Part 1");
    }
}
