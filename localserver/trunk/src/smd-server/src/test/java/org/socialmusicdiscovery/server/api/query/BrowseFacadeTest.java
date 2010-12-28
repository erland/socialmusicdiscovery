package org.socialmusicdiscovery.server.api.query;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.socialmusicdiscovery.server.api.mediaimport.ProcessingStatusCallback;
import org.socialmusicdiscovery.server.business.logic.SearchRelationPostProcessor;
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
import org.testng.annotations.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class BrowseFacadeTest extends BaseTestCase {
    private static final String HOST = "http://localhost";
    private static final int PORT = 9997;
    private static final String HOSTURL = HOST + ":" + PORT;
    private ClientConfig config;
    private JSONProvider jsonProvider = new JSONProvider();
    private SelectorThread threadSelector;

    public static class JSONProvider extends AbstractJSONProvider {
        public JSONProvider() {
            super(true);
        }

        @Override
        protected Map<Class, Class> getConversionMap() {
            Map<Class, Class> converters = new HashMap<Class, Class>();

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
    public void setUp() {
        super.setUp();
    }

    @AfterTest
    public void tearDown() {
        super.tearDown();
    }

    @BeforeMethod
    public void setUpMethod(Method m) {
        System.out.println("Executing " + getClass().getSimpleName() + "." + m.getName() + "...");
    }

    @BeforeClass
    public void setUpClass() throws Exception {
        try {
            loadTestData("org.socialmusicdiscovery.server.business.model", "Arista RCA Releases.xml");
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

        Map<String, String> initParams = new HashMap<String, String>();
        initParams.put("com.sun.jersey.config.property.packages", "org.socialmusicdiscovery.server.api;org.socialmusicdiscovery.server.business.logic.jersey");

        URI uri = UriBuilder.fromUri(HOST + "/").port(PORT).build();
        threadSelector = GrizzlyWebContainerFactory.create(uri, initParams);

        config = new DefaultClientConfig();
        config.getClasses().add(JSONProvider.class);
    }

    @AfterClass
    public void tearDownClass() throws Exception {
        if (threadSelector != null) {
            threadSelector.stopEndpoint();
        }
    }

    @Test
    public void testBrowseArtists() throws Exception {
        JSONObject result = Client.create().resource(HOSTURL + "/browse/Artist").accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
        assert result.getLong("size") == 50;
        assert result.getLong("offset") == 0;
        assert result.getLong("totalSize") == 50;
        JSONArray resultItems = result.getJSONArray("items");

        for (int i = 0; i < resultItems.length(); i++) {
            JSONObject item = ((JSONObject) resultItems.get(i)).getJSONObject("item");
            Artist artist = jsonProvider.fromJson(item.toString(), Artist.class);
            assert artist != null;
            assert artist.getName() != null;
            JSONArray childItems = ((JSONObject) resultItems.get(i)).optJSONArray("childItems");
            assert childItems == null;
        }

        result = Client.create().resource(HOSTURL + "/browse/Artist?offset=5&size=10&childs=true").accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
        assert result.getLong("size") == 10;
        assert result.getLong("offset") == 5;
        assert result.getLong("totalSize") == 50;
        resultItems = result.getJSONArray("items");

        for (int i = 0; i < resultItems.length(); i++) {
            JSONObject item = ((JSONObject) resultItems.get(i)).getJSONObject("item");
            Artist artist = jsonProvider.fromJson(item.toString(), Artist.class);
            assert artist != null;
            assert artist.getName() != null;
            JSONArray childItems = ((JSONObject) resultItems.get(i)).optJSONArray("childItems");
            assert childItems != null;
        }
    }

    @Test
    public void testBrowseReleases() throws Exception {
        JSONObject result = Client.create().resource(HOSTURL + "/browse/Release").accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
        assert result.getLong("size") == 5;
        assert result.getLong("offset") == 0;
        assert result.getLong("totalSize") == 5;
        JSONArray resultItems = result.getJSONArray("items");

        for (int i = 0; i < resultItems.length(); i++) {
            JSONObject item = ((JSONObject) resultItems.get(i)).getJSONObject("item");
            Release release = jsonProvider.fromJson(item.toString(), Release.class);
            assert release != null;
            assert release.getName() != null;
            JSONArray childItems = ((JSONObject) resultItems.get(i)).optJSONArray("childItems");
            assert childItems == null;
        }

        result = Client.create().resource(HOSTURL + "/browse/Release?offset=2&size=10&childs=true").accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
        assert result.getLong("size") == 3;
        assert result.getLong("offset") == 2;
        assert result.getLong("totalSize") == 5;
        resultItems = result.getJSONArray("items");

        for (int i = 0; i < resultItems.length(); i++) {
            JSONObject item = ((JSONObject) resultItems.get(i)).getJSONObject("item");
            Release release = jsonProvider.fromJson(item.toString(), Release.class);
            assert release != null;
            assert release.getName() != null;
            JSONArray childItems = ((JSONObject) resultItems.get(i)).optJSONArray("childItems");
            assert childItems != null;
        }
    }

    @Test
    public void testBrowseTracks() throws Exception {
        JSONObject result = Client.create().resource(HOSTURL + "/browse/Track").accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
        assert result.getLong("size") == 79;
        assert result.getLong("offset") == 0;
        assert result.getLong("totalSize") == 79;
        JSONArray resultItems = result.getJSONArray("items");

        for (int i = 0; i < resultItems.length(); i++) {
            JSONObject item = ((JSONObject) resultItems.get(i)).getJSONObject("item");
            Track track = jsonProvider.fromJson(item.toString(), Track.class);
            assert track != null;
            assert track.getNumber() != null;
            JSONArray childItems = ((JSONObject) resultItems.get(i)).optJSONArray("childItems");
            assert childItems == null;
        }

        result = Client.create().resource(HOSTURL + "/browse/Track?offset=5&size=10&childs=true").accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
        assert result.getLong("size") == 10;
        assert result.getLong("offset") == 5;
        assert result.getLong("totalSize") == 79;
        resultItems = result.getJSONArray("items");

        for (int i = 0; i < resultItems.length(); i++) {
            JSONObject item = ((JSONObject) resultItems.get(i)).getJSONObject("item");
            Track track = jsonProvider.fromJson(item.toString(), Track.class);
            assert track != null;
            assert track.getNumber() != null;
            JSONArray childItems = ((JSONObject) resultItems.get(i)).optJSONArray("childItems");
            assert childItems != null;
        }
    }

    @Test
    public void testBrowseWorks() throws Exception {
        JSONObject result = Client.create().resource(HOSTURL + "/browse/Work").accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
        assert result.getLong("size") == 79;
        assert result.getLong("offset") == 0;
        assert result.getLong("totalSize") == 79;
        JSONArray resultItems = result.getJSONArray("items");

        for (int i = 0; i < resultItems.length(); i++) {
            JSONObject item = ((JSONObject) resultItems.get(i)).getJSONObject("item");
            Work work = jsonProvider.fromJson(item.toString(), Work.class);
            assert work != null;
            assert work.getName() != null;
            JSONArray childItems = ((JSONObject) resultItems.get(i)).optJSONArray("childItems");
            assert childItems == null;
        }

        result = Client.create().resource(HOSTURL + "/browse/Work?offset=5&size=10&childs=true").accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
        assert result.getLong("size") == 10;
        assert result.getLong("offset") == 5;
        assert result.getLong("totalSize") == 79;
        resultItems = result.getJSONArray("items");

        for (int i = 0; i < resultItems.length(); i++) {
            JSONObject item = ((JSONObject) resultItems.get(i)).getJSONObject("item");
            Work work = jsonProvider.fromJson(item.toString(), Work.class);
            assert work != null;
            assert work.getName() != null;
            JSONArray childItems = ((JSONObject) resultItems.get(i)).optJSONArray("childItems");
            assert childItems != null;
        }
    }

    @Test
    public void testBrowseClassifications() throws Exception {
        JSONObject result = Client.create().resource(HOSTURL + "/browse/Classification").accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
        assert result.getLong("size") == 17;
        assert result.getLong("offset") == 0;
        assert result.getLong("totalSize") == 17;
        JSONArray resultItems = result.getJSONArray("items");

        for (int i = 0; i < resultItems.length(); i++) {
            JSONObject item = ((JSONObject) resultItems.get(i)).getJSONObject("item");
            Classification classification = jsonProvider.fromJson(item.toString(), Classification.class);
            assert classification != null;
            assert classification.getName() != null;
            JSONArray childItems = ((JSONObject) resultItems.get(i)).optJSONArray("childItems");
            assert childItems == null;
        }

        result = Client.create().resource(HOSTURL + "/browse/Classification?offset=5&size=10&childs=true").accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
        assert result.getLong("size") == 10;
        assert result.getLong("offset") == 5;
        assert result.getLong("totalSize") == 17;
        resultItems = result.getJSONArray("items");

        for (int i = 0; i < resultItems.length(); i++) {
            JSONObject item = ((JSONObject) resultItems.get(i)).getJSONObject("item");
            Classification classification = jsonProvider.fromJson(item.toString(), Classification.class);
            assert classification != null;
            assert classification.getName() != null;
            JSONArray childItems = ((JSONObject) resultItems.get(i)).optJSONArray("childItems");
            assert childItems != null;
        }
    }
}
