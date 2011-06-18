package org.socialmusicdiscovery.server.business.model.core;

import com.google.gson.annotations.Expose;
import org.socialmusicdiscovery.server.business.model.AbstractSMDIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReference;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * See {@link Image}
 */
@javax.persistence.Entity
@Table(name = "images")
@org.hibernate.annotations.Cache(usage = org.hibernate.annotations.CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SMDIdentityReferenceEntity.ReferenceType(type = Image.class)
public class ImageEntity extends AbstractSMDIdentityEntity implements Image {

    @Column(nullable = false, length = 1024)
    @Expose
    private String uri;

    @Expose
    //@ManyToOne(cascade=CascadeType.ALL)
    @ManyToOne
    @JoinColumn(name = "related_to_id", nullable = false)
    private SMDIdentityReferenceEntity relatedTo;
 
    @Expose
    @Column(nullable = true, length = 36)
    private String type;
    
    @Expose
    @Column(name = "provider_id", nullable = false, length = 40)
    private String providerId;

	@Expose
    @Column(name = "provider_image_id", nullable = true, length = 64)
    private String providerImageId;


    public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public String getProviderImageId() {
		return providerImageId;
	}

	public void setProviderImageId(String providerImageId) {
		this.providerImageId = providerImageId;
	}

    
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUri() {
    	return uri.toString();
    }

    public SMDIdentityReference getRelatedTo() {
        return relatedTo;
    }

    public void setRelatedTo(SMDIdentityReference relatedTo) {
        this.relatedTo = (SMDIdentityReferenceEntity) relatedTo;
    }

    private void setRelatedTo(Class<? extends SMDIdentity> clazz, String id) {
        this.relatedTo = new SMDIdentityReferenceEntity(id, SMDIdentityReferenceEntity.typeForClass(clazz));
    }
    
    public void setUri(String uri) {
    	this.uri = uri;
    }
	
}
