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
import com.google.inject.Inject;
import com.google.inject.Provides;
import org.hibernate.SessionFactory;
import org.hibernate.stat.*;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;

/**
 * Provides a {@link StatisticsLogger} instance that logs statistics provided by Hibernate
 */
public class JPAStatisticsLoggerModule extends AbstractModule {
    private static class HibernateStatisticsLogger implements StatisticsLogger {
        @Inject
        SessionFactory sessionFactory;

        public HibernateStatisticsLogger() {
            InjectHelper.injectMembers(this);
        }

        @Override
        public void start() {
            sessionFactory.getStatistics().clear();
        }

        @Override
        public void finish() {
            Statistics statistics = sessionFactory.getStatistics();
            if(statistics.isStatisticsEnabled()) {
                for (String entity : statistics.getEntityNames()) {
                    EntityStatistics entityStatistics = statistics.getEntityStatistics(entity);
                    System.out.println("Entity "+entity+":"+
                            " fetch="+entityStatistics.getFetchCount()+
                            ", insert="+entityStatistics.getInsertCount()+
                            ", load="+entityStatistics.getLoadCount()+
                            ", update="+entityStatistics.getUpdateCount());
                }
                for (String role : statistics.getCollectionRoleNames()) {
                    CollectionStatistics collectionStatistics = statistics.getCollectionStatistics(role);
                    System.out.println("Collection "+role+":"+
                            " fetch="+collectionStatistics.getFetchCount()+
                            ", recreate="+collectionStatistics.getRecreateCount()+
                            ", update="+collectionStatistics.getUpdateCount()+
                            ", load="+collectionStatistics.getLoadCount()+
                            ", delete="+collectionStatistics.getRemoveCount());
                }
                for (String query : statistics.getQueries()) {
                    QueryStatistics queryStatistics = statistics.getQueryStatistics(query);
                    System.out.println("Query "+query+":"+
                            " cacheHit="+queryStatistics.getCacheHitCount()+
                            ", cacheMiss="+queryStatistics.getCacheMissCount()+
                            ", cachePut="+queryStatistics.getCachePutCount()+
                            ", count="+queryStatistics.getExecutionCount()+
                            ", minTime="+queryStatistics.getExecutionMinTime()+
                            ", avgTime="+queryStatistics.getExecutionAvgTime()+
                            ", maxTime="+queryStatistics.getExecutionMaxTime()+
                            ", rows="+queryStatistics.getExecutionRowCount());
                }
                for (String region : statistics.getSecondLevelCacheRegionNames()) {
                    SecondLevelCacheStatistics secondLevelCacheStatistics = statistics.getSecondLevelCacheStatistics(region);
                    System.out.println("SecondLevelCache "+region+":"+
                            " cacheHit="+secondLevelCacheStatistics.getHitCount()+
                            ", cacheMiss="+secondLevelCacheStatistics.getMissCount()+
                            ", cachePut="+secondLevelCacheStatistics.getPutCount()+
                            ", onDisk="+secondLevelCacheStatistics.getElementCountOnDisk()+
                            ", inMemory="+secondLevelCacheStatistics.getElementCountInMemory()+
                            ", inMemorySize="+secondLevelCacheStatistics.getSizeInMemory());
                }
                System.out.println("Summary flushCount="+statistics.getFlushCount());
                System.out.println("Summary transactionCount="+statistics.getTransactionCount());
                System.out.println("Summary successfulTransactionCount="+statistics.getSuccessfulTransactionCount());
                System.out.println("Summary sessionOpenCount="+statistics.getSessionOpenCount());
                System.out.println("Summary sessionCloseCount="+statistics.getSessionCloseCount());
                System.out.println("Summary queryCacheHitCount="+statistics.getQueryCacheHitCount());
                System.out.println("Summary queryCacheMissCount="+statistics.getQueryCacheMissCount());
                System.out.println("Summary queryCacheHitCount="+statistics.getQueryCachePutCount());
                System.out.println("Summary secondLevelCacheHitCount="+statistics.getSecondLevelCacheHitCount());
                System.out.println("Summary secondLevelCacheMissCount="+statistics.getSecondLevelCacheMissCount());
                System.out.println("Summary secondLevelCachePutCount="+statistics.getSecondLevelCachePutCount());
            }
        }
    }

    @Override
    protected void configure() {
    }

    @Provides
    public StatisticsLogger getStatisticsLogger() {
        return new HibernateStatisticsLogger();
    }
}
