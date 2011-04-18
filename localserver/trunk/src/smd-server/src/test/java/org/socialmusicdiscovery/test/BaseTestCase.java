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

package org.socialmusicdiscovery.test;

import com.google.inject.Inject;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.logic.injections.database.DatabaseProvider;
import org.socialmusicdiscovery.server.business.repository.GlobalIdentityRepository;
import org.socialmusicdiscovery.server.business.repository.SMDIdentityReferenceRepository;
import org.socialmusicdiscovery.server.business.repository.classification.ClassificationReferenceRepository;
import org.socialmusicdiscovery.server.business.repository.classification.ClassificationRepository;
import org.socialmusicdiscovery.server.business.repository.core.*;
import org.socialmusicdiscovery.server.business.repository.subjective.CreditRepository;
import org.socialmusicdiscovery.server.business.repository.subjective.RelationRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.sql.DriverManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class BaseTestCase {
    @Inject
    private EntityManagerFactory emFactory;
    @Inject
    protected EntityManager em;
    @Inject
    protected ReleaseRepository releaseRepository;
    @Inject
    protected ArtistRepository artistRepository;
    @Inject
    protected ContributorRepository contributorRepository;
    @Inject
    protected LabelRepository labelRepository;
    @Inject
    protected PersonRepository personRepository;
    @Inject
    protected RecordingRepository recordingRepository;
    @Inject
    protected TrackRepository trackRepository;
    @Inject
    protected PlayableElementRepository playableElementRepository;
    @Inject
    protected WorkRepository workRepository;
    @Inject
    protected GlobalIdentityRepository globalIdentityRepository;
    @Inject
    protected ClassificationRepository classificationRepository;
    @Inject
    protected ClassificationReferenceRepository classificationReferenceRepository;
    @Inject
    protected CreditRepository creditRepository;
    @Inject
    protected RelationRepository relationRepository;
    @Inject
    protected SMDIdentityReferenceRepository smdIdentityReferenceRepository;

    protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy");

    private static boolean initialized = false;
    private DatabaseProvider provider = null;

    public DatabaseProvider getProvider() {
        if (System.getProperty("org.socialmusicdiscovery.server.database") == null) {
            System.setProperty("org.socialmusicdiscovery.server.database", "h2-memory");
        }
        if (provider == null) {
            provider = InjectHelper.instanceWithName(DatabaseProvider.class, System.getProperty("org.socialmusicdiscovery.server.database"));
        }
        return provider;
    }

    public void setUp() {
        if (!initialized) {
            getProvider().start();
            initialized = true;
        }
        InjectHelper.injectMembers(this);
    }

    public void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emFactory != null && emFactory.isOpen()) {
            emFactory.close();
        }

        if (initialized) {
            getProvider().stop();
            initialized = false;
        }
    }

    public static String getParentDirectory(String path) {
        String parentDir = "/";
        int lastIndex;

        if (path != null && path.trim().length() > 0) {
            path = path.trim();

            if (path.endsWith("/") && path.length() > 1) {
                path = path.substring(0, path.length() - 1);
            }

            if (path.length() > 1) {
                lastIndex = path.lastIndexOf("/");

                if (lastIndex > 0) {
                    parentDir = path.substring(0, lastIndex);
                }
            }
        }

        return parentDir;
    }

    public String getTestDataDiretory() {
        String path = getClass().getResource("/META-INF/persistence.xml").getPath();
        if (path != null) {
            path = getParentDirectory(path);
            if (path != null) {
                path = getParentDirectory(path);
            }
            if (path != null) {
                path = getParentDirectory(path);
            }
            if (path != null) {
                path = getParentDirectory(path);
            }
        }
        if (path != null) {
            return path + "/" + "src/test/test-data/";
        } else {
            return "src/test/test-data/";
        }
    }

    public void loadTestData(String pkg, String file) throws Exception {
        loadTestData(DatabaseOperation.CLEAN_INSERT, getTestDataDiretory() + pkg.replaceAll("\\.", "/") + "/" + file);
    }

    public void addTestData(String pkg, String file) throws Exception {
        loadTestData(DatabaseOperation.INSERT, getTestDataDiretory() + pkg.replaceAll("\\.", "/") + "/" + file);
    }

    protected void loadTestData(DatabaseOperation dboperation, String file) throws Exception {
        Class.forName(getProvider().getDriver());
        String username = "";
        String password = "";
        if (provider.getUrl().startsWith("jdbc:mysql")) {
            if (System.getProperty("mysql.username") != null) {
                username = System.getProperty("mysql.username");
            }
            if (System.getProperty("mysql.password") != null) {
                password = System.getProperty("mysql.password");
            }
        }
        IDatabaseConnection connection = new DatabaseConnection(DriverManager.getConnection(provider.getUrl(), username, password));
        DatabaseConfig config = connection.getConfig();
        if (provider.getUrl().startsWith("jdbc:h2")) {
            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());
        } else if (provider.getUrl().startsWith("jdbc:hsql")) {
            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new HsqldbDataTypeFactory());
        } else if (provider.getUrl().startsWith("jdbc:mysql")) {
            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
        }

        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        IDataSet ds = builder.build(new File(file));

        // Load the data set through a sequence filter to ensure statements are executed in correct order
        ITableFilter filter = new DatabaseSequenceFilter(connection);
        IDataSet dataset = new FilteredDataSet(filter, ds);

        dboperation.execute(connection, dataset);
    }
}
