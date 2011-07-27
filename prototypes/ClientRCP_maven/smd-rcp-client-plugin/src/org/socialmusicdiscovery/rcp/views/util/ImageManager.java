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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.socialmusicdiscovery.rcp.Activator;
import org.socialmusicdiscovery.rcp.content.DataSource.Root;
import org.socialmusicdiscovery.rcp.content.ObservableEntity;

/**
 * <p>
 * A wrapper to manage image resources loaded thru the bundle context by means
 * of an internal {@link ImageRegistry}. Use the same way you would use an
 * {@link ImageRegistry}; this class simply adds a convenience layer on top. It
 * loads images from the "icons" folder in the plugin, and logs errors for
 * unresolved images. It may be extended to handle alternative icon sets by
 * simply changing the path of the icons' "home".
 * </p>
 * 
 * @author Peer Törngren
 * @see ImageRegistry
 * 
 */
public class ImageManager {
	private static final String ICON_PARENT = "icons/strawman/"; //$NON-NLS-1$
	private static final String ICON_SUFFIX = ".png"; //$NON-NLS-1$

	private final ImageRegistry imageRegistry = new ImageRegistry();
	private final Set<String> knownMissingImageNames = new HashSet<String>();

	public Image getEntityImage(ObservableEntity entity) {
		String imageName = entity.getTypeName().toLowerCase();
		return getOrLoad(imageName);
	}
	
	public Image getRootImage(Root root) {
		String imageName = root.getType().getSimpleName().toLowerCase()+"Root"; //$NON-NLS-1$
		return getOrLoad(imageName);
	}

	/**
	 * <p>Load image based on name. Surround with appropriate prefix (path) and
	 * suffix (file type). Caller only needs to know the base name of the image.
	 * If image is not found, method logs a warning and returns
	 * <code>null</code>.</p>
	 * 
	 * @param imageName
	 * @return {@link Image} or <code>null</code>
	 */
	public Image getOrLoad(String imageName) {
		Image image = imageRegistry.get(imageName);
		if (image == null) {
			String path = ICON_PARENT + imageName + ICON_SUFFIX;
			ImageDescriptor descriptor = Activator.getImageDescriptor(path);
			// ImageDescriptor img = ImageDescriptor.createFromFile(getClass(), imageFilename);
			if (descriptor == null) {
				handleMissingImage(imageName, path);
			} else {
				imageRegistry.put(imageName, descriptor);
				image = imageRegistry.get(imageName);
			}
		}
		return image;
	}

	private void handleMissingImage(String imageName, String path) {
		if (!knownMissingImageNames.contains(imageName)) {
			LogFactory.getLog(getClass()).warn("No image found: " + imageName + " (" + path + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			knownMissingImageNames.add(imageName);
		}
	}

}
