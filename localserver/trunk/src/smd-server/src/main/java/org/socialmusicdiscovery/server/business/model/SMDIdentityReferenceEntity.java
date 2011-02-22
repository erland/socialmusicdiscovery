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

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.annotation.*;

@javax.persistence.Entity
@Table(name = "smdidentity_references")
public class SMDIdentityReferenceEntity implements SMDIdentityReference {
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface ReferenceType {
        public Class type();
    };

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false)
    private String type;

    public SMDIdentityReferenceEntity() {
    }

    public SMDIdentityReferenceEntity(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public static SMDIdentityReference forEntity(SMDIdentity entity) {
        return new SMDIdentityReferenceEntity(entity.getId(), typeForClass(entity.getClass()));
    }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SMDIdentityReferenceEntity)) return false;

        return id.equals(((SMDIdentityReferenceEntity) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
