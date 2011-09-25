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

package org.socialmusicdiscovery.yggdrasil.foundation.views.util;

import org.socialmusicdiscovery.yggdrasil.foundation.content.ModelObject;

/**
 * A standard label provider for any kind of {@link ModelObject} that only need
 * its name and/or image presented in the column. Caller must supply the simple
 * (NOT nested) property name that will return an instance of
 * {@link ModelObject}. If no property name is supplied, the element itself must
 * be a {@link ModelObject}, and the label provider will render the name of the
 * element.
 * 
 * @author Peer Törngren
 */
public class ModelObjectColumnLabelProvider extends AbstractColumnLabelProviderDelegate {

	private final String modelObjectPropertyName;

	public ModelObjectColumnLabelProvider(String modelObjectPropertyName) {
		super(modelObjectPropertyName+"."+ModelObject.PROP_name);
		this.modelObjectPropertyName = modelObjectPropertyName;
	}

	public ModelObjectColumnLabelProvider() {
		super(ModelObject.PROP_name);
		this.modelObjectPropertyName = null;
	}

	@Override
	protected String doGetText(Object element)  {
		ModelObject modelObject = (ModelObject) (modelObjectPropertyName==null ? element : getValue(element, modelObjectPropertyName));
		return safeName(modelObject);
	}

}
