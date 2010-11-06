package org.socialmusicdiscovery.server.business.model;

import org.socialmusicdiscovery.server.business.logic.SMDApplication;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.persistence.Query;
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
        try {
            Query query = em.createQuery("from Release where name=:name");
            query.setParameter("name","The Bodyguard (Original Soundtrack Album)");
            Release release = (Release) query.getSingleResult();
            assert(release != null);

            SMDApplication.printRelease(release);
        }catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }finally {
            em.getTransaction().commit();
        }
    }
}
