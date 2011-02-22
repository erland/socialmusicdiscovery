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
    public void testBrowseTypes() throws Exception {
        JSONArray resultItems = Client.create().resource(HOSTURL + "/browse").accept(MediaType.APPLICATION_JSON).get(JSONArray.class);
        assert resultItems.length() == 9;

        for (int i = 0; i < resultItems.length(); i++) {
            String id = ((JSONObject) resultItems.get(i)).getString("id");
            assert id!=null;
            Long count = ((JSONObject) resultItems.get(i)).optLong("count");
            assert count==0;
        }
        String release = "Release:bb348240-8d6d-4a22-8b92-ab8446d54235";
        resultItems = Client.create().resource(HOSTURL + "/browse?criteria="+release+"&counters=true").accept(MediaType.APPLICATION_JSON).get(JSONArray.class);
        assert resultItems.length() == 7;

        for (int i = 0; i < resultItems.length(); i++) {
            String id = ((JSONObject) resultItems.get(i)).getString("id");
            assert id!=null;
            Long count = ((JSONObject) resultItems.get(i)).optLong("count");
            assert count!=null;
            assert count>0;

            JSONObject result = Client.create().resource(HOSTURL + "/browse/"+id+"?criteria="+release+"&childs=true").accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
            assert result.getLong("size") == count;

            JSONArray childResultItems = result.getJSONArray("items");
            for (int j = 0; j < childResultItems.length(); j++) {
                JSONObject item = ((JSONObject) childResultItems.get(j)).getJSONObject("item");
                String childId = item.getString("id");
                JSONArray childItems = ((JSONObject) childResultItems.get(j)).optJSONArray("childItems");
                assert childItems != null;

                Map<String,Long> childMap = new HashMap<String,Long>();
                for (int k=0;k<childItems.length();k++) {
                    childMap.put(((JSONObject)childItems.get(k)).getString("id"),((JSONObject)childItems.get(k)).getLong("count"));
                }

                JSONArray childCounters = Client.create().resource(HOSTURL + "/browse?criteria="+release+"&criteria="+id+":"+childId+"&counters=true").accept(MediaType.APPLICATION_JSON).get(JSONArray.class);
                for (int v = 0; v < childCounters.length(); v++) {
                    String childCounterId = ((JSONObject) childCounters.get(v)).getString("id");
                    assert childCounterId!=null;
                    Long childCounter = ((JSONObject) childCounters.get(v)).getLong("count");
                    assert childCounter!=null;
                    assert childMap.containsKey(childCounterId);
                    if(!childMap.get(childCounterId).equals(childCounter)) {
                        JSONObject testing = Client.create().resource(HOSTURL + "/browse/"+childCounterId+"?criteria="+release+"&criteria="+id+":"+childId).accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
                        testing.toString();
                    }
                    assert childMap.get(childCounterId).equals(childCounter);
                }
            }
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
