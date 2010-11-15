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
import org.socialmusicdiscovery.server.api.mediaimport.MediaImporter;
import org.socialmusicdiscovery.server.api.mediaimport.MediaImporterCallback;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.model.SMDEntityReference;
import org.socialmusicdiscovery.server.business.model.classification.Classification;
import org.socialmusicdiscovery.server.business.model.core.*;
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
public class SqueezeboxServer implements MediaImporter {
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

    private HashMap<TypeIdentity, Collection<Classification>> classificationCache = new HashMap<TypeIdentity, Collection<Classification>>();
    private HashMap<String, Collection<Artist>> artistCache = new HashMap<String, Collection<Artist>>();
    private HashMap<String, Collection<Release>> releaseCache = new HashMap<String, Collection<Release>>();

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
    private ContributorRepository contributorRepository;

    @Inject
    private WorkRepository workRepository;

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
    public void execute(MediaImporterCallback progressHandler) {
        abort = false;
        TrackListData trackList = null;
        final long CHUNK_SIZE = 20;
        final String SERVICE_URL = "http://" + squeezeboxServerHost + ":" + squeezeboxServerPort + "/jsonrpc.js";
        long offset = 0;

        artistCache.clear();
        releaseCache.clear();
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
        classificationCache.clear();
        releaseCache.clear();
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
    private void importNewPlayableElement(TrackData data) {
        Collection<PlayableElement> playableElements = playableElementRepository.findBySmdID(data.getSmdID());
        if (playableElements.size() > 0) {
            // Update URI for previously imported playable elements of same format
            for (PlayableElement playableElement : playableElements) {
                if (playableElement.getFormat().equals(data.getFormat()) &&
                        !playableElement.getUri().equals(data.getUrl())) {
                    playableElement.setUri(data.getUrl());
                }
            }
        } else {
            // This playable element doesn't exist, yet, let's create it
            PlayableElement playableElement = new PlayableElement();
            playableElement.setUri(data.getUrl());
            playableElement.setFormat(data.getFormat());
            playableElement.setSmdID(data.getSmdID());
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
            if (tags.containsKey(("TITLE"))) {
                String title = tags.get("TITLE").iterator().next();

                // Create a Work entity based on the TITLE tag
                //TODO: Implement WORK tag support instead of just using TITLE
                Work work = new Work();
                work.setName(title);
                Set<Contributor> workContributors = getContributorsForTag(tags.get("COMPOSER"), "composer");
                if (workContributors.size() > 0) {
                    saveContributors(workContributors);
                    work.setContributors(workContributors);
                }
                workRepository.create(work);


                // Create a Recording entity represented by the Work and various Contributors
                Recording recording = new Recording();
                recording.setWork(work);

                Set<Contributor> albumArtistContributors = getContributorsForTag(tags.get("ALBUMARTIST"), "performer");
                Set<Contributor> artistContributors = getContributorsForTag(tags.get("ARTIST"), "performer");
                Set<Contributor> trackArtistContributors = getContributorsForTag(tags.get("TRACKARTIST"), "performer");
                Set<Contributor> performerContributors = getContributorsForTag(tags.get("PERFORMER"), "performer");
                Set<Contributor> conductorContributors = getContributorsForTag(tags.get("CONDUCTOR"), "conductor");
                Set<Contributor> recordingContributors = new HashSet<Contributor>();

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
                if (tags.containsKey("GENRE")) {
                    createClassificationsForTag(tags.get("GENRE"), "genre", recording.getReference());
                }
                if (tags.containsKey("STYLE")) {
                    createClassificationsForTag(tags.get("STYLE"), "style", recording.getReference());
                }
                if (tags.containsKey("MOOD")) {
                    createClassificationsForTag(tags.get("MOOD"), "mood", recording.getReference());
                }

                // Create a Release entity based on the ALBUM tag
                if (tags.containsKey("ALBUM")) {
                    String albumName = tags.get("ALBUM").iterator().next();

                    // YEAR tag is optional but we use it if it exists
                    String year = null;
                    if (tags.containsKey("YEAR")) {
                        year = tags.get("YEAR").iterator().next();
                    }

                    // Create a new Release entity if it isn't already available
                    //TODO: We need to implement handling of greatest hits album here which might have exactly the same name
                    Collection<Release> releases = this.releaseCache.get(albumName.toLowerCase());
                    if (releases == null) {
                        releases = releaseRepository.findByName(albumName);
                        if (releases.size() > 0) {
                            this.releaseCache.put(albumName.toLowerCase(), releases);
                        }
                    }
                    Release release = null;
                    if (releases.size() == 0) {
                        release = new Release();
                        release.setName(albumName);
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
                        this.releaseCache.put(albumName.toLowerCase(), Arrays.asList(release));
                    } else {
                        // We use the first Release entity found if it already existsted
                        release = releases.iterator().next();
                    }

                    // Create a Track entity if there is a TRACKNUM tag
                    Track track = null;
                    if (tags.containsKey("TRACKNUM")) {
                        track = new Track();
                        String trackNum = tags.get("TRACKNUM").iterator().next();

                        // Sometimes TRACKNUM is represented as 1/10
                        if (trackNum.contains("/")) {
                            trackNum = trackNum.substring(0, trackNum.indexOf("/"));
                        }
                        track.setNumber(Integer.parseInt(trackNum));
                        track.getPlayableElements().addAll(playableElements);
                        track.setRecording(recording);
                        trackRepository.create(track);

                        // Create a Media entity if there is a DISC tag
                        if (tags.containsKey("DISC")) {
                            String discNo = tags.get("DISC").iterator().next();

                            // DISC tags can sometimes have the syntax 1/3
                            if (discNo.contains("/")) {
                                discNo = discNo.substring(0, discNo.indexOf("/"));
                            }

                            // Use existing Media entity if there is one already for this disc number
                            List<Medium> mediums = release.getMediums();
                            Medium medium = null;
                            for (Medium m : mediums) {
                                if (m.getNumber().equals(Integer.parseInt(discNo))) {
                                    medium = m;
                                }
                            }

                            // Create a new Media entity if we don't already have one
                            if (medium == null) {
                                medium = new Medium();
                                medium.setNumber(Integer.parseInt(discNo));
                                release.getMediums().add(medium);
                                mediumRepository.create(medium);
                            }
                            medium.getTracks().add(track);
                        } else {
                            release.getTracks().add(track);
                        }
                    } else {
                        // TODO: We need to handle recordings without a track number
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
                Collection<Artist> existingArtists = this.artistCache.get(artistName.toLowerCase());
                if (existingArtists == null) {
                    existingArtists = artistRepository.findByName(artistName);
                    if (existingArtists.size() > 0) {
                        this.artistCache.put(artistName.toLowerCase(), existingArtists);
                    }
                }
                if (existingArtists.size() == 0) {
                    Artist artist = new Artist();
                    artist.setName(artistName);
                    artistRepository.create(artist);
                    artists.add(artist);
                    this.artistCache.put(artistName.toLowerCase(), Arrays.asList(artist));
                } else {
                    artists.addAll(existingArtists);
                }
            }
            for (Artist artist : artists) {
                Contributor contributor = new Contributor();
                contributor.setArtist(artist);
                contributor.setType(contributorType);
                contributors.add(contributor);
            }
        }
        return contributors;
    }

    /**
     * Store Contributor entities for the set of contributors specified
     *
     * @param contributors The list of contributors to create
     */
    private void saveContributors(Set<Contributor> contributors) {
        for (Contributor contributor : contributors) {
            contributorRepository.create(contributor);
        }
    }

    /**
     * Create and save Classification entities for the specified list of classification names and classification type
     * The created Classification entities is also related to the provided SMDEntity reference.
     * If a Classification entity of the same name and type already exists, it is reused.
     *
     * @param classificationNames The list of classification names to create entities for
     * @param classificationType  Type of classification to create
     * @param reference           The reference to relate the created Classification entities to
     */
    private void createClassificationsForTag(Collection<String> classificationNames, String classificationType, SMDEntityReference reference) {
        if (classificationNames != null) {
            for (String classificationName : classificationNames) {
                TypeIdentity classificationId = new TypeIdentity(classificationType.toLowerCase(), classificationName.toLowerCase());
                Collection<Classification> existingClassifications = this.classificationCache.get(classificationId);
                if (existingClassifications == null) {
                    existingClassifications = classificationRepository.findByNameAndType(classificationName, classificationType);
                    if (existingClassifications.size() > 0) {
                        this.classificationCache.put(classificationId, existingClassifications);
                    }
                }
                if (existingClassifications.size() == 0) {
                    Classification classification = new Classification();
                    classification.setName(classificationName);
                    classification.setType(classificationType);
                    classification.getReferences().add(reference);
                    classificationRepository.create(classification);
                    this.classificationCache.put(classificationId, Arrays.asList(classification));
                } else {
                    for (Classification classification : existingClassifications) {
                        classification.getReferences().add(reference);
                    }
                }
            }
        }
    }
}
