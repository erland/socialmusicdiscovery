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

package org.socialmusicdiscovery.server.api;

/**
 * Configuration context interface which provides access to configuration parameters
 */
public interface ConfigurationContext {
    /**
     * Get the string configuration parameter with specified identity
     * @param id The identity of the configuration parameter
     * @return The value of the configuration parameter
     */
    String getStringParameter(String id);

    /**
     * Get the string configuration parameter with specified identity
     * @param id The identity of the configuration parameter
     * @param defaultValue The default value to use if parameter doesn't exist
     * @return The value of the configuration parameter
     */
    String getStringParameter(String id, String defaultValue);

    /**
     * Get the boolean configuration parameter with specified identity
     * @param id The identity of the configuration parameter
     * @return The value of the configuration parameter
     */
    Boolean getBooleanParameter(String id);

    /**
     * Get the boolean configuration parameter with specified identity
     * @param id The identity of the configuration parameter
     * @param defaultValue The default value to use if parameter doesn't exist
     * @return The value of the configuration parameter
     */
    Boolean getBooleanParameter(String id, Boolean defaultValue);

    /**
     * Get the integer configuration parameter with specified identity
     * @param id The identity of the configuration parameter
     * @return The value of the configuration parameter
     */
    Integer getIntegerParameter(String id);

    /**
     * Get the integer configuration parameter with specified identity
     * @param id The identity of the configuration parameter
     * @param defaultValue The default value to use if parameter doesn't exist
     * @return The value of the configuration parameter
     */
    Integer getIntegerParameter(String id, Integer defaultValue);

    /**
     * Get the double configuration parameter with specified identity
     * @param id The identity of the configuration parameter
     * @return The value of the configuration parameter
     */
    Double getDoubleParameter(String id);

    /**
     * Get the double configuration parameter with specified identity
     * @param id The identity of the configuration parameter
     * @param defaultValue The default value to use if parameter doesn't exist
     * @return The value of the configuration parameter
     */
    Double getDoubleParameter(String id, Double defaultValue);
}
