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

package org.socialmusicdiscovery.yggdrasil.core.editors;

import java.util.Collection;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ListDialog;
import org.socialmusicdiscovery.rcp.content.ElementProvider;
import org.socialmusicdiscovery.rcp.content.ModelObject;
import org.socialmusicdiscovery.rcp.event.Observable;
import org.socialmusicdiscovery.rcp.views.util.LabelProviderFactory;
import org.socialmusicdiscovery.rcp.views.util.ObservableComposite;

/**
 * <p>
 * A reusable component for selecting elements. Expect to evolve to offer
 * content assist (aka "type-ahead").
 * </p>
 * 
 * <p>
 * Note: to make panel "blend" with various backgrounds (e.g white forms or grey
 * dialogs), set background mode on parent to {@link SWT#INHERIT_DEFAULT}. 
 * Example:<pre>
 * 	public SomeParent(Composite parent, int style) {
 * 		super(parent, style);
 * 		setBackgroundMode(SWT.INHERIT_DEFAULT);
 * 		...
 * </pre>
 * 
 * </p>
 * 
 * @author Peer Törngren
 * 
 */
public class SelectionPanel<T extends ModelObject> extends ObservableComposite {

	private class ButtonSelectionListener extends SelectionAdapter {

		@SuppressWarnings("unchecked")
		@Override
		public void widgetSelected(SelectionEvent e) {
			ListDialog ld = new ListDialog(getShell());
			ld.setContentProvider(new ArrayContentProvider());
			ld.setInput(getElements());
			ld.setLabelProvider(LabelProviderFactory.forStaticList());
			ld.setMessage(button.getToolTipText());
			ld.setTitle("Select");
			ld.open();
			
			Object[] allSelected = ld.getResult();
			Object selected = allSelected!=null && allSelected.length==1 ? allSelected[0] : null;
			setSelected((T) selected);
		}

		private Object getElements() {
			boolean useProvider = elements==null || (elements.isEmpty() && provider!=null);
			return useProvider ? provider.getElements() : elements;
		}
	}

	public static final String PROP_selected = "selected";
	public static final String PROP_provider = "provider";
	
	private Text text;
	private Button button;
	private Label label; 
	private Collection<T> elements;
	private T selected;
	private ElementProvider<T> provider;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public SelectionPanel(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);
		
		label = new Label(this, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		label.setText("<dynamic>");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		text = new Text(this, SWT.BORDER | SWT.READ_ONLY);
		text.setToolTipText("Select label by clicking button (not yet possible to type in field)");
		text.setText("");
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		button = new Button(this, SWT.NONE);
		button.setText("...");
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		button.addSelectionListener(new ButtonSelectionListener());
	}

	/**
	 * A convenience method for manually binding selection to a property, since
	 * GUI builder doesn't seem to like generic properties (the
	 * {@link #getSelected()} property does not show up in binder UI).
	 * 
	 * @param bindingContext
	 * @param bean
	 * @param beanPropertyName
	 * @param propertyNamesToObserve names of properties to observe on the element held by the bean property (typically the name)   
	 */
	public void bindSelection(DataBindingContext bindingContext, Observable bean, String beanPropertyName, String... propertyNamesToObserve) {
//		TODO observe composite property to update text field when selected entity changes 
		IObservableValue beanObserveValue = BeansObservables.observeValue(bean, beanPropertyName);
		IObservableValue selectedObserveValue = BeansObservables.observeValue(this, PROP_selected);
		bindingContext.bindValue(selectedObserveValue, beanObserveValue, null, null);
	}
	
	public T getSelected() {
		return selected;
	}
	
	public void setSelected(T selected) {
		firePropertyChange(PROP_selected, this.selected, this.selected = selected);
		String name = selected==null ? "" : selected.getName();
		text.setText(name);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	public Label getLabel() {
		return label;
	}
	
	public Text getText() {
		return text;
	}
	
	public Button getButton() {
		return button;
	}

	/**
	 * Use this method for static list of selectable elements. This takes
	 * precedence over {@link #setElementProvider(ElementProvider)} if both are
	 * called.
	 * 
	 * @param elements
	 * @see #setElementProvider(ElementProvider)
	 */
	public void setElements(Collection<T> elements) {
		this.elements = elements;
	}

	/**
	 * Use this method for dynamic list of selectable elements. This is
	 * overridden by {@link #setElements(Collection)} if both are called.
	 * 
	 * @param elements
	 * @see #setElementProvider(ElementProvider)
	 */
	public void setElementProvider(ElementProvider<T> provider) {
		this.provider = provider;
	}

	public void setup(String labelText, String buttonTooltipText, Collection<T> elements) {
		label.setText(labelText);
		button.setToolTipText(buttonTooltipText);
		setElements(elements);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		setEnabled(enabled, getButton(), getText()); // TODO set read-only style on Text? 
	}

	private static void setEnabled(boolean enabled, Control... controls) {
		for (Control control : controls) {
			control.setEnabled(enabled);
		}
	}

}
