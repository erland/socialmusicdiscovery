package org.socialmusicdiscovery.rcp.content;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.runtime.IAdaptable;
import org.socialmusicdiscovery.rcp.event.Observable;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.Track;

/**
 * The <b>M</b> in the <b>MVC</b> pattern. All user-presentable instances must
 * implement this interface. Most implementations will probably want to extend
 * {@link AbstractObservableEntity}.
 * 
 * @author Peer Törngren
 * 
 */
public interface ModelObject extends Observable, IAdaptable {

	/**
	 * The name of the 'name' property, according to bean standards. Use
	 * primarily to define the name of events fired when the name property
	 * changes.
	 */
	String PROP_name = "name"; //$NON-NLS-1$

	/**
	 * A human readable and viewer friendly name of this instance. In most
	 * cases, this is the name of the {@link Artist}, {@link Release},
	 * {@link Track} etc. The returned string is used to present the instance in
	 * lists, tables and trees. Note that we differ between <code>null</code>
	 * and an empty string; <code>null</code> means "no name defined", an empty
	 * string means "blank name".
	 * 
	 * @return a human readable string - may be <code>null</code> if instance is
	 *         not (yet) named, or empty if name is intentionally blank
	 *         (remember the artist formerly known as Prince?)
	 */
	String getName();
	
	/**
	 * <p>
	 * Get an observable, read-only collection of all children of this instance.
	 * If instance has no children, method returns an empty collection.
	 * </p>
	 * <p>
	 * Design note: we return a {@link List}, not a {@link Set} or a
	 * {@link Collection}, since we want the ability to modify the list -
	 * implementers may return a {@link WritableList}. We also need a specific
	 * collection type to make data binding easy; most/all data binding methods need
	 * to know if they observe a {@link List} or a {@link Set}.
	 * 
	 * @return {@link IObservableList}, possibly empty
	 */
	IObservableList getObservableChildren();
}