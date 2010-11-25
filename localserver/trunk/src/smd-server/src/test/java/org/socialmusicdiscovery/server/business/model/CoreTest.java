package org.socialmusicdiscovery.server.business.model;

import org.socialmusicdiscovery.server.api.mediaimport.ProcessingStatusCallback;
import org.socialmusicdiscovery.server.business.logic.SMDApplication;
import org.socialmusicdiscovery.server.business.logic.SearchRelationPostProcessor;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.*;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;


public class CoreTest extends BaseTestCase {
    @BeforeTest
    public void setUp()  {
        super.setUp();
    }

    @AfterTest
    public void tearDown() {
        super.tearDown();
    }

    @BeforeMethod
    public void setUpMethod(Method m) {
        System.out.println("Executing "+getClass().getSimpleName()+"."+m.getName()+"...");
        em.clear();
    }
    @AfterMethod
    public void tearDownMethod(Method m) {
        if(em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
    
    @Test
    public void testModelCreation() throws Exception {
        loadTestData(getClass().getPackage().getName(),"Empty Tables.xml");
        em.getTransaction().begin();
        try {
            Release release = new Release();
            release.setName("The Bodyguard (Original Soundtrack Album)");
            release.setDate(DATE_FORMAT.parse("1992"));
            releaseRepository.create(release);

            Work work = new Work();
            work.setName("I Will Always Love You");
            workRepository.create(work);

            Contributor contributorDollyParton = new Contributor();
            contributorDollyParton.setType("composer");
            Artist artistDollyParton = new Artist();
            artistDollyParton.setName("Dolly Parton");
            artistRepository.create(artistDollyParton);
            contributorDollyParton.setArtist(artistDollyParton);
            contributorRepository.create(contributorDollyParton);

            work.getContributors().add(contributorDollyParton);
            workRepository.create(work);

            Recording recording = new Recording();
            recording.setWork(work);
            recordingRepository.create(recording);

            Contributor contributorWhitneyHouston= new Contributor();
            contributorWhitneyHouston.setType("artist");
            Artist artistWhitneyHouston= new Artist();
            artistWhitneyHouston.setName("Whitney Houston");
            artistRepository.create(artistWhitneyHouston);
            contributorWhitneyHouston.setArtist(artistWhitneyHouston);
            contributorRepository.create(contributorWhitneyHouston);

            Contributor contributorRickyMinor= new Contributor();
            contributorRickyMinor.setType("conductor");
            Artist artistRickyMinor= new Artist();
            artistRickyMinor.setName("Ricky Minor");
            artistRepository.create(artistRickyMinor);
            contributorRickyMinor.setArtist(artistRickyMinor);
            contributorRepository.create(contributorRickyMinor);

            recording.getContributors().addAll(Arrays.asList(contributorWhitneyHouston,contributorRickyMinor));
            recordingRepository.create(recording);

            Track track = new Track();
            track.setNumber(1);
            track.setRecording(recording);
            trackRepository.create(track);

            release.setTracks(Arrays.asList(track));
            releaseRepository.create(release);
        }
        finally{
            em.getTransaction().commit();
        }
        em.getTransaction().begin();
        Query query = em.createQuery("from Release where name=:name");
        query.setParameter("name","The Bodyguard (Original Soundtrack Album)");
        Release release = (Release) query.getSingleResult();
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
        em.getTransaction().commit();
    }

    @Test
    public void testModelRead() throws Exception {
        loadTestData(getClass().getPackage().getName(),"The Bodyguard.xml");
        em.getTransaction().begin();

        Query query = em.createQuery("from Release where name=:name");
        query.setParameter("name","The Bodyguard (Original Soundtrack Album)");
        Release release = (Release) query.getSingleResult();
        assert(release != null);

        SMDApplication.printRelease(release);
        em.getTransaction().commit();
    }

    @Test
    public void testModelDeleteRelease() throws Exception {
        loadTestData(getClass().getPackage().getName(),"The Bodyguard.xml");
        new SearchRelationPostProcessor().execute(new ProcessingStatusCallback() {
            public void progress(String module, String currentDescription, Long currentNo, Long totalNo) {}
            public void failed(String module, String error) {}
            public void finished(String module) {}
            public void aborted(String module) {}
        });

        em.getTransaction().begin();
        Query query = em.createQuery("from Release where name=:name");
        query.setParameter("name","The Bodyguard (Original Soundtrack Album)");
        Release release = (Release) query.getSingleResult();
        assert(release != null);

        String releaseId = release.getId();
        assert 0 < em.createQuery("from Track where release_id=:release").setParameter("release",releaseId).getResultList().size();
        assert 0 < em.createQuery("from ReleaseSearchRelation where id=:release").setParameter("release",releaseId).getResultList().size();
        assert 0 < em.createQuery("from WorkSearchRelation where reference=:release").setParameter("release",releaseId).getResultList().size();
        int recordings = em.createQuery("from Recording").getResultList().size();

        releaseRepository.remove(release);
        //em.flush();

        query = em.createQuery("from Release where name=:name");
        query.setParameter("name","The Bodyguard (Original Soundtrack Album)");
        try {
            release = (Release) query.getSingleResult();
        } catch (NoResultException e) {
            release = null;
        }
        assert(release == null);

        assert 0 == em.createQuery("from Track where release_id=:release").setParameter("release",releaseId).getResultList().size();
        assert 0 == em.createQuery("from ReleaseSearchRelation where id=:release").setParameter("release",releaseId).getResultList().size();
        assert 0 == em.createQuery("from WorkSearchRelation where reference=:release").setParameter("release",releaseId).getResultList().size();
        assert recordings == em.createQuery("from Recording").getResultList().size();

        em.getTransaction().commit();
    }

    @Test
    public void testModelDeletePerson() throws Exception {
        loadTestData(getClass().getPackage().getName(),"The Bodyguard.xml");
        new SearchRelationPostProcessor().execute(new ProcessingStatusCallback() {
            public void progress(String module, String currentDescription, Long currentNo, Long totalNo) {}
            public void failed(String module, String error) {}
            public void finished(String module) {}
            public void aborted(String module) {}
        });

        em.getTransaction().begin();
        Query query = em.createQuery("from Person where name=:name");
        query.setParameter("name","Dolly Rebecca Parton");
        Person person = (Person) query.getSingleResult();
        assert(person != null);

        String personId = person.getId();
        assert 0 < em.createQuery("from ReleaseSearchRelation where reference=:person").setParameter("person",personId).getResultList().size();

        personRepository.remove(person);
        em.flush();

        query = em.createQuery("from Person where name=:name");
        query.setParameter("name","Dolly Rebecca Parton");
        try {
            person = (Person) query.getSingleResult();
        } catch (NoResultException e) {
            person = null;
        }
        assert(person == null);

        assert 0 == em.createQuery("from ReleaseSearchRelation where reference=:person").setParameter("person",personId).getResultList().size();

        query = em.createQuery("from Artist where name=:name");
        query.setParameter("name","Dolly Parton");
        Artist artist = (Artist) query.getSingleResult();
        assert(artist != null);
        assert(artist.getPerson() == null);

        em.getTransaction().commit();
    }

    @Test
    public void testModelDeleteArtist() throws Exception {
        loadTestData(getClass().getPackage().getName(),"The Bodyguard.xml");
        new SearchRelationPostProcessor().execute(new ProcessingStatusCallback() {
            public void progress(String module, String currentDescription, Long currentNo, Long totalNo) {}
            public void failed(String module, String error) {}
            public void finished(String module) {}
            public void aborted(String module) {}
        });

        em.getTransaction().begin();
        Query query = em.createQuery("from Artist where name=:name");
        query.setParameter("name","Dolly Parton");
        Artist artist = (Artist) query.getSingleResult();
        assert(artist != null);

        artistRepository.remove(artist);
        em.flush();

        query = em.createQuery("from Artist where name=:name");
        query.setParameter("name","Dolly Parton");
        try {
            artist = (Artist) query.getSingleResult();
        } catch (NoResultException e) {
            artist = null;
        }
        assert(artist == null);

        query = em.createQuery("from Person where name=:name");
        query.setParameter("name","Dolly Rebecca Parton");
        Person person = (Person) query.getSingleResult();
        assert(person != null);
        em.getTransaction().commit();
    }

    @Test
    public void testModelDeleteTrackRecordingFailure() throws Exception {
        loadTestData(getClass().getPackage().getName(),"The Bodyguard.xml");
        new SearchRelationPostProcessor().execute(new ProcessingStatusCallback() {
            public void progress(String module, String currentDescription, Long currentNo, Long totalNo) {}
            public void failed(String module, String error) {}
            public void finished(String module) {}
            public void aborted(String module) {}
        });

        em.getTransaction().begin();
        Query query = em.createQuery("from Release where name=:name");
        query.setParameter("name","The Bodyguard (Original Soundtrack Album)");
        Release release = (Release) query.getSingleResult();
        assert(release != null);

        Track trackForRecording = null;
        Recording recording = null;
        for (Track track : release.getTracks()) {
            if(track.getNumber().equals(1)) {
                trackForRecording = track;
                recording=track.getRecording();
            }
        }
        assert trackForRecording != null;
        assert recording != null;

        boolean error = false;
        try {
            recordingRepository.remove(recording);
            em.flush();
            em.getTransaction().commit();
        } catch (Exception e) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            error = true;
        }

        // We shouldn't allow to remove a recording which is used
        assert error;
    }

    @Test
    public void testModelDeleteTrackRecording() throws Exception {
        loadTestData(getClass().getPackage().getName(),"The Bodyguard.xml");
        new SearchRelationPostProcessor().execute(new ProcessingStatusCallback() {
            public void progress(String module, String currentDescription, Long currentNo, Long totalNo) {}
            public void failed(String module, String error) {}
            public void finished(String module) {}
            public void aborted(String module) {}
        });

        em.getTransaction().begin();
        Query query = em.createQuery("from Release where name=:name");
        query.setParameter("name","The Bodyguard (Original Soundtrack Album)");
        Release release = (Release) query.getSingleResult();
        assert(release != null);

        Track trackForRecording = null;
        Recording recording = null;
        for (Track track : release.getTracks()) {
            if(track.getNumber().equals(1)) {
                trackForRecording = track;
                recording=track.getRecording();
            }
        }
        assert trackForRecording != null;
        assert recording != null;

        trackRepository.remove(trackForRecording);
        recordingRepository.remove(recording);
        em.flush();

        query = em.createQuery("from Release where name=:name");
        query.setParameter("name","The Bodyguard (Original Soundtrack Album)");
        release = (Release) query.getSingleResult();
        assert(release != null);

        trackForRecording = null;
        recording = null;
        for (Track track : release.getTracks()) {
            if(track.getNumber().equals(1)) {
                trackForRecording = track;
                recording=track.getRecording();
            }
        }
        assert trackForRecording == null;
        assert recording == null;
        em.getTransaction().commit();
    }

    @Test
    public void testModelDeleteRecordingWorkFailure() throws Exception {
        loadTestData(getClass().getPackage().getName(),"The Bodyguard.xml");
        new SearchRelationPostProcessor().execute(new ProcessingStatusCallback() {
            public void progress(String module, String currentDescription, Long currentNo, Long totalNo) {}
            public void failed(String module, String error) {}
            public void finished(String module) {}
            public void aborted(String module) {}
        });

        em.getTransaction().begin();
        Query query = em.createQuery("from Release where name=:name");
        query.setParameter("name","The Bodyguard (Original Soundtrack Album)");
        Release release = (Release) query.getSingleResult();
        assert(release != null);

        Track trackForRecording = null;
        Recording recordingForWork = null;
        Work work = null;
        for (Track track : release.getTracks()) {
            if(track.getNumber().equals(1)) {
                trackForRecording = track;
                recordingForWork=track.getRecording();
                work = recordingForWork.getWork();
            }
        }
        assert trackForRecording != null;
        assert recordingForWork != null;
        assert work != null;

        boolean error = false;
        try {
            workRepository.remove(work);
            em.flush();
            em.getTransaction().commit();
        } catch (Exception e) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            error = true;
        }
        // We shouldn't allow to remove a recording which is used
        assert error;
    }

    @Test
    public void testModelDeleteRecordingWork() throws Exception {
        loadTestData(getClass().getPackage().getName(),"The Bodyguard.xml");
        new SearchRelationPostProcessor().execute(new ProcessingStatusCallback() {
            public void progress(String module, String currentDescription, Long currentNo, Long totalNo) {}
            public void failed(String module, String error) {}
            public void finished(String module) {}
            public void aborted(String module) {}
        });

        em.getTransaction().begin();
        Query query = em.createQuery("from Release where name=:name");
        query.setParameter("name","The Bodyguard (Original Soundtrack Album)");
        Release release = (Release) query.getSingleResult();
        assert(release != null);

        Track trackForRecording = null;
        Recording recordingForWork = null;
        Work work = null;
        for (Track track : release.getTracks()) {
            if(track.getNumber().equals(1)) {
                trackForRecording = track;
                recordingForWork=track.getRecording();
                work = recordingForWork.getWork();
            }
        }
        assert trackForRecording != null;
        assert recordingForWork != null;
        assert work != null;

        trackRepository.remove(trackForRecording);
        em.flush();
        recordingRepository.remove(recordingForWork);
        em.flush();
        workRepository.remove(work);
        em.flush();

        query = em.createQuery("from Release where name=:name");
        query.setParameter("name","The Bodyguard (Original Soundtrack Album)");
        release = (Release) query.getSingleResult();
        assert(release != null);

        trackForRecording = null;
        recordingForWork = null;
        work = null;
        for (Track track : release.getTracks()) {
            if(track.getNumber().equals(1)) {
                trackForRecording = track;
                recordingForWork=track.getRecording();
                work = recordingForWork.getWork();
            }
        }
        assert recordingForWork == null;
        assert trackForRecording == null;
        assert work == null;
        em.getTransaction().commit();
    }
}
