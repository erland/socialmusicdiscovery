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

package org.socialmusicdiscovery.server.database.sampledata;

import com.sun.jersey.api.client.Client;
import org.socialmusicdiscovery.server.business.model.classification.Classification;
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
        importedRoles.put("Songwriter", Contributor.COMPOSER);
        importedRoles.put("Composed By", Contributor.COMPOSER);
        importedRoles.put("Trumpet", Contributor.PERFORMER);
        importedRoles.put("Vocals", Contributor.PERFORMER);
        importedRoles.put("Lead Vocals", Contributor.PERFORMER);
        importedRoles.put("Saxophone", Contributor.PERFORMER);
        importedRoles.put("Orchestra", Contributor.PERFORMER);
    }
    private static final String USER_AGENT = "Social Music Discovery";

    @Test(groups = {"manual"})
    public void importTheBodyguardRelease() throws Exception {
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        importRelease(result, "1794218");
        printCollectedData(result);
    }

    @Test(groups = {"manual"})
    public void importTheBodyguardReleaseAsDbUnit() throws Exception {
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        importRelease(result, "1794218");
        printCollectedDataAsDbUnit(result);
    }

    @Test(groups = {"manual"})
    public void importCorneliusRelease() throws Exception {
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        importRelease(result, "1992309");
        printCollectedData(result);
    }

    @Test(groups = {"manual"})
    public void importAristaAndRCAReleasesAsDbUnit() throws Exception {
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        Map<String, String> artistCache = new HashMap<String, String>();
        Map<String, String> personCache = new HashMap<String, String>();
        Map<String, String> labelCache = new HashMap<String, String>();
        Map<String, String> genreCache = new HashMap<String, String>();
        Map<String, String> styleCache = new HashMap<String, String>();
        importRelease(result, artistCache, personCache, labelCache, genreCache, styleCache, "1794218"); // Whitney Collection Arista
        importRelease(result, artistCache, personCache, labelCache, genreCache, styleCache, "389486");  // Whitney Arista
        importRelease(result, artistCache, personCache, labelCache, genreCache, styleCache, "1656019"); // Lisa Stanfield Arista
        importRelease(result, artistCache, personCache, labelCache, genreCache, styleCache, "2012327"); // Whitney RCA release
        importRelease(result, artistCache, personCache, labelCache, genreCache, styleCache, "2575167"); // Dolly Parton RCA release
        printCollectedDataAsDbUnit(result);
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
            String data = Client.create().resource("http://www.discogs.com/artist/" + URLEncoder.encode(discogsArtistId, "UTF-8") + "?f=xml&api_key=" + API_KEY).accept(MediaType.APPLICATION_XML).header("Accept-Encoding", "gzip").header("User-Agent",USER_AGENT).get(String.class);
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
        Map<String, String> labelCache = new HashMap<String, String>();
        Map<String, String> genreCache = new HashMap<String, String>();
        Map<String, String> styleCache = new HashMap<String, String>();
        importRelease(result, artistCache, personCache, labelCache, genreCache, styleCache, discogsReleaseId);
    }

    private void importRelease(Map<String, List<String>> result, Map<String, String> artistCache, Map<String, String> personCache, Map<String, String> labelCache, Map<String, String> genreCache, Map<String, String> styleCache, String discogsReleaseId) throws ParserConfigurationException, IOException, SAXException {
        String data = Client.create().resource("http://www.discogs.com/release/" + discogsReleaseId + "?f=xml&api_key=" + API_KEY).accept(MediaType.APPLICATION_XML).header("Accept-Encoding", "gzip").header("User-Agent",USER_AGENT).get(String.class);

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(data.getBytes()));
        NodeList releases = doc.getElementsByTagName("release");
        for (int i = 0; i < releases.getLength(); i++) {
            Element release = (Element) releases.item(i);
            String releaseTitle = getChildrenByTagName(release, "title").get(0).getTextContent();
            String releaseId = UUID.randomUUID().toString();

            String labelId = "NULL";
            List<Element> labelsElement = getChildrenByTagName(release, "labels");
            if (labelsElement.size() > 0) {
                List<Element> labels = getChildrenByTagName(labelsElement.get(0), "label");
                if (labels.size() > 0) {
                    String name = labels.get(0).getAttribute("name");
                    labelId = labelCache.get(name);
                    if (labelId == null) {
                        labelId = UUID.randomUUID().toString();
                        labelCache.put(name, labelId);
                        addLabel(result, labelId, name);
                    }
                }
            }

            addRelease(result, releaseId, releaseTitle, labelId);

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

                    if (trackNumber.toString().matches("^[A-Z][0-9]+$")) {
                        String diskId = mediumCache.get(trackNumber.substring(0, 1));
                        if (diskId == null) {
                            diskId = UUID.randomUUID().toString();
                            mediumCache.put(trackNumber.substring(0, 1), diskId);
                            addMedium(result, releaseId, diskId, trackNumber.substring(0, 1));
                        }
                        addTrack(result, releaseId, recordingId, trackId, diskId, Integer.parseInt(trackNumber.substring(1)));
                    } else if (trackNumber.toString().matches("^[0-9]-[0-9]+$")) {
                        String diskId = mediumCache.get(trackNumber.substring(0, 1));
                        if (diskId == null) {
                            diskId = UUID.randomUUID().toString();
                            mediumCache.put(trackNumber.substring(0, 1), diskId);
                            addMedium(result, releaseId, diskId, trackNumber.substring(0, 1));
                        }
                        addTrack(result, releaseId, recordingId, trackId, diskId, Integer.parseInt(trackNumber.substring(2)));
                    } else if (trackNumber.toString().matches("^[0-9]+$")) {
                        addTrack(result, releaseId, recordingId, trackId, Integer.parseInt(trackNumber));
                    } else {
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

            List<Element> genresElement = getChildrenByTagName(release, "genres");
            if (genresElement.size() > 0) {
                List<Element> genreElement = getChildrenByTagName(genresElement.get(0), "genre");
                for (Element element : genreElement) {
                    String name = element.getTextContent();
                    String id = genreCache.get(name);
                    if (id == null) {
                        id = UUID.randomUUID().toString();
                        genreCache.put(name, id);
                        addClassification(result, id, name, Classification.GENRE);
                    }
                    addClassificationReference(result, id, releaseId);
                }
            }

            List<Element> stylesElement = getChildrenByTagName(release, "styles");
            if (stylesElement.size() > 0) {
                List<Element> styleElement = getChildrenByTagName(stylesElement.get(0), "style");
                for (Element element : styleElement) {
                    String name = element.getTextContent();
                    String id = genreCache.get(name);
                    if (id == null) {
                        id = UUID.randomUUID().toString();
                        genreCache.put(name, id);
                        addClassification(result, id, name, Classification.STYLE);
                    }
                    addClassificationReference(result, id, releaseId);
                }
            }
        }
    }

    @Override
    protected String getChangedBy() {
        return "discogs";
    }
    @Override
    protected String getChangedTime() {
        return "2011-01-01T00:00:00";
    }
}
