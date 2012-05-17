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

import org.apache.commons.io.input.BoundedInputStream;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.asf.io.RandomAccessFileInputstream;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.TagTextField;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTXXX;
import org.socialmusicdiscovery.server.plugins.mediaimport.TagData;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Mp3TagReader extends AbstractTagReader {
    private static final Map<String, String> mappedTags = new HashMap<String, String>();

    static {
        mappedTags.put("COMM", "COMMENT");
        mappedTags.put("TALB", "ALBUM");
        mappedTags.put("TBPM", "BPM");
        mappedTags.put("TCOM", "COMPOSER");
        mappedTags.put("TCMP", "COMPILATION");
        mappedTags.put("YTCP", "COMPILATION"); // non-standard v2.3 frame
        mappedTags.put("TCON", "GENRE");
        mappedTags.put("TYER", "YEAR"); // Recording year (2.3)
        mappedTags.put("TRDA", "YEAR"); // Recording dates (2.3)
        mappedTags.put("TDRC", "YEAR"); // Recording time (2.4)
        mappedTags.put("TDOR", "ORIGYEAR"); // Original release time (2.4)
        mappedTags.put("TORY", "ORIGYEAR"); // Original release year (2.3)
        mappedTags.put("TDRL", "RELEASEYEAR"); // Release time (2.4)
        mappedTags.put("XDOR", "ORIGYEAR"); // Original release time (2.3 unofficial)
        mappedTags.put("TIT2", "TITLE");
        mappedTags.put("TPE1", "ARTIST");
        mappedTags.put("TPE2", "BAND");
        mappedTags.put("TPE3", "CONDUCTOR");
        mappedTags.put("TPOS", "PART");
        mappedTags.put("TRCK", "TRACKNUM");
        mappedTags.put("YRVA", "RVAD");
        mappedTags.put("UFID", "MUSICBRAINZ_TRACK_ID");
        mappedTags.put("USLT", "LYRICS");
    }

    private static final Map<String, String> sortTags = new HashMap<String, String>();

    static {
        sortTags.put("TSOA", "ALBUM");
        sortTags.put("YTSA", "ALBUM");
        sortTags.put("TSOP", "ARTIST");
        sortTags.put("YTSP", "ARTIST");      // non-standard iTunes tag
        sortTags.put("TSOT", "TITLE");
        sortTags.put("TSOT", "TITLE");
        sortTags.put("YTST", "TITLE");       // non-standard iTunes tag
        sortTags.put("TST ", "TITLE");     // broken iTunes tag
        sortTags.put("TSO2", "ALBUMARTIST");
        sortTags.put("YTS2", "ALBUMARTIST"); // non-standard iTunes tag
        sortTags.put("TSOC", "COMPOSER");
        sortTags.put("YTSC", "COMPOSER");    // non-standard iTunes tag
        sortTags.put("XSOP", "ARTISTSORT");
    }

    @Override
    protected String getSortTag(String tag) {
        tag = tag.toUpperCase();
        if (sortTags.containsKey(tag)) {
            return sortTags.get(tag);
        } else {
            return super.getSortTag(tag);
        }
    }

    @Override
    protected String getMappedTagName(String tag) {
        tag = tag.toUpperCase();
        if (mappedTags.containsKey(tag)) {
            return mappedTags.get(tag);
        } else {
            return super.getMappedTagName(tag);
        }
    }

    @Override
    protected Boolean isSupportedFileType(File file) throws IOException {
        return file.getCanonicalPath().toUpperCase().endsWith(".MP3");
    }

    @Override
    protected String getFormat(File file) throws IOException {
        return "mp3";
    }

    @Override
    protected String getSmdId(File file) throws IOException {
        try {
            AudioFile f = AudioFileIO.read(file);
            MP3AudioHeader header = (MP3AudioHeader) f.getAudioHeader();

            RandomAccessFile randomAccessFile = new RandomAccessFile(file.getCanonicalPath(), "r");
            byte[] tagData = new byte[128];
            randomAccessFile.seek(randomAccessFile.length() - 128);
            randomAccessFile.read(tagData);
            ByteBuffer bBuf = ByteBuffer.allocate(128);
            bBuf.put(tagData);
            bBuf.rewind();
            byte[] tag = new byte[3];
            bBuf.get(tag);
            long length = randomAccessFile.length() - header.getMp3StartByte();
            if (new String(tag).equals("TAG")) {
                length -= 128;
            }
            randomAccessFile.seek(header.getMp3StartByte());
            InputStream is = new BoundedInputStream(new RandomAccessFileInputstream(randomAccessFile), length);
            MessageDigest m = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) > 0) {
                m.update(buffer, 0, read);
            }
            randomAccessFile.close();
            return new BigInteger(1, m.digest()).toString(16) + String.format("-%08x", length);
        } catch (TagException e) {
            throw new IOException(e);
        } catch (ReadOnlyFileException e) {
            throw new IOException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        } catch (CannotReadException e) {
            throw new IOException(e);
        } catch (InvalidAudioFrameException e) {
            throw new IOException(e);
        }
    }

    @Override
    protected TagData getTagData(TagField tagField) {
        if (tagField instanceof TagTextField) {
            TagTextField vtf = (TagTextField) tagField;
            if (tagField instanceof AbstractID3v2Frame && ((AbstractID3v2Frame) tagField).getBody() instanceof FrameBodyTXXX) {
                FrameBodyTXXX t = (FrameBodyTXXX) ((AbstractID3v2Frame) tagField).getBody();
                return new TagData(t.getDescription(), vtf.getContent(), vtf.getContent().toUpperCase());
            } else if (tagField instanceof AbstractID3v2Frame) {
                return new TagData(vtf.getId(), vtf.getContent(), vtf.getContent().toUpperCase());
            }
        }
        return null;
    }

}
