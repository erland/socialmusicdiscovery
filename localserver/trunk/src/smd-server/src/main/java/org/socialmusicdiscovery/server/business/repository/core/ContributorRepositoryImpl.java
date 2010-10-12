package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.repository.SMDEntityRepositoryImpl;
import org.socialmusicdiscovery.server.business.model.core.Contributor;

import javax.persistence.EntityManager;

public class ContributorRepositoryImpl extends SMDEntityRepositoryImpl<Contributor> implements ContributorRepository {
    public ContributorRepositoryImpl() {}
    @Inject
    public ContributorRepositoryImpl(EntityManager em) {super(em);}
}
