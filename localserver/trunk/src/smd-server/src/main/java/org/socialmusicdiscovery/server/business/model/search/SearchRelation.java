package org.socialmusicdiscovery.server.business.model.search;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.socialmusicdiscovery.server.business.model.SMDEntity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
@IdClass(SearchRelation.class)
public class SearchRelation implements Serializable {
    @Id
    @Column(length=36)
    private String id;
    @Id
    private String referenceType;
    @Id
    @Column(length=36)
    private String reference;

    public SearchRelation() {}
    public SearchRelation(String id, SMDEntity reference) {
        setId(id);
        setReference(reference);
    }
    public SearchRelation(String id, String referenceType, String reference) {
        setId(id);
        setReferenceType(referenceType);
        setReference(reference);
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

    public void setReference(SMDEntity reference) {
        if(reference!=null) {
            setReferenceType(reference.getClass().getName());
            setReference(reference.getId());
        }else {
            this.referenceType = null;
            this.reference = null;
        }
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this,o);
    }
}
