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
import java.lang.annotation.*;

@javax.persistence.Entity
@org.hibernate.annotations.Cache(usage = org.hibernate.annotations.CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Table(name = "smdidentity_references")
public class SMDIdentityReferenceEntity implements SMDIdentityReference {
    /**
     * Annotation which should be applied to entity classes to specify the interface class which should be used when setting the value in the
     * {@link #type} attribute
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface ReferenceType {
        public Class type();
    }

    /**
     * Unique identity of the referenced entity
     */
    @Id
    @Expose
    @Column(length = 36)
    private String id;

    /**
     * Type of the referenced identity, see also {@link ReferenceType}
     */
    @Expose
    @Column(nullable = false)
    private String type;

    public SMDIdentityReferenceEntity() {
    }

    public SMDIdentityReferenceEntity(String id, String type) {
        this.id = id;
        this.type = type;
    }

    /**
     * Returns a reference to the specified entity.
     * @param entity The entity to return a reference to
     * @return A reference to the specified entity
     */
    public static SMDIdentityReference forEntity(SMDIdentity entity) {
        return new SMDIdentityReferenceEntity(entity.getId(), typeForClass(entity.getClass()));
    }

    /**
     * Returns the type value the specified entity class uses in the {@link #type} attribute. The {@link ReferenceType} annotation on the entity
     * class will be used to get the type
     * @param cls The entity class to get type for
     * @return The type value
     */
    public static String typeForClass(Class cls) {
        Annotation annotation = cls.getAnnotation(ReferenceType.class);
        if(annotation instanceof ReferenceType) {
            return ((ReferenceType)annotation).type().getSimpleName();
        }
        throw new RuntimeException("Unsupported SMDIdentity: "+cls.getSimpleName());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        if (!(o instanceof SMDIdentityReferenceEntity)) return false;

        return getId().equals(((SMDIdentityReferenceEntity) o).getId());
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
