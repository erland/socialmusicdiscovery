package org.socialmusicdiscovery.server.business.model.subjective;

import org.socialmusicdiscovery.server.business.model.SMDEntity;
import org.socialmusicdiscovery.server.business.model.core.Release;

import javax.persistence.Table;
import java.util.Collection;

@javax.persistence.Entity
@Table(name="series")
public class Series extends SMDEntity<Series> {
    private String name;
    //TODO: How do we annotate this without affecting Release table
    private Collection<Release> releases;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Release> getReleases() {
        return releases;
    }

    public void setReleases(Collection<Release> releases) {
        this.releases = releases;
    }
}
