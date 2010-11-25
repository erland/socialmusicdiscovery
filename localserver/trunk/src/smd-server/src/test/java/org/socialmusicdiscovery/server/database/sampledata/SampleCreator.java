package org.socialmusicdiscovery.server.database.sampledata;

import org.socialmusicdiscovery.server.business.model.core.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.*;

public class SampleCreator {
    protected void printCollectedData(Map<String, List<String>> result) {
        for (Map.Entry<String, List<String>> entry : result.entrySet()) {
            System.out.println(entry.getKey());
            for (String line : entry.getValue()) {
                System.out.println(line);
            }
            System.out.println();
            System.out.println();
        }
    }

    protected void addSMDEntityReference(Map<String, List<String>> result, String id, String type) {
        if (result.get("smdentity_references") == null) {
            result.put("smdentity_references", new ArrayList<String>(Arrays.asList(
                    "id,type")));
        }
        result.get("smdentity_references").add(id + "," + type);
    }

    protected void addRelease(Map<String, List<String>> result, String id, String name) {
        if (result.get("releases") == null) {
            result.put("releases", new ArrayList<String>(Arrays.asList(
                    "id,name")));
        }
        result.get("releases").add(id + "," + name);
        addSMDEntityReference(result, id, Release.class.getName());
    }

    protected void addArtist(Map<String, List<String>> result, String id, String name) {
        addArtist(result, id, name, "NULL");
    }

    protected void addArtist(Map<String, List<String>> result, String id, String name, String personId) {
        if (result.get("artists") == null) {
            result.put("artists", new ArrayList<String>(Arrays.asList(
                    "id,name,person_id")));
        }
        result.get("artists").add(id + "," + name + "," + personId);
        addSMDEntityReference(result, id, Artist.class.getName());
    }

    protected void addPerson(Map<String, List<String>> result, String id, String name) {
        if (result.get("persons") == null) {
            result.put("persons", new ArrayList<String>(Arrays.asList(
                    "id,name")));
        }
        result.get("persons").add(id + "," + name);
        addSMDEntityReference(result, id, Person.class.getName());
    }

    protected void addTrack(Map<String, List<String>> result, String releaseId, String recordingId, String id) {
        addTrack(result, releaseId, recordingId, id, "NULL", null);
    }

    protected void addTrack(Map<String, List<String>> result, String releaseId, String recordingId, String id, Integer number) {
        addTrack(result, releaseId, recordingId, id, "NULL", number);
    }

    protected void addMedium(Map<String, List<String>> result, String releaseId, String id, String name) {
        addMedium(result, releaseId, id, "NULL", name);
    }

    protected void addMedium(Map<String, List<String>> result, String releaseId, String id, String number, String name) {
        if (result.get("mediums") == null) {
            result.put("mediums", new ArrayList<String>(Arrays.asList(
                    "id,release_id,number,name")));
        }
        result.get("mediums").add(id + "," + releaseId + "," + number + "," + name);
        addSMDEntityReference(result, id, Medium.class.getName());
    }

    protected void addTrack(Map<String, List<String>> result, String releaseId, String recordingId, String id, String mediumId, Integer number) {
        if (result.get("tracks") == null) {
            result.put("tracks", new ArrayList<String>(Arrays.asList(
                    "id,release_id,recording_id,medium_id,number")));
        }
        result.get("tracks").add(id + "," + releaseId + "," + recordingId + "," + mediumId + "," + (number != null ? "" + number : "NULL"));
        addSMDEntityReference(result, id, Track.class.getName());
    }

    protected void addRecording(Map<String, List<String>> result, String id, String workId, String name) {
        if (result.get("recordings") == null) {
            result.put("recordings", new ArrayList<String>(Arrays.asList(
                    "id,name,work_id")));
        }
        result.get("recordings").add(id + ",NULL," + workId);
        addSMDEntityReference(result, id, Recording.class.getName());
    }

    protected void addWork(Map<String, List<String>> result, String id, String name) {
        if (result.get("works") == null) {
            result.put("works", new ArrayList<String>(Arrays.asList(
                    "id,name")));
        }
        result.get("works").add(id + "," + name);
        addSMDEntityReference(result, id, Work.class.getName());
    }

    protected void addReleaseContributor(Map<String, List<String>> result, String releaseId, String artistId, String type) {
        if (result.get("contributors") == null) {
            result.put("contributors", new ArrayList<String>(Arrays.asList(
                    "id,artist_id,release_id,session_id,recording_id,work_id,type")));
        }
        String id = UUID.randomUUID().toString();
        addSMDEntityReference(result, id, Contributor.class.getName());
        result.get("contributors").add(id + "," + artistId + "," + releaseId + ",NULL,NULL,NULL," + type);
    }

    protected void addRecordingContributor(Map<String, List<String>> result, String recordingId, String artistId, String type) {
        if (result.get("contributors") == null) {
            result.put("contributors", new ArrayList<String>(Arrays.asList(
                    "id,artist_id,release_id,session_id,recording_id,work_id,type")));
        }
        String id = UUID.randomUUID().toString();
        addSMDEntityReference(result, id, Contributor.class.getName());
        result.get("contributors").add(id + "," + artistId + ",NULL,NULL," + recordingId + ",NULL," + type);
    }

    protected void addWorkContributor(Map<String, List<String>> result, String workId, String artistId, String type) {
        if (result.get("contributors") == null) {
            result.put("contributors", new ArrayList<String>(Arrays.asList(
                    "id,artist_id,release_id,session_id,recording_id,work_id,type")));
        }
        String id = UUID.randomUUID().toString();
        addSMDEntityReference(result, id, Contributor.class.getName());
        result.get("contributors").add(id + "," + artistId + ",NULL,NULL,NULL," + workId + "," + type);
    }

    protected List<Element> getChildrenByTagName(Element parent, String name) {
        List<Element> nodeList = new ArrayList<Element>();
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == Node.ELEMENT_NODE &&
                    name.equals(child.getNodeName())) {
                nodeList.add((Element) child);
            }
        }

        return nodeList;
    }

}