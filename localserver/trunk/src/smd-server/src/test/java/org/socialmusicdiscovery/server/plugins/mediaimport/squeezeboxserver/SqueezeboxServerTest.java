package org.socialmusicdiscovery.server.plugins.mediaimport.squeezeboxserver;

import org.socialmusicdiscovery.server.business.model.GlobalIdentity;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.ReleaseEntity;
import org.socialmusicdiscovery.server.business.model.core.Track;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;

public class SqueezeboxServerTest extends BaseTestCase {
    SqueezeboxServer squeezeboxServer;

    @BeforeTest
    public void setUp() {
        super.setUp();
        squeezeboxServer = new SqueezeboxServer();
    }

    @AfterTest
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testBodyguard() throws Exception {
        loadTestData("org.socialmusicdiscovery.server.business.model", "Empty Tables.xml");
        em.getTransaction().begin();
        try {
            TrackData trackData = new TrackData();
            trackData.setFile("/music/The Bodyguard/I Will Always Love you.flac");
            trackData.setFormat("flc");
            trackData.setSmdID("00000000000000000000000000000001-00000000-00000000");
            trackData.setUrl("file://" + trackData.getFile());
            trackData.setTags(Arrays.asList(
                    new TagData(TagData.ALBUM, "The Bodyguard (Original Soundtrack Album)"),
                    new TagData(TagData.MUSICBRAINZ_ALBUM_ID, "11cafb9e-5fbc-49c7-b920-4ff754e03e93"),
                    new TagData(TagData.TITLE, "I Will Always Love You"),
                    new TagData(TagData.MUSICBRAINZ_TRACK_ID, "86cf33ac-5b7b-401b-9188-608bb2752063"),
                    new TagData(TagData.YEAR, "1992"),
                    new TagData(TagData.TRACKNUM, "1"),
                    new TagData(TagData.ALBUMARTIST, "Whitney Houston"),
                    new TagData(TagData.MUSICBRAINZ_ALBUMARTIST_ID, "0307edfc-437c-4b48-8700-80680e66a228"),
                    new TagData(TagData.ARTIST, "Whitney Houston", "Houston, Whitney"),
                    new TagData(TagData.MUSICBRAINZ_ARTIST_ID, "0307edfc-437c-4b48-8700-80680e66a228"),
                    new TagData(TagData.GENRE, "Soundtrack"),
                    new TagData(TagData.CONDUCTOR, "Ricky Minor"),
                    new TagData(TagData.COMPOSER, "Dolly Parton")
            ));
            squeezeboxServer.importNewPlayableElement(trackData);

            trackData = new TrackData();
            trackData.setFile("/music/The Bodyguard/Queen Of The Night.flac");
            trackData.setFormat("flc");
            trackData.setSmdID("00000000000000000000000000000005-00000000-00000000");
            trackData.setUrl("file://" + trackData.getFile());
            trackData.setTags(Arrays.asList(
                    new TagData(TagData.ALBUM, "The Bodyguard (Original Soundtrack Album)"),
                    new TagData(TagData.MUSICBRAINZ_ALBUM_ID, "11cafb9e-5fbc-49c7-b920-4ff754e03e93"),
                    new TagData(TagData.TITLE, "Queen Of The Night"),
                    new TagData(TagData.MUSICBRAINZ_TRACK_ID, "bdd8624a-d0ac-480f-8fe5-253bd99b7d3f"),
                    new TagData(TagData.YEAR, "1992"),
                    new TagData(TagData.TRACKNUM, "5"),
                    new TagData(TagData.ALBUMARTIST, "Whitney Houston"),
                    new TagData(TagData.MUSICBRAINZ_ALBUMARTIST_ID, "0307edfc-437c-4b48-8700-80680e66a228"),
                    new TagData(TagData.ARTIST, "Whitney Houston", "Houston, Whitney"),
                    new TagData(TagData.MUSICBRAINZ_ARTIST_ID, "0307edfc-437c-4b48-8700-80680e66a228"),
                    new TagData(TagData.GENRE, "Soundtrack"),
                    new TagData(TagData.COMPOSER, "Babyface"),
                    new TagData(TagData.COMPOSER, "Daryl Simmons"),
                    new TagData(TagData.COMPOSER, "L.A. Reid"),
                    new TagData(TagData.COMPOSER, "Whitney Houston")
            ));
            squeezeboxServer.importNewPlayableElement(trackData);

            trackData = new TrackData();
            trackData.setFile("/music/The Bodyguard/It's Gonna Be A Lovely Day.flac");
            trackData.setFormat("flc");
            trackData.setSmdID("00000000000000000000000000000009-00000000-00000000");
            trackData.setUrl("file://" + trackData.getFile());
            trackData.setTags(Arrays.asList(
                    new TagData(TagData.ALBUM, "The Bodyguard (Original Soundtrack Album)"),
                    new TagData(TagData.MUSICBRAINZ_ALBUM_ID, "11cafb9e-5fbc-49c7-b920-4ff754e03e93"),
                    new TagData(TagData.TITLE, "It's Gonna Be A Lovely Day"),
                    new TagData(TagData.MUSICBRAINZ_TRACK_ID, "f2ba4ef6-7017-4b93-9176-c079ed0a97e9"),
                    new TagData(TagData.YEAR, "1992"),
                    new TagData(TagData.TRACKNUM, "9"),
                    new TagData(TagData.ALBUMARTIST, "Whitney Houston"),
                    new TagData(TagData.MUSICBRAINZ_ALBUMARTIST_ID, "0307edfc-437c-4b48-8700-80680e66a228"),
                    new TagData(TagData.ARTIST, "The S.O.U.L. S.Y.S.T.E.M.", "S.O.U.L. S.Y.S.T.E.M., The"),
                    new TagData(TagData.MUSICBRAINZ_ARTIST_ID, "95eeac68-0305-41b1-b4f6-ab6594ee21c6"),
                    new TagData(TagData.GENRE, "Soundtrack"),
                    new TagData(TagData.COMPOSER, "Bill Withers"),
                    new TagData(TagData.COMPOSER, "David Cole"),
                    new TagData(TagData.COMPOSER, "Robert Clivilles"),
                    new TagData(TagData.COMPOSER, "Skip Scarborough"),
                    new TagData(TagData.COMPOSER, "Tommy Never")
            ));
            squeezeboxServer.importNewPlayableElement(trackData);

            trackData = new TrackData();
            trackData.setFile("/music/The Bodyguard/Theme From The Bodyguard");
            trackData.setFormat("flc");
            trackData.setSmdID("00000000000000000000000000000013-00000000-00000000");
            trackData.setUrl("file://" + trackData.getFile());
            trackData.setTags(Arrays.asList(
                    new TagData(TagData.ALBUM, "The Bodyguard (Original Soundtrack Album)"),
                    new TagData(TagData.MUSICBRAINZ_ALBUM_ID, "11cafb9e-5fbc-49c7-b920-4ff754e03e93"),
                    new TagData(TagData.TITLE, "Theme From The Bodyguard"),
                    new TagData(TagData.MUSICBRAINZ_TRACK_ID, "c3af5bb7-5711-495e-8fae-af8d730497cd"),
                    new TagData(TagData.YEAR, "1992"),
                    new TagData(TagData.TRACKNUM, "13"),
                    new TagData(TagData.ALBUMARTIST, "Whitney Houston"),
                    new TagData(TagData.MUSICBRAINZ_ALBUMARTIST_ID, "0307edfc-437c-4b48-8700-80680e66a228"),
                    new TagData(TagData.GENRE, "Soundtrack"),
                    new TagData(TagData.STYLE, "Instrumental"),
                    new TagData(TagData.COMPOSER, "Alan Silverstri"),
                    new TagData(TagData.CONDUCTOR, "William Ross"),
                    new TagData(TagData.PERFORMER, "Gary Grant")
            ));
            squeezeboxServer.importNewPlayableElement(trackData);

            Collection<ReleaseEntity> releases = releaseRepository.findAll();
            assert releases != null;
            assert releases.size() == 1;

            Release release = releases.iterator().next();
            assert release != null;

            assert release.getTracks() != null;
            assert release.getTracks().size() == 4;
            assert release.getContributors().size() == 1;

            for (Track track : release.getTracks()) {
                assert track.getNumber() != null;
                assert track.getRecording() != null;
                assert track.getRecording().getWork() != null;

                if (track.getNumber().equals(1)) {
                    assert track.getRecording().getWork().getName().equals("I Will Always Love You");
                    assert track.getRecording().getContributors().size() == 1;
                    assert track.getRecording().getWork().getContributors().size() == 1;
                } else if (track.getNumber().equals(5)) {
                    assert track.getRecording().getWork().getName().equals("Queen Of The Night");
                    assert track.getRecording().getContributors().size() == 0;
                    assert track.getRecording().getWork().getContributors().size() == 4;
                } else if (track.getNumber().equals(9)) {
                    assert track.getRecording().getWork().getName().equals("It's Gonna Be A Lovely Day");
                    assert track.getRecording().getContributors().size() == 1;
                    assert track.getRecording().getWork().getContributors().size() == 5;
                } else if (track.getNumber().equals(13)) {
                    assert track.getRecording().getWork().getName().equals("Theme From The Bodyguard");
                    assert track.getRecording().getContributors().size() == 2;
                    assert track.getRecording().getWork().getContributors().size() == 1;
                } else {
                    assert false;
                }
            }

            GlobalIdentity identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_MUSICBRAINZ, release);
            assert identity != null;
            assert identity.getUri().equals("11cafb9e-5fbc-49c7-b920-4ff754e03e93");

            identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_MUSICBRAINZ, release.getContributors().iterator().next().getArtist());
            assert identity != null;
            assert identity.getUri().equals("0307edfc-437c-4b48-8700-80680e66a228");

            for (Track track : release.getTracks()) {
                if (track.getNumber().equals(1)) {
                    identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_MUSICBRAINZ, track);
                    assert identity != null;
                    assert identity.getUri().equals("86cf33ac-5b7b-401b-9188-608bb2752063");
                } else if (track.getNumber().equals(5)) {
                    identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_MUSICBRAINZ, track);
                    assert identity != null;
                    assert identity.getUri().equals("bdd8624a-d0ac-480f-8fe5-253bd99b7d3f");
                } else if (track.getNumber().equals(9)) {
                    identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_MUSICBRAINZ, track);
                    assert identity != null;
                    assert identity.getUri().equals("f2ba4ef6-7017-4b93-9176-c079ed0a97e9");
                    identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_MUSICBRAINZ, track.getRecording().getContributors().iterator().next().getArtist());
                    assert identity != null;
                    assert identity.getUri().equals("95eeac68-0305-41b1-b4f6-ab6594ee21c6");
                } else if (track.getNumber().equals(13)) {
                    identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_MUSICBRAINZ, track);
                    assert identity != null;
                    assert identity.getUri().equals("c3af5bb7-5711-495e-8fae-af8d730497cd");
                }
            }

        } finally {
            em.getTransaction().commit();
        }
    }
}
