package org.socialmusicdiscovery.server.api.management;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.socialmusicdiscovery.server.business.model.SMDEntityReference;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Person;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FacadeTest extends BaseTestCase {
    @BeforeTest
    public void setUp()  {
        super.setUp();
    }

    @AfterTest
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testFacades() throws Exception {
        loadTestData("org.socialmusicdiscovery.server.business.model","The Bodyguard.xml");

        Collection<SMDEntityReference> references = smdEntityReferenceRepository.findAll();

        Map<String, String> initParams = new HashMap<String, String>();
        initParams.put("com.sun.jersey.config.property.packages", "org.socialmusicdiscovery.server.api.management");

        System.out.println("Starting grizzly...");
        URI uri = UriBuilder.fromUri("http://localhost/").port(9998).build();
        SelectorThread threadSelector = GrizzlyWebContainerFactory.create(uri, initParams);

        Collection<Artist> artists = Client.create().resource("http://localhost:9998/artists/search").accept("application/json").get(new GenericType<Collection<Artist>>() {});
        assert artists.size() == 16;
        for(Artist a: artists) {
            assert a.getName()!= null;
            if(!a.getName().equals("The S.O.U.L. S.Y.S.T.E.M.")) {
                assert a.getPerson() != null;
                assert a.getPerson().getName() != null;
            }else {
                assert a.getPerson()==null;
            }
        }

        Artist myArtist = new Artist();
        myArtist.setName("Anne-Sophie Mutter");
        Artist a = Client.create().resource("http://localhost:9998/artists/create").type("application/json").post(Artist.class,myArtist);
        assert a!=null;
        assert a.getName().equals(myArtist.getName());
        assert a.getId()!=null;

        a = Client.create().resource("http://localhost:9998/artists/get?id="+a.getId()).accept("application/json").get(Artist.class);
        assert a!=null;
        assert a.getName().equals(myArtist.getName());
        assert a.getId()!=null;
        assert a.getPerson()==null;

        Person myPerson = new Person();
        myPerson.setName("Anne-Sophie Mutter");
        Person p = Client.create().resource("http://localhost:9998/persons/create").type("application/json").post(Person.class,myPerson);
        assert p!=null;
        assert p.getName().equals(myPerson.getName());
        assert p.getId()!=null;

        a.setPerson(p);
        a = Client.create().resource("http://localhost:9998/artists/update").type("application/json").post(Artist.class,a);
        assert a!=null;
        assert a.getName().equals(myArtist.getName());
        assert a.getId()!=null;
        assert a.getPerson()!=null;
        assert a.getPerson().getName().equals("Anne-Sophie Mutter");

        String result = Client.create().resource("http://localhost:9998/artists/delete?id="+a.getId()).accept("application/json").get(String.class);
        result = Client.create().resource("http://localhost:9998/persons/delete?id="+p.getId()).accept("application/json").get(String.class);

        artists = Client.create().resource("http://localhost:9998/artists/search").accept("application/json").get(new GenericType<Collection<Artist>>() {});
        assert artists.size() == 16;

        Collection<Person> persons = Client.create().resource("http://localhost:9998/persons/search").accept("application/json").get(new GenericType<Collection<Person>>() {});
        assert persons.size() == 15;

        Collection<SMDEntityReference> refs = smdEntityReferenceRepository.findAll();

        threadSelector.stopEndpoint();
    }
}
