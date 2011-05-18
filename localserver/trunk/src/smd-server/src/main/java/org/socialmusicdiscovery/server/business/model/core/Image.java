package org.socialmusicdiscovery.server.business.model.core;


import org.socialmusicdiscovery.server.business.model.SMDIdentity;

public interface Image extends SMDIdentity {
	
    String getUri();
    
    void setUri(String uri);

}
