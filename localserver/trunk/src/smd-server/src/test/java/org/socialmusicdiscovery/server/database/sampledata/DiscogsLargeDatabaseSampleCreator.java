package org.socialmusicdiscovery.server.database.sampledata;

import liquibase.ClassLoaderFileOpener;
import liquibase.Liquibase;
import liquibase.exception.JDBCException;
import liquibase.exception.LiquibaseException;
import org.socialmusicdiscovery.server.api.mediaimport.ProcessingStatusCallback;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.logic.SearchRelationPostProcessor;
import org.socialmusicdiscovery.server.business.logic.injections.database.DatabaseProvider;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DiscogsLargeDatabaseSampleCreator extends SampleCreator {
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
    public void createLargeDiscogsSample() throws Exception {
        final Map<String, List<String>> result = new HashMap<String, List<String>>();
        final Map<String, String> artistCache = new HashMap<String, String>();

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
                if (importRelease(data, new HashMap<String, List<String>>(), new HashMap<String, String>(), minTracksPerRelease)) {
                    importRelease(data, result, artistCache, minTracksPerRelease);
                }
                if (result.get("tracks") != null && result.get("tracks").size() > noOfTracks) {
                    break;
                }
                if (result.get("tracks") != null && (i == 1 || i % 1000 == 0)) {
                    System.out.println("Found " + result.get("tracks").size() + " of " + noOfTracks + " tracks");
                }
            }
        }

        try {
            System.out.println("Finished parsing discogs dump, starting to load data into database...");
            String directory = getTestClassesDirectory() + File.separator + "org" + File.separator + "socialmusicdiscovery" + File.separator + "server" + File.separator + "database" + File.separator + "sampledata" + File.separator + "large";
            new File(directory).mkdir();
            for (Map.Entry<String, List<String>> entry : result.entrySet()) {
                FileWriter writer = new FileWriter(directory + File.separator + entry.getKey() + ".csv");
                for (String value : entry.getValue()) {
                    writer.write(value);
                    writer.write("\n");
                }
                writer.close();
            }

            DatabaseProvider provider = null;
            String database = InjectHelper.instanceWithName(String.class, "org.socialmusicdiscovery.server.database");
            if (database != null) {
                provider = InjectHelper.instanceWithName(DatabaseProvider.class, database);
                if (provider == null) {
                    throw new RuntimeException("No database provider exists for: " + database);
                }
            } else {
                throw new RuntimeException("No database provider configured");
            }
            provider.start();
            Connection connection = provider.getConnection();
            Liquibase liquibase = new Liquibase("org/socialmusicdiscovery/server/database/smd-database.changelog.xml", new
                    ClassLoaderFileOpener(),
                    connection);
            if (System.getProperty("liquibase") == null || !System.getProperty("liquibase").equals("false")) {
                liquibase.update("");
            }
            liquibase = new Liquibase("org/socialmusicdiscovery/server/database/sampledata/large/large.xml", new
                    ClassLoaderFileOpener(),
                    connection);
            liquibase.update("");

            // Ensure that we don't delete the database contents
            System.setProperty("hibernate.hbm2ddl.auto", "validate");
            System.out.println("Starting to update search relations...");
            new SearchRelationPostProcessor().execute(new ProcessingStatusCallback() {
                public void progress(String module, String currentDescription, Long currentNo, Long totalNo) {
                    System.out.println(currentNo + " of " + totalNo + ": " + currentDescription);
                }

                public void failed(String module, String error) {
                    System.err.println("Failed with error: " + error);
                }

                public void finished(String module) {
                    System.out.println("Finish updating search relations");
                }

                public void aborted(String module) {
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (JDBCException e) {
            throw new RuntimeException(e);
        } catch (LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean importRelease(String data, Map<String, List<String>> result, Map<String, String> artistCache, Long minTracksPerRelease) throws Exception {

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(data.getBytes()));
        NodeList releases = doc.getElementsByTagName("release");
        for (int i = 0; i < releases.getLength(); i++) {
            Element release = (Element) releases.item(i);
            String releaseTitle = getChildrenByTagName(release, "title").get(0).getTextContent();
            String releaseId = UUID.randomUUID().toString();

            if (releaseTitle.contains("<") || releaseTitle.contains(">") || releaseTitle.contains(",") || releaseTitle.contains("\"")) {
                // Skip releases with problematic characters
                return false;
            }
            addRelease(result, releaseId, releaseTitle);

            List<Element> albumArtistsElement = getChildrenByTagName(release, "artists");
            if (albumArtistsElement.size() > 0) {
                List<Element> albumArtists = getChildrenByTagName(albumArtistsElement.get(0), "artist");
                for (Element albumArtist : albumArtists) {
                    String name = getChildrenByTagName(albumArtist, "name").get(0).getTextContent();
                    if (name.contains("<") || name.contains(">") || name.contains(",") || name.contains("\"")) {
                        // Skip releases with strange artist names
                        return false;
                    }
                    if (!name.equals("Various")) {
                        String id = artistCache.get(name);
                        if (id == null) {
                            id = UUID.randomUUID().toString();
                            artistCache.put(name, id);
                            addArtist(result, id, name);
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
                        if (name.contains("<") || name.contains(">") || name.contains(",") || name.contains("\"")) {
                            // Skip releases with problematic characters
                            return false;
                        }
                        if (id == null) {
                            id = UUID.randomUUID().toString();
                            artistCache.put(name, id);
                            addArtist(result, id, name);
                        }
                        addReleaseContributor(result, releaseId, id, importedRoles.get(role));
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
                for (Element track : tracks) {
                    String trackNumber = getChildrenByTagName(track, "position").get(0).getTextContent();
                    String trackTitle = getChildrenByTagName(track, "title").get(0).getTextContent();
                    String trackId = UUID.randomUUID().toString();
                    String recordingId = UUID.randomUUID().toString();
                    String workId = UUID.randomUUID().toString();

                    if (trackTitle.contains("<") || trackTitle.contains(">") || trackTitle.contains(",") || trackTitle.contains("\"")) {
                        // Skip releases with problematic characters
                        return false;
                    }

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
                            if (name.contains("<") || name.contains(">") || name.contains(",") || name.contains("\"")) {
                                // Skip releases with problematic characters
                                return false;
                            }
                            String id = artistCache.get(name);
                            if (id == null) {
                                id = UUID.randomUUID().toString();
                                artistCache.put(name, id);
                                addArtist(result, id, name);
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
                                if (name.contains("<") || name.contains(">") || name.contains(",") || name.contains("\"")) {
                                    // Skip releases with problematic characters
                                    return false;
                                }
                                if (id == null) {
                                    id = UUID.randomUUID().toString();
                                    artistCache.put(name, id);
                                    addArtist(result, id, name);
                                }
                                addRecordingContributor(result, recordingId, id, importedRoles.get(role));
                            }
                        }
                    }
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

    public String getTestClassesDirectory() {
        String path = getClass().getResource("/META-INF/persistence.xml").getPath();
        if (path != null) {
            path = getParentDirectory(path);
            if (path != null) {
                path = getParentDirectory(path);
            }
        }
        return path;
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
}
