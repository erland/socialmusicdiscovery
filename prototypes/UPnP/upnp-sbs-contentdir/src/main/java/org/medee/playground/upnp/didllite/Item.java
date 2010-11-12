package org.medee.playground.upnp.didllite;

import org.medee.playground.upnp.didllite.jaxb.ItemType;
import org.medee.playground.upnp.didllite.jaxb.ObjectFactory;

public class Item implements AllowedUnderDIDLLite {
	
	private static ObjectFactory factory = DidlLiteDocument.getObjectFactory();
	
	@Override
	public Object getElement() {
		return element;
	}

	ItemType element;

	public Item() {
		super();
		element = factory.createItemType();
		element.setClazz(factory.createClassType());
		element.setTitle(factory.createElementType());
	}

	public String getClazz() {
		return element.getClazz().getValue();

	}

	public void setClazz(String clazz) {
		element.getClazz().setValue(clazz);
	}

	public String getTitle() {
		return element.getTitle().getValue();
	}
	
	public void setTitle(String title) {
		element.getTitle().setValue(title);
	}
	
	public String getId() {
		return element.getId();
	}
	
	public void setId(String id) {
		element.setId(id);
	}

	public String getParentId() {
		return element.getParentID();
	}

	public void setParentId(String parentId) {
			element.setParentID(parentId);
	}
}
