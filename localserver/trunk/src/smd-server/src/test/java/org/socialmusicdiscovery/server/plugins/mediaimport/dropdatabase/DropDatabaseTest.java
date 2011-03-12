package org.socialmusicdiscovery.server.plugins.mediaimport.dropdatabase;

import org.socialmusicdiscovery.server.api.mediaimport.ProcessingStatusCallback;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class DropDatabaseTest extends BaseTestCase {
    DropDatabase dropDatabase;
    boolean finished;

    @BeforeTest
    public void setUp() {
        super.setUp();
        dropDatabase = new DropDatabase();
    }

    @AfterTest
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testDropDatabase() throws Exception {
        loadTestData("org.socialmusicdiscovery.server.business.model", "Arista RCA Releases.xml");
        finished = false;
        dropDatabase.execute(new ProcessingStatusCallback() {
            @Override
            public void progress(String module, String currentDescription, Long currentNo, Long totalNo) {}
            @Override
            public void failed(String module, String error) {
                System.err.println("ERROR: "+error);
            }
            @Override
            public void finished(String module) {
                finished = true;
            }
            @Override
            public void aborted(String module) {}
        });
        assert finished;
        // Clean up code to ensure tables and constraints are created by annotations and not Liquibase
        // Some Liquibase constraints cause problems during unit testing
        EntityManager cleanUpEM = Persistence.createEntityManagerFactory("smd", getProvider().getProperties()).createEntityManager();
        cleanUpEM.getTransaction().begin();
        cleanUpEM.getTransaction().commit();
    }
}
