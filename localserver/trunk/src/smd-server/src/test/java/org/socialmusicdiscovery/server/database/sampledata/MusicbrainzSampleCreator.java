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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MusicbrainzSampleCreator extends SampleCreator {
    @Test(groups = {"manual"})
    public void importRelease() throws Exception {
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        importRelease(result, "11cafb9e-5fbc-49c7-b920-4ff754e03e93");
        printCollectedData(result);
    }

    /**
     * Import data from the specified release identity from musicbrainz.org
     *
     * @param result               A map with arrays of comma separated strings which can be used with Liquibase loadData element
     * @param musicbrainzReleaseId The musicbrainz release identity to import
     */
    private void importRelease(Map<String, List<String>> result, String musicbrainzReleaseId) throws ParserConfigurationException, IOException, SAXException {
        Map<String, String> artistCache = new HashMap<String, String>();
        String data = Client.create().resource("http://musicbrainz.org/ws/1/release/" + musicbrainzReleaseId + "?type=xml&inc=tracks+artist").accept(MediaType.APPLICATION_XML).get(String.class);
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(data.getBytes()));
        NodeList releases = doc.getElementsByTagName("release");
        for (int i = 0; i < releases.getLength(); i++) {
            Element release = (Element) releases.item(i);
            String releaseTitle = getChildrenByTagName(release, "title").get(0).getTextContent();
            String releaseId = UUID.randomUUID().toString();

            addRelease(result, releaseId, releaseTitle);

            List<Element> albumArtists = getChildrenByTagName(release, "artist");
            for (Element albumArtist : albumArtists) {
                String name = getChildrenByTagName(albumArtist, "name").get(0).getTextContent();
                String id = artistCache.get(name);
                if (id == null) {
                    id = UUID.randomUUID().toString();
                    artistCache.put(name, id);
                    addArtist(result, id, name);
                }
                addReleaseContributor(result, releaseId, id, Contributor.PERFORMER);
            }

            List<Element> tracklist = getChildrenByTagName(release, "track-list");
            if (tracklist.size() > 0) {
                List<Element> tracks = getChildrenByTagName(tracklist.get(0), "track");
                int trackNumber = 1;
                for (Element track : tracks) {
                    String trackTitle = getChildrenByTagName(track, "title").get(0).getTextContent();
                    String trackId = UUID.randomUUID().toString();
                    String recordingId = UUID.randomUUID().toString();
                    String workId = UUID.randomUUID().toString();
                    addWork(result, workId, trackTitle);
                    addRecording(result, recordingId, workId, "NULL");
                    addTrack(result, releaseId, recordingId, trackId, trackNumber);
                    List<Element> artists = getChildrenByTagName(track, "artist");
                    for (Element artist : artists) {
                        String name = getChildrenByTagName(artist, "name").get(0).getTextContent();
                        String id = artistCache.get(name);
                        if (id == null) {
                            id = UUID.randomUUID().toString();
                            artistCache.put(name, id);
                            addArtist(result, id, name);
                        }
                        addRecordingContributor(result, recordingId, id, Contributor.PERFORMER);
                    }
                    trackNumber++;
                }
            }
        }
    }
}
