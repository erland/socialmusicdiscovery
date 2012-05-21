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

package org.socialmusicdiscovery.server.plugins.mediaimport.filesystem;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.api.mediaimport.MediaImporter;
import org.socialmusicdiscovery.server.api.mediaimport.ProcessingStatusCallback;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameter;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameterEntity;
import org.socialmusicdiscovery.server.business.model.core.PlayableElementEntity;
import org.socialmusicdiscovery.server.business.repository.core.PlayableElementRepository;
import org.socialmusicdiscovery.server.plugins.mediaimport.AbstractTagImporter;
import org.socialmusicdiscovery.server.plugins.mediaimport.TrackData;
import org.socialmusicdiscovery.server.plugins.mediaimport.filesystem.tagreader.FlacTagReader;
import org.socialmusicdiscovery.server.plugins.mediaimport.filesystem.tagreader.Mp3TagReader;
import org.socialmusicdiscovery.server.plugins.mediaimport.filesystem.tagreader.TagReader;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;

/**
 * Media import module for that scans file system for music
 */
public class FileSystem extends AbstractTagImporter implements MediaImporter {
    @Inject
    private PlayableElementRepository playableElementRepository;

    /**
     * @inherit
     */
    public String getId() {
        return "filesystem";
    }

    /**
     * @inherit
     */
    @Override
    public void init(Map<String, String> executionParameters) {
        super.init(executionParameters);
    }

    /**
     * @inherit
     */
    public void executeImport(ProcessingStatusCallback progressHandler) {

        String musicFoldersParameter = getConfiguration().getStringParameter("musicfolders");
        if (musicFoldersParameter != null && musicFoldersParameter.trim().length() > 0) {
            String[] musicFolders = musicFoldersParameter.split(",");

            for (String musicFolder : musicFolders) {
                musicFolder = musicFolder.trim();
                File directory = new File(musicFolder);
                if (directory.isDirectory()) {
                    List<File> files = scanDirectory(directory);
                    scanFiles(files, progressHandler);
                }
            }
        }
        if (isAborted()) {
            progressHandler.aborted(getId());
        } else {
            progressHandler.finished(getId());
        }
    }

    /**
     * Scans the specified directory for files of supported file extensions
     *
     * @param directory Directory to scan
     * @return List of files found
     */
    List<File> scanDirectory(File directory) {
        List<File> files = new ArrayList<File>();
        if (directory.isDirectory()) {
            files.addAll(Arrays.asList(directory.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    Boolean shouldScan = true;
                    if (getExecutionConfiguration().getBooleanParameter("incremental", Boolean.FALSE)) {
                        Collection<PlayableElementEntity> playableElements = playableElementRepository.findByURIWithRelations(file.toURI().toString(), null, null);
                        for (PlayableElementEntity playableElement : playableElements) {
                            if (playableElement != null && playableElement.getLastModified().equals(file.lastModified())) {
                                shouldScan = false;
                                break;
                            }
                        }
                    }
                    return shouldScan &&
                            !file.isDirectory() &&
                            !file.isHidden() && (
                            file.getName().toUpperCase().endsWith(".FLAC") ||
                                    file.getName().toUpperCase().endsWith(".MP3"));
                }
            })));
            File[] directories = directory.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory();
                }
            });
            for (File dir : directories) {
                files.addAll(scanDirectory(dir));
            }
        }
        return files;
    }

    /**
     * Read tags from the list of files and import them into the SMD database
     *
     * @param files           Files to read
     * @param progressHandler Progress handler that should be used to report status
     */
    private void scanFiles(List<File> files, ProcessingStatusCallback progressHandler) {
        long i = 0;
        long offset = 0;
        final long CHUNK_SIZE = 20;
        for (File file : files) {
            if (!isAborted()) {
                if (i == 0) {
                    entityManager.getTransaction().begin();
                }
                try {
                    TrackData track = scanFile(file);
                    progressHandler.progress(getId(), track.getFile(), offset + 1, (long) files.size());
                    if (track != null) {
                        try {
                            importNewPlayableElement(track);
                        } catch (ConstraintViolationException e) {
                            //TODO: Change this so it uses the logging framework
                            System.err.println("ERROR when importing: " + track.getFile() + ": ");
                            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                                System.err.println("- " + violation.getLeafBean().getClass().getSimpleName() + "." + violation.getPropertyPath().toString() + ": " + violation.getMessage());
                            }
                        }
                    } else {
                        System.err.println("ERROR when importing: " + file.getCanonicalPath() + ": ");
                        System.err.println("- Unable to read tags");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    //progressHandler.failed(getId(), e.getLocalizedMessage());
                }

                i++;
                offset++;
                if (i >= CHUNK_SIZE) {
                    entityManager.flush();
                    entityManager.clear();
                    entityManager.getTransaction().commit();
                    i = 0;
                }
            }
        }
        if (entityManager.getTransaction().isActive()) {
            entityManager.flush();
            entityManager.clear();
            entityManager.getTransaction().commit();
        }
    }


    /**
     * Read tags from specified file and return the tag data
     *
     * @param file File to read
     * @return Tag data read from the file
     * @throws IOException If tags can't be read of file can't be accessed
     */
    TrackData scanFile(File file) throws IOException {
        String separatorCharacters = getConfiguration().getStringParameter("separatorCharacters");
        TagReader[] tagReaders = {new FlacTagReader(separatorCharacters), new Mp3TagReader(separatorCharacters)};
        for (TagReader tagReader : tagReaders) {
            TrackData data = tagReader.getTrackData(file);
            if (data != null) {
                data.setLastModified(file.lastModified());
                return data;
            }
        }
        return null;
    }

    @Override
    public Collection<ConfigurationParameter> getDefaultConfiguration() {
        Collection<ConfigurationParameter> parameters = super.getDefaultConfiguration();
        parameters.add(new ConfigurationParameterEntity("musicfolders", ConfigurationParameter.Type.STRING, ""));
        parameters.add(new ConfigurationParameterEntity("separatorCharacters", ConfigurationParameter.Type.STRING, ";"));
        return parameters;
    }
}
