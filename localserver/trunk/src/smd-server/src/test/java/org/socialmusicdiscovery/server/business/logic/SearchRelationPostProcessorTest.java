package org.socialmusicdiscovery.server.business.logic;

import org.socialmusicdiscovery.server.api.mediaimport.ProcessingStatusCallback;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.model.search.*;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.*;

import javax.persistence.Query;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;

public class SearchRelationPostProcessorTest extends BaseTestCase {
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
    public void testSearchRelations() throws Exception {
        loadTestData("org.socialmusicdiscovery.server.business.model", "The Bodyguard.xml");
        new SearchRelationPostProcessor().execute(new ProcessingStatusCallback() {
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

        Query query = em.createQuery("from Release where name=:name");
        query.setParameter("name", "The Bodyguard (Original Soundtrack Album)");
        Release release = (Release) query.getSingleResult();
        assert (release != null);

        Recording recording = release.getTracks().get(0).getRecording();
        Collection<Contributor> recordingContributors = recording.getContributors();
        Work work = recording.getWork();
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

        // Verify release search relations
        Collection<ReleaseSearchRelation> releaseSearchRelations = release.getSearchRelations();
        // Recording + Work + Artists + Persons + Contributors
        int found = 1 + 1 + artists.size() + persons.size() + recordingContributors.size() + workContributors.size();
        for (ReleaseSearchRelation searchRelation : releaseSearchRelations) {
            if (searchRelation.getReference().equals(recording.getId())) {
                found--;
                continue;
            } else if (searchRelation.getReference().equals(work.getId())) {
                found--;
                continue;
            } else {
                for (Artist artist : artists) {
                    if (searchRelation.getReference().equals(artist.getId()) && searchRelation.getReferenceType().equals(Artist.class.getName())) {
                        found--;
                        continue;
                    }
                }
                for (Person person : persons) {
                    if (searchRelation.getReference().equals(person.getId())) {
                        found--;
                        continue;
                    }
                }
                for (Contributor contributor : workContributors) {
                    if (searchRelation.getReference().equals(contributor.getArtist().getId()) &&
                            searchRelation.getReferenceType().equals(Artist.class.getName() + "#" + contributor.getType())) {
                        found--;
                        continue;
                    }
                }
                for (Contributor contributor : recordingContributors) {
                    if (searchRelation.getReference().equals(contributor.getArtist().getId()) &&
                            searchRelation.getReferenceType().equals(Artist.class.getName() + "#" + contributor.getType())) {
                        found--;
                        continue;
                    }
                }
            }
        }
        assert found == 0;

        // Verify recording search relations
        Collection<RecordingSearchRelation> recordingSearchRelations = recording.getSearchRelations();
        // Release + Artists + Persons + Contributors
        found = 1 + artists.size() + persons.size() + workContributors.size() + recordingContributors.size();
        for (RecordingSearchRelation searchRelation : recordingSearchRelations) {
            if (searchRelation.getReference().equals(release.getId())) {
                found--;
                continue;
            } else {
                for (Artist artist : artists) {
                    if (searchRelation.getReference().equals(artist.getId()) && searchRelation.getReferenceType().equals(Artist.class.getName())) {
                        found--;
                        continue;
                    }
                }
                for (Person person : persons) {
                    if (searchRelation.getReference().equals(person.getId())) {
                        found--;
                        continue;
                    }
                }
                for (Contributor contributor : workContributors) {
                    if (searchRelation.getReference().equals(contributor.getArtist().getId()) &&
                            searchRelation.getReferenceType().equals(Artist.class.getName() + "#" + contributor.getType())) {
                        found--;
                        continue;
                    }
                }
                for (Contributor contributor : recordingContributors) {
                    if (searchRelation.getReference().equals(contributor.getArtist().getId()) &&
                            searchRelation.getReferenceType().equals(Artist.class.getName() + "#" + contributor.getType())) {
                        found--;
                        continue;
                    }
                }
            }
        }
        assert found == 0;


        // Verify work search relations
        Collection<WorkSearchRelation> workSearchRelations = work.getSearchRelations();
        // Release + Recording + Artists + Persons + Contributors
        found = 1 + 1 + artists.size() + persons.size() + workContributors.size() + recordingContributors.size();
        for (WorkSearchRelation searchRelation : workSearchRelations) {
            if (searchRelation.getReference().equals(release.getId())) {
                found--;
                continue;
            } else if (searchRelation.getReference().equals(recording.getId())) {
                found--;
                continue;
            } else {
                for (Artist artist : artists) {
                    if (searchRelation.getReference().equals(artist.getId()) && searchRelation.getReferenceType().equals(Artist.class.getName())) {
                        found--;
                        continue;
                    }
                }
                for (Person person : persons) {
                    if (searchRelation.getReference().equals(person.getId())) {
                        found--;
                        continue;
                    }
                }
                for (Contributor contributor : workContributors) {
                    if (searchRelation.getReference().equals(contributor.getArtist().getId()) &&
                            searchRelation.getReferenceType().equals(Artist.class.getName() + "#" + contributor.getType())) {
                        found--;
                        continue;
                    }
                }
                for (Contributor contributor : recordingContributors) {
                    if (searchRelation.getReference().equals(contributor.getArtist().getId()) &&
                            searchRelation.getReferenceType().equals(Artist.class.getName() + "#" + contributor.getType())) {
                        found--;
                        continue;
                    }
                }
            }
        }
        assert found == 0;

        // Verify artist search relations
        for (Artist artist : artists) {
            Collection<ArtistSearchRelation> artistSearchRelations = artist.getSearchRelations();
            // Release + Recording + Work + Contributor
            found = 1 + 1 + 1 + 1;
            for (ArtistSearchRelation searchRelation : artistSearchRelations) {
                if (searchRelation.getReference().equals(release.getId()) && searchRelation.getReferenceType().equals(Release.class.getName())) {
                    found--;
                } else if (searchRelation.getReference().equals(recording.getId()) && searchRelation.getReferenceType().equals(Recording.class.getName())) {
                    found--;
                } else if (searchRelation.getReference().equals(work.getId()) && searchRelation.getReferenceType().equals(Work.class.getName())) {
                    found--;
                } else {
                    for (Contributor contributor : workContributors) {
                        if (searchRelation.getReference().equals(work.getId()) &&
                                searchRelation.getReferenceType().equals(Work.class.getName() + "#" + contributor.getType())) {
                            found--;
                            break;
                        }
                    }
                    for (Contributor contributor : recordingContributors) {
                        if (searchRelation.getReference().equals(recording.getId()) &&
                                searchRelation.getReferenceType().equals(Recording.class.getName() + "#" + contributor.getType())) {
                            found--;
                            break;
                        }
                    }
                }
            }
            assert found == 0;
        }

        // Verify person search relations
        for (Artist artist : artists) {
            Person person = artist.getPerson();
            Collection<PersonSearchRelation> personSearchRelations = person.getSearchRelations();
            // Release + Recording + Work
            found = 1 + 1 + 1;
            for (PersonSearchRelation searchRelation : personSearchRelations) {
                if (searchRelation.getReference().equals(release.getId()) && searchRelation.getReferenceType().equals(Release.class.getName())) {
                    found--;
                } else if (searchRelation.getReference().equals(recording.getId()) && searchRelation.getReferenceType().equals(Recording.class.getName())) {
                    found--;
                } else if (searchRelation.getReference().equals(work.getId()) && searchRelation.getReferenceType().equals(Work.class.getName())) {
                    found--;
                } else if (searchRelation.getReference().equals(artist.getId()) && searchRelation.getReferenceType().equals(Artist.class.getName())) {
                    found--;
                }
            }
            assert found == 0;
        }

        em.getTransaction().commit();
    }

}
