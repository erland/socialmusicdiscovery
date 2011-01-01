package org.socialmusicdiscovery.server.business.model.search;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;
import org.socialmusicdiscovery.server.business.model.classification.Classification;
import org.socialmusicdiscovery.server.business.model.core.Contributor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
@IdClass(SearchRelationEntity.PK.class)
public abstract class SearchRelationEntity {
    public static class PK implements Serializable {
        public String id;
        public String referenceType;
        public String reference;
        public String type;

        public PK() {}
        public PK(String id, String referenceType, String reference, String type) {
            this.id = id;
            this.referenceType = referenceType;
            this.reference = reference;
            this.type = type;
        }
        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        @Override
        public boolean equals(Object o) {
            return EqualsBuilder.reflectionEquals(this, o);
        }
    }
    @Id
    @Column(length = 36)
    private String id;
    @Id
    private String referenceType;
    @Id
    @Column(length = 36)
    private String reference;
    @Id
    private String type;

    public SearchRelationEntity() {
        setType("");
    }

    public SearchRelationEntity(SMDIdentity identity, SMDIdentity reference) {
        setId(identity.getId());
        setReference(reference);
        setType("");
    }

    public SearchRelationEntity(SMDIdentity identity, Contributor contributor) {
        setId(identity.getId());
        setReference(contributor);
    }

    public SearchRelationEntity(SMDIdentity identity, Classification classification) {
        setId(identity.getId());
        setReference(classification);
    }

    public SearchRelationEntity(String id, String referenceType, String reference) {
        setId(id);
        setReferenceType(referenceType);
        setReference(reference);
        setType("");
    }

    public SearchRelationEntity(String id, String referenceType, String reference, String type) {
        setId(id);
        setReferenceType(referenceType);
        setReference(reference);
        setType(type);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setReference(SMDIdentity reference) {
        setReferenceType(SMDIdentityReferenceEntity.typeForClass(reference.getClass()));
        setReference(reference.getId());
    }

    public void setReference(Contributor contributor) {
        setReferenceType(SMDIdentityReferenceEntity.typeForClass(contributor.getArtist().getClass()));
        setReference(contributor.getArtist().getId());
        setType(contributor.getType());
    }

    public void setReference(Classification classification) {
        setReferenceType(SMDIdentityReferenceEntity.typeForClass(classification.getClass()));
        setReference(classification.getId());
        setType(classification.getType());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }
}
