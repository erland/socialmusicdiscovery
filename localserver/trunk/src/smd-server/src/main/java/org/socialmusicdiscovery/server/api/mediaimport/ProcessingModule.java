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

import org.socialmusicdiscovery.server.api.ConfigurationContext;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameter;

import java.util.Collection;

/**
 * Interface that all processing modules have to implement
 */
public interface ProcessingModule {
    /**
     * Returns the unique identity of the processing module, this is used when you want to issue a command to the processing module
     * @return
     */
    String getId();

    /**
     * Called when the processing module is supposed to execute its logic, the module should use the provided callback interface to
     * report the progress of the operation
     * @param progressHandler A callback object which the processing module should call to report the current status
     */
    void execute(ProcessingStatusCallback progressHandler);

    /**
     * Abort the current processing operation in progress
     */
    void abort();

    /**
     * Will be called to retrieve available configuration parameters and their default value, a plugin should return all its configuration
     * parameters in this call to make them accessible from the configuration user interface
     * @return A list of configuration parameters with their default values
     */
    Collection<ConfigurationParameter> getDefaultConfiguration();

    /**
     * Will be called initially before the plugin is started or whenever a configuration parameter is changed
     */
    void setConfiguration(ConfigurationContext configuration);

    /**
     * Will be called just before the execute module to do any initialization needed before the import module is
     * ready for usage
     */
    void init();
}
