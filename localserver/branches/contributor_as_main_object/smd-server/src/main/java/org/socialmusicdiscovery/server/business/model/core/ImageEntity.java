package org.socialmusicdiscovery.server.business.model.core;

import javax.persistence.Column;
import javax.persistence.Table;


import org.socialmusicdiscovery.server.business.model.AbstractSMDIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;


import com.google.gson.annotations.Expose;

@javax.persistence.Entity
@Table(name = "images")
@SMDIdentityReferenceEntity.ReferenceType(type = Image.class)
public class ImageEntity extends AbstractSMDIdentityEntity implements Image {

    @Column(nullable = false, length = 1024)
    @Expose
    private String uri;
    

	public String getUri() {
    	return uri.toString();
    }

    public void setUri(String uri) {
    	this.uri = uri;
    }
	
}
