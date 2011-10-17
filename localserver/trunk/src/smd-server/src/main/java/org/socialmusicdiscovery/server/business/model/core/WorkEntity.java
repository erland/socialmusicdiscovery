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
import org.hibernate.Hibernate;
import org.socialmusicdiscovery.server.business.logic.SortAsHelper;
import org.socialmusicdiscovery.server.business.model.AbstractSMDIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.*;

/**
 * See {@link Work}
 */
@javax.persistence.Entity
@Table(name = "works")
@DiscriminatorColumn(name = "type")
@DiscriminatorValue("Work")
@SMDIdentityReferenceEntity.ReferenceType(type = Work.class)
public class WorkEntity extends AbstractSMDIdentityEntity implements Work, ContributorOwner {
    @Expose
    @Column(length = 511, nullable = false)
    @Size(min = 1, max = 511)
    private String name;
    @Expose
    @Column(name="sort_as", length = 511, nullable = false)
    @Size(min = 1, max = 511)
    private String sortAs;
    @Expose
    private Date date;
    @OneToMany(targetEntity = PartEntity.class)
    @JoinColumn(name = "parent_id")
    @OrderBy("sortAs")
    private List<Part> parts = new ArrayList<Part>();

    @OneToMany(targetEntity = ContributorEntity.class, mappedBy = "work", cascade = {CascadeType.REMOVE}, orphanRemoval = true)
    private Set<Contributor> contributors = new HashSet<Contributor>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }

    public String getSortAs() {
        return sortAs;
    }

    public void setSortAs(String sortAs) {
        this.sortAs = sortAs;
    }

    public void setSortAsAutomatically() {
        setSortAs(SortAsHelper.getSortAsForValue(Work.class.getSimpleName(), getName()));
    }

    public Set<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(Set<Contributor> contributors) {
        this.contributors = contributors;
    }

    public void addContributor(ContributorEntity contributor) {
        if(Hibernate.isInitialized(contributors)) {
            this.contributors.add(contributor);
        }
        contributor.setWork(this);
    }

    public void removeContributor(ContributorEntity contributor) {
        if(Hibernate.isInitialized(contributors)) {
            this.contributors.remove(contributor);
        }
        contributor.setWork(null);
    }
}
