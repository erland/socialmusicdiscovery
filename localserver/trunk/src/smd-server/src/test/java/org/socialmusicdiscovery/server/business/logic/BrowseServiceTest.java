package org.socialmusicdiscovery.server.business.logic;

import org.socialmusicdiscovery.server.api.mediaimport.ProcessingStatusCallback;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;
import org.socialmusicdiscovery.server.business.model.classification.Classification;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationEntity;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.service.browse.*;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class BrowseServiceTest extends BaseTestCase {
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
        System.out.println("Executing "+getClass().getSimpleName()+"."+m.getName()+"...");
        em.clear();
    }

    @BeforeClass
    public void setUpClass() {
        try {
            loadTestData("org.socialmusicdiscovery.server.business.model","Arista RCA Releases.xml");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        new SearchRelationPostProcessor().execute(new ProcessingStatusCallback() {
            public void progress(String module, String currentDescription, Long currentNo, Long totalNo) {}
            public void failed(String module, String error) {}
            public void finished(String module) {}
            public void aborted(String module) {}
        });
    }

    @AfterMethod
    public void tearDownMethod(Method m) {
        if(em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

    @Test
    public void testBrowseTracks() throws Exception {
        TrackBrowseService browseService = new TrackBrowseService();
        Collection<ResultItem<Track>> result = browseService.findChildren(new ArrayList<String>(),new ArrayList<String>(),null,null);
        assert result.size()==79;
        boolean foundMedium = false;
        boolean foundWithoutMedium = false;
        for (ResultItem<Track> trackResultItem : result) {
            if(trackResultItem.getItem().getMedium()!=null) {
                foundMedium=true;
            }else {
                foundWithoutMedium=true;
            }
            boolean foundArtist = false;
            boolean foundRelease = false;
            boolean foundWork = false;
            for (Map.Entry child : trackResultItem.getChildItems().entrySet()) {
                if(((String)child.getKey()).startsWith(SMDIdentityReferenceEntity.typeForClass(ArtistEntity.class))) {
                    foundArtist=true;
                }else if(child.getKey().equals(SMDIdentityReferenceEntity.typeForClass(ReleaseEntity.class))) {
                    foundRelease=true;
                }else if(child.getKey().equals(SMDIdentityReferenceEntity.typeForClass(WorkEntity.class))) {
                    foundWork=true;
                }else if(((String) child.getKey()).startsWith(SMDIdentityReferenceEntity.typeForClass(ClassificationEntity.class))) {
                    // Not mandatory
                }else {
                    assert false;
                }
            }
            assert foundArtist;
            assert foundWork;
            if(trackResultItem.getItem().getRecording().getWork().getName().equals("Axel F")) {
                assert !foundRelease;
            }else {
                assert foundRelease;
            }
        }
        assert foundMedium;
        assert foundWithoutMedium;

        result = browseService.findChildren(new ArrayList<String>(),new ArrayList<String>(),0,10);
        assert result.size()==10;
        Track track = (Track)result.toArray(new ResultItem[result.size()])[result.size()-1].getItem();
        result = browseService.findChildren(new ArrayList<String>(),new ArrayList<String>(),9,10);
        assert result.size()==10;
        assert track.getId().equals(result.iterator().next().getItem().getId());

        result = browseService.findChildren(Arrays.asList(
                "231424b6-b3a9-45b8-bce2-77e694e67319", // Whitney Houston
                "d7465c1b-2115-42cf-b96c-141a3ef93a47", // Dolly Parton
                "bf88a696-0cc5-4cfa-b690-85ddb25835cc", // Arista
                "d972b0fa-42f5-45f9-ba56-2cede7666446" // The Bodyguard
        ),new ArrayList<String>(),null,null);
        assert result.size()==1;
        assert result.iterator().next().getItem().getRecording().getWork().getName().equals("I Will Always Love You");
        for (ResultItem<Track> releaseResultItem : result) {
            boolean foundWork=false;
            boolean foundOneWork=false;
            boolean foundRelease=false;
            for (Map.Entry child : releaseResultItem.getChildItems().entrySet()) {
                if(child.getKey().equals(SMDIdentityReferenceEntity.typeForClass(ReleaseEntity.class))) {
                    foundRelease=true;
                }else if(child.getKey().equals(SMDIdentityReferenceEntity.typeForClass(WorkEntity.class))) {
                    foundWork = true;
                    if(child.getValue().equals(1L)) {
                        foundOneWork=true;
                    }
                }
            }
            assert foundWork;
            assert foundOneWork;
            assert !foundRelease;
        }

        result = browseService.findChildren(Arrays.asList(
                SMDIdentityReferenceEntity.typeForClass(ArtistEntity.class)+".composer:231424b6-b3a9-45b8-bce2-77e694e67319", // Whitney Houston
                SMDIdentityReferenceEntity.typeForClass(ArtistEntity.class)+".performer:231424b6-b3a9-45b8-bce2-77e694e67319", // Whitney Houston
                SMDIdentityReferenceEntity.typeForClass(ReleaseEntity.class)+":d972b0fa-42f5-45f9-ba56-2cede7666446", // The Bodyguard
                SMDIdentityReferenceEntity.typeForClass(LabelEntity.class)+":bf88a696-0cc5-4cfa-b690-85ddb25835cc" // Arista
        ),new ArrayList<String>(),null,null);
        assert result.size()==1;
        assert result.iterator().next().getItem().getRecording().getWork().getName().equals("Queen Of The Night");

        for (ResultItem<Track> releaseResultItem : result) {
            boolean foundWork=false;
            boolean foundOneWork=false;
            boolean foundRelease=false;
            for (Map.Entry child : releaseResultItem.getChildItems().entrySet()) {
                if(child.getKey().equals(SMDIdentityReferenceEntity.typeForClass(ReleaseEntity.class))) {
                    foundRelease=true;
                }else if(child.getKey().equals(SMDIdentityReferenceEntity.typeForClass(WorkEntity.class))) {
                    foundWork = true;
                    if(child.getValue().equals(1L)) {
                        foundOneWork=true;
                    }
                }
            }
            assert foundWork;
            assert foundOneWork;
            assert !foundRelease;
        }
    }

    @Test
    public void testBrowseArtists() throws Exception {
        ArtistBrowseService browseService = new ArtistBrowseService();
        Collection<ResultItem<Artist>> result = browseService.findChildren(new ArrayList<String>(),new ArrayList<String>(),null,null);
        assert result.size()==50;
        for (ResultItem<Artist> artistResultItem : result) {
            boolean foundClassification = false;
            boolean foundRelease = false;
            boolean foundWork = false;
            boolean foundTrack = false;
            for (Map.Entry child : artistResultItem.getChildItems().entrySet()) {
                if(((String)child.getKey()).startsWith(SMDIdentityReferenceEntity.typeForClass(ClassificationEntity.class))) {
                    foundClassification=true;
                }else if(((String) child.getKey()).startsWith(SMDIdentityReferenceEntity.typeForClass(ReleaseEntity.class))) {
                    foundRelease=true;
                }else if(((String) child.getKey()).startsWith(SMDIdentityReferenceEntity.typeForClass(WorkEntity.class))) {
                    foundWork=true;
                }else if(((String) child.getKey()).startsWith(SMDIdentityReferenceEntity.typeForClass(TrackEntity.class))) {
                    foundTrack=true;
                }else if(((String) child.getKey()).startsWith(SMDIdentityReferenceEntity.typeForClass(ArtistEntity.class))) {
                    // Not mandatory
                }else {
                    assert false;
                }
            }
            assert foundRelease;
            assert foundClassification;
            assert foundWork;
            assert foundTrack;
        }

        result = browseService.findChildren(new ArrayList<String>(),new ArrayList<String>(),0,10);
        assert result.size()==10;
    }

    @Test
    public void testBrowseReleases() throws Exception {
        ReleaseBrowseService browseService = new ReleaseBrowseService();
        Collection<ResultItem<Release>> result = browseService.findChildren(new ArrayList<String>(),new ArrayList<String>(),null,null);
        assert result.size()==5;
        for (ResultItem<Release> releaseResultItem : result) {
            boolean foundClassification = false;
            boolean foundArtist = false;
            boolean foundWork = false;
            boolean foundTrack = false;
            for (Map.Entry child : releaseResultItem.getChildItems().entrySet()) {
                if(((String)child.getKey()).startsWith(SMDIdentityReferenceEntity.typeForClass(ClassificationEntity.class))) {
                    foundClassification=true;
                }else if(((String) child.getKey()).startsWith(SMDIdentityReferenceEntity.typeForClass(ArtistEntity.class))) {
                    foundArtist=true;
                }else if(child.getKey().equals(SMDIdentityReferenceEntity.typeForClass(WorkEntity.class))) {
                    foundWork=true;
                }else if(child.getKey().equals(SMDIdentityReferenceEntity.typeForClass(TrackEntity.class))) {
                    foundTrack=true;
                }else {
                    assert false;
                }
            }
            assert foundArtist;
            assert foundClassification;
            assert foundWork;
            assert foundTrack;
        }

        result = browseService.findChildren(new ArrayList<String>(),new ArrayList<String>(),0,2);
        assert result.size()==2;
    }

    @Test
    public void testBrowseWorks() throws Exception {
        WorkBrowseService browseService = new WorkBrowseService();
        Collection<ResultItem<Work>> result = browseService.findChildren(new ArrayList<String>(),new ArrayList<String>(),null,null);
        assert result.size()==79;
        for (ResultItem<Work> workResultItem : result) {
            boolean foundClassification = false;
            boolean foundArtist = false;
            boolean foundRelease = false;
            boolean foundTrack = false;
            for (Map.Entry child : workResultItem.getChildItems().entrySet()) {
                if(((String)child.getKey()).startsWith(SMDIdentityReferenceEntity.typeForClass(ClassificationEntity.class))) {
                    foundClassification=true;
                }else if(((String) child.getKey()).startsWith(SMDIdentityReferenceEntity.typeForClass(ArtistEntity.class))) {
                    foundArtist=true;
                }else if(child.getKey().equals(SMDIdentityReferenceEntity.typeForClass(ReleaseEntity.class))) {
                    foundRelease=true;
                }else if(child.getKey().equals(SMDIdentityReferenceEntity.typeForClass(TrackEntity.class))) {
                    foundTrack=true;
                }else {
                    assert false;
                }
            }
            assert foundArtist;
            assert foundClassification;
            assert foundRelease;
            assert foundTrack;
        }

        result = browseService.findChildren(new ArrayList<String>(),new ArrayList<String>(),0,10);
        assert result.size()==10;
    }

    @Test
    public void testBrowseClassifications() throws Exception {
        ClassificationBrowseService browseService = new ClassificationBrowseService();
        Collection<ResultItem<Classification>> result = browseService.findChildren(new ArrayList<String>(),new ArrayList<String>(),null,null);
        assert result.size()==17;
        for (ResultItem<Classification> classificationResultItem : result) {
            boolean foundArtist = false;
            boolean foundRelease = false;
            boolean foundWork = false;
            boolean foundTrack = false;
            for (Map.Entry child : classificationResultItem.getChildItems().entrySet()) {
                if(((String)child.getKey()).startsWith(SMDIdentityReferenceEntity.typeForClass(ArtistEntity.class))) {
                    foundArtist=true;
                }else if(child.getKey().equals(SMDIdentityReferenceEntity.typeForClass(ReleaseEntity.class))) {
                    foundRelease=true;
                }else if(child.getKey().equals(SMDIdentityReferenceEntity.typeForClass(WorkEntity.class))) {
                    foundWork=true;
                }else if(child.getKey().equals(SMDIdentityReferenceEntity.typeForClass(TrackEntity.class))) {
                    foundTrack=true;
                }else {
                    assert false;
                }
            }
            assert foundRelease;
            assert foundArtist;
            assert foundWork;
            assert foundTrack;
        }

        result = browseService.findChildren(new ArrayList<String>(),new ArrayList<String>(),0,10);
        assert result.size()==10;
    }
}
