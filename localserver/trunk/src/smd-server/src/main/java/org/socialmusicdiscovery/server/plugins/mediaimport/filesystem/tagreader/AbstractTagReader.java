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

package org.socialmusicdiscovery.server.plugins.mediaimport.filesystem.tagreader;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.TagTextField;
import org.socialmusicdiscovery.server.plugins.mediaimport.TagData;
import org.socialmusicdiscovery.server.plugins.mediaimport.TrackData;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Abstract helper class for tag readers, it's recommended that all tag readers inherit this class
 */
public abstract class AbstractTagReader implements TagReader {
    /**
     * Characters which are used to separate multiple values in a tag
     */
    private String separatorCharacters;

    /**
     * Mapping between tag name and SMD name
     */
    private static final Map<String, String> mappedTags = new HashMap<String, String>();

    static {
        mappedTags.put("ALBUM", "ALBUM");
        mappedTags.put("DISC", "DISC");
        mappedTags.put("DISCNUMBER", "DISC");

        mappedTags.put("ARTIST", "ARTIST");
        mappedTags.put("CONDUCTOR", "CONDUCTOR");
        mappedTags.put("COMPOSER", "COMPOSER");
        mappedTags.put("BAND", "BAND");
        mappedTags.put("ENSEMBLE", "BAND");
        mappedTags.put("ALBUMARTIST", "ALBUMARTIST");
        mappedTags.put("ALBUM ARTIST", "ALBUMARTIST");
        mappedTags.put("TRACKARTIST", "TRACKARTIST");
        mappedTags.put("PERFORMER", "PERFORMER");

        mappedTags.put("TITLE", "TITLE");
        mappedTags.put("TRACKNUM", "TRACKNUM");

        mappedTags.put("GENRE", "GENRE");

        mappedTags.put("DATE", "YEAR");
        mappedTags.put("YEAR", "YEAR");

        mappedTags.put("MOOD", "MOOD");
        mappedTags.put("STYLE", "STYLE");

        mappedTags.put("WORK", "WORK");
        mappedTags.put("PART", "PART");
        mappedTags.put("MOVEMENT", "PART");

        mappedTags.put("MUSICBRAINZ_ID", "MUSICBRAINZ_ID");
        mappedTags.put("MUSICBRAINZ_ALBUM_ID", "MUSICBRAINZ_ALBUM_ID");
        mappedTags.put("MUSICBRAINZ_ARTIST_ID", "MUSICBRAINZ_ARTIST_ID");
        mappedTags.put("MUSICBRAINZ_ALBUMARTIST_ID", "MUSICBRAINZ_ALBUMARTIST_ID");

        mappedTags.put("MUSICBRAINZ_ALBUM_ARTIST", "ALBUMARTIST");
        mappedTags.put("MUSICBRAINZ_ALBUM_TYPE", "ALBUMTYPE");

        mappedTags.put("TRACKNUMBER", "TRACKNUM");
        mappedTags.put("LABEL", "LABEL");
        mappedTags.put("PUBLISHER", "LABEL");
        mappedTags.put("ORGANIZATION", "LABEL");

        mappedTags.put("COUNTRY", "COUNTRY");

        mappedTags.put("DISCOGS_RELEASED", "RELEASEYEAR");
        mappedTags.put("ORIGYEAR", "ORIGYEAR");
        mappedTags.put("RELEASEYEAR", "RELEASEYEAR");
        mappedTags.put("RELEASE DATE", "RELEASEYEAR");

        mappedTags.put("DISCOGS_LABEL_LINK", "DISCOGS_LABEL_LINK");
        mappedTags.put("DISCOGS_RELEASE_ID", "DISCOGS_RELEASE_ID");
        mappedTags.put("DISCOGS_ARTIST_LINK", "DISCOGS_ARTIST_LINK");

        mappedTags.put("DISCNUMBER", "DISC");
    }

    /**
     * Mapping between sort tags and their respective normal tags
     */
    private static final Map<String, String> sortTags = new HashMap<String, String>();

    static {
        sortTags.put("ALBUMSORT", "ALBUM");

        sortTags.put("ARTISTSORT", "ARTIST");
        sortTags.put("COMPOSERSORT", "COMPOSER");
        sortTags.put("CONDUCTORSORT", "CONDUCTOR");
        sortTags.put("BANDSORT", "BAND");
        sortTags.put("ALBUMARTISTSORT", "ALBUMARTIST");
        sortTags.put("TRACKARTISTSORT", "TRACKARTIST");
        sortTags.put("PERFORMERSORT", "PERFORMER");

        sortTags.put("TITLESORT", "TITLE");

        sortTags.put("GENRESORT", "GENRE");
        sortTags.put("MOODSORT", "MOOD");
        sortTags.put("STYLESORT", "STYLE");

        sortTags.put("WORKSORT", "WORK");
        sortTags.put("PARTSORT", "PART");
        sortTags.put("MOVEMENTSORT", "MOVEMENT");

        sortTags.put("MUSICBRAINZ_ALBUMARTISTSORT", "ALBUMARTIST");
    }

    protected AbstractTagReader(String separatorCharacters) {
        this.separatorCharacters = separatorCharacters;
    }

    /**
     * Checks if this tag reader support this type of file
     *
     * @param file The file to check
     * @return true if supported, else false
     * @throws IOException
     */
    protected abstract Boolean isSupportedFileType(File file) throws IOException;

    /**
     * Get format identity of this file
     *
     * @param file The file to get format for
     * @return The format identity
     * @throws IOException
     */
    protected abstract String getFormat(File file) throws IOException;

    /**
     * Get or calculate the SMDId for this file
     *
     * @param file The file to get/calculate SMDId for
     * @return The SMDId for the file
     * @throws IOException
     */
    protected abstract String getSmdId(File file) throws IOException;

    /**
     * Get the name of the sort tag for the specified tag
     *
     * @param tag The tag to get sort tag name for
     * @return The name of the sort tag or null if no sort tag is defined for this tag
     */
    protected String getSortTag(String tag) {
        tag = tag.toUpperCase();
        if (sortTags.containsKey(tag)) {
            return sortTags.get(tag);
        } else {
            return null;
        }
    }

    /**
     * Get the SMD name for the specified tag
     *
     * @param tag The tag to get the SMD name for
     * @return The SMD name for the tag or null if this tag isn't supported by SMD
     */
    protected String getMappedTagName(String tag) {
        tag = tag.toUpperCase();
        if (mappedTags.containsKey(tag)) {
            return mappedTags.get(tag);
        } else {
            return null;
        }
    }

    /**
     * Get tag data for the specified tag
     *
     * @param tagField The tag to get tag data for
     * @return The tag data or null if no tag data could be retrieved
     */
    protected TagData getTagData(TagField tagField) {
        if (tagField instanceof TagTextField) {
            TagTextField vtf = (TagTextField) tagField;
            return new TagData(vtf.getId(), vtf.getContent(), vtf.getContent().toUpperCase());
        }
        return null;
    }

    /**
     * Basic implementation that uses the abstract methods implemented by sub classes to
     * retrieve tag data for the specified file
     *
     * @param file The file to read tags from
     * @return The read tag data or null no data could be read with this tag reader
     * @throws IOException
     */
    @Override
    public TrackData getTrackData(File file) throws IOException {
        if (!file.exists() || !isSupportedFileType(file)) {
            return null;
        }
        try {
            TrackData result = new TrackData();
            result.setFile(file.getCanonicalPath());
            result.setUrl(file.toURI().toString());
            result.setFormat(getFormat(file));
            result.setSmdID(getSmdId(file));
            result.setTags(new ArrayList<TagData>());
            Map<String, List<String>> normalTags = new HashMap<String, List<String>>();
            Map<String, List<String>> sortTags = new HashMap<String, List<String>>();
            AudioFile f = AudioFileIO.read(file);
            Tag tag = f.getTag();
            Iterator<TagField> it = tag.getFields();
            while (it.hasNext()) {
                TagField tf = it.next();
                TagData tagData = getTagData(tf);
                if (tagData != null) {
                    String[] values;
                    if (separatorCharacters != null && separatorCharacters.trim().length() > 0) {
                        values = tagData.getValue().split("[" + separatorCharacters + "]");
                    } else {
                        values = new String[]{tagData.getValue()};
                    }
                    for (String value : values) {
                        String name = getMappedTagName(tagData.getName());
                        if (name != null) {
                            if (normalTags.containsKey(name)) {
                                normalTags.get(name).add(value);
                            } else {
                                normalTags.put(name, new ArrayList<String>(Arrays.asList(value)));
                            }
                        } else if (getSortTag(tagData.getName()) != null) {
                            String sortName = getSortTag(tagData.getName());
                            if (sortTags.containsKey(sortName)) {
                                sortTags.get(sortName).add(value);
                            } else {
                                sortTags.put(sortName, new ArrayList<String>(Arrays.asList(value)));
                            }
                        }
                    }
                }
            }
            for (Map.Entry<String, List<String>> entry : normalTags.entrySet()) {
                List<String> values = entry.getValue();
                List<String> sortValues = null;
                if (sortTags.containsKey(entry.getKey())) {
                    sortValues = sortTags.get(entry.getKey());
                }
                int i = 0;
                for (String value : values) {
                    String sortValue = null;
                    if (sortValues != null && sortValues.size() > i) {
                        sortValue = sortValues.get(i);
                    }
                    result.getTags().add(new TagData(entry.getKey(), value, sortValue));
                }
            }
            return result;
        } catch (TagException e) {
            throw new IOException(e);
        } catch (ReadOnlyFileException e) {
            throw new IOException(e);
        } catch (CannotReadException e) {
            throw new IOException(e);
        } catch (InvalidAudioFrameException e) {
            throw new IOException(e);
        }
    }
}
