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

package org.socialmusicdiscovery.yggdrasil.foundation.content;

import java.util.Date;
import java.util.List;

import org.socialmusicdiscovery.server.business.model.core.Part;
import org.socialmusicdiscovery.server.business.model.core.Work;
import org.socialmusicdiscovery.yggdrasil.foundation.util.ChangeReplicator;
import org.socialmusicdiscovery.yggdrasil.foundation.util.GenericWritableList;
import org.socialmusicdiscovery.yggdrasil.foundation.util.PartOrderManager;

import com.google.gson.annotations.Expose;

/**
 * @author Peer Törngren
 */
public class ObservableWork extends AbstractContributableEntity<Work> implements Work {

	public static final String PROP_date = "date";
	public static final String PROP_parts = "parts";
	
	@Expose private Date date;
	private GenericWritableList<Part> parts = new GenericWritableList<Part>();
	
	public ObservableWork() {
		this(Work.TYPE);
	}
	
	/**
	 * Exposed for subclasses that need to declare their specific type.
	 */
	protected ObservableWork(String type) {
		super(type);
	}
	
	@Override
	protected void postInflate() {
		getParts().addAll(asOrderedList(getRoot().findAll(this)));
		ChangeReplicator.replicate(getParts(), this, PROP_parts);
		PartOrderManager.manage(getParts());
	}

	@Override
	public Date getDate() {
		return date;
	}
	
	@Override
	public GenericWritableList<Part> getParts() {
		return parts;
	}

	public void setDate(Date date) {
		firePropertyChange(PROP_date, this.date, this.date = date);
	}
	public void setParts(List<Part> parts) {
		updateList(PROP_parts, this.parts, parts);
	}
	
}
