package org.socialmusicdiscovery.test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.socialmusicdiscovery.server.business.logic.JPAModule;
import org.socialmusicdiscovery.server.business.repository.SMDEntityReferenceRepository;
import org.socialmusicdiscovery.server.business.repository.core.*;
import org.socialmusicdiscovery.server.business.repository.subjective.CreditRepository;
import org.socialmusicdiscovery.server.business.repository.subjective.RelationRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
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
    protected PersonRepository personRepository;
    @Inject
    protected RecordingRepository recordingRepository;
    @Inject
    protected TrackRepository trackRepository;
    @Inject
    protected WorkRepository workRepository;
    @Inject
    protected CreditRepository creditRepository;
    @Inject
    protected RelationRepository relationRepository;
    @Inject
    protected SMDEntityReferenceRepository smdEntityReferenceRepository;

    protected final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy");
    private String DATABASE ="memory:unit-test";
    //private String DATABASE ="target/smd-database";

    private static boolean initialized = false;

    public void setUp()  {
        if(!initialized) {
            try {
                Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
                DriverManager.getConnection("jdbc:derby:"+DATABASE +";create=true").close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            initialized = true;
        }
        Guice.createInjector(new JPAModule()).injectMembers(this);
    }

    public void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emFactory != null && emFactory.isOpen()) {
            emFactory.close();
        }

        if (initialized) {
            try {
                DriverManager.getConnection("jdbc:derby:" + DATABASE + ";shutdown=true").close();
            } catch (SQLNonTransientConnectionException ex) {
                if (ex.getErrorCode() != 45000) {
                    throw new RuntimeException(ex);
                }
                // Shutdown success
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            initialized = false;
        }
    }

    public void loadTestData(String pkg, String file) throws Exception {
        loadTestData(DatabaseOperation.CLEAN_INSERT,"src/test/test-data/"+pkg.replaceAll("\\.","/")+"/"+file);
    }

    public void addTestData(String pkg,String file) throws Exception {
        loadTestData(DatabaseOperation.INSERT,"src/test/test-data/"+pkg.replaceAll("\\.","/")+"/"+file);
    }

    protected void loadTestData(DatabaseOperation dboperation,String file) throws Exception {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        IDatabaseConnection connection = new DatabaseConnection(DriverManager.getConnection("jdbc:derby:"+DATABASE,"",""));
        //IDatabaseConnection connection = new DatabaseConnection(DriverManager.getConnection("jdbc:derby:target/unit-test","",""));

        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        IDataSet ds = builder.build(new File(file));

        // Load the data set through a sequence filter to ensure statements are executed in correct order
        ITableFilter filter = new DatabaseSequenceFilter(connection);
        IDataSet dataset = new FilteredDataSet(filter, ds);

        dboperation.execute(connection,dataset);
    }
}
