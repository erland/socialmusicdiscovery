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
import org.dbunit.DatabaseUnitException;
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
import org.socialmusicdiscovery.server.api.mediaimport.ProcessingStatusCallback;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.logic.SearchRelationPostProcessor;
import org.socialmusicdiscovery.server.business.logic.injections.database.DatabaseProvider;
import org.testng.annotations.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Abstract base class for all test cases which needs to load/access database.
 * The responsibility of this class is to abstract the database setup/tear down from all the other test cases
 */
public abstract class BaseTestCase {
    @Inject
    private EntityManagerFactory emFactory;
    @Inject
    protected EntityManager em;

    protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy");

    private static boolean initialized = false;
    private DatabaseProvider provider = null;

    /**
     * Get the initialized database provider which is going to be used during testing, to override the default set the
     * org.socialmusicdiscovery.server.database VM parameter
     *
     * @return An initialized DatabaseProvider instance
     */
    protected DatabaseProvider getProvider() {
        if (System.getProperty("org.socialmusicdiscovery.server.database") == null) {
            System.setProperty("org.socialmusicdiscovery.server.database", "h2-memory");
        }
        if (provider == null) {
            provider = InjectHelper.instanceWithName(DatabaseProvider.class, System.getProperty("org.socialmusicdiscovery.server.database"));
        }
        return provider;
    }

    /**
     * Clear entity manager to ensure one test method doesn't affect the result of a following test method
     */
    @BeforeMethod
    protected void clearEntityManager() {
        if(em!=null && em.isOpen()) {
            em.clear();
        }
    }

    /**
     * Makes sure any open transaction is rolled back and completed after each test method
     * @param m The test case method
     */
    @AfterMethod
    protected void rollbackTransaction(Method m) {
        if(em!=null && em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

    /**
     * Logs the name of the test method
     * @param m The test case method
     */
    @BeforeMethod
    protected void logTestMethod(Method m) {
        System.out.println("Executing " + getClass().getSimpleName() + "." + m.getName() + "...");
    }


    /**
     * Initialize the database provider and entity manager to make sure they are ready to use.
     */
    @BeforeTest
    protected void setUpProvider() {
        if (!initialized) {
            getProvider().start();
            initialized = true;
        }
    }

    /**
     * Inject any Google Guice members to ensure they are initialized before any test cases are executed
     */
    @BeforeClass
    protected void injectMembers() {
        InjectHelper.injectMembers(this);
    }

    /**
     * Shutdown database provider and entity manager after the test case hase been executed.
     */
    @AfterTest
    protected void tearDownProvider() {
        // We don't want to fail the test case if the entity manager is already closed
        if (em != null && em.isOpen()) {
            em.close();
        }

        // We don't want to fail if the entity manager factory is already closed
        if (emFactory != null && emFactory.isOpen()) {
            emFactory.close();
        }

        // We don't want to file the test case if the database provider hasn't been initialized
        if (initialized) {
            getProvider().stop();
            initialized = false;
        }
    }

    /**
     * Update search relations, this method currently needs to be called if you perform some data modifications and later on want to use
     * search and browse methods
     */
    protected void updateSearchRelations() {
        SearchRelationPostProcessor searchRelationPostProcessor = new SearchRelationPostProcessor();
        searchRelationPostProcessor.init(null);
        searchRelationPostProcessor.execute(new ProcessingStatusCallback() {
            public void progress(String module, String currentDescription, Long currentNo, Long totalNo) {
            }

            public void failed(String module, String error) {
            }

            public void finished(String module) {
            }

            public void aborted(String module) {
            }
        });
    }

    /**
     * Get the parent directory of the path specified, this will return /src if you specify /src/test as input
     * @param path The path to get parent directory for
     * @return The parent directory
     */
    protected String getParentDirectory(String path) {
        String parentDir = "/";
        int lastIndex;

        // Only decend into paths that actually contains something
        if (path != null && path.trim().length() > 0) {
            path = path.trim();

            // Ignore any / character at the end
            if (path.endsWith("/") && path.length() > 1) {
                path = path.substring(0, path.length() - 1);
            }

            if (path.length() > 1) {
                lastIndex = path.lastIndexOf("/");

                if (lastIndex > 0) {
                    // Get path up until the last /
                    parentDir = path.substring(0, lastIndex);
                }
            }
        }

        return parentDir;
    }

    /**
     * Get the directory where the DbUnit test data files can be found
     * @return The DbUnit test data file directory
     */
    protected String getTestDataDiretory() {
        // We need to get the directory from the classpath to make sure the test case works the same independent of the current directory
        // when running the test case. This is required to make the test case easy to run both from Eclipse, IntelliJ IDEA and maven.
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

    /**
     * Load the specified DbUnit test data file into the currently used database provider, this function will first remove and existing test data
     * from the tables specified in the test data file and then load the new data.
     * @param pkg The package which the test data files are stored in
     * @param file The file name of the test data file to use
     */
    protected void loadTestData(String pkg, String file) {
        loadTestData(DatabaseOperation.CLEAN_INSERT, getTestDataDiretory() + pkg.replaceAll("\\.", "/") + "/" + file);
    }

    /**
     * Load the specified DbUnit test data file into the currently used database provider, in comparison to {@link #loadTestData(String, String)}
     * this function will not remove any existing test data, it will just load the new data in the test data file on top of any existing data
     * @param pkg The package which the test data files are stored in
     * @param file The file name of the test data file to use
     */
    protected void addTestData(String pkg, String file) {
        loadTestData(DatabaseOperation.INSERT, getTestDataDiretory() + pkg.replaceAll("\\.", "/") + "/" + file);
    }

    /**
     * Load the specified DbUnit test data file into the currently used database provider using the specified {@link DatabaseOperation} operation
     * type. It's prefered that you use {@link #addTestData(String, String)} or {@link #loadTestData(String, String)} instead, this method is just a
     * backup if you need to do something special.
     * @param dboperation The {@link DatabaseOperation} operation type which should be used when loading the test data
     * @param file The full path to the test data file, it's recommended to use {@link #getTestDataDiretory()} to calculate this
     */
    protected void loadTestData(DatabaseOperation dboperation, String file) {
        try {
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
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (DatabaseUnitException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
