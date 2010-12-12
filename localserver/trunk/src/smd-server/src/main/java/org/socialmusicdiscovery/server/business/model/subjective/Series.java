package org.socialmusicdiscovery.server.business.model.subjective;

import org.socialmusicdiscovery.server.business.model.SMDEntity;
import org.socialmusicdiscovery.server.business.model.core.Release;

import javax.persistence.*;
import java.util.Collection;

@javax.persistence.Entity
@Table(name = "series")
public class Series extends SMDEntity {
    @Column(nullable = false)
    private String name;

    @OneToMany
    @JoinTable(name = "release_series",
            joinColumns = @JoinColumn(name = "serie_id"),
            inverseJoinColumns = @JoinColumn(name = "release_id"))
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
