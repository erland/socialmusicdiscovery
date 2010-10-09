package org.socialmusicdiscovery.server.business.model.subjective;

import jo4neo.neo;
import org.socialmusicdiscovery.server.business.model.SMDEntity;
import org.socialmusicdiscovery.server.business.model.core.Release;

import java.util.Collection;

public class Series extends SMDEntity<Series> {
    @neo(index=true)
    private String name;
    //TODO: How do we annotate this without affecting Release table
    @neo
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
