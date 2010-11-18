package org.socialmusicdiscovery.server.plugins.mediaimport.squeezeboxserver;

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
