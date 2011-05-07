/*
 *  Copyright 2010-2011, Social Music Discovery project
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of Social Music Discovery project nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL SOCIAL MUSIC DISCOVERY PROJECT BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.socialmusicdiscovery.server.business.logic.injections;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.logic.TransactionManager;
import org.socialmusicdiscovery.server.business.logic.injections.database.DatabaseProvider;

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
    public SessionFactory provideSessionFactory(EntityManagerFactory entityManagerFactory) {
        if(entityManagerFactory instanceof HibernateEntityManagerFactory) {
            return ((HibernateEntityManagerFactory)entityManagerFactory).getSessionFactory();
        }
        return null;
    }

    @Provides
    public EntityManager provideEntityManager(EntityManagerFactory entityManagerFactory) {
        EntityManager entityManager = ENTITY_MANAGER_CACHE.get();
        if (entityManager == null) {
          ENTITY_MANAGER_CACHE.set(entityManager = entityManagerFactory.createEntityManager());
        }
        return entityManager;
    }

    @Provides
    public TransactionManager provideTransactionManager(final EntityManagerFactory entityManagerFactory) {
        return new TransactionManager() {
            @Override
            public void begin() {
                EntityManager entityManager = ENTITY_MANAGER_CACHE.get();
                if(entityManager==null) {
                    entityManager = entityManagerFactory.createEntityManager();
                }
                entityManager.clear();
                entityManager.getTransaction().begin();
                ENTITY_MANAGER_CACHE.set(entityManager);
            }

            @Override
            public void end() {
                EntityManager entityManager = ENTITY_MANAGER_CACHE.get();
                ENTITY_MANAGER_CACHE.remove();
                if(entityManager!=null) {
                    if(entityManager.getTransaction().getRollbackOnly()) {
                        entityManager.getTransaction().rollback();
                    }else {
                        entityManager.getTransaction().commit();
                    }
                }
            }

            @Override
            public void setRollbackOnly() {
                EntityManager entityManager = ENTITY_MANAGER_CACHE.get();
                if(entityManager!=null) {
                    entityManager.getTransaction().setRollbackOnly();
                }
            }
        };
    }
}
