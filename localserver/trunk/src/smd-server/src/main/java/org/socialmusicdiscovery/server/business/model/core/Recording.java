package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;

import java.util.Date;
import java.util.Set;

public interface Recording extends SMDIdentity {
    String getName();

    void setName(String name);

    Date getDate();

    void setDate(Date date);

    Recording getMixOf();

    void setMixOf(Recording mixOf);

    Set<Contributor> getContributors();

    void setContributors(Set<Contributor> contributors);

    Set<Work> getWorks();

    void setWorks(Set<Work> works);
}
