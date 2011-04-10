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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.ui.IEditorInput;
import org.socialmusicdiscovery.rcp.content.DataSource.Root;
import org.socialmusicdiscovery.rcp.util.ChangeMonitor;
import org.socialmusicdiscovery.rcp.util.Util;
import org.socialmusicdiscovery.server.business.model.core.Recording;
import org.socialmusicdiscovery.server.business.model.core.Track;
import org.socialmusicdiscovery.server.business.model.core.Work;

import com.google.gson.annotations.Expose;

/**
 * The {@link ObservableRecording} can have a local name, but if that is
 * missing it derives its name from its {@link Work}s (if any).
 * 
 * @author Peer Törngren
 * 
 */
public class ObservableRecording extends AbstractContributableEntity<Recording> implements Recording {

	private class MyDerivedNameUpdater implements Runnable {
		@Override
		public void run() {
			setDerivedName(resolveDerivedName());
		}
	}

	private class MyIsDerivedNameUsedMonitor implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			setDerivedNameUsed(resolveIsDerivedNameUsed());
		}
	}

	public static final String PROP_date = "date";
	public static final String PROP_mixOf = "mixOf";
	public static final String PROP_works = "works";
	public static final String PROP_derivedName = "derivedName";
	public static final String PROP_isDerivedNameUsed = "derivedNameUsed";
	
	private transient String derivedName;
	private boolean isDerivedNameUsed;
	
	@Expose private Date date;
	@Expose private Recording mixOf;
	@Expose private Set<Work> works = new HashSet<Work>();

	@Override
	protected void postInflate() {
		hookListeners();
	}

	private void hookListeners() {
		hookIsDerivedNameUsed();
		hookDerivedName();
	}

	private void hookIsDerivedNameUsed() {
		this.isDerivedNameUsed = resolveIsDerivedNameUsed();
		addPropertyChangeListener(PROP_name, new MyIsDerivedNameUsedMonitor());
	}

	private void hookDerivedName() {
		MyDerivedNameUpdater listener = new MyDerivedNameUpdater();
		ChangeMonitor.observe(listener, this, PROP_works, ObservableWork.PROP_name);
		listener.run();
	}

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public Recording getMixOf() {
		return mixOf;
	}

	@Override
	public Set<Work> getWorks() {
		return works;
	}

	@Override
	public void setDate(Date date) {
		firePropertyChange(PROP_date, this.date, this.date = date);
	}

	@Override
	public void setMixOf(Recording mixOf) {
		firePropertyChange(PROP_mixOf, this.mixOf, this.mixOf = mixOf);
	}

	@Override
	public void setWorks(Set<Work> works) {
		updateSet(PROP_works, this.works, works);
	}

	
	/**
	 * <p>
	 * By default we return the locally defined name or the derived name if
	 * local name is missing. The result will display as the name. If user
	 * changes anything, it will be saved as the locally defined name (we only
	 * override {@link #getName()}, not
	 * {@link AbstractObservableEntity#setName(String)}).
	 * </p>
	 * <p>
	 * This is really a kludge to handle the fact that {@link IEditorInput} is
	 * required to return a name, whereas some {@link ModelObject}s (like this
	 * one) use a derived name.
	 * </p>
	 * 
	 * @see <code>org.eclipse.ui.internal.PartTester.testEditorInput(IEditorInput)</code>
	 */
	@Override
	public String getName() {
		return resolveIsDerivedNameUsed() ? getDerivedName() : super.getName();
	}

	private boolean resolveIsDerivedNameUsed() {
		return Util.isEmpty(super.getName());
	}
	
	public String getDerivedName() {
		return derivedName;
	}

	private void setDerivedName(String derivedName) {
		this.derivedName = derivedName;
		firePropertyChange(PROP_derivedName); // silent refresh, do not mark dirty
		if (isDerivedNameUsed()) {
			firePropertyChange(PROP_name);
		}
	}

	public boolean isDerivedNameUsed() {
		return isDerivedNameUsed;
	}

	private void setDerivedNameUsed(boolean isDerivedNameUsed) {
		this.isDerivedNameUsed = isDerivedNameUsed;
		firePropertyChange(PROP_isDerivedNameUsed);
	}
	
	private String resolveDerivedName() {
		return Util.composeTitle(getWorks());
	}

	public Set<ObservableTrack> getTracks() {
		Root<Track> root = getDataSource().resolveRoot(Track.class);
		// FIXME return stable, observable collection where changes propagate to Release.getTracks()
		Set<ObservableTrack> tracks = root.findAll(this);
		return tracks;
	}

}
