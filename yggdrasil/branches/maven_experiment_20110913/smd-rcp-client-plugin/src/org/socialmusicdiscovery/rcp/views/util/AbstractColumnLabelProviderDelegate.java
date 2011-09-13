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

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.socialmusicdiscovery.rcp.content.ModelObject;
import org.socialmusicdiscovery.rcp.content.ObservableEntity;
import org.socialmusicdiscovery.rcp.error.FatalApplicationException;
import org.socialmusicdiscovery.rcp.util.ViewerUtil;

/**
 * <p>An abstraction of common features for {@link ColumnLabelProvider}s that serve as
 * delegates in a {@link DelegatingObservableMapLabelProvider}. Primarily, this class
 * knows what properties the container must observe. Subclasses are responsible for 
 * presenting the appropriate text, images etc when requested to do so (when one of 
 * the observed properties changes).</p>
 * 
 * <p>This class offers a few convenience methods for its subclasses, in particular 
 * ways to load and dispose resources, to read property values thru reflection and 
 * handle errors when rendering fails for some reason.</p>
 * 
 * @author Peer Törngren
 * 
 * @see ViewerUtil#bind(org.eclipse.jface.viewers.StructuredViewer, org.eclipse.core.databinding.observable.set.IObservableSet, AbstractColumnLabelProviderDelegate...)
 *
 */
public abstract class AbstractColumnLabelProviderDelegate<T> extends ColumnLabelProvider  {

	protected final ImageManager imageManager = new ImageManager();
	private final List<String> propertyNamesToObserve;

	public AbstractColumnLabelProviderDelegate(String... propertyNamesToObserve) {
		this.propertyNamesToObserve = Arrays.asList(propertyNamesToObserve);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Image getImage(Object element) {
		return doGetImage((T) element);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getText(Object element) {
		try {
			return doGetText((T)element);
		} catch (Exception e) {
			LogFactory.getLog(getClass()).error("Unable to read text for element: "+element, e); //$NON-NLS-1$
			return e.getLocalizedMessage();
		}
	}


	/**
	 * This is the only method that subclasses are obliged to implement, but
	 * subclasses can of course override any superclass method to return 
	 * specific fonts, images or colors etc.
	 * 
	 * @param element
	 * @return
	 * @throws Exception
	 */
	protected abstract String doGetText(T element);

	public List<String> getPropertyNamesToObserve() {
		return propertyNamesToObserve;
	}

	protected Image doGetImage(T element) {
		return null;
	}
	
	protected String safeName(ModelObject object) {
		return object==null ? "" : object.getName();
	}

	protected String safeType(ObservableEntity object) {
		return object==null ? "" : object.getTypeName();
	}

	protected String safeNumber(Number numberOrNull) {
		return numberOrNull==null ? "" : numberOrNull.toString();
	}

	protected <V> V getValue(Object element, String propertyName) {
		try {
			return (V) PropertyUtils.getProperty(element, propertyName);
		} catch (IllegalAccessException e) {
			throw newFatalException(element, propertyName, e);
		} catch (InvocationTargetException e) {
			throw newFatalException(element, propertyName, e);
		} catch (NoSuchMethodException e) {
			throw newFatalException(element, propertyName, e);
		}
	}

	private static FatalApplicationException newFatalException(Object element, String propertyName, Throwable e) {
		return new FatalApplicationException("Unable to read property: "+element+", property: "+propertyName, e); //$NON-NLS-1$
	}

}
