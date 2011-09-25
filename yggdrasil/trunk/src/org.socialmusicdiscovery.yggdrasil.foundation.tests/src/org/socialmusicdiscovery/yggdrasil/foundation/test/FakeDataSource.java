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

package org.socialmusicdiscovery.yggdrasil.foundation.test;

import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.swt.widgets.Shell;
import org.socialmusicdiscovery.server.api.OperationStatus;
import org.socialmusicdiscovery.server.api.management.mediaimport.MediaImportStatus;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.yggdrasil.foundation.content.DataSource;
import org.socialmusicdiscovery.yggdrasil.foundation.content.ObservableEntity;

/**
 * TODO replace with mock object.
 * @author Peer Törngren
 *
 */
public class FakeDataSource implements DataSource {

	public FakeDataSource() {
		super();
	}

	@Override
	public boolean persist(Shell shell, ObservableEntity... entities) {
		return true;
	}

	@Override
	public <T extends SMDIdentity> void delete(ObservableEntity victim) {
		// no-op;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public IObservableList getObservableChildren() {
		return null;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
	}

	@Override
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
	}

	@Override
	public PropertyChangeListener[] getPropertyChangeListeners() {
		return null;
	}

	@Override
	public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
		return null;
	}

	@Override
	public boolean hasListeners(String propertyName) {
		return false;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		
	}

	@Override
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
	}

	@Override
	public void firePropertyChange(String propertyName) {
	}

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public List<? extends Root> getRoots() {
		return null;
	}

	@Override
	public <T extends SMDIdentity> Root<T> resolveRoot(T entity) {
		return null;
	}

	@Override
	public <T extends SMDIdentity> Root<T> resolveRoot(Class<T> type) {
		return null;
	}

	@Override
	public boolean isConnected() {
		return false;
	}

	@Override
	public boolean disconnect() {
		return false;
	}

	@Override
	public boolean connect() {
		return false;
	}

	@Override
	public <T extends SMDIdentity> boolean inflate(ObservableEntity<T> shallowEntity) {
		return false;
	}

	@Override
	public OperationStatus startImport(String module) {
		return null;
	}

	@Override
	public MediaImportStatus getImportStatus(String module) {
		return null;
	}

	@Override
	public void cancelImport(String module) {
	}

	@Override
	public void initialize(Shell shell) {
	}

	@Override
	public void setAutoConnect(boolean b) {
	}

	@Override
	public boolean isAutoConnect() {
		return false;
	}

}
