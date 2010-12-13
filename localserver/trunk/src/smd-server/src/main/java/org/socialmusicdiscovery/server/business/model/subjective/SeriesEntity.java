package org.socialmusicdiscovery.server.business.model.subjective;

import com.google.gson.annotations.Expose;
import org.socialmusicdiscovery.server.business.model.AbstractSMDIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.ReleaseEntity;

import javax.persistence.*;
import java.util.Collection;

@javax.persistence.Entity
@Table(name = "series")
@SMDIdentityReferenceEntity.ReferenceType(type = Series.class)
public class SeriesEntity extends AbstractSMDIdentityEntity implements Series {
    @Column(nullable = false)
    @Expose
    private String name;

    @OneToMany(targetEntity = ReleaseEntity.class)
    @JoinTable(name = "release_series",
            joinColumns = @JoinColumn(name = "serie_id"),
            inverseJoinColumns = @JoinColumn(name = "release_id"))
    @Expose
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
