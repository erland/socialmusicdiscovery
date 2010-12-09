package org.socialmusicdiscovery.rcp.views.util;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.socialmusicdiscovery.server.business.model.SMDEntity;

public abstract class AbstractComposite<T extends SMDEntity<?>> extends Composite {

	private IWorkbenchPart part;

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
	 * For the time being, subclasses must unfortunately implement the accessors
	 * in order to enable data binding in the UI design tool.
	 * 
	 * @param entity
	 */
	public abstract void setEntity(T entity);

	/**
	 * For the time being, subclasses must unfortunately implement the accessors
	 * in order to enable data binding in the UI design tool.
	 * 
	 * @param entity
	 */
	public abstract T getEntity();

}
