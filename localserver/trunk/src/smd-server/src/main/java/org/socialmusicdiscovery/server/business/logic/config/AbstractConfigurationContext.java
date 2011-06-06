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

import org.socialmusicdiscovery.server.api.ConfigurationContext;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;

/**
 * Abstract base class which can be used by all {@link ConfigurationContext} implementations to
 * simplify type conversions
 */
public abstract class AbstractConfigurationContext implements ConfigurationContext {
    public AbstractConfigurationContext() {
        InjectHelper.injectMembers(this);
    }

    /**
     * @inheritDoc
     */
    @Override
    public abstract String getStringParameter(String id, String defaultValue);

    /**
     * @inheritDoc
     */
    @Override
    public String getStringParameter(String id) {
        return getStringParameter(id, null);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Boolean getBooleanParameter(String id) {
        return getBooleanParameter(id, null);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Boolean getBooleanParameter(String id, Boolean defaultValue) {
        String value = getStringParameter(id);
        return value != null ? Boolean.valueOf(value) : defaultValue;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Integer getIntegerParameter(String id) {
        return getIntegerParameter(id, null);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Integer getIntegerParameter(String id, Integer defaultValue) {
        String value = getStringParameter(id);
        return value != null ? Integer.valueOf(value) : defaultValue;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Double getDoubleParameter(String id) {
        return getDoubleParameter(id, null);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Double getDoubleParameter(String id, Double defaultValue) {
        String value = getStringParameter(id);
        return value != null ? Double.valueOf(value) : defaultValue;
    }
}
