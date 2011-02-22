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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Track t = Client.create(config).resource(HOSTURL+"/tracks").type(MediaType.APPLICATION_JSON).post(Track.class,myTrack);
        assert t!=null;
        assert t.getNumber().equals(myTrack.getNumber());
        assert t.getId()!=null;
        assert t.getRecording()!=null;
        assert t.getRecording().getId().equals(recording.getId());
        assert t.getRecording().getWorks()!=null;
        assert t.getRecording().getWorks().size()==1;
        assert t.getRecording().getWorks().iterator().next().getName().equals(myWork.getName());

        t = Client.create(config).resource(HOSTURL+"/tracks/"+t.getId()).accept(MediaType.APPLICATION_JSON).get(Track.class);

        assert t!=null;
        assert t.getNumber().equals(myTrack.getNumber());
        assert t.getId()!=null;

        t.setNumber(4);
        t = Client.create(config).resource(HOSTURL+"/tracks/"+t.getId()).type(MediaType.APPLICATION_JSON).put(Track.class, t);
        assert t!=null;
        assert t.getNumber().equals(4);
        assert t.getId()!=null;

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
    public void testReleaseContributors() throws Exception {
        Artist myArtist = new ArtistEntity();
        myArtist.setName("Anne-Sophie Mutter");
        Artist a = Client.create(config).resource(HOSTURL+"/artists").type(MediaType.APPLICATION_JSON).post(Artist.class,myArtist);
        assert a!=null;
        assert a.getName().equals(myArtist.getName());
        assert a.getId()!=null;

        Release myRelease = new ReleaseEntity();
        myRelease.setName("Test Release");
        myRelease.getContributors().add(new ContributorEntity(a,Contributor.PERFORMER));
        Release r = Client.create(config).resource(HOSTURL+"/releases").type(MediaType.APPLICATION_JSON).post(Release.class,myRelease);

        assert r!=null;
        assert r.getName().equals(myRelease.getName());
        assert r.getId()!=null;
        assert r.getContributors()!=null;
        assert r.getContributors().size()==1;
        assert r.getContributors().iterator().next().getType().equals(Contributor.PERFORMER);
        assert r.getContributors().iterator().next().getArtist().getName().equals(myArtist.getName());

        r = Client.create(config).resource(HOSTURL+"/releases/"+r.getId()).accept(MediaType.APPLICATION_JSON).get(Release.class);

        assert r!=null;
        assert r.getName().equals(myRelease.getName());
        assert r.getId()!=null;
        assert r.getContributors()!=null;
        assert r.getContributors().size()==1;
        assert r.getContributors().iterator().next().getType().equals(Contributor.PERFORMER);
        assert r.getContributors().iterator().next().getArtist().getName().equals(myArtist.getName());

        r.getContributors().clear();
        r.getContributors().add(new ContributorEntity(myArtist,Contributor.COMPOSER));
        r = Client.create(config).resource(HOSTURL+"/releases/"+r.getId()).type(MediaType.APPLICATION_JSON).put(Release.class, r);
        assert r!=null;
        assert r.getId()!=null;
        assert r.getContributors()!=null;
        assert r.getContributors().size()==1;
        assert r.getContributors().iterator().next().getType().equals(Contributor.COMPOSER);
        assert r.getContributors().iterator().next().getArtist().getName().equals(myArtist.getName());

        Client.create(config).resource(HOSTURL+"/releases/"+r.getId()).accept(MediaType.APPLICATION_JSON).delete();
        Client.create(config).resource(HOSTURL+"/artists/"+a.getId()).accept(MediaType.APPLICATION_JSON).delete();

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
    public void testWorkContributors() throws Exception {
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

        Work myWork = new WorkEntity();
        myWork.setName("Violin Concert");
        myWork.getContributors().add(new ContributorEntity(a,Contributor.COMPOSER));
        Work w = Client.create(config).resource(HOSTURL+"/works").type(MediaType.APPLICATION_JSON).post(Work.class,myWork);

        assert w!=null;
        assert w.getName().equals(myWork.getName());
        assert w.getId()!=null;
        assert w.getContributors()!=null;
        assert w.getContributors().size()==1;
        assert w.getContributors().iterator().next().getType().equals(Contributor.COMPOSER);
        assert w.getContributors().iterator().next().getArtist().getName().equals(myArtist.getName());

        w = Client.create(config).resource(HOSTURL+"/works/"+w.getId()).accept(MediaType.APPLICATION_JSON).get(Work.class);

        assert w!=null;
        assert w.getName().equals(myWork.getName());
        assert w.getId()!=null;
        assert w.getContributors()!=null;
        assert w.getContributors().size()==1;
        assert w.getContributors().iterator().next().getType().equals(Contributor.COMPOSER);
        assert w.getContributors().iterator().next().getArtist().getName().equals(myArtist.getName());

        w.getContributors().add(new ContributorEntity(a2,Contributor.COMPOSER));
        w = Client.create(config).resource(HOSTURL+"/works/"+w.getId()).type(MediaType.APPLICATION_JSON).put(Work.class, w);
        assert w!=null;
        assert w.getId()!=null;
        assert w.getContributors()!=null;
        assert w.getContributors().size()==2;
        boolean foundFirstArtist = false;
        boolean foundSecondArtist = false;
        for (Contributor contributor : w.getContributors()) {
             assert contributor.getType().equals(Contributor.COMPOSER);
            if(contributor.getArtist().getName().equals(myArtist.getName())) {
                foundFirstArtist = true;
            }else if(contributor.getArtist().getName().equals(mySecondArtist.getName())) {
                foundSecondArtist = true;
            }
        }
        assert foundFirstArtist;
        assert foundSecondArtist;

        Client.create(config).resource(HOSTURL+"/works/"+w.getId()).accept(MediaType.APPLICATION_JSON).delete();
        Client.create(config).resource(HOSTURL+"/artists/"+a.getId()).accept(MediaType.APPLICATION_JSON).delete();
        Client.create(config).resource(HOSTURL+"/artists/"+a2.getId()).accept(MediaType.APPLICATION_JSON).delete();

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
    public void testRecordingContributors() throws Exception {
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
        myRecording.getContributors().add(new ContributorEntity(myArtist,Contributor.PERFORMER));
        Recording r = Client.create(config).resource(HOSTURL+"/recordings").type(MediaType.APPLICATION_JSON).post(Recording.class,myRecording);

        assert r!=null;
        assert r.getId()!=null;
        assert r.getWorks().iterator().next().getName().equals(myWork.getName());
        assert r.getContributors()!=null;
        assert r.getContributors().size()==1;
        assert r.getContributors().iterator().next().getType().equals(Contributor.PERFORMER);
        assert r.getContributors().iterator().next().getArtist().getName().equals(myArtist.getName());

        r = Client.create(config).resource(HOSTURL+"/recordings/"+r.getId()).accept(MediaType.APPLICATION_JSON).get(Recording.class);

        assert r!=null;
        assert r.getWorks().iterator().next().getName().equals(myWork.getName());
        assert r.getId()!=null;
        assert r.getContributors()!=null;
        assert r.getContributors().size()==1;
        assert r.getContributors().iterator().next().getType().equals(Contributor.PERFORMER);
        assert r.getContributors().iterator().next().getArtist().getName().equals(myArtist.getName());

        r.getContributors().add(new ContributorEntity(a,Contributor.CONDUCTOR));
        r = Client.create(config).resource(HOSTURL+"/recordings/"+r.getId()).type(MediaType.APPLICATION_JSON).put(Recording.class, r);
        assert r!=null;
        assert r.getId()!=null;
        assert r.getWorks().iterator().next().getName().equals(myWork.getName());
        assert r.getContributors()!=null;
        assert r.getContributors().size()==2;
        boolean coundPerformer = false;
        boolean foundConductor = false;
        for (Contributor contributor : r.getContributors()) {
            assert contributor.getArtist().getName().equals(myArtist.getName());
            if(contributor.getType().equals(Contributor.PERFORMER)) {
                coundPerformer = true;
            }else if(contributor.getType().equals(Contributor.CONDUCTOR)) {
                foundConductor = true;
            }
        }
        assert coundPerformer;
        assert foundConductor;

        Client.create(config).resource(HOSTURL+"/recordings/"+r.getId()).accept(MediaType.APPLICATION_JSON).delete();
        Client.create(config).resource(HOSTURL+"/works/"+w.getId()).accept(MediaType.APPLICATION_JSON).delete();
        Client.create(config).resource(HOSTURL+"/artists/"+a.getId()).accept(MediaType.APPLICATION_JSON).delete();

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
    public void testRecordingSessionContributors() throws Exception {
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
        myRecordingSession.getContributors().add(new ContributorEntity(myArtist,Contributor.PERFORMER));
        RecordingSession rs = Client.create(config).resource(HOSTURL+"/recordingsessions").type(MediaType.APPLICATION_JSON).post(RecordingSession.class,myRecordingSession);

        assert rs!=null;
        assert rs.getId()!=null;
        assert rs.getRecordings().size()==1;
        assert rs.getRecordings().iterator().next().getWorks().iterator().next().getName().equals(myWork.getName());
        assert rs.getContributors()!=null;
        assert rs.getContributors().size()==1;
        assert rs.getContributors().iterator().next().getType().equals(Contributor.PERFORMER);
        assert rs.getContributors().iterator().next().getArtist().getName().equals(myArtist.getName());

        rs = Client.create(config).resource(HOSTURL+"/recordingsessions/"+rs.getId()).accept(MediaType.APPLICATION_JSON).get(RecordingSession.class);

        assert rs!=null;
        assert rs.getRecordings().iterator().next().getWorks().iterator().next().getName().equals(myWork.getName());
        assert rs.getId()!=null;
        assert rs.getContributors()!=null;
        assert rs.getContributors().size()==1;
        assert rs.getContributors().iterator().next().getType().equals(Contributor.PERFORMER);
        assert rs.getContributors().iterator().next().getArtist().getName().equals(myArtist.getName());

        rs.getContributors().iterator().next().setType(Contributor.CONDUCTOR);
        rs = Client.create(config).resource(HOSTURL+"/recordingsessions/"+rs.getId()).type(MediaType.APPLICATION_JSON).put(RecordingSession.class, rs);
        assert rs!=null;
        assert rs.getId()!=null;
        assert rs.getRecordings().iterator().next().getWorks().iterator().next().getName().equals(myWork.getName());
        assert rs.getContributors()!=null;
        assert rs.getContributors().size()==1;
        assert rs.getContributors().iterator().next().getType().equals(Contributor.CONDUCTOR);
        assert rs.getContributors().iterator().next().getArtist().getName().equals(myArtist.getName());

        Client.create(config).resource(HOSTURL+"/recordingsessions/"+rs.getId()).accept(MediaType.APPLICATION_JSON).delete();
        Client.create(config).resource(HOSTURL+"/recordings/"+r.getId()).accept(MediaType.APPLICATION_JSON).delete();
        Client.create(config).resource(HOSTURL+"/works/"+w.getId()).accept(MediaType.APPLICATION_JSON).delete();
        Client.create(config).resource(HOSTURL+"/artists/"+a.getId()).accept(MediaType.APPLICATION_JSON).delete();

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
}
