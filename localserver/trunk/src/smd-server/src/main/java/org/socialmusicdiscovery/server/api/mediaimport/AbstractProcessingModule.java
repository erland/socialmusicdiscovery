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

package org.socialmusicdiscovery.server.api.mediaimport;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.api.ConfigurationContext;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameter;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.HashSet;

/**
 * Abstract helper class which all media importer or post processor modules should inherit from to simplify the implementation to avoid the need
 * to implement methods for which they are satisfied with the default behavior.
 */
public abstract class AbstractProcessingModule implements ProcessingModule {
    /**
     * Configuration context used by this media importer or post processing module
     */
    protected ConfigurationContext configuration = null;

    /**
     * Indication if the current execution has been requested to be aborted
     */
    private Boolean aborted;

    /**
     * Entity manager to use for this execution
     */
    @Inject
    protected EntityManager entityManager;

    /**
     * Default implementation that does nothing, override this if tye processing module supports to be aborted
     */
    @Override
    public void abort() {
        this.aborted = true;
    }

    /**
     * Default implementation which can be used by processing modules that doesn't have any configuration parameters, override this if the
     * processing module offers configuration parameters
     * @return
     */
    @Override
    public Collection<ConfigurationParameter> getDefaultConfiguration() {
        return new HashSet<ConfigurationParameter>();
    }

    /**
     * Default implementation that just stores the configuration context and makes them accessible through the
     * {@link #getConfiguration} method
     * @param configuration A collection with configuraiton parameters
     */
    @Override
    public void setConfiguration(ConfigurationContext configuration) {
        this.configuration = configuration;
    }

    protected ConfigurationContext getConfiguration() {
        return configuration;
    }

    /**
     * Indicates if the current execution has been requested to be aborted, modules performing long operations should call this method at regular
     * intervals to check if they should be aborted or safely can continue to do their work a while longer
     * @return true if the current execution should be aborted
     */
    protected Boolean isAborted() {
        return this.aborted;
    }

    /**
     * Default implementation which resets abort flag and makes sure the entity manager is ready to use
     */
    @Override
    public void init() {
        InjectHelper.injectMembers(this);
        this.aborted = false;
    }
}
