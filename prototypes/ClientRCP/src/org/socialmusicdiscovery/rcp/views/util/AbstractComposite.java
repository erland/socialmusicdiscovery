package org.socialmusicdiscovery.rcp.views.util;

import org.eclipse.swt.widgets.Composite;
import org.socialmusicdiscovery.server.business.model.SMDEntity;

public abstract class AbstractComposite<T extends SMDEntity<?>> extends Composite {

	public AbstractComposite(Composite parent, int style) {
		super(parent, style);
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
