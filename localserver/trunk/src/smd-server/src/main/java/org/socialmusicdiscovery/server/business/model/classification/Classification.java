package org.socialmusicdiscovery.server.business.model.classification;

import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReference;

import java.util.Set;

public interface Classification extends SMDIdentity {
    final static String GENRE = "genre";
    final static String MOOD = "mood";
    final static String STYLE = "style";

    String getType();

    void setType(String type);

    String getName();

    void setName(String name);

    Set<Classification> getChilds();

    void setChilds(Set<Classification> childs);

    Set<SMDIdentityReference> getReferences();

    void setReferences(Set<SMDIdentityReference> references);
}
