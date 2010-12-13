package org.socialmusicdiscovery.server.business.model.core;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;

import java.util.Date;
import java.util.Set;

public interface Work extends SMDIdentity {
    String getName();

    void setName(String name);

    Date getDate();

    void setDate(Date date);

    Set<Work> getParts();

    void setParts(Set<Work> parts);

    Work getParent();

    void setParent(Work parent);

    Set<Contributor> getContributors();

    void setContributors(Set<Contributor> contributors);
}
