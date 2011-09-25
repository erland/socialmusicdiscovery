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

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ModelObject;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableEntity;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableMedium;

/**
 * Simple, static label provider. Returns a reasonable name for most/all known
 * entity types.
 * 
 * @author Peer Törngren
 * 
 */
public class DefaultLabelProvider extends LabelProvider implements ILabelProvider {

	private ImageManager imageManager = new ImageManager();

	@Override
	public Image getImage(Object element) {
		return element instanceof ObservableEntity ? imageManager.getEntityImage((ObservableEntity) element) : super.getImage(element);
	}

	@Override
	public String getText(Object element) {
		if (element instanceof ObservableMedium) {
			ObservableMedium m = (ObservableMedium)element;
			Integer number = m.getNumber();
			String name = getName(m);
			return name==null ? number.toString() : number + " - " + name;
		}
		if (element instanceof ModelObject) {
			return getName((ModelObject)element);
		} 
		return super.getText(element);
	}

	private String getName(ModelObject mo) {
		return mo.getName();
	}
	

}
