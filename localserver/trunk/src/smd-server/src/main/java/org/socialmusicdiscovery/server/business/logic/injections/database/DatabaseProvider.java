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

package org.socialmusicdiscovery.server.business.logic.injections.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * All database providers needs to implement this interface
 */
public interface DatabaseProvider {
    /**
     * Get the list of properties that should be overridden when creating the {@link javax.persistence.EntityManagerFactory}}
     *
     * @return A map of properties
     */
    Map<String, String> getProperties();

    /**
     * The class name of the JDBC driver to use for this provider
     *
     * @return The class name
     */
    String getDriver();

    /**
     * The URL to use when accessing this provider
     *
     * @return The JDBC database url
     */
    String getUrl();

    /**
     * Called initially before the first connection is established with the provider
     */
    void start();

    /**
     * Called at the end after the last connection towards the provider has been closed
     */
    void stop();

    /**
     * Called to get a new connection, the caller is responsible to call close() on the connection
     * when it has finished using it
     */
    Connection getConnection() throws SQLException;
}
