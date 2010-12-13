package org.socialmusicdiscovery.server.api.management;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
import org.socialmusicdiscovery.server.business.model.GlobalIdentity;
import org.socialmusicdiscovery.server.business.model.GlobalIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReference;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;
import org.socialmusicdiscovery.server.business.model.classification.Classification;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationEntity;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.model.subjective.*;
import org.socialmusicdiscovery.server.support.json.AbstractJSONProvider;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FacadeTest extends BaseTestCase {
    private static final String HOST = "http://localhost";
    private static final int PORT = 9997;
    private static final String HOSTURL = HOST+":"+PORT;

    public static class JSONProvider extends AbstractJSONProvider {
        public JSONProvider() {
            super(true);
        }

        @Override
        protected Map<Class, Class> getConversionMap() {
            Map<Class, Class> converters = new HashMap<Class,Class>();

            converters.put(Label.class, LabelEntity.class);
            converters.put(Release.class, ReleaseEntity.class);
            converters.put(Contributor.class, ContributorEntity.class);
            converters.put(Artist.class, ArtistEntity.class);
            converters.put(Person.class, PersonEntity.class);
            converters.put(Medium.class, MediumEntity.class);
            converters.put(Track.class, TrackEntity.class);
            converters.put(RecordingSession.class, RecordingSessionEntity.class);
            converters.put(Recording.class, RecordingEntity.class);
            converters.put(Work.class, WorkEntity.class);
            converters.put(SMDIdentityReference.class, SMDIdentityReferenceEntity.class);
            converters.put(Classification.class, ClassificationEntity.class);
            converters.put(GlobalIdentity.class, GlobalIdentityEntity.class);
            converters.put(Relation.class, SMDIdentityReferenceEntity.class);
            converters.put(Credit.class, CreditEntity.class);
            converters.put(Series.class, SeriesEntity.class);
            
            return converters;
        }
    }

    @BeforeTest
    public void setUp()  {
        super.setUp();
    }

    @AfterTest
    public void tearDown() {
        super.tearDown();
    }

    @BeforeMethod
    public void setUpMethod(Method m) {
        System.out.println("Executing "+getClass().getSimpleName()+"."+m.getName()+"...");
    }

    @Test
    public void testFacades() throws Exception {
        loadTestData("org.socialmusicdiscovery.server.business.model","The Bodyguard.xml");

        Collection<SMDIdentityReferenceEntity> references = smdIdentityReferenceRepository.findAll();

        Map<String, String> initParams = new HashMap<String, String>();
        initParams.put("com.sun.jersey.config.property.packages", "org.socialmusicdiscovery.server.api;org.socialmusicdiscovery.server.business.logic.jersey");

        URI uri = UriBuilder.fromUri(HOST+"/").port(PORT).build();
        SelectorThread threadSelector = GrizzlyWebContainerFactory.create(uri, initParams);

        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(JSONProvider.class);
        Collection<Artist> artists = Client.create(config).resource(HOSTURL+"/artists").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Artist>>() {});
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

        Artist myArtist = new ArtistEntity();
        myArtist.setName("Anne-Sophie Mutter");
        Artist a = Client.create(config).resource(HOSTURL+"/artists").type(MediaType.APPLICATION_JSON).post(Artist.class,myArtist);
        assert a!=null;
        assert a.getName().equals(myArtist.getName());
        assert a.getId()!=null;

        a = Client.create(config).resource(HOSTURL+"/artists/"+a.getId()).accept(MediaType.APPLICATION_JSON).get(Artist.class);
        assert a!=null;
        assert a.getName().equals(myArtist.getName());
        assert a.getId()!=null;
        assert a.getPerson()==null;

        Person myPerson = new PersonEntity();
        myPerson.setName("Anne-Sophie Mutter");
        Person p = Client.create(config).resource(HOSTURL+"/persons").type(MediaType.APPLICATION_JSON).post(Person.class,myPerson);
        assert p!=null;
        assert p.getName().equals(myPerson.getName());
        assert p.getId()!=null;

        a.setPerson(p);
        a = Client.create(config).resource(HOSTURL+"/artists/"+a.getId()).type(MediaType.APPLICATION_JSON).put(Artist.class,a);
        assert a!=null;
        assert a.getName().equals(myArtist.getName());
        assert a.getId()!=null;
        assert a.getPerson()!=null;
        assert a.getPerson().getName().equals("Anne-Sophie Mutter");

        Client.create(config).resource(HOSTURL+"/artists/"+a.getId()).accept(MediaType.APPLICATION_JSON).delete();
        Client.create(config).resource(HOSTURL+"/persons/"+p.getId()).accept(MediaType.APPLICATION_JSON).delete();

        artists = Client.create(config).resource(HOSTURL+"/artists").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Artist>>() {});
        assert artists.size() == 16;

        Collection<Person> persons = Client.create(config).resource(HOSTURL+"/persons").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Person>>() {});
        assert persons.size() == 15;

        Collection<SMDIdentityReferenceEntity> refs = smdIdentityReferenceRepository.findAll();

        threadSelector.stopEndpoint();
    }
}
