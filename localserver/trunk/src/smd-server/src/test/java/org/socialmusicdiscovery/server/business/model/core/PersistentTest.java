package org.socialmusicdiscovery.server.business.model.core;

import org.apache.derby.impl.io.VFMemoryStorageFactory;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.socialmusicdiscovery.server.business.logic.SMDApplication;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;


public class PersistentTest {
    private EntityManagerFactory emFactory;

    private EntityManager em;

    private final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy");

    @BeforeTest
    public void setUp()  {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            DriverManager.getConnection("jdbc:derby:memory:unit-test;create=true").close();
            //DriverManager.getConnection("jdbc:derby:target/unit-test;create=true").close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            emFactory = Persistence.createEntityManagerFactory("smd");
            em = emFactory.createEntityManager();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @AfterTest
    public void tearDown() throws SQLException, IOException {
           if (em != null && em.isOpen()) {
               em.close();
           }
           if (emFactory != null && emFactory.isOpen()) {
               emFactory.close();
           }
           try {
               DriverManager.getConnection("jdbc:derby:memory:unit-test;shutdown=true").close();
               //DriverManager.getConnection("jdbc:derby:target/unit-test;shutdown=true").close();
           } catch (SQLNonTransientConnectionException ex) {
               if (ex.getErrorCode() != 45000) {
                   throw ex;
               }
               // Shutdown success
           }
           VFMemoryStorageFactory.purgeDatabase(new File("target/unit-test").getCanonicalPath());
       }


    private void loadTestData(String file) throws ClassNotFoundException, SQLException, DatabaseUnitException, MalformedURLException {
        loadTestData(DatabaseOperation.CLEAN_INSERT,"src/test/test-data/"+this.getClass().getPackage().getName().replaceAll("\\.","/")+"/"+file);
    }
    private void addTestData(String file) throws ClassNotFoundException, SQLException, DatabaseUnitException, MalformedURLException {
        loadTestData(DatabaseOperation.INSERT,"src/test/test-data/"+this.getClass().getPackage().getName().replaceAll("\\.","/")+"/"+file);
    }

    private void loadTestData(DatabaseOperation dboperation,String file) throws ClassNotFoundException, SQLException, DatabaseUnitException, MalformedURLException {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        IDatabaseConnection connection = new DatabaseConnection(DriverManager.getConnection("jdbc:derby:memory:unit-test","",""));
        //IDatabaseConnection connection = new DatabaseConnection(DriverManager.getConnection("jdbc:derby:target/unit-test","",""));
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        IDataSet ds = builder.build(new File(file));
        dboperation.execute(connection,ds);
    }

    @Test
    public void testModelCreation() throws ParseException, Exception {
        loadTestData("Empty Tables.xml");
        em.getTransaction().begin();
        try {
            Release release = new Release();
            release.setName("The Bodyguard (Original Soundtrack Album)");
            release.setDate(DATE_FORMAT.parse("1992"));
            em.persist(release);

            Work work = new Work();
            work.setName("I Will Always Love You");
            em.persist(work);

            Contributor contributorDollyParton = new Contributor();
            contributorDollyParton.setType("composer");
            Artist artistDollyParton = new Artist();
            artistDollyParton.setName("Dolly Parton");
            em.persist(artistDollyParton);
            contributorDollyParton.setArtist(artistDollyParton);
            em.persist(contributorDollyParton);

            work.setContributors(Arrays.asList(contributorDollyParton));
            em.persist(work);

            Recording recording = new Recording();
            recording.setWork(work);
            em.persist(recording);

            Contributor contributorWhitneyHouston= new Contributor();
            contributorWhitneyHouston.setType("artist");
            Artist artistWhitneyHouston= new Artist();
            artistWhitneyHouston.setName("Whitney Houston");
            em.persist(artistWhitneyHouston);
            contributorWhitneyHouston.setArtist(artistWhitneyHouston);
            em.persist(contributorWhitneyHouston);

            Contributor contributorRickyMinor= new Contributor();
            contributorRickyMinor.setType("conductor");
            Artist artistRickyMinor= new Artist();
            artistRickyMinor.setName("Ricky Minor");
            em.persist(artistRickyMinor);
            contributorRickyMinor.setArtist(artistRickyMinor);
            em.persist(contributorRickyMinor);

            recording.setContributors(Arrays.asList(contributorWhitneyHouston,contributorRickyMinor));
            em.persist(recording);

            Track track = new Track();
            track.setNumber(1);
            track.setRecording(recording);
            em.persist(track);

            release.setTracks(Arrays.asList(track));
            em.persist(release);
        }
        finally{
            em.getTransaction().commit();
        }
        em.getTransaction().begin();
        Query query = em.createQuery("from Release where name=:name");
        query.setParameter("name","The Bodyguard (Original Soundtrack Album)");
        Release release = (Release) query.getSingleResult();
        assert(release != null);

        SMDApplication.printRelease(release);

        assert(release.getName().equals("The Bodyguard (Original Soundtrack Album)"));
        assert(DATE_FORMAT.format(release.getDate()).equals("1992"));
        assert(release.getTracks() != null);
        assert(release.getTracks().size() == 1);
        Track track = release.getTracks().iterator().next();
        assert(track.getNumber().equals(1));
        assert(track.getRecording() != null);
        assert(track.getRecording().getContributors() != null);
        assert(track.getRecording().getContributors().size()==2);
        Iterator<Contributor> iterator = track.getRecording().getContributors().iterator();
        Contributor contributor1 = iterator.next();
        assert(contributor1.getType() != null);
        assert(contributor1.getArtist() != null);
        assert(contributor1.getType().equals("conductor") || contributor1.getType().equals("artist"));
        if(contributor1.getType().equals("conductor")) {
            assert(contributor1.getArtist() != null);
            assert(contributor1.getArtist().getName().equals("Ricky Minor"));
            Contributor contributor2 = iterator.next();
            assert(contributor2.getType() != null);
            assert(contributor2.getType().equals("artist"));
            assert(contributor2.getArtist() != null);
            assert(contributor2.getArtist().getName().equals("Whitney Houston"));
        }else {
            assert(contributor1.getArtist() != null);
            assert(contributor1.getArtist().getName().equals("Whitney Houston"));
            Contributor contributor2 = iterator.next();
            assert(contributor2.getType() != null);
            assert(contributor2.getType().equals("conductor"));
            assert(contributor2.getArtist() != null);
            assert(contributor2.getArtist().getName().equals("Ricky Minor"));
        }
        em.getTransaction().commit();
    }

    @Test
    public void testModelRead() throws ClassNotFoundException, SQLException, DatabaseUnitException, MalformedURLException {
        loadTestData("The Bodyguard.xml");
        em.getTransaction().begin();
        try {
            Query query = em.createQuery("from Release where name=:name");
            query.setParameter("name","The Bodyguard (Original Soundtrack Album)");
            Release release = (Release) query.getSingleResult();
            assert(release != null);

            SMDApplication.printRelease(release);
        }catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }finally {
            em.getTransaction().commit();
        }
    }
}
