package org.socialmusicdiscovery.server.api.management;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
import org.socialmusicdiscovery.server.business.model.SMDEntityReference;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Person;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FacadeTest extends BaseTestCase {
    private static final String HOST = "http://localhost";
    private static final int PORT = 9998;
    private static final String HOSTURL = HOST+":"+PORT;

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
        URI uri = UriBuilder.fromUri(HOST+"/").port(PORT).build();
        SelectorThread threadSelector = GrizzlyWebContainerFactory.create(uri, initParams);

        Collection<Artist> artists = Client.create().resource(HOSTURL+"/artists").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Artist>>() {});
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
        Artist a = Client.create().resource(HOSTURL+"/artists").type(MediaType.APPLICATION_JSON).post(Artist.class,myArtist);
        assert a!=null;
        assert a.getName().equals(myArtist.getName());
        assert a.getId()!=null;

        a = Client.create().resource(HOSTURL+"/artists/"+a.getId()).accept(MediaType.APPLICATION_JSON).get(Artist.class);
        assert a!=null;
        assert a.getName().equals(myArtist.getName());
        assert a.getId()!=null;
        assert a.getPerson()==null;

        Person myPerson = new Person();
        myPerson.setName("Anne-Sophie Mutter");
        Person p = Client.create().resource(HOSTURL+"/persons").type(MediaType.APPLICATION_JSON).post(Person.class,myPerson);
        assert p!=null;
        assert p.getName().equals(myPerson.getName());
        assert p.getId()!=null;

        a.setPerson(p);
        a = Client.create().resource(HOSTURL+"/artists/"+a.getId()).type(MediaType.APPLICATION_JSON).put(Artist.class,a);
        assert a!=null;
        assert a.getName().equals(myArtist.getName());
        assert a.getId()!=null;
        assert a.getPerson()!=null;
        assert a.getPerson().getName().equals("Anne-Sophie Mutter");

        Client.create().resource(HOSTURL+"/artists/"+a.getId()).accept(MediaType.APPLICATION_JSON).delete();
        Client.create().resource(HOSTURL+"/persons/"+p.getId()).accept(MediaType.APPLICATION_JSON).delete();

        artists = Client.create().resource(HOSTURL+"/artists").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Artist>>() {});
        assert artists.size() == 16;

        Collection<Person> persons = Client.create().resource(HOSTURL+"/persons").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Person>>() {});
        assert persons.size() == 15;

        Collection<SMDEntityReference> refs = smdEntityReferenceRepository.findAll();

        threadSelector.stopEndpoint();
    }
}
