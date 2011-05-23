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

package org.socialmusicdiscovery.server.business.logic;

import org.socialmusicdiscovery.server.api.mediaimport.ProcessingStatusCallback;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationEntity;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.model.search.PersonSearchRelationEntity;
import org.socialmusicdiscovery.server.business.model.search.ReleaseSearchRelationEntity;
import org.socialmusicdiscovery.server.business.model.search.SearchRelationEntity;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.Test;

import javax.persistence.Query;
import java.util.Collection;
import java.util.HashSet;

public class SearchRelationPostProcessorTest extends BaseTestCase {
    @Test
    public void testSearchRelations() throws Exception {
        loadTestData("org.socialmusicdiscovery.server.business.model", "The Bodyguard.xml");
        SearchRelationPostProcessor searchRelationPostProcessor = new SearchRelationPostProcessor();
        searchRelationPostProcessor.init();
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

        em.getTransaction().begin();

        Query query = em.createQuery("from ReleaseEntity where name=:name");
        query.setParameter("name", "The Bodyguard (Original Soundtrack Album)");
        ReleaseEntity release = (ReleaseEntity) query.getSingleResult();
        assert (release != null);

        Track track = release.getTracks().get(0);
        Recording recording = track.getRecording();
        Collection<Contributor> recordingContributors = recording.getContributors();
        Work work = recording.getWorks().iterator().next();
        Collection<Contributor> workContributors = work.getContributors();
        Collection<Artist> artists = new HashSet<Artist>();
        Collection<Person> persons = new HashSet<Person>();
        for (Contributor contributor : recordingContributors) {
            artists.add(contributor.getArtist());
            if (contributor.getArtist().getPerson() != null) {
                persons.add(contributor.getArtist().getPerson());
            }
        }
        for (Contributor contributor : workContributors) {
            artists.add(contributor.getArtist());
            if (contributor.getArtist().getPerson() != null) {
                persons.add(contributor.getArtist().getPerson());
            }
        }
        Collection<ClassificationEntity> releaseClassifications = classificationRepository.findByReference(release.getId());
        Collection<ClassificationEntity> recordingClassifications = classificationRepository.findByReference(recording.getId());
        Collection<ClassificationEntity> workClassifications = classificationRepository.findByReference(work.getId());
        Collection<ClassificationEntity> aggregatedClassifications = new HashSet<ClassificationEntity>(releaseClassifications);
        aggregatedClassifications.addAll(recordingClassifications);
        aggregatedClassifications.addAll(workClassifications);

        // Verify release search relations
        Collection<ReleaseSearchRelationEntity> releaseSearchRelations = release.getSearchRelations();
        // Recording + Track + Work + Artists + Contributors
        int found = 1 + 1 + 1+ recordingContributors.size() + workContributors.size() + aggregatedClassifications.size();
        for (ReleaseSearchRelationEntity searchRelation : releaseSearchRelations) {
            if (searchRelation.getReference().equals(recording.getId())) {
                found--;
            } else if (searchRelation.getReference().equals(work.getId())) {
                found--;
            } else if (searchRelation.getReference().equals(track.getId())) {
                found--;
            }else if (searchRelation.getReferenceType().equals(SMDIdentityReferenceEntity.typeForClass(ArtistEntity.class)) && searchRelation.getType().equals("")) {
                assert false;
            } else {
                for (Contributor contributor : workContributors) {
                    if (searchRelation.getReference().equals(contributor.getArtist().getId()) &&
                            searchRelation.getReferenceType().equals(SMDIdentityReferenceEntity.typeForClass(ArtistEntity.class)) &&
                            searchRelation.getType().equals(contributor.getType())) {
                        found--;
                    }
                }
                for (Contributor contributor : recordingContributors) {
                    if (searchRelation.getReference().equals(contributor.getArtist().getId()) &&
                            searchRelation.getReferenceType().equals(SMDIdentityReferenceEntity.typeForClass(ArtistEntity.class)) &&
                            searchRelation.getType().equals(contributor.getType())) {
                        found--;
                    }
                }
                for (ClassificationEntity classification : aggregatedClassifications) {
                    if (searchRelation.getReference().equals(classification.getId()) &&
                            searchRelation.getType().equals(classification.getType())) {
                        found--;
                    }
                }
            }
        }
        assert found == 0;

        // Verify recording search relations
        Collection<SearchRelationEntity> recordingSearchRelations = ((RecordingEntity) recording).getSearchRelations();
        // Release + Track + Artists + Contributors
        found = 1 + 1 + workContributors.size() + recordingContributors.size()+recordingClassifications.size();
        for (SearchRelationEntity searchRelation : recordingSearchRelations) {
            if (searchRelation.getReference().equals(release.getId())) {
                found--;
            } else if (searchRelation.getReference().equals(track.getId())) {
                found--;
            }else if (searchRelation.getReferenceType().equals(SMDIdentityReferenceEntity.typeForClass(ArtistEntity.class)) && searchRelation.getType().equals("")) {
                assert false;
            } else {
                for (Contributor contributor : workContributors) {
                    if (searchRelation.getReference().equals(contributor.getArtist().getId()) &&
                            searchRelation.getReferenceType().equals(SMDIdentityReferenceEntity.typeForClass(ArtistEntity.class)) &&
                            searchRelation.getType().equals(contributor.getType())) {
                        found--;
                    }
                }
                for (Contributor contributor : recordingContributors) {
                    if (searchRelation.getReference().equals(contributor.getArtist().getId()) &&
                            searchRelation.getReferenceType().equals(SMDIdentityReferenceEntity.typeForClass(ArtistEntity.class)) &&
                            searchRelation.getType().equals(contributor.getType())) {
                        found--;
                    }
                }
                for (ClassificationEntity classification : aggregatedClassifications) {
                    if (searchRelation.getReference().equals(classification.getId()) &&
                            searchRelation.getType().equals(classification.getType())) {
                        found--;
                    }
                }
            }
        }
        assert found == 0;


        // Verify person search relations
        for (Artist artist : artists) {
            Person person = artist.getPerson();
            Collection<PersonSearchRelationEntity> personSearchRelations = ((PersonEntity) person).getSearchRelations();
            // Contributor
            found = 1;
            if(artist.getName().equals("Whitney Houston")) {
                // Add reference to self because combining performer/composer role on other works
                found++;
            }
            for (PersonSearchRelationEntity searchRelation : personSearchRelations) {
                if (searchRelation.getReference().equals(release.getId()) && searchRelation.getReferenceType().equals(SMDIdentityReferenceEntity.typeForClass(ReleaseEntity.class))) {
                    found--;
                } else if (searchRelation.getReference().equals(recording.getId()) && searchRelation.getReferenceType().equals(SMDIdentityReferenceEntity.typeForClass(RecordingEntity.class))) {
                    found--;
                } else if (searchRelation.getReference().equals(track.getId()) && searchRelation.getReferenceType().equals(SMDIdentityReferenceEntity.typeForClass(TrackEntity.class))) {
                    found--;
                } else if (searchRelation.getReference().equals(work.getId()) && searchRelation.getReferenceType().equals(SMDIdentityReferenceEntity.typeForClass(WorkEntity.class))) {
                    found--;
                } else if (searchRelation.getReference().equals(artist.getId()) && searchRelation.getReferenceType().equals(SMDIdentityReferenceEntity.typeForClass(ArtistEntity.class)) && searchRelation.getType().equals("")) {
                    assert false;
                } else if (searchRelation.getReference().equals(artist.getId()) && searchRelation.getReferenceType().equals(SMDIdentityReferenceEntity.typeForClass(ArtistEntity.class)) && !searchRelation.getType().equals("")) {
                    found--;
                }
            }
            assert found == 0;
        }

        em.getTransaction().commit();
    }

}
