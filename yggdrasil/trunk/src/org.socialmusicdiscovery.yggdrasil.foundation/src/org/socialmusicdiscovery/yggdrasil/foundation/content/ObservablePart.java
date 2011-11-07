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

import org.socialmusicdiscovery.server.business.model.core.Part;
import org.socialmusicdiscovery.server.business.model.core.Work;
import org.socialmusicdiscovery.yggdrasil.foundation.util.NotYetImplemented;

import com.google.gson.annotations.Expose;

/**
 * @author Peer Törngren
 *
 */
public class ObservablePart extends ObservableWork implements Part {
	
	public static final String PROP_number = "number";
	public static final String PROP_parent = "parent";
	
	@Expose	private Integer number;
	@Expose	private Work parent;

	public ObservablePart() {
		super(Part.TYPE);
	}
	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		firePropertyChange(PROP_number, this.number, this.number = number);
	}

	public Work getParent() {
		return parent;
	}

	public void setParent(Work parent) {
		firePropertyChange(PROP_parent, this.parent, this.parent = parent);
	}

	public ObservablePart newInstance() {
		if (NotYetImplemented.confirm("Add")) {
			ObservablePart newInstance = getRoot().newInstance(getClass());
			newInstance.setParent(getParent());
			getParent().getParts().add(newInstance);
			return newInstance;
		}
		return null;
	}
	@Override
	public void delete() {
		super.delete();
		getParent().getParts().remove(this);
	}


}
