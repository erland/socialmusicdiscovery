package org.socialmusicdiscovery.server.business.model.core;


import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReference;

/**
 * Represents a link to a local image file or a online image
 */
public interface Image extends SMDIdentity {
	
    String getUri();
    
    void setUri(String uri);
    
    void setRelatedTo(SMDIdentityReference relatedTo);
    
    SMDIdentityReference getRelatedTo();
    
    String getType();

	void setType(String type);

}
