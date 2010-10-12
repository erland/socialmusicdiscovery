package org.socialmusicdiscovery.server.business.repository;

import java.util.Collection;

public interface EntityRepository<K, E> {
    void create(E entity);
    void remove(E entity);
    E merge(E entity);
    E findById(K id);
    Collection<E> findAll();
}
