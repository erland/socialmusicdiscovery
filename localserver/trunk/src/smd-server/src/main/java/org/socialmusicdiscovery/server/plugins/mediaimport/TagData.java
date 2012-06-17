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

package org.socialmusicdiscovery.server.plugins.mediaimport;

public class TagData {
    public static final String ARTIST = "ARTIST";
    public static final String ALBUMARTIST = "ALBUMARTIST";
    public static final String TRACKARTIST = "TRACKARTIST";
    public static final String COMPOSER = "COMPOSER";
    public static final String CONDUCTOR = "CONDUCTOR";
    public static final String PERFORMER = "PERFORMER";
    public static final String ALBUM = "ALBUM";
    public static final String WORK = "WORK";
    public static final String TITLE = "TITLE";
    public static final String DISC = "DISC";
    public static final String TRACKNUM = "TRACKNUM";
    public static final String YEAR = "YEAR";
    public static final String GENRE = "GENRE";
    public static final String MOOD = "MOOD";
    public static final String STYLE = "STYLE";

    public static final String MUSICBRAINZ_ARTIST_ID = "MUSICBRAINZ_ARTIST_ID";
    public static final String MUSICBRAINZ_ALBUMARTIST_ID = "MUSICBRAINZ_ALBUMARTIST_ID";
    public static final String MUSICBRAINZ_ALBUM_ID = "MUSICBRAINZ_ALBUM_ID";
    public static final String MUSICBRAINZ_TRACK_ID = "MUSICBRAINZ_TRACK_ID";

    public static final String SPOTIFY_ARTIST_ID = "SPOTIFY_ARTIST_ID";
    public static final String SPOTIFY_ALBUM_ID = "SPOTIFY_ALBUM_ID";
    public static final String SPOTIFY_TRACK_ID = "SPOTIFY_TRACK_ID";

    public static final String DISCOGS_RELEASE_ID = "DISCOGS_RELEASE_ID";

    public static final String SBS_COVER_ID = "SBSCOVERID";

    private String name;
    private String value;
    private String sortValue;

    public TagData() {
    }

    public TagData(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public TagData(String name, String value, String sortValue) {
        this.name = name;
        this.value = value;
        this.sortValue = sortValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSortValue() {
        return sortValue;
    }

    public void setSortValue(String sortValue) {
        this.sortValue = sortValue;
    }
}
