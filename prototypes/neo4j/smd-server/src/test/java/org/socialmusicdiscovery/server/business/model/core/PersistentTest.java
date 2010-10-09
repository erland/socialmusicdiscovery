package org.socialmusicdiscovery.server.business.model.core;

import jo4neo.ObjectGraph;
import jo4neo.ObjectGraphFactory;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.socialmusicdiscovery.server.business.logic.SMDApplication;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;


public class PersistentTest {
    private ObjectGraph em;
   private GraphDatabaseService emFactory;

    private final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy");

    @BeforeTest
    public void setUp()  {
        emFactory = new EmbeddedGraphDatabase("target/neo4j");
        em = ObjectGraphFactory.instance().get(emFactory);
    }

    @AfterTest
    public void tearDown() throws IOException {
       emFactory.shutdown();
   }
    public void cleanUp() {
        Transaction t = em.beginTx();
        try  {
            for (Release obj: em.get(Release.class)) {
                if(obj!=null) em.delete(obj);
            }
            for (Recording obj: em.get(Recording.class)) {
                if(obj!=null) em.delete(obj);
            }
            for (RecordingSession obj: em.get(RecordingSession.class)) {
                if(obj!=null) em.delete(obj);
            }
            for (Label obj: em.get(Label.class)) {
                if(obj!=null) em.delete(obj);
            }
            for (Track obj: em.get(Track.class)) {
                if(obj!=null) em.delete(obj);
            }
            for (Medium obj: em.get(Medium.class)) {
                if(obj!=null) em.delete(obj);
            }
            for (Work obj: em.get(Work.class)) {
                if(obj!=null) em.delete(obj);
            }
            for (Contributor obj: em.get(Contributor.class)) {
                if(obj!=null) em.delete(obj);
            }
            for (Artist obj: em.get(Artist.class)) {
                if(obj!=null) em.delete(obj);
            }
            for (Person obj: em.get(Person.class)) {
                if(obj!=null) em.delete(obj);
            }
            t.success();
        }finally {
            t.finish();
        }

    }


    private void loadTestData(String file) throws Exception, MalformedURLException {
        cleanUp();
        addTestData(file);
    }
    private void addTestData(String file) throws Exception, MalformedURLException {
        Transaction t = em.beginTx();
        Neo4JLoader.loadFile(em,"src/test/test-data/"+this.getClass().getPackage().getName().replaceAll("\\.","/")+"/"+file);
        t.success();
        t.finish();
    }

    @Test
    public void testModelCreation() throws ParseException, Exception {
        cleanUp();
        Transaction t = em.beginTx();
        try {
            Release release = new Release();
            release.setName("The Bodyguard (Original Soundtrack Album)");
            release.setDate(DATE_FORMAT.parse("1992"));
            em.persist(release);

            Work work = new Work();
            work.setName("I Will Always Love You");
            em.persist(work);

            Contributor contributorDollyParton = new Contributor();
            contributorDollyParton.setType("composer");
            Artist artistDollyParton = new Artist();
            artistDollyParton.setName("Dolly Parton");
            em.persist(artistDollyParton);
            contributorDollyParton.setArtist(artistDollyParton);
            em.persist(contributorDollyParton);

            work.setContributors(Arrays.asList(contributorDollyParton));
            em.persist(work);

            Recording recording = new Recording();
            recording.setWork(work);
            em.persist(recording);

            Contributor contributorWhitneyHouston= new Contributor();
            contributorWhitneyHouston.setType("artist");
            Artist artistWhitneyHouston= new Artist();
            artistWhitneyHouston.setName("Whitney Houston");
            em.persist(artistWhitneyHouston);
            contributorWhitneyHouston.setArtist(artistWhitneyHouston);
            em.persist(contributorWhitneyHouston);

            Contributor contributorRickyMinor= new Contributor();
            contributorRickyMinor.setType("conductor");
            Artist artistRickyMinor= new Artist();
            artistRickyMinor.setName("Ricky Minor");
            em.persist(artistRickyMinor);
            contributorRickyMinor.setArtist(artistRickyMinor);
            em.persist(contributorRickyMinor);

            recording.setContributors(Arrays.asList(contributorWhitneyHouston,contributorRickyMinor));
            em.persist(recording);

            Track track = new Track();
            track.setNumber(1);
            track.setRecording(recording);
            em.persist(track);

            release.setTracks(Arrays.asList(track));
            em.persist(release);
            t.success();
        }
        finally{
            t.finish();
        }

        Release release = new Release();
        release = em.find(release).where(release.getName()).is("The Bodyguard (Original Soundtrack Album)").result();
        assert(release != null);

        SMDApplication.printRelease(release);

        assert(release.getName().equals("The Bodyguard (Original Soundtrack Album)"));
        assert(DATE_FORMAT.format(release.getDate()).equals("1992"));
        assert(release.getTracks() != null);
        assert(release.getTracks().size() == 1);
        Track track = release.getTracks().iterator().next();
        assert(track.getNumber().equals(1));
        assert(track.getRecording() != null);
        assert(track.getRecording().getContributors() != null);
        assert(track.getRecording().getContributors().size()==2);
        Iterator<Contributor> iterator = track.getRecording().getContributors().iterator();
        Contributor contributor1 = iterator.next();
        assert(contributor1.getType() != null);
        assert(contributor1.getArtist() != null);
        assert(contributor1.getType().equals("conductor") || contributor1.getType().equals("artist"));
        if(contributor1.getType().equals("conductor")) {
            assert(contributor1.getArtist() != null);
            assert(contributor1.getArtist().getName().equals("Ricky Minor"));
            Contributor contributor2 = iterator.next();
            assert(contributor2.getType() != null);
            assert(contributor2.getType().equals("artist"));
            assert(contributor2.getArtist() != null);
            assert(contributor2.getArtist().getName().equals("Whitney Houston"));
        }else {
            assert(contributor1.getArtist() != null);
            assert(contributor1.getArtist().getName().equals("Whitney Houston"));
            Contributor contributor2 = iterator.next();
            assert(contributor2.getType() != null);
            assert(contributor2.getType().equals("conductor"));
            assert(contributor2.getArtist() != null);
            assert(contributor2.getArtist().getName().equals("Ricky Minor"));
        }
    }

    @Test
    public void testModelRead() throws Exception, MalformedURLException {
        loadTestData("The Bodyguard.xml");
        Transaction t = em.beginTx();
        try {
            Release release = new Release();
            release = em.find(release).where(release.getName()).is("The Bodyguard (Original Soundtrack Album)").result();
            assert(release != null);

            SMDApplication.printRelease(release);
            t.success();
        }catch (Exception e) {
            t.failure();
            e.printStackTrace();
        }finally {
            t.finish();
        }
    }
}
