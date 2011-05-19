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

package org.socialmusicdiscovery.server.api.management;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
import org.socialmusicdiscovery.server.api.mediaimport.ProcessingStatusCallback;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.logic.SearchRelationPostProcessor;
import org.socialmusicdiscovery.server.business.logic.config.ConfigurationManager;
import org.socialmusicdiscovery.server.business.model.*;
import org.socialmusicdiscovery.server.business.model.classification.Classification;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationEntity;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameter;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameterEntity;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.model.subjective.*;
import org.socialmusicdiscovery.server.support.json.AbstractJSONProvider;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;

public class FacadeTest extends BaseTestCase {
    private static final String HOST = "http://localhost";
    private static final int PORT = 9997;
    private static final String HOSTURL = HOST+":"+PORT;
    private SelectorThread threadSelector;
    private ClientConfig config;

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
            converters.put(PlayableElement.class, PlayableElementEntity.class);
            converters.put(ConfigurationParameter.class, ConfigurationParameterEntity.class);
            converters.put(SMDIdentity.class,SMDIdentity.class);

            return converters;
        }

        @Override
        protected Map<String, Class> getObjectTypeConversionMap() {
            Map<String, Class> converters = new HashMap<String,Class>();

            converters.put(Release.TYPE, ReleaseEntity.class);
            converters.put(Work.TYPE, WorkEntity.class);
            converters.put(Recording.TYPE, RecordingEntity.class);
            converters.put(RecordingSession.TYPE, RecordingSessionEntity.class);
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

    @BeforeClass
    public void init() throws Exception {
        loadTestData("org.socialmusicdiscovery.server.business.model","The Bodyguard.xml");
        SearchRelationPostProcessor searchRelationPostProcessor = new SearchRelationPostProcessor();
        searchRelationPostProcessor.init();
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
        Map<String, String> initParams = new HashMap<String, String>();
        initParams.put("com.sun.jersey.config.property.packages", "org.socialmusicdiscovery.server.api;org.socialmusicdiscovery.server.business.logic.jersey");

        URI uri = UriBuilder.fromUri(HOST+"/").port(PORT).build();
        threadSelector = GrizzlyWebContainerFactory.create(uri, initParams);

        config = new DefaultClientConfig();
        config.getClasses().add(JSONProvider.class);
    }

    @AfterClass
    public void exit() {
        threadSelector.stopEndpoint();
    }

    @Test
    public void testArtist() throws Exception {
        Collection<SMDIdentityReferenceEntity> references = smdIdentityReferenceRepository.findAll();

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

    }

    @Test
    public void testPerson() throws Exception {
        Person myPerson = new PersonEntity();
        myPerson.setName("Test Person");
        Person p = Client.create(config).resource(HOSTURL+"/persons").type(MediaType.APPLICATION_JSON).post(Person.class,myPerson);

        assert p!=null;
        assert p.getName().equals(myPerson.getName());
        assert p.getId()!=null;

        p = Client.create(config).resource(HOSTURL+"/persons/"+p.getId()).accept(MediaType.APPLICATION_JSON).get(Person.class);

        assert p!=null;
        assert p.getName().equals(myPerson.getName());
        assert p.getId()!=null;

        p.setName("Test Person 2");
        p = Client.create(config).resource(HOSTURL+"/persons/"+p.getId()).type(MediaType.APPLICATION_JSON).put(Person.class, p);
        assert p!=null;
        assert p.getName().equals("Test Person 2");
        assert p.getId()!=null;

        Collection<Person> persons = Client.create(config).resource(HOSTURL+"/persons").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Person>>() {});
        assert persons!=null;
        assert persons.size()>0;
        boolean found = false;
        for (Person person : persons) {
            if(person.getId().equals(p.getId())) {
                found = true;
            }
        }
        assert found;

        Client.create(config).resource(HOSTURL+"/persons/"+p.getId()).accept(MediaType.APPLICATION_JSON).delete();

        persons = Client.create(config).resource(HOSTURL+"/persons").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Person>>() {});
        assert persons!=null;
        found = false;
        for (Person person : persons) {
            if(person.getId().equals(p.getId())) {
                found = true;
            }
        }
        assert !found;
    }

    @Test
    public void testRelease() throws Exception {
        Release myRelease = new ReleaseEntity();
        myRelease.setName("Test Release");
        Release r = Client.create(config).resource(HOSTURL+"/releases").type(MediaType.APPLICATION_JSON).post(Release.class,myRelease);

        assert r!=null;
        assert r.getName().equals(myRelease.getName());
        assert r.getId()!=null;

        r = Client.create(config).resource(HOSTURL+"/releases/"+r.getId()).accept(MediaType.APPLICATION_JSON).get(Release.class);

        assert r!=null;
        assert r.getName().equals(myRelease.getName());
        assert r.getId()!=null;

        r.setName("Test Release 2");
        r = Client.create(config).resource(HOSTURL+"/releases/"+r.getId()).type(MediaType.APPLICATION_JSON).put(Release.class, r);
        assert r!=null;
        assert r.getName().equals("Test Release 2");
        assert r.getId()!=null;

        Collection<Release> releases = Client.create(config).resource(HOSTURL+"/releases").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Release>>() {});
        assert releases !=null;
        assert releases.size()>0;
        boolean found = false;
        for (Release release : releases) {
            if(release.getId().equals(r.getId())) {
                found = true;
            }
        }
        assert found;

        Client.create(config).resource(HOSTURL+"/releases/"+r.getId()).accept(MediaType.APPLICATION_JSON).delete();

        releases = Client.create(config).resource(HOSTURL+"/releases").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Release>>() {});
        assert releases !=null;
        found = false;
        for (Release release : releases) {
            if(release.getId().equals(r.getId())) {
                found = true;
            }
        }
        assert !found;
    }

    @Test
    public void testRecording() throws Exception {
        Work myWork = new WorkEntity();
        myWork.setName("Test Work");
        Work w = Client.create(config).resource(HOSTURL+"/works").type(MediaType.APPLICATION_JSON).post(Work.class,myWork);

        Recording myRecording = new RecordingEntity();
        myRecording.setName("Test Recording");
        myRecording.getWorks().add(w);
        Recording r = Client.create(config).resource(HOSTURL+"/recordings").type(MediaType.APPLICATION_JSON).post(Recording.class,myRecording);

        assert r!=null;
        assert r.getName().equals(myRecording.getName());
        assert r.getId()!=null;
        assert r.getWorks()!=null;
        assert r.getWorks().size()==1;
        assert r.getWorks().iterator().next().getName().equals(myWork.getName());

        r = Client.create(config).resource(HOSTURL+"/recordings/"+r.getId()).accept(MediaType.APPLICATION_JSON).get(Recording.class);

        assert r!=null;
        assert r.getName().equals(myRecording.getName());
        assert r.getId()!=null;

        r.setName("Test Recording 2");
        r = Client.create(config).resource(HOSTURL+"/recordings/"+r.getId()).type(MediaType.APPLICATION_JSON).put(Recording.class, r);
        assert r!=null;
        assert r.getName().equals("Test Recording 2");
        assert r.getId()!=null;

        Collection<Recording> recordings = Client.create(config).resource(HOSTURL+"/recordings").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Recording>>() {});
        assert recordings !=null;
        assert recordings.size()>0;
        boolean found = false;
        for (Recording recording : recordings) {
            if(recording.getId().equals(r.getId())) {
                found = true;
            }
        }
        assert found;

        Client.create(config).resource(HOSTURL+"/recordings/"+r.getId()).accept(MediaType.APPLICATION_JSON).delete();
        Client.create(config).resource(HOSTURL+"/works/"+w.getId()).accept(MediaType.APPLICATION_JSON).delete();

        recordings = Client.create(config).resource(HOSTURL+"/recordings").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Recording>>() {});
        assert recordings !=null;
        found = false;
        for (Recording recording : recordings) {
            if(recording.getId().equals(r.getId())) {
                found = true;
            }
        }
        assert !found;
    }

    @Test
    public void testLabel() throws Exception {
        Label myLabel = new LabelEntity();
        myLabel.setName("Test Label");
        Label l = Client.create(config).resource(HOSTURL+"/labels").type(MediaType.APPLICATION_JSON).post(Label.class,myLabel);

        assert l!=null;
        assert l.getName().equals(myLabel.getName());
        assert l.getId()!=null;

        l = Client.create(config).resource(HOSTURL+"/labels/"+l.getId()).accept(MediaType.APPLICATION_JSON).get(Label.class);

        assert l!=null;
        assert l.getName().equals(myLabel.getName());
        assert l.getId()!=null;

        l.setName("Test Release 2");
        l = Client.create(config).resource(HOSTURL+"/labels/"+l.getId()).type(MediaType.APPLICATION_JSON).put(Label.class, l);
        assert l!=null;
        assert l.getName().equals("Test Release 2");
        assert l.getId()!=null;

        Collection<Label> labels = Client.create(config).resource(HOSTURL+"/labels").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Label>>() {});
        assert labels !=null;
        assert labels.size()>0;
        boolean found = false;
        for (Label label : labels) {
            if(label.getId().equals(l.getId())) {
                found = true;
            }
        }
        assert found;

        Client.create(config).resource(HOSTURL+"/labels/"+l.getId()).accept(MediaType.APPLICATION_JSON).delete();

        labels = Client.create(config).resource(HOSTURL+"/labels").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Label>>() {});
        assert labels !=null;
        found = false;
        for (Label label : labels) {
            if(label.getId().equals(l.getId())) {
                found = true;
            }
        }
        assert !found;
    }

    @Test
    public void testTrack() throws Exception {
        Release myRelease = new ReleaseEntity();
        myRelease.setName("Test Release");
        Release release = Client.create(config).resource(HOSTURL+"/releases").type(MediaType.APPLICATION_JSON).post(Release.class,myRelease);

        Work myWork = new WorkEntity();
        myWork.setName("Test Work");
        Work w = Client.create(config).resource(HOSTURL+"/works").type(MediaType.APPLICATION_JSON).post(Work.class,myWork);

        Recording myRecording = new RecordingEntity();
        myRecording.setName("Test Recording");
        myRecording.getWorks().add(w);
        Recording recording = Client.create(config).resource(HOSTURL+"/recordings").type(MediaType.APPLICATION_JSON).post(Recording.class,myRecording);

        Track myTrack = new TrackEntity();
        myTrack.setRelease(release);
        myTrack.setRecording(recording);
        myTrack.setNumber(13);

        PlayableElement myFlacPlayableElement = new PlayableElementEntity();
        myFlacPlayableElement.setSmdID("7777777");
        myFlacPlayableElement.setFormat("flc");
        myFlacPlayableElement.setUri("file:///music/SomeRelease/track.flac");
        myTrack.getPlayableElements().add(myFlacPlayableElement);

        PlayableElement myMP3PlayableElement = new PlayableElementEntity();
        myMP3PlayableElement.setSmdID("8888888");
        myMP3PlayableElement.setFormat("mp3");
        myMP3PlayableElement.setUri("file:///music/SomeRelease/track.mp3");
        myTrack.getPlayableElements().add(myMP3PlayableElement);

        Track t = Client.create(config).resource(HOSTURL+"/tracks").type(MediaType.APPLICATION_JSON).post(Track.class,myTrack);
        assert t!=null;
        assert t.getNumber().equals(myTrack.getNumber());
        assert t.getId()!=null;
        assert t.getRecording()!=null;
        assert t.getRecording().getId().equals(recording.getId());
        assert t.getRecording().getWorks()!=null;
        assert t.getRecording().getWorks().size()==1;
        assert t.getRecording().getWorks().iterator().next().getName().equals(myWork.getName());
        assert t.getPlayableElements()!=null;
        assert t.getPlayableElements().size()==2;

        t = Client.create(config).resource(HOSTURL+"/tracks/"+t.getId()).accept(MediaType.APPLICATION_JSON).get(Track.class);

        assert t!=null;
        assert t.getNumber().equals(myTrack.getNumber());
        assert t.getId()!=null;

        t.setNumber(4);
        t.getPlayableElements().remove(myFlacPlayableElement);
        t.getPlayableElements().iterator().next().setUri("file:///music/SomeOtherRelease/track.mp3");
        t = Client.create(config).resource(HOSTURL+"/tracks/"+t.getId()).type(MediaType.APPLICATION_JSON).put(Track.class, t);
        assert t!=null;
        assert t.getNumber().equals(4);
        assert t.getId()!=null;
        assert t.getPlayableElements()!=null;
        assert t.getPlayableElements().size()==1;
        assert t.getPlayableElements().iterator().next().getUri().equals("file:///music/SomeOtherRelease/track.mp3");

        Collection<Track> tracks = Client.create(config).resource(HOSTURL+"/tracks").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Track>>() {});
        assert tracks !=null;
        assert tracks.size()>0;
        boolean found = false;
        for (Track track : tracks) {
            if(track.getId().equals(t.getId())) {
                found = true;
            }
        }
        assert found;

        tracks = Client.create(config).resource(HOSTURL+"/tracks?recording="+recording.getId()+"WRONG").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Track>>() {});
        assert tracks.size()==0;

        tracks = Client.create(config).resource(HOSTURL+"/tracks?recording="+recording.getId()).accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Track>>() {});
        assert tracks !=null;
        assert tracks.size()>0;
        found = false;
        for (Track track : tracks) {
            if(track.getId().equals(t.getId())) {
                found = true;
            }
        }
        assert found;

        Collection<PlayableElement> previousPlayableElements = Client.create(config).resource(HOSTURL+"/playableelements").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<PlayableElement>>() {});

        Client.create(config).resource(HOSTURL+"/tracks/"+t.getId()).accept(MediaType.APPLICATION_JSON).delete();
        Client.create(config).resource(HOSTURL+"/recordings/"+recording.getId()).accept(MediaType.APPLICATION_JSON).delete();
        Client.create(config).resource(HOSTURL+"/works/"+w.getId()).accept(MediaType.APPLICATION_JSON).delete();
        Client.create(config).resource(HOSTURL+"/releases/"+release.getId()).accept(MediaType.APPLICATION_JSON).delete();

        tracks = Client.create(config).resource(HOSTURL+"/tracks").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Track>>() {});
        assert tracks !=null;
        found = false;
        for (Track track : tracks) {
            if(track.getId().equals(t.getId())) {
                found = true;
            }
        }
        assert !found;

        Collection<PlayableElement> playableElements = Client.create(config).resource(HOSTURL+"/playableelements").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<PlayableElement>>() {});
        assert playableElements!=null;
        assert playableElements.size()==previousPlayableElements.size()-1;
    }

    @Test
    public void testPlayableElement() throws Exception {
        PlayableElement myPlayableElement = new PlayableElementEntity();
        myPlayableElement.setUri("file:///music/SomeRelease/track.flac");
        myPlayableElement.setFormat("flc");
        myPlayableElement.setSmdID("9999999");
        PlayableElement playableElement = Client.create(config).resource(HOSTURL+"/playableelements").type(MediaType.APPLICATION_JSON).post(PlayableElement.class,myPlayableElement);

        assert playableElement!=null;
        assert playableElement.getFormat().equals("flc");
        assert playableElement.getSmdID().equals("9999999");
        assert playableElement.getUri().equals("file:///music/SomeRelease/track.flac");

        playableElement = Client.create(config).resource(HOSTURL+"/playableelements/"+playableElement.getId()).accept(MediaType.APPLICATION_JSON).get(PlayableElement.class);

        assert playableElement!=null;
        assert playableElement.getSmdID().equals(myPlayableElement.getSmdID());
        assert playableElement.getId()!=null;

        playableElement.setUri("file:///music/OtherFolder/SomeRelease/track.flac");
        playableElement = Client.create(config).resource(HOSTURL+"/playableelements/"+playableElement.getId()).type(MediaType.APPLICATION_JSON).put(PlayableElement.class, playableElement);
        assert playableElement!=null;
        assert playableElement.getSmdID().equals(myPlayableElement.getSmdID());
        assert playableElement.getUri().equals("file:///music/OtherFolder/SomeRelease/track.flac");
        assert playableElement.getId()!=null;

        Collection<PlayableElement> playableElements = Client.create(config).resource(HOSTURL+"/playableelements").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<PlayableElement>>() {});
        assert playableElements !=null;
        assert playableElements.size()>0;
        boolean found = false;
        for (PlayableElement element : playableElements) {
            if(element.getId().equals(playableElement.getId())) {
                found = true;
            }
        }
        assert found;

        playableElements = Client.create(config).resource(HOSTURL+"/playableelements?uri="+playableElement.getUri()+"WRONG").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<PlayableElement>>() {});
        assert playableElements.size()==0;

        playableElements = Client.create(config).resource(HOSTURL+"/playableelements?uri="+playableElement.getUri()).accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<PlayableElement>>() {});
        assert playableElements.size()>0;

        found = false;
        for (PlayableElement element : playableElements) {
            if(element.getId().equals(playableElement.getId())) {
                found = true;
            }
        }
        assert found;

        playableElements = Client.create(config).resource(HOSTURL+"/playableelements?smdID="+playableElement.getSmdID()).accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<PlayableElement>>() {});
        assert playableElements.size()>0;

        playableElements = Client.create(config).resource(HOSTURL+"/playableelements?uriContains=OtherFolder").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<PlayableElement>>() {
        });
        assert playableElements.size()>0;

        Client.create(config).resource(HOSTURL+"/playableelements/"+playableElement.getId()).accept(MediaType.APPLICATION_JSON).delete();

        playableElements = Client.create(config).resource(HOSTURL+"/playableelements").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<PlayableElement>>() {});
        assert playableElements !=null;
        found = false;
        for (PlayableElement element : playableElements) {
            if(element.getId().equals(playableElement.getId())) {
                found = true;
            }
        }
        assert !found;
    }

    @Test
    public void testReleaseMedium() throws Exception {
        Release myRelease = new ReleaseEntity();
        myRelease.setName("Test Release");
        Medium myMedium = new MediumEntity();
        myMedium.setNumber(1);
        myRelease.getMediums().add(myMedium);
        Release r = Client.create(config).resource(HOSTURL+"/releases").type(MediaType.APPLICATION_JSON).post(Release.class,myRelease);

        assert r!=null;
        assert r.getMediums()!=null;
        assert r.getMediums().size()==1;
        Medium m = r.getMediums().iterator().next();
        assert m!=null;
        assert m.getNumber().equals(myMedium.getNumber());
        assert m.getId()!=null;

        r = Client.create(config).resource(HOSTURL+"/releases/"+r.getId()).accept(MediaType.APPLICATION_JSON).get(Release.class);

        assert r!=null;
        assert r.getMediums()!=null;
        assert r.getMediums().size()==1;
        m = r.getMediums().iterator().next();
        assert m!=null;
        assert m.getNumber().equals(myMedium.getNumber());
        assert m.getId()!=null;

        m.setNumber(4);
        r = Client.create(config).resource(HOSTURL+"/releases/"+r.getId()).type(MediaType.APPLICATION_JSON).put(Release.class, r);
        assert r!=null;
        assert r.getMediums()!=null;
        assert r.getMediums().size()==1;
        m = r.getMediums().iterator().next();
        assert m!=null;
        assert m.getNumber().equals(4);
        assert m.getId()!=null;

        List<MediumEntity> mediums = em.createQuery("from MediumEntity").getResultList();
        assert mediums !=null;
        boolean found = false;
        for (Medium medium : mediums) {
            if(medium.getId().equals(m.getId())) {
                found = true;
            }
        }
        assert found;

        Client.create(config).resource(HOSTURL+"/releases/"+r.getId()).accept(MediaType.APPLICATION_JSON).delete();

        mediums = (List<MediumEntity>) em.createQuery("from MediumEntity").getResultList();
        assert mediums !=null;
        found = false;
        for (Medium medium : mediums) {
            if(medium.getId().equals(m.getId())) {
                found = true;
            }
        }
        assert !found;
    }

    @Test
    public void testContributorRelease() throws Exception {
        Artist myArtist = new ArtistEntity();
        myArtist.setName("Anne-Sophie Mutter");
        Artist a = Client.create(config).resource(HOSTURL+"/artists").type(MediaType.APPLICATION_JSON).post(Artist.class,myArtist);
        assert a!=null;
        assert a.getName().equals(myArtist.getName());
        assert a.getId()!=null;

        Release myRelease = new ReleaseEntity();
        myRelease.setName("Test Release");
        Release r = Client.create(config).resource(HOSTURL+"/releases").type(MediaType.APPLICATION_JSON).post(Release.class,myRelease);

        assert r!=null;
        assert r.getName().equals(myRelease.getName());
        assert r.getId()!=null;
        assert r.getContributors()!=null;
        assert r.getContributors().size()==0;

        r = Client.create(config).resource(HOSTURL+"/releases/"+r.getId()).accept(MediaType.APPLICATION_JSON).get(Release.class);

        assert r!=null;
        assert r.getName().equals(myRelease.getName());
        assert r.getId()!=null;
        assert r.getContributors()!=null;
        assert r.getContributors().size()==0;

        ContributorEntity myContributor = new ContributorEntity(a,Contributor.PERFORMER);
        myContributor.setOwner(r);
        Contributor c = Client.create(config).resource(HOSTURL+"/contributors").type(MediaType.APPLICATION_JSON).post(Contributor.class,myContributor);
        assert c!=null;
        assert c.getId()!=null;
        assert c.getArtist()!=null;
        assert c.getArtist().equals(a);
        assert ((ContributorEntity)c).getOwner()!=null;
        assert ((ContributorEntity)c).getOwner().getId().equals(r.getId());

        r = Client.create(config).resource(HOSTURL+"/releases/"+r.getId()).accept(MediaType.APPLICATION_JSON).get(Release.class);

        assert r!=null;
        assert r.getName().equals(myRelease.getName());
        assert r.getId()!=null;
        assert r.getContributors()==null || r.getContributors().size() == 0;

        Collection<ContributorEntity> contributors = Client.create(config).resource(HOSTURL+"/contributors?release="+r.getId()).type(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ContributorEntity>>() {});
        assert contributors.size()==1;
        assert contributors.iterator().next().getOwner()!=null;
        assert contributors.iterator().next().getOwner().equals(r);
        assert contributors.iterator().next().getType().equals(Contributor.PERFORMER);

        contributors = Client.create(config).resource(HOSTURL+"/contributors?owner="+r.getId()).type(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ContributorEntity>>() {});
        assert contributors.size()==1;
        assert contributors.iterator().next().getOwner()!=null;
        assert contributors.iterator().next().getOwner().equals(r);
        assert contributors.iterator().next().getType().equals(Contributor.PERFORMER);

        c.setType(Contributor.CONDUCTOR);
        c = Client.create(config).resource(HOSTURL+"/contributors/"+c.getId()).type(MediaType.APPLICATION_JSON).put(Contributor.class,c);
        assert c!=null;
        assert c.getId()!=null;
        assert c.getArtist()!=null;
        assert c.getArtist().equals(a);
        assert ((ContributorEntity)c).getOwner()!=null;
        assert ((ContributorEntity)c).getOwner().equals(r);

        contributors = Client.create(config).resource(HOSTURL+"/contributors?release="+r.getId()).type(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ContributorEntity>>() {});
        assert contributors.size()==1;
        assert contributors.iterator().next().getOwner()!=null;
        assert contributors.iterator().next().getOwner().equals(r);
        assert contributors.iterator().next().getType().equals(Contributor.CONDUCTOR);

        ContributorEntity mySecondContributor = new ContributorEntity(a,Contributor.PERFORMER);
        mySecondContributor.setOwner(r);
        c = Client.create(config).resource(HOSTURL+"/contributors").type(MediaType.APPLICATION_JSON).post(Contributor.class,mySecondContributor);
        assert c!=null;
        assert c.getId()!=null;
        assert c.getArtist()!=null;
        assert c.getArtist().equals(a);
        assert ((ContributorEntity)c).getOwner()!=null;
        assert ((ContributorEntity)c).getOwner().equals(r);

        contributors = Client.create(config).resource(HOSTURL+"/contributors?release="+r.getId()).type(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ContributorEntity>>() {});
        assert contributors.size()==2;
        boolean firstFound = false;
        boolean secondFound = false;
        for (ContributorEntity contributor : contributors) {
            assert contributor.getOwner()!=null;
            assert contributor.getOwner().equals(r);
            if(contributor.getType().equals(Contributor.CONDUCTOR)) {
                firstFound = true;
            }else if(contributor.getType().equals(Contributor.PERFORMER)) {
                secondFound = true;
            }
        }
        assert firstFound;
        assert secondFound;

        Client.create(config).resource(HOSTURL+"/contributors/"+myContributor.getId()).type(MediaType.APPLICATION_JSON).delete();

        contributors = Client.create(config).resource(HOSTURL+"/contributors?release="+r.getId()).type(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ContributorEntity>>() {});
        assert contributors.size()==1;
        assert contributors.iterator().next().getOwner()!=null;
        assert contributors.iterator().next().getOwner().equals(r);
        assert contributors.iterator().next().getType().equals(Contributor.PERFORMER);

        Client.create(config).resource(HOSTURL+"/releases/"+r.getId()).accept(MediaType.APPLICATION_JSON).delete();
        Client.create(config).resource(HOSTURL+"/artists/"+a.getId()).accept(MediaType.APPLICATION_JSON).delete();

        contributors = Client.create(config).resource(HOSTURL+"/contributors?release="+r.getId()).type(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ContributorEntity>>() {});
        assert contributors.size()==0;

        Collection<Release> releases = Client.create(config).resource(HOSTURL+"/releases").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Release>>() {});
        assert releases !=null;
        boolean found = false;
        for (Release release : releases) {
            if(release.getId().equals(r.getId())) {
                found = true;
            }
        }
        assert !found;
    }

    @Test
    public void testContributorWork() throws Exception {
        Artist myArtist = new ArtistEntity();
        myArtist.setName("Beethoven");
        Artist a = Client.create(config).resource(HOSTURL+"/artists").type(MediaType.APPLICATION_JSON).post(Artist.class,myArtist);
        assert a!=null;
        assert a.getName().equals(myArtist.getName());
        assert a.getId()!=null;

        Artist mySecondArtist = new ArtistEntity();
        mySecondArtist.setName("Secretary of Beethoven");
        Artist a2 = Client.create(config).resource(HOSTURL+"/artists").type(MediaType.APPLICATION_JSON).post(Artist.class,mySecondArtist);
        assert a2!=null;
        assert a2.getName().equals(mySecondArtist.getName());
        assert a2.getId()!=null;

        Artist myThirdArtist = new ArtistEntity();
        myThirdArtist.setName("Assistant of Beethoven");
        Artist a3 = Client.create(config).resource(HOSTURL+"/artists").type(MediaType.APPLICATION_JSON).post(Artist.class,myThirdArtist);
        assert a3!=null;
        assert a3.getName().equals(myThirdArtist.getName());
        assert a3.getId()!=null;

        Work myWork = new WorkEntity();
        myWork.setName("Violin Concert");
        Work w = Client.create(config).resource(HOSTURL+"/works").type(MediaType.APPLICATION_JSON).post(Work.class,myWork);

        assert w!=null;
        assert w.getName().equals(myWork.getName());
        assert w.getId()!=null;
        assert w.getContributors()!=null;
        assert w.getContributors().size()==0;

        w = Client.create(config).resource(HOSTURL+"/works/"+w.getId()).accept(MediaType.APPLICATION_JSON).get(Work.class);

        assert w!=null;
        assert w.getName().equals(myWork.getName());
        assert w.getId()!=null;
        assert w.getContributors()!=null;
        assert w.getContributors().size()==0;

        ContributorEntity myContributor = new ContributorEntity(a,Contributor.COMPOSER);
        myContributor.setOwner(w);
        Contributor c = Client.create(config).resource(HOSTURL+"/contributors").type(MediaType.APPLICATION_JSON).post(Contributor.class,myContributor);
        assert c!=null;
        assert c.getId()!=null;
        assert c.getArtist()!=null;
        assert c.getArtist().equals(a);
        assert ((ContributorEntity)c).getOwner()!=null;
        assert ((ContributorEntity)c).getOwner().getId().equals(w.getId());

        w = Client.create(config).resource(HOSTURL+"/works/"+w.getId()).accept(MediaType.APPLICATION_JSON).get(Work.class);

        assert w!=null;
        assert w.getName().equals(myWork.getName());
        assert w.getId()!=null;
        assert w.getContributors()==null || w.getContributors().size()==0;

        Collection<ContributorEntity> contributors = Client.create(config).resource(HOSTURL+"/contributors?work="+w.getId()).type(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ContributorEntity>>() {});
        assert contributors.size()==1;
        assert contributors.iterator().next().getOwner()!=null;
        assert contributors.iterator().next().getOwner().equals(w);
        assert contributors.iterator().next().getType().equals(Contributor.COMPOSER);

        contributors = Client.create(config).resource(HOSTURL+"/contributors?owner="+w.getId()).type(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ContributorEntity>>() {});
        assert contributors.size()==1;
        assert contributors.iterator().next().getOwner()!=null;
        assert contributors.iterator().next().getOwner().equals(w);
        assert contributors.iterator().next().getType().equals(Contributor.COMPOSER);

        ContributorEntity mySecondContributor = new ContributorEntity(a2,Contributor.COMPOSER);
        mySecondContributor.setOwner(w);
        c = Client.create(config).resource(HOSTURL+"/contributors").type(MediaType.APPLICATION_JSON).post(Contributor.class,mySecondContributor);
        assert c!=null;
        assert c.getId()!=null;
        assert c.getArtist()!=null;
        assert c.getArtist().equals(a2);
        assert ((ContributorEntity)c).getOwner()!=null;
        assert ((ContributorEntity)c).getOwner().getId().equals(w.getId());

        contributors = Client.create(config).resource(HOSTURL+"/contributors?work="+w.getId()).type(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ContributorEntity>>() {});
        assert contributors.size()==2;
        boolean firstFound = false;
        boolean secondFound = false;
        for (ContributorEntity contributor : contributors) {
            assert contributor.getOwner()!=null;
            assert contributor.getOwner().equals(w);
            if(contributor.getArtist().equals(a)) {
                firstFound = true;
            }else if(contributor.getArtist().equals(a2)) {
                secondFound = true;
            }
        }
        assert firstFound;
        assert secondFound;

        c.setArtist(myThirdArtist);
        c = Client.create(config).resource(HOSTURL+"/contributors/"+c.getId()).type(MediaType.APPLICATION_JSON).put(Contributor.class, c);
        assert c!=null;
        assert c.getId()!=null;
        assert c.getArtist()!=null;
        assert c.getArtist().equals(a3);
        assert ((ContributorEntity)c).getOwner()!=null;
        assert ((ContributorEntity)c).getOwner().getId().equals(w.getId());

        contributors = Client.create(config).resource(HOSTURL+"/contributors?work="+w.getId()).type(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ContributorEntity>>() {});
        assert contributors.size()==2;
        firstFound = false;
        secondFound = false;
        for (ContributorEntity contributor : contributors) {
            assert contributor.getOwner()!=null;
            assert contributor.getOwner().equals(w);
            if(contributor.getArtist().equals(a)) {
                firstFound = true;
            }else if(contributor.getArtist().equals(a3)) {
                secondFound = true;
            }
        }
        assert firstFound;
        assert secondFound;

        Client.create(config).resource(HOSTURL+"/contributors/"+mySecondContributor.getId()).type(MediaType.APPLICATION_JSON).delete();

        contributors = Client.create(config).resource(HOSTURL+"/contributors?work="+w.getId()).type(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ContributorEntity>>() {});
        assert contributors.size()==1;
        assert contributors.iterator().next().getArtist().equals(a);

        Client.create(config).resource(HOSTURL+"/works/"+w.getId()).accept(MediaType.APPLICATION_JSON).delete();
        Client.create(config).resource(HOSTURL+"/artists/"+a.getId()).accept(MediaType.APPLICATION_JSON).delete();
        Client.create(config).resource(HOSTURL+"/artists/"+a2.getId()).accept(MediaType.APPLICATION_JSON).delete();
        Client.create(config).resource(HOSTURL+"/artists/"+a3.getId()).accept(MediaType.APPLICATION_JSON).delete();

        contributors = Client.create(config).resource(HOSTURL+"/contributors?work="+w.getId()).type(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ContributorEntity>>() {});
        assert contributors.size()==0;

        Collection<Work> works = Client.create(config).resource(HOSTURL+"/works").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Work>>() {});
        assert works !=null;
        boolean found = false;
        for (Work work : works) {
            if(work.getId().equals(w.getId())) {
                found = true;
            }
        }
        assert !found;
    }

    @Test
    public void testContributorRecording() throws Exception {
        Artist myArtist = new ArtistEntity();
        myArtist.setName("Some Artist");
        Artist a = Client.create(config).resource(HOSTURL+"/artists").type(MediaType.APPLICATION_JSON).post(Artist.class,myArtist);
        assert a!=null;
        assert a.getName().equals(myArtist.getName());
        assert a.getId()!=null;


        Work myWork = new WorkEntity();
        myWork.setName("Violin Concert");
        Work w = Client.create(config).resource(HOSTURL+"/works").type(MediaType.APPLICATION_JSON).post(Work.class,myWork);

        assert w!=null;
        assert w.getName().equals(myWork.getName());
        assert w.getId()!=null;

        Recording myRecording = new RecordingEntity();
        myRecording.getWorks().add(w);
        Recording r = Client.create(config).resource(HOSTURL+"/recordings").type(MediaType.APPLICATION_JSON).post(Recording.class,myRecording);

        assert r!=null;
        assert r.getId()!=null;
        assert r.getWorks().iterator().next().getName().equals(myWork.getName());
        assert r.getContributors()!=null;
        assert r.getContributors().size()==0;

        r = Client.create(config).resource(HOSTURL+"/recordings/"+r.getId()).accept(MediaType.APPLICATION_JSON).get(Recording.class);

        assert r!=null;
        assert r.getWorks().iterator().next().getName().equals(myWork.getName());
        assert r.getId()!=null;
        assert r.getContributors()!=null;
        assert r.getContributors().size()==0;

        ContributorEntity myContributor = new ContributorEntity(a,Contributor.PERFORMER);
        myContributor.setOwner(r);
        Contributor c = Client.create(config).resource(HOSTURL+"/contributors").type(MediaType.APPLICATION_JSON).post(Contributor.class,myContributor);
        assert c!=null;
        assert c.getId()!=null;
        assert c.getArtist()!=null;
        assert c.getArtist().equals(a);
        assert c.getType().equals(Contributor.PERFORMER);
        assert ((ContributorEntity)c).getOwner()!=null;
        assert ((ContributorEntity)c).getOwner().getId().equals(r.getId());

        c = Client.create(config).resource(HOSTURL+"/contributors/"+c.getId()).type(MediaType.APPLICATION_JSON).get(Contributor.class);
        assert c!=null;
        assert c.getId()!=null;
        assert c.getArtist()!=null;
        assert c.getArtist().equals(a);
        assert c.getType().equals(Contributor.PERFORMER);
        assert ((ContributorEntity)c).getOwner()!=null;
        assert ((ContributorEntity)c).getOwner().getId().equals(r.getId());

        r = Client.create(config).resource(HOSTURL+"/recordings/"+r.getId()).accept(MediaType.APPLICATION_JSON).get(Recording.class);

        assert r!=null;
        assert r.getWorks().iterator().next().getName().equals(myWork.getName());
        assert r.getId()!=null;
        assert r.getContributors()==null || r.getContributors().size()==0;

        Collection<ContributorEntity> contributors = Client.create(config).resource(HOSTURL+"/contributors?recording="+r.getId()).type(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ContributorEntity>>() {});
        assert contributors.size()==1;
        assert contributors.iterator().next().getOwner()!=null;
        assert contributors.iterator().next().getOwner().equals(r);
        assert contributors.iterator().next().getType().equals(Contributor.PERFORMER);

        contributors = Client.create(config).resource(HOSTURL+"/contributors?owner="+r.getId()).type(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ContributorEntity>>() {});
        assert contributors.size()==1;
        assert contributors.iterator().next().getOwner()!=null;
        assert contributors.iterator().next().getOwner().equals(r);
        assert contributors.iterator().next().getType().equals(Contributor.PERFORMER);

        c.setType(Contributor.CONDUCTOR);
        c = Client.create(config).resource(HOSTURL+"/contributors/"+c.getId()).type(MediaType.APPLICATION_JSON).put(Contributor.class, c);
        assert c!=null;
        assert c.getId()!=null;
        assert c.getArtist()!=null;
        assert c.getArtist().equals(a);
        assert c.getType().equals(Contributor.CONDUCTOR);
        assert ((ContributorEntity)c).getOwner()!=null;
        assert ((ContributorEntity)c).getOwner().getId().equals(r.getId());

        contributors = Client.create(config).resource(HOSTURL+"/contributors?recording="+r.getId()).type(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ContributorEntity>>() {});
        assert contributors.size()==1;
        assert contributors.iterator().next().getOwner()!=null;
        assert contributors.iterator().next().getOwner().equals(r);
        assert contributors.iterator().next().getType().equals(Contributor.CONDUCTOR);

        ContributorEntity mySecondContributor = new ContributorEntity(a, Contributor.PERFORMER);
        mySecondContributor.setOwner(r);
        c = Client.create(config).resource(HOSTURL+"/contributors").type(MediaType.APPLICATION_JSON).post(Contributor.class,mySecondContributor);
        assert c!=null;
        assert c.getId()!=null;
        assert c.getArtist()!=null;
        assert c.getArtist().equals(a);
        assert c.getType().equals(Contributor.PERFORMER);
        assert ((ContributorEntity)c).getOwner()!=null;
        assert ((ContributorEntity)c).getOwner().getId().equals(r.getId());

        c = Client.create(config).resource(HOSTURL+"/contributors/"+c.getId()).type(MediaType.APPLICATION_JSON).get(Contributor.class);
        assert c!=null;
        assert c.getId()!=null;
        assert c.getArtist()!=null;
        assert c.getArtist().equals(a);
        assert c.getType().equals(Contributor.PERFORMER);
        assert ((ContributorEntity)c).getOwner()!=null;
        assert ((ContributorEntity)c).getOwner().getId().equals(r.getId());

        contributors = Client.create(config).resource(HOSTURL+"/contributors?recording="+r.getId()).type(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ContributorEntity>>() {});
        assert contributors.size()==2;
        boolean firstFound = false;
        boolean secondFound = false;
        for (ContributorEntity contributor : contributors) {
            assert contributor.getOwner()!=null;
            assert contributor.getOwner().equals(r);
            assert contributor.getArtist().equals(a);
            if(contributor.getType().equals(Contributor.PERFORMER)) {
                firstFound = true;
            }else if(contributor.getType().equals(Contributor.CONDUCTOR)) {
                secondFound = true;
            }
        }
        assert firstFound;
        assert secondFound;

        Client.create(config).resource(HOSTURL+"/contributors/"+mySecondContributor.getId()).type(MediaType.APPLICATION_JSON).delete();

        contributors = Client.create(config).resource(HOSTURL+"/contributors?recording="+r.getId()).type(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ContributorEntity>>() {});
        assert contributors.size()==1;
        assert contributors.iterator().next().getOwner()!=null;
        assert contributors.iterator().next().getOwner().equals(r);
        assert contributors.iterator().next().getType().equals(Contributor.CONDUCTOR);

        Client.create(config).resource(HOSTURL+"/recordings/"+r.getId()).accept(MediaType.APPLICATION_JSON).delete();
        Client.create(config).resource(HOSTURL+"/works/"+w.getId()).accept(MediaType.APPLICATION_JSON).delete();
        Client.create(config).resource(HOSTURL+"/artists/"+a.getId()).accept(MediaType.APPLICATION_JSON).delete();

        contributors = Client.create(config).resource(HOSTURL+"/contributors?recording="+r.getId()).type(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ContributorEntity>>() {});
        assert contributors.size()==0;

        Collection<Recording> recordings = Client.create(config).resource(HOSTURL+"/recordings").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Recording>>() {});
        assert recordings !=null;
        boolean found = false;
        for (Recording recording : recordings) {
            if(recording.getId().equals(r.getId())) {
                found = true;
            }
        }
        assert !found;
    }

    @Test
    public void testContributorRecordingSession() throws Exception {
        Artist myArtist = new ArtistEntity();
        myArtist.setName("Some Artist");
        Artist a = Client.create(config).resource(HOSTURL+"/artists").type(MediaType.APPLICATION_JSON).post(Artist.class,myArtist);
        assert a!=null;
        assert a.getName().equals(myArtist.getName());
        assert a.getId()!=null;


        Work myWork = new WorkEntity();
        myWork.setName("Violin Concert");
        Work w = Client.create(config).resource(HOSTURL+"/works").type(MediaType.APPLICATION_JSON).post(Work.class,myWork);

        assert w!=null;
        assert w.getName().equals(myWork.getName());
        assert w.getId()!=null;

        Recording myRecording = new RecordingEntity();
        myRecording.getWorks().add(w);
        Recording r = Client.create(config).resource(HOSTURL+"/recordings").type(MediaType.APPLICATION_JSON).post(Recording.class,myRecording);
        assert r!=null;
        assert r.getWorks().iterator().next().getName().equals(myWork.getName());
        assert r.getId()!=null;

        RecordingSession myRecordingSession = new RecordingSessionEntity();
        myRecordingSession.getRecordings().add(r);
        RecordingSession rs = Client.create(config).resource(HOSTURL+"/recordingsessions").type(MediaType.APPLICATION_JSON).post(RecordingSession.class,myRecordingSession);

        assert rs!=null;
        assert rs.getId()!=null;
        assert rs.getRecordings().size()==1;
        assert rs.getRecordings().iterator().next().getWorks().iterator().next().getName().equals(myWork.getName());
        assert rs.getContributors()!=null;
        assert rs.getContributors().size()==0;

        rs = Client.create(config).resource(HOSTURL+"/recordingsessions/"+rs.getId()).accept(MediaType.APPLICATION_JSON).get(RecordingSession.class);

        assert rs!=null;
        assert rs.getRecordings().iterator().next().getWorks().iterator().next().getName().equals(myWork.getName());
        assert rs.getId()!=null;
        assert rs.getContributors()!=null;
        assert rs.getContributors().size()==0;

        ContributorEntity myContributor = new ContributorEntity(a,Contributor.PERFORMER);
        myContributor.setOwner(rs);
        Contributor c = Client.create(config).resource(HOSTURL+"/contributors").type(MediaType.APPLICATION_JSON).post(Contributor.class,myContributor);
        assert c!=null;
        assert c.getId()!=null;
        assert c.getArtist()!=null;
        assert c.getArtist().equals(a);
        assert c.getType().equals(Contributor.PERFORMER);
        assert ((ContributorEntity)c).getOwner()!=null;
        assert ((ContributorEntity)c).getOwner().getId().equals(rs.getId());

        c = Client.create(config).resource(HOSTURL+"/contributors/"+c.getId()).type(MediaType.APPLICATION_JSON).get(Contributor.class);
        assert c!=null;
        assert c.getId()!=null;
        assert c.getArtist()!=null;
        assert c.getArtist().equals(a);
        assert c.getType().equals(Contributor.PERFORMER);
        assert ((ContributorEntity)c).getOwner()!=null;
        assert ((ContributorEntity)c).getOwner().getId().equals(rs.getId());

        rs = Client.create(config).resource(HOSTURL+"/recordingsessions/"+rs.getId()).accept(MediaType.APPLICATION_JSON).get(RecordingSession.class);

        assert rs!=null;
        assert rs.getRecordings().iterator().next().getWorks().iterator().next().getName().equals(myWork.getName());
        assert rs.getId()!=null;
        assert rs.getContributors()==null || rs.getContributors().size()==0;

        Collection<ContributorEntity> contributors = Client.create(config).resource(HOSTURL+"/contributors?recordingSession="+rs.getId()).type(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ContributorEntity>>() {});
        assert contributors.size()==1;
        assert contributors.iterator().next().getOwner()!=null;
        assert contributors.iterator().next().getOwner().equals(rs);
        assert contributors.iterator().next().getType().equals(Contributor.PERFORMER);

        contributors = Client.create(config).resource(HOSTURL+"/contributors?owner="+rs.getId()).type(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ContributorEntity>>() {});
        assert contributors.size()==1;
        assert contributors.iterator().next().getOwner()!=null;
        assert contributors.iterator().next().getOwner().equals(rs);
        assert contributors.iterator().next().getType().equals(Contributor.PERFORMER);

        ContributorEntity mySecondContributor = new ContributorEntity(a, Contributor.COMPOSER);
        mySecondContributor.setOwner(rs);
        c = Client.create(config).resource(HOSTURL+"/contributors").type(MediaType.APPLICATION_JSON).post(Contributor.class,mySecondContributor);
        assert c!=null;
        assert c.getId()!=null;
        assert c.getArtist()!=null;
        assert c.getArtist().equals(a);
        assert c.getType().equals(Contributor.COMPOSER);
        assert ((ContributorEntity)c).getOwner()!=null;
        assert ((ContributorEntity)c).getOwner().getId().equals(rs.getId());

        c = Client.create(config).resource(HOSTURL+"/contributors/"+c.getId()).type(MediaType.APPLICATION_JSON).get(Contributor.class);
        assert c!=null;
        assert c.getId()!=null;
        assert c.getArtist()!=null;
        assert c.getArtist().equals(a);
        assert c.getType().equals(Contributor.COMPOSER);
        assert ((ContributorEntity)c).getOwner()!=null;
        assert ((ContributorEntity)c).getOwner().getId().equals(rs.getId());

        contributors = Client.create(config).resource(HOSTURL+"/contributors?recordingSession="+rs.getId()).type(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ContributorEntity>>() {});
        assert contributors.size()==2;
        boolean firstFound = false;
        boolean secondFound = false;
        for (ContributorEntity contributor : contributors) {
            assert contributor.getOwner()!=null;
            assert contributor.getOwner().equals(rs);
            assert contributor.getArtist().equals(a);
            if(contributor.getType().equals(Contributor.PERFORMER)) {
                firstFound = true;
            }else if(contributor.getType().equals(Contributor.COMPOSER)) {
                secondFound = true;
            }
        }
        assert firstFound;
        assert secondFound;

        mySecondContributor.setType(Contributor.CONDUCTOR);
        c = Client.create(config).resource(HOSTURL+"/contributors/"+mySecondContributor.getId()).type(MediaType.APPLICATION_JSON).put(Contributor.class,mySecondContributor);
        assert c!=null;
        assert c.getId()!=null;
        assert c.getArtist()!=null;
        assert c.getArtist().equals(a);
        assert c.getType().equals(Contributor.CONDUCTOR);
        assert ((ContributorEntity)c).getOwner()!=null;
        assert ((ContributorEntity)c).getOwner().getId().equals(rs.getId());

        c = Client.create(config).resource(HOSTURL+"/contributors/"+c.getId()).type(MediaType.APPLICATION_JSON).get(Contributor.class);
        assert c!=null;
        assert c.getId()!=null;
        assert c.getArtist()!=null;
        assert c.getArtist().equals(a);
        assert c.getType().equals(Contributor.CONDUCTOR);
        assert ((ContributorEntity)c).getOwner()!=null;
        assert ((ContributorEntity)c).getOwner().getId().equals(rs.getId());

        contributors = Client.create(config).resource(HOSTURL+"/contributors?recordingSession="+rs.getId()).type(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ContributorEntity>>() {});
        assert contributors.size()==2;
        firstFound = false;
        secondFound = false;
        for (ContributorEntity contributor : contributors) {
            assert contributor.getOwner()!=null;
            assert contributor.getOwner().equals(rs);
            assert contributor.getArtist().equals(a);
            if(contributor.getType().equals(Contributor.PERFORMER)) {
                firstFound = true;
            }else if(contributor.getType().equals(Contributor.CONDUCTOR)) {
                secondFound = true;
            }
        }
        assert firstFound;
        assert secondFound;

        Client.create(config).resource(HOSTURL+"/contributors/"+mySecondContributor.getId()).type(MediaType.APPLICATION_JSON).delete();

        contributors = Client.create(config).resource(HOSTURL+"/contributors?recordingSession="+rs.getId()).type(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ContributorEntity>>() {});
        assert contributors.size()==1;
        assert contributors.iterator().next().getOwner()!=null;
        assert contributors.iterator().next().getOwner().equals(rs);
        assert contributors.iterator().next().getType().equals(Contributor.PERFORMER);

        Client.create(config).resource(HOSTURL+"/recordingsessions/"+rs.getId()).accept(MediaType.APPLICATION_JSON).delete();
        Client.create(config).resource(HOSTURL+"/recordings/"+r.getId()).accept(MediaType.APPLICATION_JSON).delete();
        Client.create(config).resource(HOSTURL+"/works/"+w.getId()).accept(MediaType.APPLICATION_JSON).delete();
        Client.create(config).resource(HOSTURL+"/artists/"+a.getId()).accept(MediaType.APPLICATION_JSON).delete();

        contributors = Client.create(config).resource(HOSTURL+"/contributors?recordingSession="+rs.getId()).type(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ContributorEntity>>() {});
        assert contributors.size()==0;

        Collection<RecordingSession> recordingsessions = Client.create(config).resource(HOSTURL+"/recordingsessions").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<RecordingSession>>() {});
        assert recordingsessions !=null;
        boolean found = false;
        for (RecordingSession recordingSession : recordingsessions) {
            if(recordingSession.getId().equals(rs.getId())) {
                found = true;
            }
        }
        assert !found;
    }

    @Test
    public void testCRUDConfig() throws Exception {
        ConfigurationManager defaultValueConfigurationManager = InjectHelper.instanceWithName(ConfigurationManager.class, "default-value");
        defaultValueConfigurationManager.setParametersForPath("",new ArrayList<ConfigurationParameter>());

        ConfigurationParameter myBooleanConfig = new ConfigurationParameterEntity();
        myBooleanConfig.setId("somebooleanconfig");
        myBooleanConfig.setType(ConfigurationParameter.Type.BOOLEAN);
        myBooleanConfig.setValue("true");
        ConfigurationParameter  p = Client.create(config).resource(HOSTURL+"/configurations").type(MediaType.APPLICATION_JSON).post(ConfigurationParameter.class,myBooleanConfig);
        assert p!=null;
        assert p.getType().equals(ConfigurationParameter.Type.BOOLEAN);
        assert p.getValue().equals("true");
        p = Client.create(config).resource(HOSTURL+"/configurations/"+p.getId()).accept(MediaType.APPLICATION_JSON).get(ConfigurationParameter.class);
        assert p!=null;
        assert p.getType().equals(ConfigurationParameter.Type.BOOLEAN);
        assert p.getValue().equals("true");

        ConfigurationParameter myNumberConfig = new ConfigurationParameterEntity();
        myNumberConfig.setId("somenumberconfig");
        myNumberConfig.setType(ConfigurationParameter.Type.INTEGER);
        myNumberConfig.setValue("42");
        p = Client.create(config).resource(HOSTURL+"/configurations").type(MediaType.APPLICATION_JSON).post(ConfigurationParameter.class,myNumberConfig);
        assert p!=null;
        assert p.getType().equals(ConfigurationParameter.Type.INTEGER);
        assert p.getValue().equals("42");
        p = Client.create(config).resource(HOSTURL+"/configurations/"+p.getId()).accept(MediaType.APPLICATION_JSON).get(ConfigurationParameter.class);
        assert p!=null;
        assert p.getType().equals(ConfigurationParameter.Type.INTEGER);
        assert p.getValue().equals("42");

        ConfigurationParameter myStringConfig = new ConfigurationParameterEntity();
        myStringConfig.setId("somestringconfig");
        myStringConfig.setType(ConfigurationParameter.Type.STRING);
        myStringConfig.setValue("hello");
        p = Client.create(config).resource(HOSTURL+"/configurations").type(MediaType.APPLICATION_JSON).post(ConfigurationParameter.class,myStringConfig);
        assert p!=null;
        assert p.getType().equals(ConfigurationParameter.Type.STRING);
        assert p.getValue().equals("hello");
        p = Client.create(config).resource(HOSTURL+"/configurations/"+p.getId()).accept(MediaType.APPLICATION_JSON).get(ConfigurationParameter.class);
        assert p!=null;
        assert p.getType().equals(ConfigurationParameter.Type.STRING);
        assert p.getValue().equals("hello");

        p.setValue("good bye");
        p = Client.create(config).resource(HOSTURL+"/configurations/"+p.getId()).type(MediaType.APPLICATION_JSON).put(ConfigurationParameter.class, p);
        assert p!=null;
        assert p.getValue().equals("good bye");
        p = Client.create(config).resource(HOSTURL+"/configurations/"+p.getId()).accept(MediaType.APPLICATION_JSON).get(ConfigurationParameter.class);
        assert p!=null;
        assert p.getType().equals(ConfigurationParameter.Type.STRING);
        assert p.getValue().equals("good bye");

        Collection<ConfigurationParameter> configurations = Client.create(config).resource(HOSTURL+"/configurations").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ConfigurationParameter>>() {});
        assert configurations.contains(myBooleanConfig);
        assert configurations.contains(myStringConfig);
        assert configurations.contains(myNumberConfig);

        configurations = Client.create(config).resource(HOSTURL+"/configurations?path=someboolean").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ConfigurationParameter>>() {});
        assert configurations.contains(myBooleanConfig);
        assert !configurations.contains(myStringConfig);
        assert !configurations.contains(myNumberConfig);

        Client.create(config).resource(HOSTURL+"/configurations/"+myBooleanConfig.getId()).type(MediaType.APPLICATION_JSON).delete();
        configurations = Client.create(config).resource(HOSTURL+"/configurations").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ConfigurationParameter>>() {});
        assert !configurations.contains(myBooleanConfig);
        assert configurations.contains(myStringConfig);
        assert configurations.contains(myNumberConfig);

        Client.create(config).resource(HOSTURL+"/configurations").type(MediaType.APPLICATION_JSON).delete();
        configurations = Client.create(config).resource(HOSTURL+"/configurations").accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<ConfigurationParameter>>() {});
        assert !configurations.contains(myBooleanConfig);
        assert !configurations.contains(myStringConfig);
        assert !configurations.contains(myNumberConfig);

        assert configurations.size()==0;
    }
}
