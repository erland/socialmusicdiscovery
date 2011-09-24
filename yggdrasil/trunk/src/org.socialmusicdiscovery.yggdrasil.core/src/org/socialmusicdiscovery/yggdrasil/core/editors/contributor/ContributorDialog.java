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

package org.socialmusicdiscovery.yggdrasil.core.editors.contributor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.socialmusicdiscovery.rcp.content.AbstractContributableEntity;
import org.socialmusicdiscovery.rcp.content.ArtistProvider;
import org.socialmusicdiscovery.rcp.content.ContributorRoleProvider;
import org.socialmusicdiscovery.rcp.content.ObservableArtist;
import org.socialmusicdiscovery.rcp.content.ObservableContributor;
import org.socialmusicdiscovery.rcp.dialogs.FactoryDialog;
import org.socialmusicdiscovery.rcp.editors.AbstractEditorDialog;
import org.socialmusicdiscovery.rcp.util.ClassUtil;
import org.socialmusicdiscovery.server.business.model.core.Contributor;

/**
 * A dialog for creating a new {@link Contributor}.
 * 
 * @author Peer Törngren
 *
 */
public class ContributorDialog extends AbstractEditorDialog<ObservableContributor> 
		implements FactoryDialog<AbstractContributableEntity, ObservableContributor> {

	/**
	 * Preliminary stub. Need to think about how to handle validation properly.
	 */
	private class MyButtonManager implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			okButton.setEnabled(isValid(ui.getTemplate()));
		}
		
		private boolean isValid(Contributor prospect) {
			return isFullyInitialized(prospect) && isUnique(prospect);
		}

		private boolean isFullyInitialized(Contributor template) {
			boolean haveArtist = template.getArtist()!=null;
			boolean haveType = template.getType()!=null;
			return haveArtist && haveType;
		}
		
		private boolean isUnique(Contributor prospect) {
			Set<Contributor> contributors = owner.getContributors();
			for (Contributor c : contributors) {
				if (equal(c, prospect)) {
					return false;
				}
			}
			return true;
		}

		private boolean equal(Contributor c, Contributor c2) {
			return c.getType().equals(c2.getType()) && c.getArtist().equals(c2.getArtist());
		}
	}
	
	private AbstractContributableEntity owner;
	private ContributorUI ui;
	private String type;
	private ObservableArtist artist;
	private ObservableContributor originalInput;

	/**
	 * Must have constructor without args to allow
	 * instantiation from extension registry.
	 */
	public ContributorDialog() {
		super(new Shell(), "Edit Contributor");
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
//		setMessage("Create a new contributor by selecting type of contribution and an artist. The combination of type and artist must be unique.");
		Composite area = (Composite) super.createDialogArea(parent);
		area.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		ui = new ContributorUI(area, SWT.NONE);
		ui.setRoleProvider(new ContributorRoleProvider());
		ui.setArtistProvider(new ArtistProvider());
		ui.setOwner(owner);
		ui.setType(type);
		ui.setArtist(artist);

		return area;
	}

	@Override
	public void create() {
		super.create();
		ui.getTemplate().addPropertyChangeListener(new MyButtonManager());
	}

	public ObservableContributor createChild(AbstractContributableEntity parent) {
		owner = parent;
		return openOK() ? new ObservableContributor(ui.getTemplate()) : null;
	}
	
	@Override
	protected void beforeEdit(ObservableContributor input) {
		originalInput = input;
		owner = input.getOwner();
		type = input.getType();
		artist = input.getArtist();
	}
	
	@Override
	protected ObservableContributor afterEdit() {
		ClassUtil.copyProperties(ui.getTemplate(), originalInput, ObservableContributor.PROP_owner, ObservableContributor.PROP_type, ObservableContributor.PROP_artist);
		return originalInput;
	}

}