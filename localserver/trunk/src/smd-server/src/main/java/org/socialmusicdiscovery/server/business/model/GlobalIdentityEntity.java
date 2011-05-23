/*
 *  Copyright 2010-2011, Social Music Discovery project
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of Social Music Discovery project nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL SOCIAL MUSIC DISCOVERY PROJECT BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.socialmusicdiscovery.server.business.model;

import com.google.gson.annotations.Expose;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Date;
import java.util.UUID;

/**
 * See {@link GlobalIdentity}
 */
@javax.persistence.Entity
@Table(name = "global_identities", uniqueConstraints = @UniqueConstraint(columnNames = {"source", "entityid"}))
public class GlobalIdentityEntity implements GlobalIdentity {
    /**
     * Unique identity of this entity instance
     */
    @Id
    @Column(length = 36)
    @Expose
    private String id;

    /**
     * The source that manage the globally unique identity, for example {@link GlobalIdentity#SOURCE_MUSICBRAINZ}
     */
    @Column(name = "source", nullable = false)
    @Expose
    private String source;

    /**
     * The uri that represents the globally unique identity
     */
    @Column(nullable = false)
    @Expose
    private String uri;

    /**
     * The identity of the local entity instance which this global identity represent
     * TODO: Shouldn't this be a relation to {@link SMDIdentityReferenceEntity} instead of a String ?
     */
    @Column(name = "entityid", nullable = false)
    @Expose
    private String entityId;

    /**
     * The time when this entity instance either was created or last updated
     */
    @Column(name = "last_updated", nullable = false)
    private Date lastUpdated;

    /**
     * The module which was the one that last updated this entity instance
     */
    @Column(name = "last_updated_by", nullable = false)
    private String lastUpdatedBy;

    /**
     * Constructs a new instance and assigns it a unique identity in the {@link #id} field
     */
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

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * Equal implementation which is based on the value of the {@link #getId()} method, it will not do a full comparison the object are considered
     * to be equal if they have the same identities
     * @param o The object to compare to
     * @return true if objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GlobalIdentityEntity)) return false;

        return getId().equals(((GlobalIdentityEntity) o).getId());
    }

    /**
     * Hash code implementation which is based on the value of the {@link #getId()} method, it will not include the full object in the hash code,
     * only the identity
     * @return The hash code representing this instance
     */
    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
