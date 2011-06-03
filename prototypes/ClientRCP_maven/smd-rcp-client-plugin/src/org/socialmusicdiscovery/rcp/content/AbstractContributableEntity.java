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

import java.beans.Introspector;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.socialmusicdiscovery.rcp.content.DataSource.Root;
import org.socialmusicdiscovery.rcp.util.ChangeMonitor;
import org.socialmusicdiscovery.rcp.util.ChangeReplicator;
import org.socialmusicdiscovery.rcp.util.GenericWritableSet;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.core.Contributor;

import com.google.gson.annotations.Expose;

/**
 * A common abstraction for all entities that can have contributors. Use to
 * enable common handling in shared widgets and methods. Should ideally be an
 * interface, but that causes interference with implemented interfaces in
 * different ways.
 * 
 * @author Peer Törngren
 * 
 */
public abstract class AbstractContributableEntity<T extends SMDIdentity>  extends AbstractEditableEntity<T> {

	public static final String PROP_contributors = "contributors";

	private GenericWritableSet<Contributor> contributors = new GenericWritableSet<Contributor>();
	@SuppressWarnings("unused")
	@Expose private String objectType; // used by the AbstractJSONProvider through getObjectTypeConversionMap 

	public AbstractContributableEntity(String objectType) {
		super();
		this.objectType = objectType;
	}

	/**
	 * <p>
	 * Get all contributors to this entity.
	 * </p>
	 * <p>
	 * <b>Note:</b> Subclasses <b>must</b> override this method to make sure we
	 * pick up the proper generic element type. It is enough to simply call the
	 * superclass implementation, i.e. this method.
	 * </p>
	 * <p>
	 * Rationale: The way we pick up the method signature thru
	 * {@link PropertyUtils#getPropertyDescriptors(Class)} and the
	 * {@link Introspector} returns the interface method before the superclass
	 * method. This causes the {@link ChangeMonitor} to fail since it doesn't
	 * understand what element type to listen for. A proper solution is not
	 * easily implemented since the behavior seems inherent to Java reflection.
	 * See further comments on the {@link ChangeMonitor}.
	 * </p>
	 * @see ChangeMonitor
	 */
	public GenericWritableSet<Contributor> getContributors() {
		return contributors;		
	}

	/**
	 * This method is really superfluous since we sport a {@link GenericWritableSet},
	 * but as the interface declares it we need to implement it. For databinding, use 
	 * the {@link GenericWritableSet} directly instead of binding to this property; binding 
	 * to this property will effectively wrap an observable set in another observable set. 
	 *      
	 * @param contributors
	 */
	public void setContributors(Set<Contributor> contributors) {
		updateSet(PROP_contributors, this.contributors, contributors);
	}

	/**
	 * Override to inflate all contributors and listen for changes.     
	 */
	@Override
	protected void postInflate() {
		super.postInflate();
		Root<Contributor> root = getDataSource().resolveRoot(Contributor.class);
		Collection<ObservableContributor> allContributors = root.findAll(this);
		inflateAll(allContributors);
		contributors.addAll(allContributors);
		hookContributorsListener();
	}

	/**
	 * Override to listen for contributors.     
	 */
	@Override
	protected void postCreate() {
		super.postCreate();
		hookContributorsListener();
	}
	
	private void hookContributorsListener() {
		ChangeReplicator.replicate(contributors, this, PROP_contributors);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <D extends AbstractDependentEntity> Collection<D> getDeletableDependents() {
		inflate();
		Collection<D> deletableDependents = super.getDeletableDependents();
		deletableDependents.addAll(getContributors());
		return deletableDependents;
	}

}
