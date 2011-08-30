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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.socialmusicdiscovery.rcp.content.AbstractObservableEntity;
import org.socialmusicdiscovery.rcp.content.ObservableEntity;
import org.socialmusicdiscovery.rcp.editors.widgets.ObservableComposite;

/**
 * <p>
 * An abstraction of a UI to operate on an {@link AbstractObservableEntity}
 * using JFace data binding. Intention is to create both UI layout and data
 * binding using the <a
 * href="http://code.google.com/intl/sv/javadevtools/wbpro/index.html">Google
 * WindowBuilder pro</a> tool. For this to work properly, we must both observe a
 * few design constraints/conventions, and set the tool preferences accordingly.
 * </p>
 * <p>
 * Design guidelines and tool settings will of course have to be properly described
 * at some point in time, but we're not there yet. Meanwhile, please consult
 * with the author if you need information on how to set things up.
 * </p>
 * 
 * @author Peer Törngren
 * 
 * @param <T>
 */
public abstract class AbstractComposite<T extends ObservableEntity> extends ObservableComposite {

	private IWorkbenchPart part;
	private T model;
	private DataBindingContext dbc; 

	public AbstractComposite(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * Keep track of part to let subclasses register things like context menus
	 * and selection providers. Perhaps there is a built-in way?
	 * 
	 * @return {@link IWorkbenchPart}
	 */
	public IWorkbenchPart getPart() {
		return part;
	}
	
	public void setPart(IWorkbenchPart part) {
		this.part = part;
	}

	/**
	 * Subclasses must not override this method, but may override
	 * {@link #aboutToSetModel(AbstractObservableEntity)} and/or
	 * {@link #afterSetModel(AbstractObservableEntity)} to do necessary pre- or
	 * post-setter logic, if any
	 * 
	 * @param model
	 */
	public final void setModel(T model) {
		aboutToSetModel(model);
		reset();
		this.model = model;
		this.dbc = initDataBindings();
		initManualDataBindings(dbc);
		afterSetModel(model);
	}

	/**
	 * Sub-classes are expected to override by means of the design tool; it will
	 * automatically generate this method. Note that it must be configured to
	 * <b>not</b> call this method from the constructor,m and <b>not</b> store
	 * this in a field (of the subclass) - it must override this method but not
	 * call the method or store the {@link DataBindingContext} anywhere.
	 * 
	 * @return {@link DataBindingContext}
	 */
	protected DataBindingContext initDataBindings() {
		return new DataBindingContext();
	}

	/**
	 * Do any manual data bindings necessary. Keep these out of
	 * {@link #initDataBindings()} to avoid the GUI editor overwriting manual
	 * edits. Default implementation does nothing, expect subclasses to override
	 * as required. This method is called AFTER the standard
	 * {@link #initDataBindings()} method but BEFORE
	 * {@link #afterSetModel(ObservableEntity)}.
	 * 
	 * @param bindingContext
	 */
	protected void initManualDataBindings(DataBindingContext bindingContext) {
	}

	/**
	 * Default implementation does nothing.
	 * @param model
	 */
	protected void afterSetModel(T model) {
		// no-op
	}

	/**
	 * Default implementation does nothing.
	 * @param model
	 */
	protected void aboutToSetModel(T model) {
		// no-op
	}

	/**
	 * For the time being, subclasses must unfortunately implement a type-specific getter 
	 * in order to enable data binding in the UI design tool; it does not seem to recognize 
	 * the generic type of the superclass getter.
	 * 
	 * @return T
	 */
	public final T getModel() {
		return model;
	}

	protected void reset() {
		if (dbc!=null) {
			dbc.dispose();
		}
	}
}
