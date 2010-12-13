package org.socialmusicdiscovery.server.business.model;

import com.google.gson.annotations.Expose;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.UUID;

@javax.persistence.Entity
@Table(name = "global_identities", uniqueConstraints = @UniqueConstraint(columnNames = {"source", "entityid"}))
public class GlobalIdentityEntity implements GlobalIdentity {
    @Id
    @Column(length = 36)
    @Expose
    private String id;

    @Column(name = "source", nullable = false)
    @Expose
    private String source;
    @Column(nullable = false)
    @Expose
    private String uri;
    @Column(name = "entityid", nullable = false)
    @Expose
    private String entityId;

    public GlobalIdentityEntity() {
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GlobalIdentityEntity)) return false;

        return id.equals(((GlobalIdentityEntity) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
