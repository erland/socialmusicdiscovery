package org.socialmusicdiscovery.server.business.model;

import org.socialmusicdiscovery.server.api.mediaimport.ProcessingStatusCallback;
import org.socialmusicdiscovery.server.business.logic.SearchRelationPostProcessor;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

public class ArtistFindTest extends BaseTestCase {
    @BeforeTest
    public void setUp()  {
        super.setUp();
    }

    @AfterTest
    public void tearDown() {
        super.tearDown();
    }

    @BeforeClass
    public void setUpClass() {
        try {
            loadTestData(getClass().getPackage().getName(),"The Bodyguard.xml");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        new SearchRelationPostProcessor().execute(new ProcessingStatusCallback() {
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

    @BeforeMethod
    public void setUpMethod(Method m) {
        System.out.println("Executing "+getClass().getSimpleName()+"."+m.getName()+"...");
        em.clear();
    }
    @AfterMethod
    public void tearDownMethod(Method m) {
        if(em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
    @Test
    public void testFind() {
        em.clear();
        Collection<ArtistEntity> artists = artistRepository.findAll();
        assert artists.size()==16;

        em.clear();
        artists = artistRepository.findAllWithRelations(Arrays.asList("person"),null);
        assert artists.size()==15;

        em.clear();
        artists = artistRepository.findAllWithRelations(null, Arrays.asList("person"));
        assert artists.size()==16;

        em.clear();
        Artist artist = artistRepository.findById(artists.iterator().next().getId());
        assert artist!=null;

        artists = artistRepository.findByName("Whitney Houston");
        assert artists.size()==1;
        assert artists.iterator().next().getName().equals("Whitney Houston");

        artists = artistRepository.findByNameWithRelations("Whitney Houston", Arrays.asList("person"),null);
        assert artists.size()==1;
        assert artists.iterator().next().getName().equals("Whitney Houston");
        Person person = artists.iterator().next().getPerson();
        assert person != null;
        assert person.getName().equals("Whitney Elisabeth Houston");

        artists = artistRepository.findByPartialNameWithRelations("Whitney", null, null);
        assert artists.size()==1;
        assert artists.iterator().next().getName().equals("Whitney Houston");

        artists = artistRepository.findByPersonWithRelations(person.getId(),null,null);
        assert artists.size()==1;
        assert artists.iterator().next().getName().equals("Whitney Houston");

        Collection<ReleaseEntity> releases = releaseRepository.findByName("The Bodyguard (Original Soundtrack Album)");
        assert releases.size()==1;
        Release release = releases.iterator().next();
        artists = artistRepository.findByReleaseWithRelations(release.getId(),null,null);
        assert artists.size()==16;

        artists = artistRepository.findByWorkWithRelations(release.getTracks().get(0).getRecording().getWorks().iterator().next().getId(),null,null);
        assert artists.size()==3;
    }
}
