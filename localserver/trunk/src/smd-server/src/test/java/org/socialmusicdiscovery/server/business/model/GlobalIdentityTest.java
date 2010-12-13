package org.socialmusicdiscovery.server.business.model;

import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.Track;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

public class GlobalIdentityTest extends BaseTestCase {
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
        System.out.println("Executing "+getClass().getSimpleName()+"."+m.getName()+"...");
        em.clear();
    }

    @Test
    public void testModelGlobalIdentity() throws Exception {
        loadTestData(getClass().getPackage().getName(), "The Bodyguard.xml");
        em.getTransaction().begin();
        try {
            Artist artist = artistRepository.findByName("Whitney Houston").iterator().next();
            Release release = releaseRepository.findByName("The Bodyguard (Original Soundtrack Album)").iterator().next();

            GlobalIdentityEntity identity = new GlobalIdentityEntity();
            identity.setSource(GlobalIdentity.SOURCE_MUSICBRAINZ);
            identity.setEntityId(artist.getId());
            identity.setUri("0307edfc-437c-4b48-8700-80680e66a228");
            globalIdentityRepository.create(identity);

            identity = new GlobalIdentityEntity();
            identity.setSource(GlobalIdentity.SOURCE_MUSICBRAINZ);
            identity.setEntityId(release.getId());
            identity.setUri("11cafb9e-5fbc-49c7-b920-4ff754e03e93");
            globalIdentityRepository.create(identity);

            int identifiedTracks = 0;
            for (Track track : release.getTracks()) {
                identity = new GlobalIdentityEntity();
                identity.setSource(GlobalIdentity.SOURCE_MUSICBRAINZ);
                identity.setEntityId(track.getId());
                if (track.getNumber().equals(1)) {
                    identity.setUri("86cf33ac-5b7b-401b-9188-608bb2752063");
                } else if (track.getNumber().equals(5)) {
                    identity.setUri("bdd8624a-d0ac-480f-8fe5-253bd99b7d3f");
                } else if (track.getNumber().equals(9)) {
                    identity.setUri("f2ba4ef6-7017-4b93-9176-c079ed0a97e9");
                } else if (track.getNumber().equals(13)) {
                    identity.setUri("c3af5bb7-5711-495e-8fae-af8d730497cd");
                }
                if (identity.getUri() != null) {
                    identifiedTracks++;
                    globalIdentityRepository.create(identity);
                }
            }
            assert identifiedTracks == 4;

            identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_MUSICBRAINZ, artist);
            assert identity != null;
            assert identity.getUri().equals("0307edfc-437c-4b48-8700-80680e66a228");

            identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_MUSICBRAINZ, release);
            assert identity != null;
            assert identity.getUri().equals("11cafb9e-5fbc-49c7-b920-4ff754e03e93");

            for (Track track : release.getTracks()) {
                if (track.getNumber().equals(1)) {
                    identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_MUSICBRAINZ, track);
                    assert identity != null;
                    assert identity.getUri().equals("86cf33ac-5b7b-401b-9188-608bb2752063");
                    identifiedTracks--;
                } else if (track.getNumber().equals(5)) {
                    identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_MUSICBRAINZ, track);
                    assert identity != null;
                    assert identity.getUri().equals("bdd8624a-d0ac-480f-8fe5-253bd99b7d3f");
                    identifiedTracks--;
                } else if (track.getNumber().equals(9)) {
                    identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_MUSICBRAINZ, track);
                    assert identity != null;
                    assert identity.getUri().equals("f2ba4ef6-7017-4b93-9176-c079ed0a97e9");
                    identifiedTracks--;
                } else if (track.getNumber().equals(13)) {
                    identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_MUSICBRAINZ, track);
                    assert identity != null;
                    assert identity.getUri().equals("c3af5bb7-5711-495e-8fae-af8d730497cd");
                    identifiedTracks--;
                }
            }

            assert identifiedTracks == 0;
        } finally {
            em.getTransaction().commit();
        }
    }
}
