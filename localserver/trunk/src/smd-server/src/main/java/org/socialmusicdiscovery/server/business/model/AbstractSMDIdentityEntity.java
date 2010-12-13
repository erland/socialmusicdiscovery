package org.socialmusicdiscovery.server.business.model;

import com.google.gson.annotations.Expose;

import javax.persistence.*;
import java.util.UUID;

@MappedSuperclass
public abstract class AbstractSMDIdentityEntity implements SMDIdentity {
    @Id
    @Column(length = 36)
    @Expose
    private String id;

    @OneToOne(targetEntity = SMDIdentityReferenceEntity.class, optional = false)
    @JoinColumn(name = "id")
    private SMDIdentityReference reference;

    public AbstractSMDIdentityEntity() {
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SMDIdentityReference getReference() {
        return reference;
    }

    public void setReference(SMDIdentityReference reference) {
        this.reference = reference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractSMDIdentityEntity)) return false;

        return id.equals(((AbstractSMDIdentityEntity) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
