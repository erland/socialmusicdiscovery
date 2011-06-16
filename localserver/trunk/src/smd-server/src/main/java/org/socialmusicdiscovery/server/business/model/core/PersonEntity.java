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

package org.socialmusicdiscovery.server.business.model.core;

import com.google.gson.annotations.Expose;
import org.socialmusicdiscovery.server.business.model.AbstractSMDIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;
import org.socialmusicdiscovery.server.business.model.search.PersonSearchRelationEntity;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * See {@link Person}
 */
@javax.persistence.Entity
@Table(name = "persons")
@SMDIdentityReferenceEntity.ReferenceType(type = Person.class)
public class PersonEntity extends AbstractSMDIdentityEntity implements Person {
    @Column(nullable = false)
    @Expose
    private String name;
    @Column(name="sort_as", nullable = false)
    @Expose
    private String sortAs;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "id")
    private Set<PersonSearchRelationEntity> searchRelations = new HashSet<PersonSearchRelationEntity>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<PersonSearchRelationEntity> getSearchRelations() {
        return searchRelations;
    }

    public void setSearchRelations(Set<PersonSearchRelationEntity> searchRelations) {
        this.searchRelations = searchRelations;
    }

    public String getSortAs() {
        return sortAs;
    }

    public void setSortAs(String sortAs) {
        this.sortAs = sortAs;
    }
    public void setSortAsAutomatically() {
        setSortAs(getName());
    }
}
