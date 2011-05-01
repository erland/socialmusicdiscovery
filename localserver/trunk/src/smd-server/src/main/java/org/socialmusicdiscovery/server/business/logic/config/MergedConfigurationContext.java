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

package org.socialmusicdiscovery.server.business.logic.config;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.socialmusicdiscovery.server.api.ConfigurationContext;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameter;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameterEntity;
import org.socialmusicdiscovery.server.business.repository.config.ConfigurationParameterRepository;

import java.util.HashSet;
import java.util.Set;

public class MergedConfigurationContext implements ConfigurationContext {
    @Inject
    @Named("default-value")
    ConfigurationManager defaultValuesConfigurationManager;

    @Inject
    ConfigurationParameterRepository configurationParameterRepository;

    public MergedConfigurationContext() {
        InjectHelper.injectMembers(this);
    }

    @Override
    public String getStringParameter(String id) {
        ConfigurationParameter parameter = configurationParameterRepository.findById(id);
        if(parameter == null) {
            parameter =  defaultValuesConfigurationManager.getParameter(id);
        }
        return parameter!=null && parameter.getValue()!=null?parameter.getValue():null;
    }

    @Override
    public Boolean getBooleanParameter(String id) {
        String value = getStringParameter(id);
        return value!=null?Boolean.valueOf(value):null;
    }

    @Override
    public Integer getIntegerParameter(String id) {
        String value = getStringParameter(id);
        return value!=null?Integer.valueOf(value):null;
    }

    @Override
    public Double getDoubleParameter(String id) {
        String value = getStringParameter(id);
        return value!=null?Double.valueOf(value):null;
    }

    public ConfigurationParameterEntity getParameter(String id) {
        ConfigurationParameterEntity result = configurationParameterRepository.findById(id);
        if(result == null) {
            result = defaultValuesConfigurationManager.getParameter(id);
        }
        return result;
    }

    public Set<ConfigurationParameterEntity> getParameters() {
        Set<ConfigurationParameterEntity> resultParameters = new HashSet<ConfigurationParameterEntity>(configurationParameterRepository.findAll());
        resultParameters.addAll(defaultValuesConfigurationManager.getParameters());
        return resultParameters;
    }

    public Set<ConfigurationParameterEntity> getParametersByPath(String path) {
        Set<ConfigurationParameterEntity> resultParameters = new HashSet<ConfigurationParameterEntity>(configurationParameterRepository.findByPath(path));
        resultParameters.addAll(defaultValuesConfigurationManager.getParametersByPath(path));
        return resultParameters;
    }
}