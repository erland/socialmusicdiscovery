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

import liquibase.util.csv.opencsv.CSVReader;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationEntity;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationReferenceEntity;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public abstract class SampleCreator {
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

    protected void printCollectedDataAsDbUnit(Map<String, List<String>> result) {
        try {
            for (Map.Entry<String, List<String>> entry : result.entrySet()) {
                List<String> values = new ArrayList<String>(entry.getValue());
                String columns = values.remove(0);
                for (String line : values) {
                    System.out.print("<" + entry.getKey() + " ");
                    CSVReader columnNames = new CSVReader(new StringReader(columns));
                    CSVReader columnValues = new CSVReader(new StringReader(line));
                    String[] columnValueItems = columnValues.readNext();
                    String[] columnNameItems = columnNames.readNext();

                    for (int i = 0; i < columnNameItems.length; i++) {
                        if (!columnValueItems[i].equals("NULL")) {
                            System.out.print(columnNameItems[i] + "=\"" + columnValueItems[i].replaceAll("&", "&amp;") + "\" ");
                        }
                    }
                    System.out.println("/>");
                }
                System.out.println();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void addSMDIdentityReference(Map<String, List<String>> result, String id, String type) {
        if (result.get("smdidentity_references") == null) {
            result.put("smdidentity_references", new ArrayList<String>(Arrays.asList(
                    "id,type")));
        }
        result.get("smdidentity_references").add(id + "," + type);
    }

    protected void addRelease(Map<String, List<String>> result, String id, String name) {
        if (result.get("releases") == null) {
            result.put("releases", new ArrayList<String>(Arrays.asList(
                    "id,name,sort_as,last_updated,last_updated_by")));
        }
        result.get("releases").add(id + ",\"" + name + "\",\"" + name + "\","+getChangedTime()+","+ getChangedBy());
        addSMDIdentityReference(result, id, SMDIdentityReferenceEntity.typeForClass(ReleaseEntity.class));
    }

    protected void addRelease(Map<String, List<String>> result, String id, String name, String labelId) {
        if (result.get("releases") == null) {
            result.put("releases", new ArrayList<String>(Arrays.asList(
                    "id,name,sort_as,label_id,last_updated,last_updated_by")));
        }
        result.get("releases").add(id + ",\"" + name + "\",\"" + name + "\"," + labelId+","+getChangedTime()+","+ getChangedBy());
        addSMDIdentityReference(result, id, SMDIdentityReferenceEntity.typeForClass(ReleaseEntity.class));
    }

    protected void addLabel(Map<String, List<String>> result, String id, String name) {
        if (result.get("labels") == null) {
            result.put("labels", new ArrayList<String>(Arrays.asList(
                    "id,name,sort_as,last_updated,last_updated_by")));
        }
        result.get("labels").add(id + ",\"" + name + "\",\"" + name + "\","+getChangedTime()+","+ getChangedBy());
        addSMDIdentityReference(result, id, SMDIdentityReferenceEntity.typeForClass(LabelEntity.class));
    }

    protected void addClassification(Map<String, List<String>> result, String id, String name, String type) {
        if (result.get("classifications") == null) {
            result.put("classifications", new ArrayList<String>(Arrays.asList(
                    "id,name,sort_as,type,last_updated,last_updated_by")));
        }
        result.get("classifications").add(id + ",\"" + name + "\",\"" + name + "\"," + type+","+getChangedTime()+","+ getChangedBy());
        addSMDIdentityReference(result, id, SMDIdentityReferenceEntity.typeForClass(ClassificationEntity.class));
    }

    protected void addClassificationReference(Map<String, List<String>> result, String classificationId, String referenceId) {
        if (result.get("classification_references") == null) {
            result.put("classification_references", new ArrayList<String>(Arrays.asList(
                    "id,classification_id,reference_id,last_updated,last_updated_by")));
        }
        String id = UUID.randomUUID().toString();
        result.get("classification_references").add(id+","+classificationId + "," + referenceId+","+getChangedTime()+","+ getChangedBy());
        addSMDIdentityReference(result, id, SMDIdentityReferenceEntity.typeForClass(ClassificationReferenceEntity.class));
    }

    protected void addArtist(Map<String, List<String>> result, String id, String name) {
        addArtist(result, id, name, "NULL");
    }

    protected void addArtist(Map<String, List<String>> result, String id, String name, String personId) {
        if (result.get("artists") == null) {
            result.put("artists", new ArrayList<String>(Arrays.asList(
                    "id,name,sort_as,person_id,last_updated,last_updated_by")));
        }
        result.get("artists").add(id + ",\"" + name + "\",\"" + name + "\",\"" + personId + "\","+getChangedTime()+","+ getChangedBy());
        addSMDIdentityReference(result, id, SMDIdentityReferenceEntity.typeForClass(ArtistEntity.class));
    }

    protected void addPerson(Map<String, List<String>> result, String id, String name) {
        if (result.get("persons") == null) {
            result.put("persons", new ArrayList<String>(Arrays.asList(
                    "id,name,sort_as,last_updated,last_updated_by")));
        }
        result.get("persons").add(id + ",\"" + name + "\",\"" + name + "\","+getChangedTime()+","+ getChangedBy());
        addSMDIdentityReference(result, id, SMDIdentityReferenceEntity.typeForClass(PersonEntity.class));
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
                    "id,release_id,number,name,sort_as,last_updated,last_updated_by")));
        }
        result.get("mediums").add(id + "," + releaseId + "," + number + ",\"" + name + "\",\"" + name + "\","+getChangedTime()+","+ getChangedBy());
        addSMDIdentityReference(result, id, SMDIdentityReferenceEntity.typeForClass(MediumEntity.class));
    }

    protected void addTrack(Map<String, List<String>> result, String releaseId, String recordingId, String id, String mediumId, Integer number) {
        if (result.get("tracks") == null) {
            result.put("tracks", new ArrayList<String>(Arrays.asList(
                    "id,release_id,recording_id,medium_id,number,last_updated,last_updated_by")));
        }
        result.get("tracks").add(id + "," + releaseId + "," + recordingId + "," + mediumId + "," + (number != null ? "" + number : "NULL")+","+getChangedTime()+","+ getChangedBy());
        addSMDIdentityReference(result, id, SMDIdentityReferenceEntity.typeForClass(TrackEntity.class));
    }

    protected void addRecording(Map<String, List<String>> result, String id, String workId, String name) {
        if (result.get("recordings") == null) {
            result.put("recordings", new ArrayList<String>(Arrays.asList(
                    "id,name,last_updated,last_updated_by")));
        }
        result.get("recordings").add(id + ",NULL"+","+getChangedTime()+","+ getChangedBy());
        if (result.get("recording_works") == null) {
            result.put("recording_works", new ArrayList<String>(Arrays.asList(
                    "recording_id,work_id")));
        }
        result.get("recording_works").add(id + ",\"" + workId + "\"");
        addSMDIdentityReference(result, id, SMDIdentityReferenceEntity.typeForClass(RecordingEntity.class));
    }

    protected void addWork(Map<String, List<String>> result, String id, String name) {
        if (result.get("works") == null) {
            result.put("works", new ArrayList<String>(Arrays.asList(
                    "id,name,sort_as,last_updated,last_updated_by")));
        }
        result.get("works").add(id + ",\"" + name + "\",\"" + name + "\","+getChangedTime()+","+ getChangedBy());
        addSMDIdentityReference(result, id, SMDIdentityReferenceEntity.typeForClass(WorkEntity.class));
    }

    protected void addReleaseContributor(Map<String, List<String>> result, String releaseId, String artistId, String type) {
        if (result.get("contributors") == null) {
            result.put("contributors", new ArrayList<String>(Arrays.asList(
                    "id,artist_id,release_id,session_id,recording_id,work_id,type,last_updated,last_updated_by")));
        }
        String id = UUID.randomUUID().toString();
        addSMDIdentityReference(result, id, SMDIdentityReferenceEntity.typeForClass(ContributorEntity.class));
        result.get("contributors").add(id + "," + artistId + "," + releaseId + ",NULL,NULL,NULL," + type+","+getChangedTime()+","+ getChangedBy());
    }

    protected void addRecordingContributor(Map<String, List<String>> result, String recordingId, String artistId, String type) {
        if (result.get("contributors") == null) {
            result.put("contributors", new ArrayList<String>(Arrays.asList(
                    "id,artist_id,release_id,session_id,recording_id,work_id,type,last_updated,last_updated_by")));
        }
        String id = UUID.randomUUID().toString();
        addSMDIdentityReference(result, id, SMDIdentityReferenceEntity.typeForClass(ContributorEntity.class));
        result.get("contributors").add(id + "," + artistId + ",NULL,NULL," + recordingId + ",NULL," + type+","+getChangedTime()+","+ getChangedBy());
    }

    protected void addWorkContributor(Map<String, List<String>> result, String workId, String artistId, String type) {
        if (result.get("contributors") == null) {
            result.put("contributors", new ArrayList<String>(Arrays.asList(
                    "id,artist_id,release_id,session_id,recording_id,work_id,type,last_updated,last_updated_by")));
        }
        String id = UUID.randomUUID().toString();
        addSMDIdentityReference(result, id, SMDIdentityReferenceEntity.typeForClass(ContributorEntity.class));
        result.get("contributors").add(id + "," + artistId + ",NULL,NULL,NULL," + workId + "," + type+","+getChangedTime()+","+ getChangedBy());
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

    protected abstract String getChangedBy();
    protected abstract String getChangedTime();
}
