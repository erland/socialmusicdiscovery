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
import com.sun.jersey.api.client.Client;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.socialmusicdiscovery.server.api.mediaimport.AbstractProcessingModule;
import org.socialmusicdiscovery.server.api.mediaimport.MediaImporter;
import org.socialmusicdiscovery.server.api.mediaimport.ProcessingStatusCallback;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.model.GlobalIdentity;
import org.socialmusicdiscovery.server.business.model.GlobalIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReference;
import org.socialmusicdiscovery.server.business.model.classification.Classification;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationEntity;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationReferenceEntity;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameter;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameterEntity;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.repository.GlobalIdentityRepository;
import org.socialmusicdiscovery.server.business.repository.SMDIdentityRepository;
import org.socialmusicdiscovery.server.business.repository.classification.ClassificationReferenceRepository;
import org.socialmusicdiscovery.server.business.repository.classification.ClassificationRepository;
import org.socialmusicdiscovery.server.business.repository.core.*;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Media import module for Squeezebox Server, require the Social Music Discovery plugin installed in Squeezebox Server to work
 */
public class SqueezeboxServer extends AbstractProcessingModule implements MediaImporter {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy");
    private static final DateFormat DATE_FORMAT_WITH_DATE = new SimpleDateFormat("yyyy-MM-dd");

    class TypeIdentity {
        String type;
        String id;

        TypeIdentity(String type, String id) {
            this.type = type;
            this.id = id;
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        @Override
        public boolean equals(Object o) {
            return EqualsBuilder.reflectionEquals(this, o);
        }
    }

    private Map<TypeIdentity, Collection<String>> classificationCache = new HashMap<TypeIdentity, Collection<String>>();
    private Map<String, Collection<String>> artistCache = new HashMap<String, Collection<String>>();
    private Set<String> artistMusicbrainzCache = new HashSet<String>();
    private Map<String, Collection<String>> releaseCache = new HashMap<String, Collection<String>>();
    private Set<String> releaseMusicbrainzCache = new HashSet<String>();

    private boolean abort = false;

    @Inject
    private EntityManager entityManager;

    @Inject
    private PlayableElementRepository playableElementRepository;

    @Inject
    private MediumRepository mediumRepository;

    @Inject
    private TrackRepository trackRepository;

    @Inject
    private ReleaseRepository releaseRepository;

    @Inject
    private RecordingRepository recordingRepository;

    @Inject
    private ArtistRepository artistRepository;

    @Inject
    private ClassificationRepository classificationRepository;

    @Inject
    private ClassificationReferenceRepository classificationReferenceRepository;

    @Inject
    private ContributorRepository contributorRepository;

    @Inject
    private WorkRepository workRepository;

    @Inject
    private GlobalIdentityRepository globalIdentityRepository;

    @Inject
    @Named("squeezeboxserver.host")
    private String squeezeboxServerHost;

    @Inject
    @Named("squeezeboxserver.port")
    private String squeezeboxServerPort;

    public SqueezeboxServer() {
        InjectHelper.injectMembers(this);
    }

    /**
     * @inherit
     */
    public String getId() {
        return "squeezeboxserver";
    }

    /**
     * @inherit
     */
    public void execute(ProcessingStatusCallback progressHandler) {
        abort = false;
        TrackListData trackList = null;
        final long CHUNK_SIZE = 20;
        final String SERVICE_URL = "http://" + squeezeboxServerHost + ":" + squeezeboxServerPort + "/jsonrpc.js";
        long offset = 0;

        artistCache.clear();
        artistMusicbrainzCache.clear();
        releaseCache.clear();
        releaseMusicbrainzCache.clear();
        classificationCache.clear();
        try {
            JSONObject request = createRequest(offset, CHUNK_SIZE);
            JSONObject response = Client.create().resource(SERVICE_URL).accept("application/json").post(JSONObject.class, request);
            ObjectMapper mapper = new ObjectMapper();
            trackList = mapper.readValue(response.getString("result"), TrackListData.class);

            if (trackList != null) {
                while (trackList != null && !abort) {
                    long i = 0;
                    entityManager.getTransaction().begin();
                    for (TrackData track : trackList.getTracks()) {
                        progressHandler.progress(getId(), track.getFile(), trackList.getOffset() + i + 1, trackList.getCount());
                        importNewPlayableElement(track);
                        i++;
                    }
                    entityManager.flush();
                    entityManager.clear();
                    entityManager.getTransaction().commit();
                    if (offset + trackList.getTracks().size() < trackList.getCount()) {
                        offset = offset + trackList.getTracks().size();
                        request = createRequest(offset, CHUNK_SIZE);
                        response = Client.create().resource(SERVICE_URL).accept("application/json").post(JSONObject.class, request);
                        trackList = mapper.readValue(response.getString("result"), TrackListData.class);
                    } else {
                        trackList = null;
                    }
                }

                if (abort) {
                    progressHandler.aborted(getId());
                } else {
                    progressHandler.finished(getId());
                }
            } else {
                progressHandler.failed(getId(), "Unable to retrieve data");
            }
        } catch (IOException e) {
            e.printStackTrace();
            progressHandler.failed(getId(), e.getLocalizedMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            progressHandler.failed(getId(), e.getLocalizedMessage());
        }
        artistCache.clear();
        artistMusicbrainzCache.clear();
        classificationCache.clear();
        releaseCache.clear();
        releaseMusicbrainzCache.clear();
    }

    /**
     * @inherit
     */
    public void abort() {
        abort = true;
    }

    /**
     * Creates a JSON object representing the JSON request needed to request tracks from the Social Music Discovery SBS plugin
     *
     * @param offset    Offset to use when starting to retrieve tracks
     * @param chunkSize Number of tracks to retrieve data for
     * @return A JSON object
     * @throws JSONException
     */
    private JSONObject createRequest(long offset, long chunkSize) throws JSONException {
        JSONObject request = new JSONObject();
        request.put("id", 1L);
        request.put("method", "slim.request");
        JSONArray paramsArray = new JSONArray();
        paramsArray.put("-");
        JSONArray paramsArray2 = new JSONArray();
        paramsArray2.put("socialmusicdiscovery");
        paramsArray2.put("tracks");
        paramsArray2.put("offset:" + offset);
        paramsArray2.put("size:" + chunkSize);
        paramsArray.put(paramsArray2);
        request.put("params", paramsArray);
        return request;
    }

    /**
     * Import a new playable element with related meta data
     *
     * @param data Data about the playable element to create
     */
    void importNewPlayableElement(TrackData data) {
        Collection<PlayableElementEntity> playableElements = playableElementRepository.findBySmdID(data.getSmdID());
        if (playableElements.size() > 0) {
            // Update URI for previously imported playable elements of same format
            for (PlayableElementEntity playableElement : playableElements) {
                if (playableElement.getFormat().equals(data.getFormat()) &&
                        !playableElement.getUri().equals(data.getUrl())) {
                    playableElement.setUri(data.getUrl());
                    playableElement.setLastUpdated(new Date());
                    playableElement.setLastUpdatedBy(getId());
                }
            }
        } else {
            // This playable element doesn't exist, yet, let's create it
            PlayableElementEntity playableElement = new PlayableElementEntity();
            playableElement.setUri(data.getUrl());
            playableElement.setFormat(data.getFormat());
            playableElement.setSmdID(data.getSmdID());
            playableElement.setLastUpdated(new Date());
            playableElement.setLastUpdatedBy(getId());
            playableElementRepository.create(playableElement);
            playableElements.add(playableElement);

            // Convert list of tags to a map to make them easier to handle
            Map<String, Collection<String>> tags = new HashMap<String, Collection<String>>();
            for (TagData tagData : data.getTags()) {
                if (tags.containsKey(tagData.getName())) {
                    tags.get(tagData.getName()).add(tagData.getValue());
                } else {
                    tags.put(tagData.getName(), new ArrayList<String>(Arrays.asList(tagData.getValue())));
                }
            }
            // We need a TITLE tag, tracks without titles isn't currenlty imported
            if (tags.containsKey(TagData.TITLE)) {
                String title = tags.get(TagData.TITLE).iterator().next();

                WorkEntity work = null;
                Collection<WorkEntity> existingWorks = new ArrayList<WorkEntity>();
                if (tags.containsKey((TagData.WORK))) {
                    String workName = tags.get(TagData.WORK).iterator().next();
                    existingWorks = workRepository.findByName(workName);
                    if (existingWorks.size() == 0) {
                        // Create a Work entity based on the WORK tag
                        work = new WorkEntity();
                        work.setName(workName);
                        work.setLastUpdated(new Date());
                        work.setLastUpdatedBy(getId());
                    } else {
                        work = (WorkEntity)existingWorks.iterator().next();
                    }
                } else {
                    // Create a Work entity based on the TITLE tag
                    work = new WorkEntity();
                    work.setName(title);
                    work.setLastUpdated(new Date());
                    work.setLastUpdatedBy(getId());
                }

                // Create Work entity and add composers to it if it didn't already exist
                if (existingWorks.size() == 0) {
                    if(getConfiguration().getBooleanParameter("composers")) {
                        Set<Contributor> workContributors = getContributorsForTag(tags.get(TagData.COMPOSER), Contributor.COMPOSER);
                        if (workContributors.size() > 0) {
                            saveContributors(workContributors);
                            work.setContributors(workContributors);
                        }
                    }
                    workRepository.create(work);
                }


                // Create a Recording entity represented by the Work and various Contributors
                RecordingEntity recording = new RecordingEntity();
                recording.setLastUpdated(new Date());
                recording.setLastUpdatedBy(getId());
                recording.getWorks().add(work);

                Set<Contributor> albumArtistContributors = getContributorsForTag(tags.get(TagData.ALBUMARTIST), Contributor.PERFORMER);
                Set<Contributor> artistContributors = getContributorsForTag(tags.get(TagData.ARTIST), Contributor.PERFORMER);
                Set<Contributor> trackArtistContributors = getContributorsForTag(tags.get(TagData.TRACKARTIST), Contributor.PERFORMER);
                Set<Contributor> performerContributors = getContributorsForTag(tags.get(TagData.PERFORMER), Contributor.PERFORMER);
                Set<Contributor> conductorContributors = new HashSet<Contributor>();
                if(getConfiguration().getBooleanParameter("conductors")) {
                    conductorContributors = getContributorsForTag(tags.get(TagData.CONDUCTOR), Contributor.CONDUCTOR);
                }
                Set<Contributor> recordingContributors = new HashSet<Contributor>();

                if (artistContributors.size() == 1 && tags.containsKey(TagData.MUSICBRAINZ_ARTIST_ID)) {
                    String artistId = tags.get(TagData.MUSICBRAINZ_ARTIST_ID).iterator().next();
                    if (!artistMusicbrainzCache.contains(artistId)) {
                        Artist artist = artistRepository.findById(artistCache.get(tags.get(TagData.ARTIST).iterator().next().toLowerCase()).iterator().next());
                        GlobalIdentityEntity identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_MUSICBRAINZ, artist);
                        if (identity == null) {
                            identity = new GlobalIdentityEntity();
                            identity.setSource(GlobalIdentity.SOURCE_MUSICBRAINZ);
                            identity.setEntityId(artist.getId());
                            identity.setUri(artistId);
                            identity.setLastUpdated(new Date());
                            identity.setLastUpdatedBy(getId());
                            globalIdentityRepository.create(identity);
                        }
                        artistMusicbrainzCache.add(artistId);
                    }
                }
                if (albumArtistContributors.size() == 1 && tags.containsKey(TagData.MUSICBRAINZ_ALBUMARTIST_ID)) {
                    String artistId = tags.get(TagData.MUSICBRAINZ_ALBUMARTIST_ID).iterator().next();
                    if (!artistMusicbrainzCache.contains(artistId)) {
                        Artist artist = artistRepository.findById(artistCache.get(tags.get(TagData.ALBUMARTIST).iterator().next().toLowerCase()).iterator().next());
                        GlobalIdentityEntity identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_MUSICBRAINZ, artist);
                        if (identity == null) {
                            identity = new GlobalIdentityEntity();
                            identity.setSource(GlobalIdentity.SOURCE_MUSICBRAINZ);
                            identity.setEntityId(artist.getId());
                            identity.setUri(artistId);
                            identity.setLastUpdated(new Date());
                            identity.setLastUpdatedBy(getId());
                            globalIdentityRepository.create(identity);
                        }
                        artistMusicbrainzCache.add(artistId);
                    }
                }

                // Don't use ARTIST contributors on Recording if they are exactly the same as those on Release level
                if (!equalContributors(albumArtistContributors, artistContributors)) {
                    recordingContributors.addAll(artistContributors);
                }
                // Don't use TRACKARTIST contributors if they are exactly the same as those on Release level
                if (!equalContributors(albumArtistContributors, trackArtistContributors)) {
                    recordingContributors.addAll(trackArtistContributors);
                }
                recordingContributors.addAll(conductorContributors);
                recordingContributors.addAll(performerContributors);

                if (recordingContributors.size() > 0) {
                    saveContributors(recordingContributors);
                    recording.setContributors(recordingContributors);
                }
                recordingRepository.create(recording);

                // Add GENRE, STYLE and MOOD Classification entities and related them to the created Recording entity
                if (tags.containsKey(TagData.GENRE) && getConfiguration().getBooleanParameter("genres")) {
                    createClassificationsForTag(tags.get(TagData.GENRE), Classification.GENRE, recording.getReference());
                }
                if (tags.containsKey(TagData.STYLE) && getConfiguration().getBooleanParameter("styles")) {
                    createClassificationsForTag(tags.get(TagData.STYLE), Classification.STYLE, recording.getReference());
                }
                if (tags.containsKey(TagData.MOOD) && getConfiguration().getBooleanParameter("moods")) {
                    createClassificationsForTag(tags.get(TagData.MOOD), Classification.MOOD, recording.getReference());
                }

                // Create a Release entity based on the ALBUM tag
                if (tags.containsKey(TagData.ALBUM)) {
                    String albumName = tags.get(TagData.ALBUM).iterator().next();

                    // YEAR tag is optional but we use it if it exists
                    String year = null;
                    if (tags.containsKey(TagData.YEAR)) {
                        year = tags.get(TagData.YEAR).iterator().next();
                    }

                    // Create a new Release entity if it isn't already available
                    //TODO: We need to implement handling of greatest hits album here which might have exactly the same name
                    Collection<ReleaseEntity> releases = lookup(this.releaseCache.get(albumName.toLowerCase()), releaseRepository);
                    if (releases == null) {
                        releases = releaseRepository.findByName(albumName);
                        if (releases.size() > 0) {
                            this.releaseCache.put(albumName.toLowerCase(), idList(releases));
                        }
                    }
                    ReleaseEntity release = null;
                    if (releases.size() == 0) {
                        release = new ReleaseEntity();
                        release.setName(albumName);
                        release.setLastUpdated(new Date());
                        release.setLastUpdatedBy(getId());
                        if (year != null) {
                            try {
                                if (year.length() == 10) {
                                    release.setDate(DATE_FORMAT_WITH_DATE.parse(year.substring(10)));
                                } else if (year.length() >= 4) {
                                    release.setDate(DATE_FORMAT.parse(year.substring(4)));
                                }
                            } catch (ParseException e) {
                                // Just ignore year if it can't be parsed
                            }
                        }
                        if (albumArtistContributors.size() > 0) {
                            saveContributors(albumArtistContributors);
                            release.setContributors(albumArtistContributors);
                        }
                        releaseRepository.create(release);
                        this.releaseCache.put(albumName.toLowerCase(), Arrays.asList(release.getId()));
                    } else {
                        // We use the first Release entity found if it already existsted
                        release = releases.iterator().next();
                    }

                    if (tags.containsKey(TagData.MUSICBRAINZ_ALBUM_ID)) {
                        String releaseId = tags.get(TagData.MUSICBRAINZ_ALBUM_ID).iterator().next();
                        if (!releaseMusicbrainzCache.contains(releaseId)) {
                            GlobalIdentityEntity identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_MUSICBRAINZ, release);
                            if (identity == null) {
                                identity = new GlobalIdentityEntity();
                                identity.setSource(GlobalIdentity.SOURCE_MUSICBRAINZ);
                                identity.setEntityId(release.getId());
                                identity.setUri(releaseId);
                                identity.setLastUpdated(new Date());
                                identity.setLastUpdatedBy(getId());
                                globalIdentityRepository.create(identity);
                            }
                            releaseMusicbrainzCache.add(releaseId);
                        }
                    }

                    // Create a Track entity if there is a TRACKNUM tag
                    TrackEntity track = new TrackEntity();
                    track.setLastUpdated(new Date());
                    track.setLastUpdatedBy(getId());
                    if (tags.containsKey(TagData.TRACKNUM)) {
                        String trackNum = tags.get(TagData.TRACKNUM).iterator().next();

                        // Sometimes TRACKNUM is represented as 1/10
                        if (trackNum.contains("/")) {
                            trackNum = trackNum.substring(0, trackNum.indexOf("/"));
                        }
                        try {
                            track.setNumber(Integer.parseInt(trackNum));
                        } catch (NumberFormatException e) {
                            // Ignore the track number if we can't parse it
                            System.err.println("Unable to parse track number: " + tags.get(TagData.TRACKNUM).iterator().next());
                        }
                    }
                    track.getPlayableElements().addAll(playableElements);
                    track.setRecording(recording);
                    release.addTrack(track);
                    trackRepository.create(track);

                    if (tags.containsKey(TagData.MUSICBRAINZ_TRACK_ID)) {
                        GlobalIdentityEntity identity = new GlobalIdentityEntity();
                        identity.setSource(GlobalIdentity.SOURCE_MUSICBRAINZ);
                        identity.setEntityId(track.getId());
                        identity.setUri(tags.get(TagData.MUSICBRAINZ_TRACK_ID).iterator().next());
                        identity.setLastUpdated(new Date());
                        identity.setLastUpdatedBy(getId());
                        globalIdentityRepository.create(identity);
                    }

                    // Create a Media entity if there is a DISC tag
                    if (tags.containsKey(TagData.DISC)) {
                        String discNo = tags.get(TagData.DISC).iterator().next();

                        // DISC tags can sometimes have the syntax 1/3
                        if (discNo.contains("/")) {
                            discNo = discNo.substring(0, discNo.indexOf("/"));
                        }

                        // Use existing Media entity if there is one already for this disc number
                        List<Medium> mediums = release.getMediums();
                        MediumEntity medium = null;
                        for (Medium m : mediums) {
                            if (m.getNumber() != null) {
                                try {
                                    if (m.getNumber().equals(Integer.parseInt(discNo))) {
                                        medium = (MediumEntity) m;
                                    }
                                } catch (NumberFormatException e) {
                                    // Ignore this and try with name instead
                                }
                            }
                            if (m.getName() != null && m.getName().equals(discNo)) {
                                medium = (MediumEntity) m;
                            }
                        }

                        // Create a new Media entity if we don't already have one
                        if (medium == null) {
                            medium = new MediumEntity();
                            medium.setLastUpdated(new Date());
                            medium.setLastUpdatedBy(getId());
                            try {
                                medium.setNumber(Integer.parseInt(discNo));
                            } catch (NumberFormatException e) {
                                medium.setName(discNo);
                            }
                            medium.addTrack(track);
                            release.addMedium(medium);
                            mediumRepository.create(medium);
                        }else {
                            medium.addTrack(track);
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks if two sets of Contributor contains the same artists/role combinations
     *
     * @param contributors1 The first set of contributors
     * @param contributors2 The second set of contributors
     * @return true if the sets are equal, false if they aren't
     */
    private Boolean equalContributors(Set<Contributor> contributors1, Set<Contributor> contributors2) {
        if (contributors1.size() == contributors2.size()) {
            for (Contributor contributor1 : contributors1) {
                boolean found = false;
                for (Contributor contributor2 : contributors2) {
                    if (contributor1.getArtist().getId().equals(contributor2.getArtist().getId()) &&
                            contributor1.getType().equals(contributor2.getType())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Get contributors represented by the list of artist names
     * The Artist entities will be created if they don't already exists, the Contributor entities returned will just be prepared so you will have to
     * store them manually after this call
     *
     * @param artistNames     The list of artist names to search contributors for
     * @param contributorType The type of contributors to create
     * @return A list of contributors representing the specified artist names and contributor type, empty list if no contributors are returned
     */
    private Set<Contributor> getContributorsForTag(Collection<String> artistNames, String contributorType) {
        Set<Contributor> contributors = new HashSet<Contributor>();
        if (artistNames != null) {
            Collection<Artist> artists = new ArrayList<Artist>();
            for (String artistName : artistNames) {
                Collection<ArtistEntity> existingArtists = lookup(this.artistCache.get(artistName.toLowerCase()), artistRepository);
                if (existingArtists == null) {
                    existingArtists = artistRepository.findByName(artistName);
                    if (existingArtists.size() > 0) {
                        this.artistCache.put(artistName.toLowerCase(), idList(existingArtists));
                    }
                }
                if (existingArtists.size() == 0) {
                    ArtistEntity artist = new ArtistEntity();
                    artist.setName(artistName);
                    artist.setLastUpdated(new Date());
                    artist.setLastUpdatedBy(getId());
                    artistRepository.create(artist);
                    artists.add(artist);
                    this.artistCache.put(artistName.toLowerCase(), Arrays.asList(artist.getId()));
                } else {
                    artists.addAll(existingArtists);
                }
            }
            for (Artist artist : artists) {
                ContributorEntity contributor = new ContributorEntity();
                contributor.setArtist(artist);
                contributor.setType(contributorType);
                contributor.setLastUpdated(new Date());
                contributor.setLastUpdatedBy(getId());
                contributors.add(contributor);
            }
        }
        return contributors;
    }

    private Collection<String> idList(Collection<? extends SMDIdentity> entityList) {
        if(entityList==null || entityList.size()==0) {
            return null;
        }
        List<String> idList = new ArrayList<String>(entityList.size());
        for (SMDIdentity artist : entityList) {
            idList.add(artist.getId());
        }
        return idList;
    }

    private <T> Collection<T> lookup(Collection<String> idList, SMDIdentityRepository<T> repository) {
        if(idList==null || idList.size()==0) {
            return null;
        }
        List<T> result = new ArrayList<T>(idList.size());
        for (String id : idList) {
            result.add(repository.findById(id));
        }
        return result;
    }

    /**
     * Store Contributor entities for the set of contributors specified
     *
     * @param contributors The list of contributors to create
     */
    private void saveContributors(Set<Contributor> contributors) {
        for (Contributor contributor : contributors) {
            contributorRepository.create((ContributorEntity)contributor);
        }
    }

    /**
     * Create and save Classification entities for the specified list of classification names and classification type
     * The created Classification entities is also related to the provided SMDIdentity reference.
     * If a Classification entity of the same name and type already exists, it is reused.
     *
     * @param classificationNames The list of classification names to create entities for
     * @param classificationType  Type of classification to create
     * @param reference           The reference to relate the created Classification entities to
     */
    private void createClassificationsForTag(Collection<String> classificationNames, String classificationType, SMDIdentityReference reference) {
        if (classificationNames != null) {
            for (String classificationName : classificationNames) {
                TypeIdentity classificationId = new TypeIdentity(classificationType.toLowerCase(), classificationName.toLowerCase());
                Collection<ClassificationEntity> existingClassifications = lookup(this.classificationCache.get(classificationId), classificationRepository);
                if (existingClassifications == null) {
                    existingClassifications = classificationRepository.findByNameAndType(classificationName, classificationType);
                    if (existingClassifications.size() > 0) {
                        this.classificationCache.put(classificationId, idList(existingClassifications));
                    }
                }
                ClassificationReferenceEntity classificationReference = new ClassificationReferenceEntity();
                classificationReference.setReferenceTo(reference);
                classificationReference.setLastUpdated(new Date());
                classificationReference.setLastUpdatedBy(getId());
                if (existingClassifications.size() == 0) {
                    ClassificationEntity classification = new ClassificationEntity();
                    classification.setName(classificationName);
                    classification.setType(classificationType);
                    classification.setLastUpdated(new Date());
                    classification.setLastUpdatedBy(getId());
                    classificationRepository.create(classification);
                    classification.addReference(classificationReference);
                    classificationReferenceRepository.create(classificationReference);
                    this.classificationCache.put(classificationId, Arrays.asList(classification.getId()));
                } else {
                    for (ClassificationEntity classification : existingClassifications) {
                        classification.addReference(classificationReference);
                        classificationReferenceRepository.create(classificationReference);
                    }
                }
            }
        }
    }

    @Override
    public Collection<ConfigurationParameter> getDefaultConfiguration() {
        return Arrays.asList(
                (ConfigurationParameter)new ConfigurationParameterEntity("genres", ConfigurationParameter.Type.BOOLEAN, "true"),
                (ConfigurationParameter)new ConfigurationParameterEntity("styles", ConfigurationParameter.Type.BOOLEAN, "true"),
                (ConfigurationParameter)new ConfigurationParameterEntity("moods", ConfigurationParameter.Type.BOOLEAN, "true"),
                (ConfigurationParameter)new ConfigurationParameterEntity("composers", ConfigurationParameter.Type.BOOLEAN, "true"),
                (ConfigurationParameter)new ConfigurationParameterEntity("conductors", ConfigurationParameter.Type.BOOLEAN, "true")
        );
    }
}
