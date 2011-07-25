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

package org.socialmusicdiscovery.rcp.views.util;

import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.socialmusicdiscovery.rcp.content.ObservableEntity;

public class DefaultObservableMapLabelProvider extends ObservableMapLabelProvider {
	private final ImageManager imageManager= new ImageManager();

	public DefaultObservableMapLabelProvider(IObservableMap[] observableAttributes) {
		super(observableAttributes);
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return isEntityNameColumn(element, columnIndex) ? getEntityImage((ObservableEntity) element) : super.getColumnImage(element, columnIndex);
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		return isEntityNameColumn(element, columnIndex) ? getEntityName((ObservableEntity) element) : super.getColumnText(element, columnIndex);
	}

	private boolean isEntityNameColumn(Object element, int columnIndex) {
		return columnIndex==0 && element instanceof ObservableEntity;
	}

	private String getEntityName(ObservableEntity entity) {
		// TODO better rendering, perhaps using color, font, image and/or image decorator? 
		return entity.isDirty() ? entity.getName()+" *" : entity.getName();
	}

	private Image getEntityImage(ObservableEntity entity) {
		String imageName = entity.getTypeName().toLowerCase();
		return imageManager.getOrLoad(imageName);
	}

}
