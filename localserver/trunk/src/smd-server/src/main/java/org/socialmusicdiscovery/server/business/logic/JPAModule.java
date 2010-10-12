package org.socialmusicdiscovery.server.business.logic;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAModule extends AbstractModule {
    private static final ThreadLocal<EntityManager> ENTITY_MANAGER_CACHE = new ThreadLocal<EntityManager>();
    private static EntityManagerFactory emFactory;
    @Override
    protected void configure() {
    }

    @Provides @Singleton
    public EntityManagerFactory provideEntityManagerFactory() {
        if(emFactory == null) {
            emFactory = Persistence.createEntityManagerFactory("smd");
        }
        return emFactory;
     }

    @Provides 
    public EntityManager provideEntityManager(EntityManagerFactory entityManagerFactory) {
        EntityManager entityManager = ENTITY_MANAGER_CACHE.get();
        if (entityManager == null) {
          ENTITY_MANAGER_CACHE.set(entityManager = entityManagerFactory.createEntityManager());
        }
        return entityManager;
    }
}
