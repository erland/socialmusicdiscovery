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

package org.socialmusicdiscovery.server.business.model;

import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameter;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameterEntity;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.Test;

import javax.persistence.Query;


public class ConfigTest extends BaseTestCase {

    @Test
    public void testConfigCreation() throws Exception {
        loadTestData(getClass().getPackage().getName(),"Empty Tables.xml");
        em.getTransaction().begin();
        try {
            ConfigurationParameterEntity booleanParameter = new ConfigurationParameterEntity();
            booleanParameter.setId("somebooleanparameter");
            booleanParameter.setType(ConfigurationParameter.Type.BOOLEAN);
            booleanParameter.setValue("true");
            configurationParameterRepository.create(booleanParameter);

            ConfigurationParameterEntity stringParameter = new ConfigurationParameterEntity();
            stringParameter.setId("somestringparameter");
            stringParameter.setType(ConfigurationParameter.Type.STRING);
            stringParameter.setValue("hello");
            configurationParameterRepository.create(stringParameter);

            ConfigurationParameterEntity numberParameter = new ConfigurationParameterEntity();
            numberParameter.setId("somenumberparameter");
            numberParameter.setType(ConfigurationParameter.Type.INTEGER);
            numberParameter.setValue("42");
            configurationParameterRepository.create(numberParameter);
        }
        finally{
            if(!em.getTransaction().getRollbackOnly()) {
                em.getTransaction().commit();
            }
        }
        em.getTransaction().begin();

        Query query = em.createQuery("from ConfigurationParameterEntity where id=:id");
        query.setParameter("id","somebooleanparameter");
        ConfigurationParameter param = (ConfigurationParameter) query.getSingleResult();
        assert(param != null);
        assert(param.getValue().equals("true"));
        assert(param.getType().equals(ConfigurationParameter.Type.BOOLEAN));

        query = em.createQuery("from ConfigurationParameterEntity where id=:id");
        query.setParameter("id","somestringparameter");
        param = (ConfigurationParameter) query.getSingleResult();
        assert(param != null);
        assert(param.getValue().equals("hello"));
        assert(param.getType().equals(ConfigurationParameter.Type.STRING));

        query = em.createQuery("from ConfigurationParameterEntity where id=:id");
        query.setParameter("id","somenumberparameter");
        param = (ConfigurationParameter) query.getSingleResult();
        assert(param != null);
        assert(param.getValue().equals("42"));
        assert(param.getType().equals(ConfigurationParameter.Type.INTEGER));

        em.getTransaction().commit();
    }

    @Test
    public void testConfigUpdate() throws Exception {
        loadTestData(getClass().getPackage().getName(),"Empty Tables.xml");
        em.getTransaction().begin();
        try {
            ConfigurationParameterEntity booleanParameter = new ConfigurationParameterEntity();
            booleanParameter.setId("somebooleanparameter");
            booleanParameter.setType(ConfigurationParameter.Type.BOOLEAN);
            booleanParameter.setValue("true");
            configurationParameterRepository.create(booleanParameter);

            ConfigurationParameterEntity stringParameter = new ConfigurationParameterEntity();
            stringParameter.setId("somestringparameter");
            stringParameter.setType(ConfigurationParameter.Type.STRING);
            stringParameter.setValue("helllo");
            configurationParameterRepository.create(stringParameter);

            booleanParameter = new ConfigurationParameterEntity();
            booleanParameter.setId("somebooleanparameter");
            booleanParameter.setType(ConfigurationParameter.Type.BOOLEAN);
            booleanParameter.setValue("false");
            configurationParameterRepository.merge(booleanParameter);

            stringParameter = configurationParameterRepository.findById("somestringparameter");
            stringParameter.setValue("good bye");

        }
        finally{
            if(!em.getTransaction().getRollbackOnly()) {
                em.getTransaction().commit();
            }
        }
        em.getTransaction().begin();

        Query query = em.createQuery("from ConfigurationParameterEntity where id=:id");
        query.setParameter("id","somebooleanparameter");
        ConfigurationParameter param = (ConfigurationParameter) query.getSingleResult();
        assert(param != null);
        assert(param.getValue().equals("false"));
        assert(param.getType().equals(ConfigurationParameter.Type.BOOLEAN));

        query = em.createQuery("from ConfigurationParameterEntity where id=:id");
        query.setParameter("id","somestringparameter");
        param = (ConfigurationParameter) query.getSingleResult();
        assert(param != null);
        assert(param.getValue().equals("good bye"));
        assert(param.getType().equals(ConfigurationParameter.Type.STRING));

        em.getTransaction().commit();
    }

}
