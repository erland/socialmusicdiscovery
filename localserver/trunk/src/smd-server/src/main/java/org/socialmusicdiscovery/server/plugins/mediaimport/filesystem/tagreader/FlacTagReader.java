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

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.flac.FlacStreamReader;
import org.jaudiotagger.audio.flac.metadatablock.BlockType;
import org.jaudiotagger.audio.flac.metadatablock.MetadataBlockHeader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Tag reader implementation for FLAC files
 */
public class FlacTagReader extends AbstractTagReader {

    public FlacTagReader(String separatorCharacters) {
        super(separatorCharacters);
    }

    @Override
    protected Boolean isSupportedFileType(File file) throws IOException {
        return file.getCanonicalPath().toUpperCase().endsWith(".FLAC");
    }

    @Override
    protected String getFormat(File file) throws IOException {
        return "flc";
    }

    @Override
    protected String getSmdId(File file) throws IOException {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file.getCanonicalPath(), "r");
            FlacStreamReader flacStream = new FlacStreamReader(randomAccessFile);
            flacStream.findStream();
            boolean isLastBlock = false;
            String checksum = null;
            while (!isLastBlock) {
                MetadataBlockHeader mbh = MetadataBlockHeader.readHeader(randomAccessFile);
                if (mbh.getBlockType() == BlockType.STREAMINFO) {
                    ByteBuffer rawdata = ByteBuffer.allocate(mbh.getDataLength());
                    int bytesRead = randomAccessFile.getChannel().read(rawdata);
                    if (bytesRead < mbh.getDataLength()) {
                        throw new IOException("Unable to read required number of databytes read:" + bytesRead + ":required:" + mbh.getDataLength());
                    }
                    rawdata.rewind();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 18; i < 34; i++) {
                        byte dataByte = rawdata.get(i);
                        sb.append(String.format("%02x", dataByte));
                    }
                    checksum = sb.toString();
                } else {
                    randomAccessFile.seek(randomAccessFile.getFilePointer() + mbh.getDataLength());
                }
                isLastBlock = mbh.isLastBlock();
            }
            checksum = checksum + String.format("-%08x", randomAccessFile.length() - randomAccessFile.getFilePointer());
            randomAccessFile.close();
            return checksum;
        } catch (CannotReadException e) {
            throw new IOException(e);
        }
    }
}
