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

import com.google.inject.Inject;

import org.socialmusicdiscovery.server.api.mediaimport.InitializationFailedException;
import org.socialmusicdiscovery.server.api.mediaimport.ProcessingStatusCallback;
import org.socialmusicdiscovery.server.business.logic.SearchRelationPostProcessor;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.repository.core.*;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

public class RecordingRefreshTest extends BaseTestCase {
    @Inject
    ContributorRepository contributorRepository;

    @Inject
    WorkRepository workRepository;

    @Inject
    ArtistRepository artistRepository;

    @Inject
    ReleaseRepository releaseRepository;

    @Inject
    TrackRepository trackRepository;

    @Inject
    LabelRepository labelRepository;

    @Inject
    RecordingRepository recordingRepository;

    @BeforeMethod
    public void loadData() {
        loadTestData("org.socialmusicdiscovery.server.business.model", "The Bodyguard.xml");
        SearchRelationPostProcessor searchRelationPostProcessor = new SearchRelationPostProcessor();
        try {
			searchRelationPostProcessor.init(null);
		} catch (InitializationFailedException e) {
			// TODO Better exception handling 
			//      This was added after the throw clause in ProcessingModule interface was added)
			e.printStackTrace();
		}
        searchRelationPostProcessor.execute(new ProcessingStatusCallback() {
            public void progress(String module, String currentDescription, Long currentNo, Long totalNo) {
            }

            public void failed(String module, String error) {
            }

            public void finished(String module) {
            }

            public void aborted(String module) {
            }
        });
    }

    @Test
    public void testReleaseArtistAdditionAndRemoval() {
        em.getTransaction().begin();
        ReleaseEntity release = releaseRepository.findById("20000");
        Artist artist = artistRepository.findById("6");
        RecordingEntity recording = recordingRepository.findById("40001");

        Map<String, Integer> originalCounts = getSearchRelationCounts(recording);
        ContributorEntity newContributor = new ContributorEntity(artist, "albumartist");
        newContributor.setRelease(release);
        newContributor.setLastUpdated(new Date());
        newContributor.setLastUpdatedBy("JUnit");
        contributorRepository.create(newContributor);

        releaseRepository.refresh(release);

        Map<String, Integer> newCounts = getSearchRelationCounts(recording);
        compareCounts(originalCounts, newCounts, Arrays.asList(Artist.class.getName()));
        assert newCounts.get(Artist.class.getName()).equals(originalCounts.get(Artist.class.getName()) + 1);

        contributorRepository.remove(newContributor);

        // We don't need to call releaseRepository.refresh(release); because ContributorRepository.remove is responsible to refresh automatically
        newCounts = getSearchRelationCounts(recording);
        compareCounts(originalCounts, newCounts, null);
        em.getTransaction().rollback();
    }

    @Test
    public void testReleaseArtistAdditionToBeIgnored() {
        em.getTransaction().begin();
        ReleaseEntity release = releaseRepository.findById("20000");
        Artist artist = artistRepository.findById("1");
        RecordingEntity recording = recordingRepository.findById("40001");

        Map<String, Integer> originalCounts = getSearchRelationCounts(recording);
        ContributorEntity newContributor = new ContributorEntity(artist, Contributor.PERFORMER);
        newContributor.setRelease(release);
        newContributor.setLastUpdated(new Date());
        newContributor.setLastUpdatedBy("JUnit");
        contributorRepository.create(newContributor);

        releaseRepository.refresh(release);

        Map<String, Integer> newCounts = getSearchRelationCounts(recording);
        compareCounts(originalCounts, newCounts, null);
        em.getTransaction().rollback();
    }

    @Test
    public void testTrackReleaseRemovalAndAddition() {
        em.getTransaction().begin();
        TrackEntity track = trackRepository.findById("50001");
        RecordingEntity recording = recordingRepository.findById("40001");

        Map<String, Integer> originalCounts = getSearchRelationCounts(recording);
        track.setRelease(null);

        trackRepository.refresh(track);

        Map<String, Integer> newCounts = getSearchRelationCounts(recording);
        compareCounts(originalCounts, newCounts, Arrays.asList(Release.class.getName()));
        assert newCounts.get(Release.class.getName()).equals(originalCounts.get(Release.class.getName()) - 1);

        ReleaseEntity release = releaseRepository.findById("20000");
        track.setRelease(release);

        trackRepository.refresh(track);

        newCounts = getSearchRelationCounts(recording);
        compareCounts(originalCounts, newCounts, null);
        em.getTransaction().rollback();
    }

    @Test
    public void testTrackRecordingRemovalAndAddition() {
        em.getTransaction().begin();
        TrackEntity track = trackRepository.findById("50001");
        RecordingEntity recording = recordingRepository.findById("40001");
        RecordingEntity otherRecording = recordingRepository.findById("40005");

        Map<String, Integer> originalCounts1 = getSearchRelationCounts(recording);
        Map<String, Integer> originalCounts2 = getSearchRelationCounts(otherRecording);
        track.setRecording(otherRecording);

        trackRepository.refresh(track);
        recordingRepository.refresh(recording);

        Map<String, Integer> newCounts1 = getSearchRelationCounts(recording);
        compareCounts(originalCounts1, newCounts1, Arrays.asList(Track.class.getName(), Release.class.getName()));
        assert newCounts1.get(Track.class.getName()).equals(0);
        assert newCounts1.get(Release.class.getName()).equals(0);

        Map<String, Integer> newCounts2 = getSearchRelationCounts(otherRecording);
        compareCounts(originalCounts2, newCounts2, Arrays.asList(Track.class.getName()));
        assert newCounts2.get(Track.class.getName()).equals(originalCounts2.get(Track.class.getName()) + 1);

        track.setRecording(recording);

        trackRepository.refresh(track);
        recordingRepository.refresh(otherRecording);

        newCounts1 = getSearchRelationCounts(recording);
        newCounts2 = getSearchRelationCounts(otherRecording);
        compareCounts(originalCounts1, newCounts1, null);
        compareCounts(originalCounts2, newCounts2, null);
        em.getTransaction().rollback();
    }

    /**
     * Not activated because labels currently doesn't exist as search relations
     */
    public void testReleaseLabelRemovalAndAddition() {
        em.getTransaction().begin();
        ReleaseEntity release = releaseRepository.findById("20000");
        RecordingEntity recording = recordingRepository.findById("40001");

        Map<String, Integer> originalCounts = getSearchRelationCounts(recording);
        release.setLabel(null);

        releaseRepository.refresh(release);

        Map<String, Integer> newCounts = getSearchRelationCounts(recording);
        compareCounts(originalCounts, newCounts, Arrays.asList(Label.class.getName()));
        assert newCounts.get(Label.class.getName()).equals(originalCounts.get(Label.class.getName()) - 1);

        LabelEntity label = labelRepository.findById("10000");
        release.setLabel(label);

        releaseRepository.refresh(release);

        newCounts = getSearchRelationCounts(recording);
        compareCounts(originalCounts, newCounts, null);
        em.getTransaction().rollback();
    }

    @Test
    public void testWorkArtistRemoval() {
        em.getTransaction().begin();
        ContributorEntity contributor = contributorRepository.findById("30001001");
        WorkEntity work = (WorkEntity) contributor.getWork();
        RecordingEntity recording = recordingRepository.findByWorkWithRelations(work.getId(), null, null).iterator().next();

        Map<String, Integer> originalCounts = getSearchRelationCounts(recording);
        work.removeContributor(contributor);
        workRepository.refresh(work);
        Map<String, Integer> newCounts = getSearchRelationCounts(recording);
        compareCounts(originalCounts, newCounts, Arrays.asList(Artist.class.getName()));
        assert newCounts.get(Artist.class.getName()).equals(originalCounts.get(Artist.class.getName()) - 1);
        em.getTransaction().rollback();
    }

    @Test
    public void testWorkArtistAddition() {
        em.getTransaction().begin();
        WorkEntity work = workRepository.findById("30001");
        RecordingEntity recording = recordingRepository.findById("40001");
        Artist artist = artistRepository.findById("6");

        Map<String, Integer> originalCounts = getSearchRelationCounts(recording);
        ContributorEntity newContributor = new ContributorEntity(artist, Contributor.COMPOSER);
        newContributor.setWork(work);
        newContributor.setLastUpdated(new Date());
        newContributor.setLastUpdatedBy("JUnit");
        contributorRepository.create(newContributor);

        workRepository.refresh(work);
        Map<String, Integer> newCounts = getSearchRelationCounts(recording);
        compareCounts(originalCounts, newCounts, Arrays.asList(Artist.class.getName()));
        assert newCounts.get(Artist.class.getName()).equals(originalCounts.get(Artist.class.getName()) + 1);
        em.getTransaction().rollback();
    }

    @Test
    public void testRecordingArtistRemoval() {
        em.getTransaction().begin();
        ContributorEntity contributor = contributorRepository.findById("40001003");
        RecordingEntity recording = (RecordingEntity) contributor.getRecording();

        Map<String, Integer> originalCounts = getSearchRelationCounts(recording);
        recording.removeContributor(contributor);
        recordingRepository.refresh(recording);
        Map<String, Integer> newCounts = getSearchRelationCounts(recording);
        compareCounts(originalCounts, newCounts, Arrays.asList(Artist.class.getName()));
        assert newCounts.get(Artist.class.getName()).equals(originalCounts.get(Artist.class.getName()) - 1);
        em.getTransaction().rollback();
    }

    @Test
    public void testRecordingArtistAddition() {
        em.getTransaction().begin();
        RecordingEntity recording = recordingRepository.findById("40001");
        Artist artist = artistRepository.findById("1");

        Map<String, Integer> originalCounts = getSearchRelationCounts(recording);
        ContributorEntity newContributor = new ContributorEntity(artist, Contributor.PERFORMER);
        newContributor.setRecording(recording);
        newContributor.setLastUpdated(new Date());
        newContributor.setLastUpdatedBy("JUnit");
        contributorRepository.create(newContributor);

        recordingRepository.refresh(recording);
        Map<String, Integer> newCounts = getSearchRelationCounts(recording);
        compareCounts(originalCounts, newCounts, Arrays.asList(Artist.class.getName()));
        assert newCounts.get(Artist.class.getName()).equals(originalCounts.get(Artist.class.getName()) + 1);
        em.getTransaction().rollback();
    }

    private Map<String, Integer> getSearchRelationCounts(RecordingEntity recording) {
        Map<String, Integer> counts = new HashMap<String, Integer>();
        counts.put(Artist.class.getName(), recording.getLabelSearchRelations().size());
        counts.put(Release.class.getName(), recording.getReleaseSearchRelations().size());
        counts.put(Track.class.getName(), recording.getTrackSearchRelations().size());
        counts.put(Work.class.getName(), recording.getWorkSearchRelations().size());
        counts.put(Artist.class.getName(), recording.getArtistSearchRelations().size());
        return counts;
    }

    private void compareCounts(Map<String, Integer> previousCounts, Map<String, Integer> newCounts, List<String> exclude) {
        for (Map.Entry<String, Integer> entry : previousCounts.entrySet()) {
            if (exclude == null || !exclude.contains(entry.getKey())) {
                Assert.assertEquals(newCounts.get(entry.getKey()), entry.getValue(), "Mismatch in " + entry.getKey() + " relations");
            }
        }
    }
}
