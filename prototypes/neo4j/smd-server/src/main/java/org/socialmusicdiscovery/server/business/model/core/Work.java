package org.socialmusicdiscovery.server.business.model.core;

import jo4neo.neo;
import org.socialmusicdiscovery.server.business.model.SMDEntity;

import java.util.Collection;
import java.util.Date;

public class Work extends SMDEntity<Work> {
    @neo(index=true)
    private String name;
    @neo
    private Date date;
    @neo
    private Collection<Work> parts;
    @neo
    private Collection<Contributor> contributors;

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

    public Collection<Work> getParts() {
        return parts;
    }

    public void setParts(Collection<Work> parts) {
        this.parts = parts;
    }

    public Collection<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(Collection<Contributor> contributors) {
        this.contributors = contributors;
    }
}
