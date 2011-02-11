package org.socialmusicdiscovery.rcp.views.util;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.socialmusicdiscovery.rcp.content.AbstractObservableEntity;

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
public abstract class AbstractComposite<T extends AbstractObservableEntity> extends Composite {

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
