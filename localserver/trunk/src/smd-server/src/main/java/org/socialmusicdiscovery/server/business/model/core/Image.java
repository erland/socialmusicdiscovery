package org.socialmusicdiscovery.server.business.model.core;


import org.socialmusicdiscovery.server.business.model.SMDIdentity;

/**
 * Represents a link to a local image file or a online image
 */
public interface Image extends SMDIdentity {
	
    String getUri();
    
    void setUri(String uri);

}
