package org.socialmusicdiscovery.server.database.sampledata;

import com.sun.jersey.api.client.Client;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DiscogsSampleCreator extends SampleCreator {
    /**
     * API key for discogs web service, please only use this in the Social Music Discovery application
     */
    private String API_KEY = "7cee087945";

    /**
     * Roles to import from discogs
     */
    private static final Map<String, String> importedRoles = new HashMap<String, String>();

    static {
        importedRoles.put("Conductor", Contributor.CONDUCTOR);
        importedRoles.put("Written-By", Contributor.COMPOSER);
        importedRoles.put("Composed By", Contributor.COMPOSER);
        importedRoles.put("Trumpet", Contributor.PERFORMER);
        importedRoles.put("Vocals", Contributor.PERFORMER);
        importedRoles.put("Lead Vocals", Contributor.PERFORMER);
        importedRoles.put("Saxophone", Contributor.PERFORMER);
        importedRoles.put("Orchestra", Contributor.PERFORMER);
    }

    @Test(groups = {"manual"})
    public void importTheBodyguardRelease() throws Exception {
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        importRelease(result, "1794218");
        printCollectedData(result);
    }

    @Test(groups = {"manual"})
    public void importGarageActIIAndIIIRelease() throws Exception {
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        importRelease(result, "1027095");
        printCollectedData(result);
    }

    @Test(groups = {"manual"})
    public void importGarageActIRelease() throws Exception {
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        importRelease(result, "1473127");
        printCollectedData(result);
    }

    @Test(groups = {"manual"})
    public void importTheTurnOfAFriendlyCard() throws Exception {
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        importRelease(result, "1921980");
        printCollectedData(result);
    }

    /**
     * Import personal data for the specified artist from discogs.com
     *
     * @param result          A map with arrays of comma separated strings which can be used with Liquibase loadData element
     * @param personCache     A map with previously retrieved personal data to avoid overloading discogs
     * @param discogsArtistId The discogs identity of the artist to retrieve personal data for
     */
    private String importPerson(Map<String, List<String>> result, Map<String, String> personCache, String discogsArtistId) throws ParserConfigurationException, IOException, SAXException {
        if (!personCache.containsKey(discogsArtistId)) {
            String data = Client.create().resource("http://www.discogs.com/artist/" + URLEncoder.encode(discogsArtistId, "UTF-8") + "?f=xml&api_key=" + API_KEY).accept(MediaType.APPLICATION_XML).header("Accept-Encoding", "gzip").get(String.class);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(data.getBytes()));
            NodeList artists = doc.getElementsByTagName("artist");
            if (artists.getLength() > 0) {
                Element release = (Element) artists.item(0);
                String personName = getChildrenByTagName(release, "name").get(0).getTextContent();
                List<Element> personNameElement = getChildrenByTagName(release, "realname");
                if (personNameElement.size() > 0) {
                    personName = personNameElement.get(0).getTextContent();
                }
                String personId = personCache.get(personName);
                if (personId == null) {
                    personId = UUID.randomUUID().toString();
                    personCache.put(personName, personId);
                    addPerson(result, personId, personName);
                }
                return personId;
            }
            personCache.put(discogsArtistId, null);
        }
        return null;
    }

    /**
     * Import release data from discogs.com
     *
     * @param result           A map with arrays of comma separated strings which can be used with Liquibase loadData element
     * @param discogsReleaseId The identity of the release at discogs.com
     */
    private void importRelease(Map<String, List<String>> result, String discogsReleaseId) throws ParserConfigurationException, IOException, SAXException {
        Map<String, String> artistCache = new HashMap<String, String>();
        Map<String, String> personCache = new HashMap<String, String>();
        String data = Client.create().resource("http://www.discogs.com/release/" + discogsReleaseId + "?f=xml&api_key=" + API_KEY).accept(MediaType.APPLICATION_XML).header("Accept-Encoding", "gzip").get(String.class);

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(data.getBytes()));
        NodeList releases = doc.getElementsByTagName("release");
        for (int i = 0; i < releases.getLength(); i++) {
            Element release = (Element) releases.item(i);
            String releaseTitle = getChildrenByTagName(release, "title").get(0).getTextContent();
            String releaseId = UUID.randomUUID().toString();

            addRelease(result, releaseId, releaseTitle);

            List<Element> albumArtistsElement = getChildrenByTagName(release, "artists");
            if (albumArtistsElement.size() > 0) {
                List<Element> albumArtists = getChildrenByTagName(albumArtistsElement.get(0), "artist");
                for (Element albumArtist : albumArtists) {
                    String name = getChildrenByTagName(albumArtist, "name").get(0).getTextContent();
                    if (!name.equals("Various")) {
                        String id = artistCache.get(name);
                        if (id == null) {
                            id = UUID.randomUUID().toString();
                            artistCache.put(name, id);
                            String personId = importPerson(result, personCache, name);
                            addArtist(result, id, name, personId);
                        }
                        addReleaseContributor(result, releaseId, id, Contributor.PERFORMER);
                    }
                }
            }
            List<Element> extraArtistsElement = getChildrenByTagName(release, "extraartists");
            if (extraArtistsElement.size() > 0) {
                List<Element> artists = getChildrenByTagName(extraArtistsElement.get(0), "artist");
                for (Element artist : artists) {
                    String role = getChildrenByTagName(artist, "role").get(0).getTextContent();
                    if (importedRoles.containsKey(role)) {
                        String name = getChildrenByTagName(artist, "name").get(0).getTextContent();
                        String id = artistCache.get(name);
                        if (id == null) {
                            id = UUID.randomUUID().toString();
                            artistCache.put(name, id);
                            String personId = importPerson(result, personCache, name);
                            addArtist(result, id, name, personId);
                        }
                        addReleaseContributor(result, releaseId, id, importedRoles.get(role));
                    }
                }
            }

            List<Element> tracklist = getChildrenByTagName(release, "tracklist");
            if (tracklist.size() > 0) {
                Map<String, String> mediumCache = new HashMap<String, String>();
                List<Element> tracks = getChildrenByTagName(tracklist.get(0), "track");
                for (Element track : tracks) {
                    String trackNumber = getChildrenByTagName(track, "position").get(0).getTextContent();
                    String trackTitle = getChildrenByTagName(track, "title").get(0).getTextContent();
                    String trackId = UUID.randomUUID().toString();
                    String recordingId = UUID.randomUUID().toString();
                    String workId = UUID.randomUUID().toString();
                    addWork(result, workId, trackTitle);
                    addRecording(result, recordingId, workId, "NULL");
                    try {
                        if (trackNumber.startsWith("A") || trackNumber.startsWith("B") || trackNumber.startsWith("C") || trackNumber.startsWith("D")) {
                            String diskId = mediumCache.get(trackNumber.substring(0, 1));
                            if (diskId == null) {
                                diskId = UUID.randomUUID().toString();
                                mediumCache.put(trackNumber.substring(0, 1), diskId);
                                addMedium(result, releaseId, diskId, trackNumber.substring(0, 1));
                            }
                            addTrack(result, releaseId, recordingId, trackId, diskId, Integer.parseInt(trackNumber.substring(1)));
                        } else {
                            addTrack(result, releaseId, recordingId, trackId, Integer.parseInt(trackNumber));
                        }
                    } catch (NumberFormatException e) {
                        addTrack(result, releaseId, recordingId, trackId);
                    }
                    List<Element> artistsElement = getChildrenByTagName(track, "artists");
                    if (artistsElement.size() > 0) {
                        List<Element> artists = getChildrenByTagName(artistsElement.get(0), "artist");
                        for (Element artist : artists) {
                            String name = getChildrenByTagName(artist, "name").get(0).getTextContent();
                            String id = artistCache.get(name);
                            if (id == null) {
                                id = UUID.randomUUID().toString();
                                artistCache.put(name, id);
                                String personId = importPerson(result, personCache, name);
                                addArtist(result, id, name, personId);
                            }
                            addRecordingContributor(result, recordingId, id, Contributor.PERFORMER);
                        }
                    }

                    extraArtistsElement = getChildrenByTagName(track, "extraartists");
                    if (extraArtistsElement.size() > 0) {
                        List<Element> artists = getChildrenByTagName(extraArtistsElement.get(0), "artist");
                        for (Element artist : artists) {
                            String role = getChildrenByTagName(artist, "role").get(0).getTextContent();
                            if (importedRoles.containsKey(role)) {
                                String name = getChildrenByTagName(artist, "name").get(0).getTextContent();
                                String id = artistCache.get(name);
                                if (id == null) {
                                    id = UUID.randomUUID().toString();
                                    artistCache.put(name, id);
                                    String personId = importPerson(result, personCache, name);
                                    addArtist(result, id, name, personId);
                                }
                                addRecordingContributor(result, recordingId, id, importedRoles.get(role));
                            }
                        }
                    }
                }
            }
        }
    }
}
