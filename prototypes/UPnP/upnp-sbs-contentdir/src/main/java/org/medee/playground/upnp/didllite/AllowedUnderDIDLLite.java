package org.medee.playground.upnp.didllite;


public interface AllowedUnderDIDLLite {	

	public Object getElement();

	public String getClazz(); 

	public void setClazz(String clazz);

	public String getTitle();
	
	public void setTitle(String title);
	
	public String getId();

	public void setId(String id);

	public String getParentId();
	
	public void setParentId(String parentId);
	
}
