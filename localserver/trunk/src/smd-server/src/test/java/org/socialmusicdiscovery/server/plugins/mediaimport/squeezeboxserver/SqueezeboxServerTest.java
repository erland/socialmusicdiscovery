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

package org.socialmusicdiscovery.server.plugins.mediaimport.squeezeboxserver;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.socialmusicdiscovery.server.business.logic.config.MappedConfigurationContext;
import org.socialmusicdiscovery.server.business.logic.config.MemoryConfigurationManager;
import org.socialmusicdiscovery.server.business.model.GlobalIdentity;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameter;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameterEntity;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.repository.GlobalIdentityRepository;
import org.socialmusicdiscovery.server.business.repository.core.ArtistRepository;
import org.socialmusicdiscovery.server.business.repository.core.ReleaseRepository;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SqueezeboxServerTest extends BaseTestCase {
    @Inject
    ReleaseRepository releaseRepository;
    @Inject
    ArtistRepository artistRepository;
    @Inject
    GlobalIdentityRepository globalIdentityRepository;

    SqueezeboxServer squeezeboxServer;

    @Inject
    @Named("default-value")
    MemoryConfigurationManager defaultValueConfigurationManager;

    @BeforeClass
    public void setUp() {
        squeezeboxServer = new SqueezeboxServer();
        String pluginConfigurationPath = "org.socialmusicdiscovery.server.plugins.mediaimport."+squeezeboxServer.getId()+".";

        Set<ConfigurationParameter> defaultConfiguration = new HashSet<ConfigurationParameter>();
        for (ConfigurationParameter parameter : squeezeboxServer.getDefaultConfiguration()) {
            ConfigurationParameterEntity entity = new ConfigurationParameterEntity(parameter);
            if(!entity.getId().startsWith(pluginConfigurationPath)) {
                entity.setId(pluginConfigurationPath+entity.getId());
            }
            entity.setDefaultValue(true);
            defaultConfiguration.add(entity);
        }
        defaultValueConfigurationManager.setParametersForPath(pluginConfigurationPath, defaultConfiguration);
        squeezeboxServer.setConfiguration(new MappedConfigurationContext(pluginConfigurationPath, defaultValueConfigurationManager));
        squeezeboxServer.init(null);
    }

    @BeforeMethod
    public void setUpMethod() {
        em.clear();
        squeezeboxServer.init(null);
    }

    @Test
    public void testImport() throws Exception {
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
                    new TagData(TagData.DISCOGS_RELEASE_ID, "1794218"),
                    new TagData(TagData.TITLE, "I Will Always Love You"),
                    new TagData(TagData.MUSICBRAINZ_TRACK_ID, "86cf33ac-5b7b-401b-9188-608bb2752063"),
                    new TagData(TagData.YEAR, "1992"),
                    new TagData(TagData.TRACKNUM, "1"),
                    new TagData(TagData.ALBUMARTIST, "Whitney Houston"),
                    new TagData(TagData.MUSICBRAINZ_ALBUMARTIST_ID, "0307edfc-437c-4b48-8700-80680e66a228"),
                    new TagData(TagData.ARTIST, "Whitney Houston", "Houston, Whitney"),
                    new TagData(TagData.MUSICBRAINZ_ARTIST_ID, "0307edfc-437c-4b48-8700-80680e66a228"),
                    new TagData(TagData.GENRE, "Soundtrack"),
                    new TagData(TagData.CONDUCTOR, "Ricky Minor", "Minor, Ricky"),
                    new TagData(TagData.COMPOSER, "Dolly Parton", "Dolly, Parton"),
                    new TagData(TagData.SBS_COVER_ID, "1234")
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
                    new TagData(TagData.DISCOGS_RELEASE_ID, "1794218"),
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
                    new TagData(TagData.COMPOSER, "Whitney Houston"),
                    new TagData(TagData.SBS_COVER_ID, "1234")
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
                    new TagData(TagData.DISCOGS_RELEASE_ID, "1794218"),
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
                    new TagData(TagData.COMPOSER, "Tommy Never"),
                    new TagData(TagData.SBS_COVER_ID, "1234")
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
                    new TagData(TagData.DISCOGS_RELEASE_ID, "1794218"),
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
                    new TagData(TagData.PERFORMER, "Gary Grant"),
                    new TagData(TagData.SBS_COVER_ID, "1234")
            ));
            squeezeboxServer.importNewPlayableElement(trackData);

            trackData = new TrackData();
            trackData.setFile("/music/Joe's Garage Act I/The Central Scrutinizer.flac");
            trackData.setFormat("flc");
            trackData.setSmdID("10000000000000000000000000000001-00000000-00000000");
            trackData.setUrl("file://" + trackData.getFile());
            trackData.setTags(Arrays.asList(
                    new TagData(TagData.ALBUM, "Joe's Garage Act I"),
                    new TagData(TagData.TITLE, "The Central Scrutinizer"),
                    new TagData(TagData.TRACKNUM, "1"),
                    new TagData(TagData.DISC,"A"),
                    new TagData(TagData.ARTIST, "Frank Zappa"),
                    new TagData(TagData.ARTIST, "Ike Willis")
            ));
            squeezeboxServer.importNewPlayableElement(trackData);

            trackData = new TrackData();
            trackData.setFile("/music/Joe's Garage Act I/Joe's Garage.flac");
            trackData.setFormat("flc");
            trackData.setSmdID("10000000000000000000000000000002-00000000-00000000");
            trackData.setUrl("file://" + trackData.getFile());
            trackData.setTags(Arrays.asList(
                    new TagData(TagData.ALBUM, "Joe's Garage Act I"),
                    new TagData(TagData.TITLE, "Joe's Garage"),
                    new TagData(TagData.TRACKNUM, "2"),
                    new TagData(TagData.DISC,"A"),
                    new TagData(TagData.ARTIST, "Frank Zappa"),
                    new TagData(TagData.ARTIST, "Ike Willis")
            ));
            squeezeboxServer.importNewPlayableElement(trackData);

            trackData = new TrackData();
            trackData.setFile("/music/Joe's Garage Act I/Wet T-Shirt Nite.flac");
            trackData.setFormat("flc");
            trackData.setSmdID("20000000000000000000000000000001-00000000-00000000");
            trackData.setUrl("file://" + trackData.getFile());
            trackData.setTags(Arrays.asList(
                    new TagData(TagData.ALBUM, "Joe's Garage Act I"),
                    new TagData(TagData.TITLE, "Wet T-Shirt Nite"),
                    new TagData(TagData.TRACKNUM, "1"),
                    new TagData(TagData.DISC,"B"),
                    new TagData(TagData.ARTIST, "Frank Zappa"),
                    new TagData(TagData.ARTIST, "Ike Willis")
            ));
            squeezeboxServer.importNewPlayableElement(trackData);
            em.getTransaction().commit();
            updateSearchRelations();
            em.getTransaction().begin();
            em.clear();

            Collection<ReleaseEntity> releases = releaseRepository.findAll();
            assert releases != null;
            assert releases.size() == 2;

            for (ReleaseEntity release : releases) {
                if(release.getName().equals("Joe's Garage Act I")) {
                    validateJoesGarageAct(release);
                }else {
                    validateTheBodyguard(release);
                }
            }

            em.clear();
            Collection<ArtistEntity> artists = artistRepository.findAll();
            assert artists.size()>0;
            for (ArtistEntity artist : artists) {
                Collection<ReleaseEntity> artistReleases = releaseRepository.findByArtistWithRelations(artist.getId(),null,null);
                assert artistReleases.size()>0;
            }


        } finally {
            if(em.getTransaction().getRollbackOnly()) {
                em.getTransaction().rollback();
            }else {
                em.getTransaction().commit();
            }
        }
    }
    @Test
    public void testImportLongAlbumTitle() throws Exception {
        loadTestData("org.socialmusicdiscovery.server.business.model", "Empty Tables.xml");
        boolean gotError = false;
        boolean gotSuccess = false;
        em.getTransaction().begin();
        try {
            TrackData trackData = new TrackData();
            trackData.setFile("/music/Some Album/Some Title.flac");
            trackData.setFormat("flc");
            trackData.setSmdID("00000000000000000000000000000001-00000000-00000000");
            trackData.setUrl("file://" + trackData.getFile());
            String albumTitle = "";
            for(int i=0;i<255;i++) {
                albumTitle += "\u00d4";
            }
            trackData.setTags(Arrays.asList(
                    new TagData(TagData.ALBUM, albumTitle),
                    new TagData(TagData.TITLE, "Some Title"),
                    new TagData(TagData.ARTIST, "Some Artist"),
                    new TagData(TagData.GENRE, "Some Genre")
            ));
            squeezeboxServer.importNewPlayableElement(trackData);
            gotSuccess = true;
            em.getTransaction().commit();
            em.getTransaction().begin();
            trackData.setSmdID("00000000000000000000000000000002-00000000-00000000");
            trackData.setTags(Arrays.asList(
                    new TagData(TagData.ALBUM, "a"+albumTitle),
                    new TagData(TagData.TITLE, "Some Title"),
                    new TagData(TagData.ARTIST, "Some Artist"),
                    new TagData(TagData.GENRE, "Some Genre")
            ));
            squeezeboxServer.importNewPlayableElement(trackData);
            em.getTransaction().commit();
        } catch (ConstraintViolationException e) {
            gotError = true;
        } finally {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
        assert gotSuccess;
        assert gotError;
    }

    @Test
    public void testImportLongTrackTitle() throws Exception {
        loadTestData("org.socialmusicdiscovery.server.business.model", "Empty Tables.xml");
        boolean gotError = false;
        boolean gotSuccess = false;
        em.getTransaction().begin();
        try {
            TrackData trackData = new TrackData();
            trackData.setFile("/music/Some Album/Some Title.flac");
            trackData.setFormat("flc");
            trackData.setSmdID("00000000000000000000000000000001-00000000-00000000");
            trackData.setUrl("file://" + trackData.getFile());
            String trackTitle = "";
            for(int i=0;i<511;i++) {
                trackTitle += "\u00d4";
            }
            trackData.setTags(Arrays.asList(
                    new TagData(TagData.ALBUM, "Some album"),
                    new TagData(TagData.TITLE, trackTitle),
                    new TagData(TagData.ARTIST, "Some Artist"),
                    new TagData(TagData.GENRE, "Some Genre")
            ));
            gotSuccess = true;
            em.getTransaction().commit();
            em.getTransaction().begin();
            squeezeboxServer.importNewPlayableElement(trackData);
            trackData.setTags(Arrays.asList(
                    new TagData(TagData.ALBUM, "Some album"),
                    new TagData(TagData.TITLE, "a"+trackTitle),
                    new TagData(TagData.ARTIST, "Some Artist"),
                    new TagData(TagData.GENRE, "Some Genre")
            ));
            trackData.setSmdID("00000000000000000000000000000002-00000000-00000000");
            squeezeboxServer.importNewPlayableElement(trackData);
            em.getTransaction().commit();
        } catch (ConstraintViolationException e) {
            gotError = true;
        } finally {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
        assert gotSuccess;
        assert gotError;
    }

    @Test
    public void testImportLongArtistTitle() throws Exception {
        loadTestData("org.socialmusicdiscovery.server.business.model", "Empty Tables.xml");
        boolean gotError = false;
        boolean gotSuccess = false;
        em.getTransaction().begin();
        try {
            TrackData trackData = new TrackData();
            trackData.setFile("/music/Some Album/Some Title.flac");
            trackData.setFormat("flc");
            trackData.setSmdID("00000000000000000000000000000001-00000000-00000000");
            trackData.setUrl("file://" + trackData.getFile());
            String artistTitle = "";
            for(int i=0;i<255;i++) {
                artistTitle += "\u00d4";
            }
            trackData.setTags(Arrays.asList(
                    new TagData(TagData.ALBUM, "Some album"),
                    new TagData(TagData.TITLE, "Some title"),
                    new TagData(TagData.ARTIST, artistTitle),
                    new TagData(TagData.GENRE, "Some Genre")
            ));
            squeezeboxServer.importNewPlayableElement(trackData);
            gotSuccess = true;
            em.getTransaction().commit();
            em.getTransaction().begin();
            trackData.setSmdID("00000000000000000000000000000002-00000000-00000000");
            trackData.setTags(Arrays.asList(
                    new TagData(TagData.ALBUM, "Some album"),
                    new TagData(TagData.TITLE, "Some title"),
                    new TagData(TagData.ARTIST, "a"+artistTitle),
                    new TagData(TagData.GENRE, "Some Genre")
            ));
            squeezeboxServer.importNewPlayableElement(trackData);
            em.getTransaction().commit();
        } catch (ConstraintViolationException e) {
            gotError = true;
        } finally {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
        assert gotSuccess;
        assert gotError;
    }

    @Test
    public void testImportLongGenreTitle() throws Exception {
        loadTestData("org.socialmusicdiscovery.server.business.model", "Empty Tables.xml");
        boolean gotError = false;
        boolean gotSuccess = false;
        em.getTransaction().begin();
        try {
            TrackData trackData = new TrackData();
            trackData.setFile("/music/Some Album/Some Title.flac");
            trackData.setFormat("flc");
            trackData.setSmdID("00000000000000000000000000000001-00000000-00000000");
            trackData.setUrl("file://" + trackData.getFile());
            String genreTitle = "";
            for(int i=0;i<255;i++) {
                genreTitle += "\u00d4";
            }
            trackData.setTags(Arrays.asList(
                    new TagData(TagData.ALBUM, "Some album"),
                    new TagData(TagData.TITLE, "Some title"),
                    new TagData(TagData.ARTIST, "Some artist"),
                    new TagData(TagData.GENRE, genreTitle)
            ));
            squeezeboxServer.importNewPlayableElement(trackData);
            gotSuccess = true;
            em.getTransaction().commit();
            em.getTransaction().begin();
            trackData.setSmdID("00000000000000000000000000000002-00000000-00000000");
            trackData.setTags(Arrays.asList(
                    new TagData(TagData.ALBUM, "Some album"),
                    new TagData(TagData.TITLE, "Some title"),
                    new TagData(TagData.ARTIST, "Some artist"),
                    new TagData(TagData.GENRE, "a"+genreTitle)
            ));
            squeezeboxServer.importNewPlayableElement(trackData);
            em.getTransaction().commit();
        } catch (ConstraintViolationException e) {
            gotError = true;
        } finally {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
        assert gotSuccess;
        assert gotError;
    }

    @Test
    public void testImportLongFilePath() throws Exception {
        loadTestData("org.socialmusicdiscovery.server.business.model", "Empty Tables.xml");
        boolean gotError = false;
        boolean gotSuccess = false;
        em.getTransaction().begin();
        try {
            String file = "";
            for(int i=0;i<1024-5-7;i++) {
                file += "\u00d4";
            }
            TrackData trackData = new TrackData();
            trackData.setFile(file+".flac");
            trackData.setFormat("flc");
            trackData.setSmdID("00000000000000000000000000000001-00000000-00000000");
            trackData.setUrl("file://" + trackData.getFile());
            trackData.setTags(Arrays.asList(
                    new TagData(TagData.ALBUM, "Some album"),
                    new TagData(TagData.TITLE, "Some title"),
                    new TagData(TagData.ARTIST, "Some artist"),
                    new TagData(TagData.GENRE, "Some Genre")
            ));
            squeezeboxServer.importNewPlayableElement(trackData);
            gotSuccess = true;
            em.getTransaction().commit();
            em.getTransaction().begin();
            trackData.setFile(file+"a.flac");
            trackData.setSmdID("00000000000000000000000000000002-00000000-00000000");
            trackData.setUrl("file://" + trackData.getFile());
            squeezeboxServer.importNewPlayableElement(trackData);
            em.getTransaction().commit();
        } catch (ConstraintViolationException e) {
            gotError = true;
        } finally {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
        assert gotSuccess;
        assert gotError;
    }

    @Test
    public void testImportLongWorkTitle() throws Exception {
        loadTestData("org.socialmusicdiscovery.server.business.model", "Empty Tables.xml");
        boolean gotError = false;
        boolean gotSuccess = false;
        em.getTransaction().begin();
        try {
            TrackData trackData = new TrackData();
            trackData.setFile("/music/Some Album/Some Title.flac");
            trackData.setFormat("flc");
            trackData.setSmdID("00000000000000000000000000000001-00000000-00000000");
            trackData.setUrl("file://" + trackData.getFile());
            String workTitle = "";
            for(int i=0;i<511;i++) {
                workTitle += "\u00d4";
            }
            trackData.setTags(Arrays.asList(
                    new TagData(TagData.ALBUM, "Some album"),
                    new TagData(TagData.TITLE, "Some title"),
                    new TagData(TagData.WORK, workTitle),
                    new TagData(TagData.ARTIST, "Some Artist"),
                    new TagData(TagData.GENRE, "Some Genre")
            ));
            squeezeboxServer.importNewPlayableElement(trackData);
            gotSuccess = true;
            em.getTransaction().commit();
            em.getTransaction().begin();
            trackData.setSmdID("00000000000000000000000000000002-00000000-00000000");
            trackData.setTags(Arrays.asList(
                    new TagData(TagData.ALBUM, "Some album"),
                    new TagData(TagData.TITLE, "Some title"),
                    new TagData(TagData.WORK, "a"+workTitle),
                    new TagData(TagData.ARTIST, "Some Artist"),
                    new TagData(TagData.GENRE, "Some Genre")
            ));
            squeezeboxServer.importNewPlayableElement(trackData);
            em.getTransaction().commit();
        } catch (ConstraintViolationException e) {
            gotError = true;
        } finally {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
        assert gotSuccess;
        assert gotError;
    }

    void validateJoesGarageAct(Release release) {
        assert release.getTracks() != null;
        assert release.getTracks().size() == 3;
        assert release.getMediums() != null;
        assert release.getMediums().size() == 2;
        assert release.getContributors().size() == 0;
        assert ((ReleaseEntity)release).getDefaultImage() == null;
        assert release.getTracks().size() == 3;
        for (Track track : release.getTracks()) {
            assert track.getRecording()!=null;
            assert track.getRecording().getContributors().size()==2;
            for (Contributor contributor : track.getRecording().getContributors()) {
                assert contributor.getArtist()!=null;
                assert contributor.getType().equals(Contributor.PERFORMER);
            }
        }
    }
    void validateTheBodyguard(Release release) {
        assert release.getTracks() != null;
        assert release.getTracks().size() == 4;
        assert release.getContributors().size() == 1;
        assert ((ArtistEntity)release.getContributors().iterator().next().getArtist()).getSortAs().equals("Houston, Whitney");
        assert ((ReleaseEntity)release).getDefaultImage() != null;
        assert ((ReleaseEntity)release).getDefaultImage().getProviderId().equals(SqueezeboxServerImageProvider.PROVIDER_ID);
        assert ((ReleaseEntity)release).getDefaultImage().getProviderImageId().equals("1234");

        for (Track track : release.getTracks()) {
            assert track.getNumber() != null;
            assert track.getRecording() != null;
            assert track.getRecording().getWorks() != null;
            assert track.getRecording().getWorks().size()==1;

            if (track.getNumber().equals(1)) {
                assert track.getRecording().getWorks().iterator().next().getName().equals("I Will Always Love You");
                assert track.getRecording().getContributors().size() == 1;
                assert ((ArtistEntity)track.getRecording().getContributors().iterator().next().getArtist()).getSortAs().equals("Minor, Ricky");
                assert track.getRecording().getWorks().iterator().next().getContributors().size() == 1;
            } else if (track.getNumber().equals(5)) {
                assert track.getRecording().getWorks().iterator().next().getName().equals("Queen Of The Night");
                assert track.getRecording().getContributors().size() == 0;
                assert track.getRecording().getWorks().iterator().next().getContributors().size() == 4;
            } else if (track.getNumber().equals(9)) {
                assert track.getRecording().getWorks().iterator().next().getName().equals("It's Gonna Be A Lovely Day");
                assert track.getRecording().getContributors().size() == 1;
                assert track.getRecording().getWorks().iterator().next().getContributors().size() == 5;
            } else if (track.getNumber().equals(13)) {
                assert track.getRecording().getWorks().iterator().next().getName().equals("Theme From The Bodyguard");
                assert track.getRecording().getContributors().size() == 2;
                assert track.getRecording().getWorks().iterator().next().getContributors().size() == 1;
            } else {
                assert false;
            }
        }

        GlobalIdentity identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_MUSICBRAINZ, release);
        assert identity != null;
        assert identity.getUri().equals("11cafb9e-5fbc-49c7-b920-4ff754e03e93");

        identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_DISCOGS, release);
        assert identity != null;
        assert identity.getUri().equals("1794218");

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
    }
}
