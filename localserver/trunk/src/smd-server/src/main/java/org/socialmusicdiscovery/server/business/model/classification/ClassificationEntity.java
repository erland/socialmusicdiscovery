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

package org.socialmusicdiscovery.server.business.model.classification;

import com.google.gson.annotations.Expose;
import org.hibernate.Hibernate;
import org.socialmusicdiscovery.server.business.logic.SortAsHelper;
import org.socialmusicdiscovery.server.business.model.AbstractSMDIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * See {@link Classification}
 */
@javax.persistence.Entity
@Table(name = "classifications")
@SMDIdentityReferenceEntity.ReferenceType(type = Classification.class)
public class ClassificationEntity extends AbstractSMDIdentityEntity implements Classification {
    @Column(nullable = false)
    @Expose
    private String type;
    @Column(nullable = false, length = 255)
    @Size(min = 1, max = 255)
    @Expose
    private String name;
    @Column(name="sort_as", nullable = false, length = 255)
    @Size(min = 1, max = 255)
    @Expose
    private String sortAs;
    @OneToMany(targetEntity = ClassificationEntity.class, orphanRemoval = true)
    @JoinColumn(name = "parent_id")
    @Expose
    private Set<Classification> childs = new HashSet<Classification>();

    @OneToMany(targetEntity = ClassificationReferenceEntity.class, mappedBy = "classification")
    private Set<ClassificationReference> references = new HashSet<ClassificationReference>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Classification> getChilds() {
        return childs;
    }

    public void setChilds(Set<Classification> childs) {
        this.childs = childs;
    }

    public Set<ClassificationReference> getReferences() {
        return references;
    }

    public void setReferences(Set<ClassificationReference> references) {
        this.references = references;
    }

    public String getSortAs() {
        return sortAs;
    }

    public void setSortAs(String sortAs) {
        this.sortAs = sortAs;
    }

    public void setSortAsAutomatically() {
        setSortAs(SortAsHelper.getSortAsForValue(Classification.class.getSimpleName(), getName()));
    }

    public void addReference(ClassificationReferenceEntity reference) {
        if(Hibernate.isInitialized(this.references)) {
            this.references.add(reference);
        }
        reference.setClassification(this);
    }

    public void removeReference(ClassificationReferenceEntity reference) {
        if(Hibernate.isInitialized(reference)) {
            this.references.remove(reference);
        }
        reference.setReference(null);
    }

}
