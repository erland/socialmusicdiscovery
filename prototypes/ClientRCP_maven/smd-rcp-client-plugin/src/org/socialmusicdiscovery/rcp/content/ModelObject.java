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

import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.runtime.IAdaptable;
import org.socialmusicdiscovery.rcp.event.Observable;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.Track;

/**
 * The <b>M</b> in the <b>MVC</b> pattern. All user-presentable instances must
 * implement this interface. Most implementations will probably want to extend
 * {@link AbstractObservableEntity}.
 * 
 * @author Peer Törngren
 * 
 */
public interface ModelObject extends Observable, IAdaptable {

	/**
	 * The name of the 'name' property, according to bean standards. Use
	 * primarily to define the name of events fired when the name property
	 * changes.
	 */
	String PROP_name = "name"; //$NON-NLS-1$

	/**
	 * A human readable and viewer friendly name of this instance. In most
	 * cases, this is the name of the {@link Artist}, {@link Release},
	 * {@link Track} etc. The returned string is used to present the instance in
	 * lists, tables and trees. Note that we differ between <code>null</code>
	 * and an empty string; <code>null</code> means "no name defined", an empty
	 * string means "blank name".
	 * 
	 * @return a human readable string - may be <code>null</code> if instance is
	 *         not (yet) named, or empty if name is intentionally blank
	 *         (remember the artist formerly known as Prince?)
	 */
	String getName();

	/**
	 * <p>
	 * Get an observable collection of all children of this instance. If
	 * instance has no children, method returns an empty collection.
	 * </p>
	 * <p>
	 * <b>Design note 1:</b><br>
	 * we need a specific collection type to make data binding easy; most/all
	 * data binding methods need to know if they observe a {@link List} or a
	 * {@link Set}. Implementers may return a {@link WritableList}.
	 * </p>
	 * 
	 * <p>
	 * <b>Design note 2:</b><br>
	 * We could possibly return a {@link WritableList} to allow clients to
	 * modify children. However, current expectation is that children are often
	 * derived from "drilling criteria"; i.e. the list of children depends on
	 * what type of children the client asks for. Example: for a Release, the
	 * children may be Tracks, alternative Releases, Composers, performing
	 * Artists, or .. something completely different. Hence, the expectation is
	 * that returned changes are made elsewhere, and reflected in this set.
	 * Again: this is an expectation. Time will tell what we actually need. And
	 * as stated above, some implementers may return a {@link WritableList}.
	 * </p>
	 * <p>
	 * <b>Nonsense note:</b><br>
	 * As a parent, I find the name of this method quite amusing. I wish I could
	 * implement this In Real Life ;-)<br>
	 * /Peer
	 * </p>
	 * 
	 * @return {@link IObservableList}, possibly empty (never <code>null</code>)
	 */
	IObservableList getObservableChildren();
	
	boolean hasChildren();
}