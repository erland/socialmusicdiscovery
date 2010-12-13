package org.socialmusicdiscovery.server.business.model.subjective;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.core.Release;

import java.util.Collection;

public interface Series extends SMDIdentity {
    String getName();

    void setName(String name);

    Collection<Release> getReleases();

    void setReleases(Collection<Release> releases);
}
