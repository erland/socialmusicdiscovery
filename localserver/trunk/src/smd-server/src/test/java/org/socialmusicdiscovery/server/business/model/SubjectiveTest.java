package org.socialmusicdiscovery.server.business.model;

import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.model.subjective.CreditEntity;
import org.socialmusicdiscovery.server.business.model.subjective.RelationEntity;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Collection;

public class SubjectiveTest extends BaseTestCase {
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
        em.clear();
    }

    @Test
    public void testModelCredit() throws Exception {
        loadTestData(getClass().getPackage().getName(),"The Bodyguard.xml");
        em.getTransaction().begin();
        try {
            Artist artist = artistRepository.findByName("Whitney Houston").iterator().next();
            Release release = releaseRepository.findByName("The Bodyguard (Original Soundtrack Album)").iterator().next();

            CreditEntity credit = new CreditEntity();
            credit.setArtistPersonId(artist.getId());
            credit.setReleaseRecordingWorkId(release.getId());
            credit.setType("Important contributor");
            creditRepository.create(credit);

            Collection<CreditEntity> credits = creditRepository.findCreditsForReleaseRecordingWork(release);
            assert credits != null;
            assert credits.size() == 1;

            credit = credits.iterator().next();
            assert credit != null;
            assert credit.getType().equals("Important contributor");

            artist = artistRepository.findById(credit.getArtistPersonId());
            assert artist != null;
            assert artist.getName().equals("Whitney Houston");

            release = releaseRepository.findById(credit.getReleaseRecordingWorkId());
            assert release != null;
            assert release.getName().equals("The Bodyguard (Original Soundtrack Album)");

        }catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }finally {
            em.getTransaction().commit();
        }
    }

    @Test
    public void testModelRelation() throws Exception {
        loadTestData(getClass().getPackage().getName(),"The Bodyguard.xml");
        em.getTransaction().begin();
        try {
            Artist artist = artistRepository.findByName("Whitney Houston").iterator().next();
            Artist artist2 = artistRepository.findByName("Babyface").iterator().next();
            Artist artist3 = artistRepository.findByName("Dolly Parton").iterator().next();
            Person person = personRepository.findByName("Whitney Elisabeth Houston").iterator().next();
            Person person2 = personRepository.findByName("Kenneth Brian Edmonds").iterator().next();
            Person person3= personRepository.findByName("Dolly Rebecca Parton").iterator().next();

            RelationEntity relation = new RelationEntity();
            relation.setFromId(artist.getId());
            relation.setToId(person.getId());
            relation.setType("In real life");
            relationRepository.create(relation);

            relation = new RelationEntity();
            relation.setFromId(artist2.getId());
            relation.setToId(person2.getId());
            relation.setType("In real life");
            relationRepository.create(relation);

            relation = new RelationEntity();
            relation.setFromId(artist3.getId());
            relation.setToId(artist.getId());
            relation.setType("Composer for");
            relationRepository.create(relation);

            relation = new RelationEntity();
            relation.setFromId(artist.getId());
            relation.setToId(artist2.getId());
            relation.setType("Friend to");
            relationRepository.create(relation);

            Collection<RelationEntity> relations = relationRepository.findRelationsFrom(SMDIdentityReferenceEntity.forEntity(artist));
            assert relations != null;
            assert relations .size() == 2;

            relations = relationRepository.findRelationsFrom(SMDIdentityReferenceEntity.forEntity(artist), PersonEntity.class);
            assert relations != null;
            assert relations .size() == 1;

            relation = relations.iterator().next();
            assert relation != null;
            assert relation.getType().equals("In real life");

            SMDIdentityReference fromReference = smdIdentityReferenceRepository.findById(relation.getFromId());
            SMDIdentityReference toReference = smdIdentityReferenceRepository.findById(relation.getToId());
            assert fromReference.getType().equals(ArtistEntity.class.getName());
            assert toReference.getType().equals(PersonEntity.class.getName());

            artist = artistRepository.findById(relation.getFromId());
            assert artist != null;
            assert artist.getName().equals("Whitney Houston");

            person = personRepository.findById(relation.getToId());
            assert person != null;
            assert person.getName().equals("Whitney Elisabeth Houston");

        }catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }finally {
            em.getTransaction().commit();
        }
    }
}
