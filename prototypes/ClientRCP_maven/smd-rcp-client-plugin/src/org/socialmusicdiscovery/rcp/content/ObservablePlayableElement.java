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

package org.socialmusicdiscovery.rcp.content;

import org.socialmusicdiscovery.server.business.model.core.PlayableElement;

import com.google.gson.annotations.Expose;

public class ObservablePlayableElement extends AbstractObservableEntity<PlayableElement> implements PlayableElement {

	private static final String PROP_bitrate = "bitrate"; //$NON-NLS-1$
	private static final String PROP_format = "format"; //$NON-NLS-1$
	private static final String PROP_smdID = "smdID"; //$NON-NLS-1$
	private static final String PROP_uri = "uri"; //$NON-NLS-1$
	
	@Expose private Integer bitrate;
	@Expose private String format;
	@Expose private String smdID;
	@Expose private String uri;

	@Override
	public Integer getBitrate() {
		return bitrate;
	}

	@Override
	public String getFormat() {
		return format;
	}

	@Override
	public String getSmdID() {
		return smdID;
	}

	@Override
	public String getUri() {
		return uri;
	}

	public void setBitrate(Integer bitrate) {
		firePropertyChange(PROP_bitrate, this.bitrate, this.bitrate = bitrate);
	}

	public void setFormat(String format) {
		firePropertyChange(PROP_format, this.format, this.format = format);
	}

	public void setSmdID(String smdID) {
		firePropertyChange(PROP_smdID, this.smdID, this.smdID = smdID);
	}

	public void setUri(String uri) {
		firePropertyChange(PROP_uri, this.uri, this.uri = uri);
	}
}
