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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.socialmusicdiscovery.rcp.content.DataSource.Root;
import org.socialmusicdiscovery.rcp.util.NotYetImplemented;
import org.socialmusicdiscovery.rcp.util.TextUtil;
import org.socialmusicdiscovery.rcp.util.Util;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.support.copy.CopyHelper;

import com.google.gson.annotations.Expose;

/**
 * <p>
 * The root abstraction of all elements that we edit in the client. The class
 * implements a number of interfaces that are typical for "primary" client
 * objects; these can be deleted, inserted and edited as first class objects.
 * </p>
 * 
 * <p>
 * Before opening the instance in an editor, the editor makes a "backup" of the
 * instance in order to do a "restore" if user aborts changes. This is a
 * preliminary feature pending proper support for undo/redo.
 * </p>
 * 
 * @author Peer Törngren
 * 
 * @param <T>
 *            the interface the subclass implements
 */
public abstract class AbstractEditableEntity<T extends SMDIdentity> extends AbstractObservableEntity<T> implements IEditorInput, Deletable, ItemFactory<T> {


	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO implement 
		return null;
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		String isModified = isDirty() ? " {modified}" : ""; 
		String typeName = TextUtil.getText(getGenericType());
		return "["+typeName+"] "+getName()+isModified ;
	}

	/**
	 * Default implementation calls superclass method after 
	 * deleting all {@link #getDeletableDependents()} (if any).
	 * Subclasses should override and add behavior as necessary, e.g. to remove or notify
	 * dependents as appropriate. 
	 */
	public void delete() {
		List<AbstractDependentEntity> set = new ArrayList<AbstractDependentEntity>(getDeletableDependents());
		for (AbstractDependentEntity c: set) {
			c.delete();
		}
		assert getDeletableDependents().isEmpty() : this+" - all dependedents were not removed: "+getDeletableDependents();
		getDataSource().delete(this);
	}
	
	/**
	 * Default implementation delegates to {@link Root#newInstance()}.
	 * Subclasses should override and add behavior as necessary, e.g. to
	 * initialize or notify dependents as appropriate.
	 */
	public T newInstance() {
		if (NotYetImplemented.confirm("Add")) {
			return getRoot().newInstance();
		}
		return null;
	}
	
	/**
	 * Returns an empty list. Subclasses should override as necessary.
	 * Subclasses can add to the returned list.
	 * @return {@link List}, empty 
	 */
	public <D extends AbstractDependentEntity> Collection<D> getDeletableDependents() {
		return new ArrayList<D>();
	}
	
	/**
	 * Assert that backup is a legal clone of this instance.
	 * @param backup
	 */
	private void assertBackup(AbstractObservableEntity backup) {
		// id may be null
		assert backup.getId() == getId() || backup.getId().endsWith(getId()) : "Bad id: "+backup+". Backup="+backup.getId()+", this="+getId();
		assert backup.getClass()==getClass() : "Bad class: "+backup+". Backup="+backup.getClass()+", this="+getClass();
	}

	/**
	 * Create a backup of the entity. Backup only holds the persistent data.
	 * TODO does not handle dependent entities! See {@link AbstractContributableEntity#getContributors()}
	 * @return {@link AbstractEditableEntity}
	 * @see #restore(AbstractEditableEntity)
	 */
	public AbstractEditableEntity backup() {
		AbstractEditableEntity backup = new CopyHelper().copy(this, Expose.class);
		assertBackup(backup);
		return backup;
	}

	/**
	 * Restore state from a backup of this entity. Backup only holds the persistent data.
	 * TODO does not handle dependent entities! See {@link AbstractContributableEntity#getContributors()}
	 * @see #backup()
	 */
	public void restore(AbstractObservableEntity backup) {
		assertBackup(backup);
		Util.mergeInto(this, backup);
	}

}
