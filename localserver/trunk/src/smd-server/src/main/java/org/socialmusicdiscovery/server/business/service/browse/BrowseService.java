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

package org.socialmusicdiscovery.server.business.service.browse;

import org.socialmusicdiscovery.server.api.ConfigurationContext;

import java.util.Collection;

public interface BrowseService<T> {
    /**
     * This method will be called by the {@link BrowseServiceManager} before an instance of this service is used for the first time
     *
     * @param configurationContext The configuration context which this service should use to get its configuration
     */
    void setConfiguration(ConfigurationContext configurationContext);

    /**
     * The type of objects which this service provides
     *
     * @return The identity of the object type this service provides
     */
    String getObjectType();

    /**
     * Find children immediately matching the specified browse criterias
     *
     * @param criteriaList     The list of browse criteria the children has to match
     * @param sortCriteriaList The list of sorting criteras which should be used
     * @param firstItem        The index of the first item that should be returned, used for paged requests
     * @param maxItems         The maximum number of items that should be returned, used for paged requests
     * @param childCounters    Indicates if child counters should be returned
     * @return The list of matching child object instances
     */
    Result<T> findChildren(Collection<String> criteriaList, Collection<String> sortCriteriaList, Integer firstItem, Integer maxItems, Boolean childCounters);

    /**
     * Find the object with the specified identity
     *
     * @param id The identity of the object to find
     * @return The object instance or null if it can't be found
     */
    ResultItem<T> findById(String id);
}
