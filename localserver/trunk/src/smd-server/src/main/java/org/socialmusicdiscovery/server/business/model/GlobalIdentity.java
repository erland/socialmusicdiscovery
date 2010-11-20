package org.socialmusicdiscovery.server.business.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.UUID;

@javax.persistence.Entity
@Table(name = "global_identities", uniqueConstraints = @UniqueConstraint(columnNames = {"source", "entityid"}))
public class GlobalIdentity {
    public static final String SOURCE_MUSICBRAINZ = "musicbrainz";
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "source", nullable = false)
    private String source;
    @Column(nullable = false)
    private String uri;
    @Column(name = "entityid", nullable = false)
    private String entityId;

    public GlobalIdentity() {
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
}
