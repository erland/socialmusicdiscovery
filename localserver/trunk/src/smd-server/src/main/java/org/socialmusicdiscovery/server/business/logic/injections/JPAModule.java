package org.socialmusicdiscovery.server.business.logic.injections;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.logic.injections.database.DatabaseProvider;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class JPAModule extends AbstractModule {
    private static final ThreadLocal<EntityManager> ENTITY_MANAGER_CACHE = new ThreadLocal<EntityManager>();
    private static EntityManagerFactory emFactory;

    private static Map<String, Map<String,String>> persistenceOverride = new HashMap<String,Map<String,String>>();

    @Override
    protected void configure() {
    }

    @Provides @Singleton
    public EntityManagerFactory provideEntityManagerFactory() {
        if(emFactory == null) {
            String database = System.getProperty("org.socialmusicdiscovery.server.database");
            DatabaseProvider provider;
            if(database != null) {
                provider = InjectHelper.instanceWithName(DatabaseProvider.class,database);
                if(provider == null) {
                    throw new RuntimeException("No database provider exists for: "+database);
                }
            }else {
                throw new RuntimeException("No database provider specified");
            }
            emFactory = Persistence.createEntityManagerFactory("smd",provider.getProperties());
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
