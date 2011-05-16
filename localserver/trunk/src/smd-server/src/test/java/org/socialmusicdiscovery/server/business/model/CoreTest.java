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

package org.socialmusicdiscovery.server.business.model;

import org.socialmusicdiscovery.server.api.mediaimport.ProcessingStatusCallback;
import org.socialmusicdiscovery.server.business.logic.SearchRelationPostProcessor;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.*;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.lang.reflect.Method;
import java.util.*;


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

    public void printRelease(Release release) {
        if (release != null) {
            String label = "";
            if (release.getLabel() != null) {
                label = "(" + release.getLabel().getName() + ")";
            }
            String date = "";
            if (release.getDate() != null) {
                date = " (" + DATE_FORMAT.format(release.getDate()) + ")";
            }
            System.out.println(release.getName() + date + " " + label);
            System.out.println("-------------------------------");
            for (Contributor contributor : release.getContributors()) {
                if (contributor.getArtist().getPerson() != null) {
                    System.out.println("- " + contributor.getType() + ": " + contributor.getArtist().getName() + " (" + contributor.getArtist().getPerson().getName() + ")");
                } else {
                    System.out.println("- " + contributor.getType() + ": " + contributor.getArtist().getName());
                }
            }
            if (release.getContributors().size() > 0) {
                System.out.println();
            }
            if (release.getMediums().size() > 0) {
                for (Medium medium : release.getMediums()) {
                    printTracks(((MediumEntity) medium).getTracks(), (medium.getName() != null ? medium.getName() : "" + medium.getNumber()) + " - ");
                }
            } else {
                printTracks(release.getTracks(), "");
            }
            System.out.println();
        }
    }

    private void printTracks(List<Track> tracks, String prefix) {
        for (Track track : tracks) {
            Recording recording = track.getRecording();
            Work work = recording.getWorks().iterator().next();

            System.out.println(prefix + track.getNumber() + ". " + work.getName());
            for (Contributor contributor : recording.getContributors()) {
                if (contributor.getArtist().getPerson() != null) {
                    System.out.println("- " + contributor.getType() + ": " + contributor.getArtist().getName() + " (" + contributor.getArtist().getPerson().getName() + ")");
                } else {
                    System.out.println("- " + contributor.getType() + ": " + contributor.getArtist().getName());
                }
            }
            for (Contributor contributor : work.getContributors()) {
                if (contributor.getArtist().getPerson() != null) {
                    System.out.println("- " + contributor.getType() + ": " + contributor.getArtist().getName() + " (" + contributor.getArtist().getPerson().getName() + ")");
                } else {
                    System.out.println("- " + contributor.getType() + ": " + contributor.getArtist().getName());
                }
            }
            //System.out.println();
        }
    }

    @Test
    public void testModelCreation() throws Exception {
        loadTestData(getClass().getPackage().getName(),"Empty Tables.xml");
        em.getTransaction().begin();
        try {
            ReleaseEntity release = new ReleaseEntity();
            release.setName("The Bodyguard (Original Soundtrack Album)");
            release.setDate(DATE_FORMAT.parse("1992"));
            setLastChanged(release);
            releaseRepository.create(release);

            WorkEntity work = new WorkEntity();
            work.setName("I Will Always Love You");
            setLastChanged(work);
            workRepository.create(work);

            ContributorEntity contributorDollyParton = new ContributorEntity();
            contributorDollyParton.setType("composer");
            ArtistEntity artistDollyParton = new ArtistEntity();
            artistDollyParton.setName("Dolly Parton");
            setLastChanged(artistDollyParton);
            artistRepository.create(artistDollyParton);
            contributorDollyParton.setArtist(artistDollyParton);
            setLastChanged(contributorDollyParton);
            contributorRepository.create(contributorDollyParton);

            work.addContributor(contributorDollyParton);
            workRepository.create(work);

            RecordingEntity recording = new RecordingEntity();
            recording.getWorks().add(work);
            setLastChanged(recording);
            recordingRepository.create(recording);

            ContributorEntity contributorWhitneyHouston= new ContributorEntity();
            contributorWhitneyHouston.setType("artist");
            ArtistEntity artistWhitneyHouston= new ArtistEntity();
            artistWhitneyHouston.setName("Whitney Houston");
            setLastChanged(artistWhitneyHouston);
            artistRepository.create(artistWhitneyHouston);
            contributorWhitneyHouston.setArtist(artistWhitneyHouston);
            setLastChanged(contributorWhitneyHouston);
            contributorRepository.create(contributorWhitneyHouston);

            ContributorEntity contributorRickyMinor= new ContributorEntity();
            contributorRickyMinor.setType("conductor");
            ArtistEntity artistRickyMinor= new ArtistEntity();
            artistRickyMinor.setName("Ricky Minor");
            setLastChanged(artistRickyMinor);
            artistRepository.create(artistRickyMinor);
            contributorRickyMinor.setArtist(artistRickyMinor);
            setLastChanged(contributorRickyMinor);
            contributorRepository.create(contributorRickyMinor);

            for (ContributorEntity contributorEntity : Arrays.asList(contributorWhitneyHouston, contributorRickyMinor)) {
                recording.addContributor(contributorEntity);
            }
            recordingRepository.create(recording);

            TrackEntity track = new TrackEntity();
            track.setNumber(1);
            track.setRecording(recording);
            setLastChanged(track);
            PlayableElementEntity playableElement = new PlayableElementEntity();
            playableElement.setFormat("flc");
            playableElement.setSmdID("1000");
            playableElement.setUri("file:///music/TheBodyguard/track1.flac");
            track.getPlayableElements().add(playableElement);
            playableElement = new PlayableElementEntity();
            playableElement.setFormat("mp3");
            playableElement.setSmdID("1001");
            playableElement.setUri("file:///music/TheBodyguard/track1.mp3");
            track.getPlayableElements().add(playableElement);
            trackRepository.create(track);

            release.setTracks(Arrays.asList((Track)track));
            releaseRepository.create(release);
        }
        finally{
            if(!em.getTransaction().getRollbackOnly()) {
                em.getTransaction().commit();
            }
        }
        em.getTransaction().begin();
        Query query = em.createQuery("from ReleaseEntity where name=:name");
        query.setParameter("name","The Bodyguard (Original Soundtrack Album)");
        Release release = (Release) query.getSingleResult();
        assert(release != null);

        printRelease(release);

        assert ((ReleaseEntity)release).getLastUpdated() != null;
        assert ((ReleaseEntity)release).getLastUpdatedBy() != null;
        assert(release.getName().equals("The Bodyguard (Original Soundtrack Album)"));
        assert(DATE_FORMAT.format(release.getDate()).equals("1992"));
        assert(release.getTracks() != null);
        assert(release.getTracks().size() == 1);
        Track track = release.getTracks().iterator().next();
        assert(track.getNumber().equals(1));
        assert ((TrackEntity)track).getLastUpdated() != null;
        assert ((TrackEntity)track).getLastUpdatedBy() != null;
        assert (track.getPlayableElements() != null);
        assert (track.getPlayableElements().size() == 2);
        PlayableElement playableElement = track.getPlayableElements().iterator().next();
        assert playableElement.getSmdID().equals("1000") || playableElement.getSmdID().equals("1001");
        assert(track.getRecording() != null);
        assert(track.getRecording().getContributors() != null);
        assert(track.getRecording().getContributors().size()==2);
        assert ((RecordingEntity)track.getRecording()).getLastUpdated() != null;
        assert ((RecordingEntity)track.getRecording()).getLastUpdatedBy() != null;
        Iterator<Contributor> iterator = track.getRecording().getContributors().iterator();
        Contributor contributor1 = iterator.next();
        assert(contributor1.getType() != null);
        assert(contributor1.getArtist() != null);
        assert(contributor1.getType().equals("conductor") || contributor1.getType().equals("artist"));
        assert ((ContributorEntity)contributor1).getLastUpdated() != null;
        assert ((ContributorEntity)contributor1).getLastUpdatedBy() != null;
        if(contributor1.getType().equals("conductor")) {
            assert(contributor1.getArtist() != null);
            assert(contributor1.getArtist().getName().equals("Ricky Minor"));
            Contributor contributor2 = iterator.next();
            assert(contributor2.getType() != null);
            assert(contributor2.getType().equals("artist"));
            assert(contributor2.getArtist() != null);
            assert(contributor2.getArtist().getName().equals("Whitney Houston"));
            assert ((ContributorEntity)contributor2).getLastUpdated() != null;
            assert ((ContributorEntity)contributor2).getLastUpdatedBy() != null;
        }else {
            assert(contributor1.getArtist() != null);
            assert(contributor1.getArtist().getName().equals("Whitney Houston"));
            Contributor contributor2 = iterator.next();
            assert(contributor2.getType() != null);
            assert(contributor2.getType().equals("conductor"));
            assert(contributor2.getArtist() != null);
            assert(contributor2.getArtist().getName().equals("Ricky Minor"));
            assert ((ContributorEntity)contributor2).getLastUpdated() != null;
            assert ((ContributorEntity)contributor2).getLastUpdatedBy() != null;
        }
        em.getTransaction().commit();
    }

    @Test
    public void testModelPlayableElementUpdate() throws Exception {
        loadTestData(getClass().getPackage().getName(),"The Bodyguard.xml");
        em.getTransaction().begin();
        
        Query query = em.createQuery("from ReleaseEntity where name=:name");
        query.setParameter("name","The Bodyguard (Original Soundtrack Album)");
        Release release = (Release) query.getSingleResult();
        assert(release != null);
        
        TrackEntity track = (TrackEntity) release.getTracks().get(0);
        PlayableElementEntity playableElement = new PlayableElementEntity();
        playableElement.setFormat("mp3");
        playableElement.setSmdID("1000");
        playableElement.setUri("someservice://sometrackidentifier");
        setLastChanged(playableElement);
        playableElementRepository.create(playableElement);
        track.getPlayableElements().add(playableElement);
        trackRepository.merge(track);
        
        query = em.createQuery("from ReleaseEntity where name=:name");
        query.setParameter("name","The Bodyguard (Original Soundtrack Album)");
        release = (Release) query.getSingleResult();
        assert(release != null);
        track = (TrackEntity) release.getTracks().get(0);
        assert track.getPlayableElements().size()==2;

        boolean found = false;
        for (PlayableElement element : track.getPlayableElements()) {
            if(element.getSmdID().equals("1000")) {
                found = true;
            }
        }
        assert found;

        Collection<PlayableElementEntity> previousPlayableElements = playableElementRepository.findBySmdID("fd4fe48417dd941af53799619ecf1a40-10000000");
        assert previousPlayableElements.size()>0;
        playableElement = previousPlayableElements.iterator().next();
        playableElement.setFormat("mp3");
        playableElement.setUri("someservice://sometrackidentifier");
        setLastChanged(playableElement);

        query = em.createQuery("from ReleaseEntity where name=:name");
        query.setParameter("name","The Bodyguard (Original Soundtrack Album)");
        release = (Release) query.getSingleResult();
        assert(release != null);
        track = (TrackEntity) release.getTracks().get(0);
        assert track.getPlayableElements().size()==2;

        found = false;
        for (PlayableElement element : track.getPlayableElements()) {
            if(element.getSmdID().equals("fd4fe48417dd941af53799619ecf1a40-10000000")) {
                assert element.getUri().equals("someservice://sometrackidentifier");
                found = true;
            }
        }
        assert found;

        for (PlayableElement element : track.getPlayableElements()) {
            if(element.getSmdID().equals("1000")) {
                track.getPlayableElements().remove(element);
                playableElementRepository.remove((PlayableElementEntity) element);
                break;
            }
        }

        query = em.createQuery("from ReleaseEntity where name=:name");
        query.setParameter("name","The Bodyguard (Original Soundtrack Album)");
        release = (Release) query.getSingleResult();
        assert(release != null);
        track = (TrackEntity) release.getTracks().get(0);
        assert track.getPlayableElements().size()==1;

        em.getTransaction().commit();
    }
    
    @Test
    public void testModelRead() throws Exception {
        loadTestData(getClass().getPackage().getName(),"The Bodyguard.xml");
        em.getTransaction().begin();

        Query query = em.createQuery("from ReleaseEntity where name=:name");
        query.setParameter("name","The Bodyguard (Original Soundtrack Album)");
        Release release = (Release) query.getSingleResult();
        assert(release != null);

        printRelease(release);
        em.getTransaction().commit();
    }

    @Test
    public void testModelDeleteRelease() throws Exception {
        loadTestData(getClass().getPackage().getName(),"The Bodyguard.xml");
        SearchRelationPostProcessor searchRelationPostProcessor = new SearchRelationPostProcessor();
        searchRelationPostProcessor.init();
        searchRelationPostProcessor.execute(new ProcessingStatusCallback() {
            public void progress(String module, String currentDescription, Long currentNo, Long totalNo) {}
            public void failed(String module, String error) {}
            public void finished(String module) {}
            public void aborted(String module) {}
        });

        em.getTransaction().begin();
        Query query = em.createQuery("from ReleaseEntity where name=:name");
        query.setParameter("name","The Bodyguard (Original Soundtrack Album)");
        ReleaseEntity release = (ReleaseEntity) query.getSingleResult();
        assert(release != null);

        String releaseId = release.getId();
        assert 0 < em.createQuery("from TrackEntity where release_id=:release").setParameter("release",releaseId).getResultList().size();
        assert 0 < em.createQuery("from TrackEntity as t JOIN t.playableElements where t.release=:release").setParameter("release",release).getResultList().size();
        assert 0 < em.createQuery("from PlayableElementEntity").getResultList().size();
        assert 0 < em.createQuery("from ReleaseSearchRelationEntity where id=:release").setParameter("release",releaseId).getResultList().size();
        int recordings = em.createQuery("from RecordingEntity").getResultList().size();

        releaseRepository.remove(release);
        //em.flush();

        query = em.createQuery("from ReleaseEntity where name=:name");
        query.setParameter("name","The Bodyguard (Original Soundtrack Album)");
        try {
            release = (ReleaseEntity) query.getSingleResult();
        } catch (NoResultException e) {
            release = null;
        }
        assert(release == null);

        assert 0 == em.createQuery("from TrackEntity where release_id=:release").setParameter("release",releaseId).getResultList().size();
        assert 0 == em.createQuery("from PlayableElementEntity").getResultList().size();
        assert 0 == em.createQuery("from ReleaseSearchRelationEntity where id=:release").setParameter("release",releaseId).getResultList().size();
        assert recordings == em.createQuery("from RecordingEntity").getResultList().size();

        em.getTransaction().commit();
    }

    @Test
    public void testModelDeletePerson() throws Exception {
        loadTestData(getClass().getPackage().getName(),"The Bodyguard.xml");
        SearchRelationPostProcessor searchRelationPostProcessor = new SearchRelationPostProcessor();
        searchRelationPostProcessor.init();
        searchRelationPostProcessor.execute(new ProcessingStatusCallback() {
            public void progress(String module, String currentDescription, Long currentNo, Long totalNo) {}
            public void failed(String module, String error) {}
            public void finished(String module) {}
            public void aborted(String module) {}
        });

        em.getTransaction().begin();
        Query query = em.createQuery("from PersonEntity where name=:name");
        query.setParameter("name","Dolly Rebecca Parton");
        PersonEntity person = (PersonEntity) query.getSingleResult();
        assert(person != null);

        personRepository.remove(person);
        em.flush();

        query = em.createQuery("from PersonEntity where name=:name");
        query.setParameter("name","Dolly Rebecca Parton");
        try {
            person = (PersonEntity) query.getSingleResult();
        } catch (NoResultException e) {
            person = null;
        }
        assert(person == null);

        query = em.createQuery("from ArtistEntity where name=:name");
        query.setParameter("name","Dolly Parton");
        Artist artist = (Artist) query.getSingleResult();
        assert(artist != null);
        assert(artist.getPerson() == null);

        em.getTransaction().commit();
    }

    @Test
    public void testModelDeleteArtist() throws Exception {
        loadTestData(getClass().getPackage().getName(),"The Bodyguard.xml");
        SearchRelationPostProcessor searchRelationPostProcessor = new SearchRelationPostProcessor();
        searchRelationPostProcessor.init();
        searchRelationPostProcessor.execute(new ProcessingStatusCallback() {
            public void progress(String module, String currentDescription, Long currentNo, Long totalNo) {}
            public void failed(String module, String error) {}
            public void finished(String module) {}
            public void aborted(String module) {}
        });

        em.getTransaction().begin();
        Query query = em.createQuery("from ArtistEntity where name=:name");
        query.setParameter("name","Dolly Parton");
        Artist artist = (Artist) query.getSingleResult();
        assert(artist != null);

        artistRepository.remove((ArtistEntity)artist);
        em.flush();

        query = em.createQuery("from ArtistEntity where name=:name");
        query.setParameter("name","Dolly Parton");
        try {
            artist = (Artist) query.getSingleResult();
        } catch (NoResultException e) {
            artist = null;
        }
        assert(artist == null);

        query = em.createQuery("from PersonEntity where name=:name");
        query.setParameter("name","Dolly Rebecca Parton");
        Person person = (Person) query.getSingleResult();
        assert(person != null);
        em.getTransaction().commit();
    }

    @Test
    public void testModelDeleteTrackRecordingFailure() throws Exception {
        loadTestData(getClass().getPackage().getName(),"The Bodyguard.xml");
        SearchRelationPostProcessor searchRelationPostProcessor = new SearchRelationPostProcessor();
        searchRelationPostProcessor.init();
        searchRelationPostProcessor.execute(new ProcessingStatusCallback() {
            public void progress(String module, String currentDescription, Long currentNo, Long totalNo) {}
            public void failed(String module, String error) {}
            public void finished(String module) {}
            public void aborted(String module) {}
        });

        em.getTransaction().begin();
        Query query = em.createQuery("from ReleaseEntity where name=:name");
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
            recordingRepository.remove((RecordingEntity)recording);
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
        SearchRelationPostProcessor searchRelationPostProcessor = new SearchRelationPostProcessor();
        searchRelationPostProcessor.init();
        searchRelationPostProcessor.execute(new ProcessingStatusCallback() {
            public void progress(String module, String currentDescription, Long currentNo, Long totalNo) {}
            public void failed(String module, String error) {}
            public void finished(String module) {}
            public void aborted(String module) {}
        });

        em.getTransaction().begin();
        Query query = em.createQuery("from ReleaseEntity where name=:name");
        query.setParameter("name","The Bodyguard (Original Soundtrack Album)");
        Release release = (Release) query.getSingleResult();
        assert(release != null);

        int playableElements = em.createQuery("from PlayableElementEntity").getResultList().size();

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

        trackRepository.remove((TrackEntity)trackForRecording);
        em.flush();
        recordingRepository.remove((RecordingEntity)recording);
        em.flush();

        assert playableElements == em.createQuery("from PlayableElementEntity").getResultList().size()+1;

        query = em.createQuery("from ReleaseEntity where name=:name");
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
        SearchRelationPostProcessor searchRelationPostProcessor = new SearchRelationPostProcessor();
        searchRelationPostProcessor.init();
        searchRelationPostProcessor.execute(new ProcessingStatusCallback() {
            public void progress(String module, String currentDescription, Long currentNo, Long totalNo) {}
            public void failed(String module, String error) {}
            public void finished(String module) {}
            public void aborted(String module) {}
        });

        em.getTransaction().begin();
        Query query = em.createQuery("from ReleaseEntity where name=:name");
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
                work = recordingForWork.getWorks().iterator().next();
            }
        }
        assert trackForRecording != null;
        assert recordingForWork != null;
        assert work != null;

        boolean error = false;
        try {
            workRepository.remove((WorkEntity)work);
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
        SearchRelationPostProcessor searchRelationPostProcessor = new SearchRelationPostProcessor();
        searchRelationPostProcessor.init();
        searchRelationPostProcessor.execute(new ProcessingStatusCallback() {
            public void progress(String module, String currentDescription, Long currentNo, Long totalNo) {}
            public void failed(String module, String error) {}
            public void finished(String module) {}
            public void aborted(String module) {}
        });

        em.getTransaction().begin();
        Query query = em.createQuery("from ReleaseEntity where name=:name");
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
                work = recordingForWork.getWorks().iterator().next();
            }
        }
        assert trackForRecording != null;
        assert recordingForWork != null;
        assert work != null;

        trackRepository.remove((TrackEntity)trackForRecording);
        em.flush();
        recordingRepository.remove((RecordingEntity)recordingForWork);
        em.flush();
        workRepository.remove((WorkEntity)work);
        em.flush();

        query = em.createQuery("from ReleaseEntity where name=:name");
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
                work = recordingForWork.getWorks().iterator().next();
            }
        }
        assert recordingForWork == null;
        assert trackForRecording == null;
        assert work == null;
        em.getTransaction().commit();
    }

    private void setLastChanged(AbstractSMDIdentityEntity entity) {
        entity.setLastUpdated(new Date());
        entity.setLastUpdatedBy("JUnit");
    }
}
