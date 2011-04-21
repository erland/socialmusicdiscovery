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

import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;

public class DiscogsLargeSBSDataSampleCreator {
    /**
     * Roles to import from discogs
     */
    private static final Map<String, String> importedRoles = new HashMap<String, String>();

    static {
        importedRoles.put("Conductor", "CONDUCTOR");
        importedRoles.put("Written-By", "COMPOSER");
        importedRoles.put("Composed By", "COMPOSER");
        importedRoles.put("Trumpet", "PERFORMER");
        importedRoles.put("Vocals", "PERFORMER");
        importedRoles.put("Lead Vocals", "PERFROMER");
        importedRoles.put("Saxophone", "PERFROMER");
        importedRoles.put("Orchestra", "PERFORMER");
    }

    @Test(groups = {"manual"})
    public void createLargeDiscogsSBSSample() throws Exception {
        final List<List<String>> result = new ArrayList<List<String>>();

        String discogsFile = System.getProperty("org.socialmusicdiscovery.server.sampledata.discogsfile");
        if (discogsFile == null) {
            throw new RuntimeException("You need to specify a releases dump from discogs, for example with -Dorg.socialmusicdiscovery.server.sampledata.discogsfile=/tmp/discogs_20101004_releases.xml");
        }
        if (System.getProperty("org.socialmusicdiscovery.server.database.directory") == null) {
            System.setProperty("org.socialmusicdiscovery.server.database.directory", getTargetDirectory());
        }
        BufferedReader reader = new BufferedReader(new FileReader(discogsFile));

        Long noOfTracks = 10000L;
        Long minTracksPerRelease = 8L;
        if (System.getProperty("org.socialmusicdiscovery.server.sampledata.nooftracks") != null) {
            noOfTracks = Long.parseLong(System.getProperty("org.socialmusicdiscovery.server.sampledata.nooftracks"));
        }
        if (System.getProperty("org.socialmusicdiscovery.server.sampledata.mintracksperrelease") != null) {
            minTracksPerRelease = Long.parseLong(System.getProperty("org.socialmusicdiscovery.server.sampledata.mintracksperrelease"));
        }

        System.out.println("Start parsing discogs dump...");
        StringBuffer sb = new StringBuffer();
        String line;
        int i = 0;
        while ((line = reader.readLine()) != null) {
            i++;
            sb.append(line);
            if (sb.indexOf("</release>") >= 0) {
                String data = sb.substring(0, sb.indexOf("</release>") + 10);
                sb.delete(0, sb.indexOf("</release>") + 10);
                if (importRelease(data, new ArrayList<List<String>>(), minTracksPerRelease)) {
                    importRelease(data, result, minTracksPerRelease);
                }
                if (result.size() > noOfTracks) {
                    break;
                }
                if (i == 1 || i % 1000 == 0) {
                    System.out.println("Found " + result.size() + " of " + noOfTracks + " tracks");
                }
            }
        }

        try {
            System.out.println("Finished parsing discogs dump, starting to write tags file...");
            String directory = getTargetDirectory();
            FileWriter writer = new FileWriter(directory + File.separator + "simulated_"+noOfTracks+".csv");
            for (List<String> entry : result) {
                boolean first = true;
                for (String value : entry) {
                    if(!first) {
                        writer.write("|");
                    }
                    writer.write(value);
                    first = false;
                }
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean importRelease(String data, List<List<String>> result, Long minTracksPerRelease) throws Exception {

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(data.getBytes()));
        NodeList releases = doc.getElementsByTagName("release");
        for (int i = 0; i < releases.getLength(); i++) {
            Element release = (Element) releases.item(i);
            String releaseTitle = getChildrenByTagName(release, "title").get(0).getTextContent();

            if (releaseTitle.contains("<") || releaseTitle.contains(">") || releaseTitle.contains("|")) {
                // Skip releases with problematic characters
                return false;
            }

            List<Element> albumArtistsElement = getChildrenByTagName(release, "artists");
            List<String> albumArtistsList = new ArrayList<String>();
            if (albumArtistsElement.size() > 0) {
                List<Element> albumArtists = getChildrenByTagName(albumArtistsElement.get(0), "artist");
                for (Element albumArtist : albumArtists) {
                    String name = getChildrenByTagName(albumArtist, "name").get(0).getTextContent();
                    if (name.contains("<") || name.contains(">") || name.contains("|")) {
                        // Skip releases with strange artist names
                        return false;
                    }
                    if (!name.equals("Various")) {
                        albumArtistsList.add("ALBUMARTIST=" + name);
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
                        if (name.contains("<") || name.contains(">") || name.contains("|")) {
                            // Skip releases with problematic characters
                            return false;
                        }
                        albumArtistsList.add(importedRoles.get(role) + "=" + name);
                    }
                }
            }

            List<String> genresList = new ArrayList<String>();
            String includeGenres = System.getProperty("org.socialmusicdiscovery.server.sampledata.genres");
            if(includeGenres==null||!includeGenres.equalsIgnoreCase("false")) {
                List<Element> genresElement = getChildrenByTagName(release, "genres");
                if (genresElement.size() > 0) {
                    List<Element> genres = getChildrenByTagName(genresElement.get(0), "genre");
                    for (Element genre : genres) {
                            String name = genre.getTextContent();
                            if (name.contains("<") || name.contains(">") || name.contains("|")) {
                                // Skip releases with problematic characters
                                return false;
                            }
                            genresList.add("GENRE=" + name);
                    }
                }
            }

            List<String> stylesList = new ArrayList<String>();
            String includeStyles = System.getProperty("org.socialmusicdiscovery.server.sampledata.styles");
            if(includeStyles==null||!includeStyles.equalsIgnoreCase("false")) {
                List<Element> stylesElement = getChildrenByTagName(release, "styles");
                if (stylesElement.size() > 0) {
                    List<Element> styles = getChildrenByTagName(stylesElement.get(0), "style");
                    for (Element style : styles) {
                            String name = style.getTextContent();
                            if (name.contains("<") || name.contains(">") || name.contains("|")) {
                                // Skip releases with problematic characters
                                return false;
                            }
                            genresList.add("STYLE=" + name);
                    }
                }
            }

            List<Element> tracklist = getChildrenByTagName(release, "tracklist");
            if (tracklist.size() > 0) {
                Map<String, String> mediumCache = new HashMap<String, String>();
                List<Element> tracks = getChildrenByTagName(tracklist.get(0), "track");
                if (tracks.size() < minTracksPerRelease) {
                    // Skip releases with few tracks
                    return false;
                }
                String releaseId = UUID.randomUUID().toString();
                for (Element track : tracks) {
                    List<String> trackAttributes = new ArrayList<String>();
                    trackAttributes.add("ALBUM="+releaseTitle);
                    trackAttributes.addAll(genresList);
                    trackAttributes.addAll(stylesList);
                    trackAttributes.addAll(albumArtistsList);
                    String trackNumber = getChildrenByTagName(track, "position").get(0).getTextContent();
                    String trackTitle = getChildrenByTagName(track, "title").get(0).getTextContent();

                    if (trackTitle.contains("<") || trackTitle.contains(">") || trackTitle.contains("|")) {
                        // Skip releases with problematic characters
                        return false;
                    }

                    trackAttributes.add("TITLE=" + trackTitle);

                    String trackId = null;
                    String diskId = null;
                    if (trackNumber.toString().matches("^[A-Z][0-9]+$")) {
                        diskId = trackNumber.substring(0,1);
                        trackId = trackNumber.substring(1);
                    } else if (trackNumber.toString().matches("^[0-9]-[0-9]+$")) {
                        diskId = trackNumber.substring(0,1);
                        trackId = trackNumber.substring(2);
                    } else if (trackNumber.toString().matches("^[0-9]+$")) {
                        trackId = trackNumber;
                    }

                    if(diskId!=null) {
                        trackAttributes.add("DISC=" + diskId);
                    }
                    if(trackId!=null) {
                        trackAttributes.add("TRACKNUM="+trackId);
                    }
                    trackAttributes.add(0,"PATH=/music/"+releaseTitle+"_"+releaseId+"/"+(diskId!=null?diskId+"-":"")+(trackId!=null?trackId+".":"")+trackTitle+".mp3");
                    trackAttributes.add(1,"SMDID="+UUID.randomUUID().toString().replaceAll("-",""));

                    List<Element> artistsElement = getChildrenByTagName(track, "artists");
                    if (artistsElement.size() > 0) {
                        List<Element> artists = getChildrenByTagName(artistsElement.get(0), "artist");
                        for (Element artist : artists) {
                            String name = getChildrenByTagName(artist, "name").get(0).getTextContent();
                            if (name.contains("<") || name.contains(">") || name.contains("|")) {
                                // Skip releases with problematic characters
                                return false;
                            }
                            trackAttributes.add("ARTIST="+name);
                        }
                    }

                    extraArtistsElement = getChildrenByTagName(track, "extraartists");
                    if (extraArtistsElement.size() > 0) {
                        List<Element> artists = getChildrenByTagName(extraArtistsElement.get(0), "artist");
                        for (Element artist : artists) {
                            String role = getChildrenByTagName(artist, "role").get(0).getTextContent();
                            if (importedRoles.containsKey(role)) {
                                String name = getChildrenByTagName(artist, "name").get(0).getTextContent();
                                if (name.contains("<") || name.contains(">") || name.contains("|")) {
                                    // Skip releases with problematic characters
                                    return false;
                                }
                                trackAttributes.add(importedRoles.get(role) + "=" + name);
                            }
                        }
                    }
                    result.add(trackAttributes);
                }
            }
        }
        return true;
    }

    public static String getParentDirectory(String path) {
        String parentDir = "/";
        int lastIndex;

        if (path != null && path.trim().length() > 0) {
            path = path.trim();

            if (path.endsWith("/") && path.length() > 1) {
                path = path.substring(0, path.length() - 1);
            }

            if (path.length() > 1) {
                lastIndex = path.lastIndexOf("/");

                if (lastIndex > 0) {
                    parentDir = path.substring(0, lastIndex);
                }
            }
        }

        return parentDir;
    }

    public String getTargetDirectory() {
        String path = getClass().getResource("/META-INF/persistence.xml").getPath();
        if (path != null) {
            path = getParentDirectory(path);
            if (path != null) {
                path = getParentDirectory(path);
            }
            if (path != null) {
                path = getParentDirectory(path);
            }
        }
        return path;
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
