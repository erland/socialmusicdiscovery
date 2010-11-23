package org.socialmusicdiscovery.server.business.model;

import javax.persistence.*;
import java.util.UUID;

@MappedSuperclass
public class SMDEntity<T> {
    @Id
    @Column(length = 36)
    private String id;

    @OneToOne(optional = false)
    @JoinColumn(name = "id")
    private SMDEntityReference reference;

    public SMDEntity() {
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SMDEntityReference getReference() {
        return reference;
    }

    public void setReference(SMDEntityReference reference) {
        this.reference = reference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SMDEntity)) return false;

        return id.equals(((SMDEntity) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
