package org.socialmusicdiscovery.server.business.model;

import org.socialmusicdiscovery.server.business.model.classification.Classification;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationEntity;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.Track;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Collection;

public class ClassificationTest extends BaseTestCase {
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

    @Test
    public void testModelClassification() throws Exception {
        loadTestData(getClass().getPackage().getName(), "The Bodyguard.xml");
        em.getTransaction().begin();
        try {
            Release release = releaseRepository.findByName("The Bodyguard (Original Soundtrack Album)").iterator().next();

            ClassificationEntity classification = new ClassificationEntity();
            classification.setName("Pop");
            classification.setType(Classification.GENRE);
            classification.getReferences().add(SMDIdentityReferenceEntity.forEntity(release));
            classificationRepository.create(classification);

            for (Track track : release.getTracks()) {
                classification.getReferences().add(SMDIdentityReferenceEntity.forEntity(track));
            }

            Collection<ClassificationEntity> classifications = classificationRepository.findByNameAndType("Pop", Classification.GENRE);
            assert classifications != null;
            assert classifications.size() == 1;
            classification = classifications.iterator().next();
            Collection<SMDIdentityReference> references = classification.getReferences();
            assert references != null;
            assert references.size() == 5;
            int releaseMatches = 0;
            int trackMatches = 0;
            for (SMDIdentityReference reference : references) {
                if (reference.getId().equals(release.getId())) {
                    releaseMatches++;
                    continue;
                } else {
                    for (Track track : release.getTracks()) {
                        if (reference.getId().equals(track.getId())) {
                            trackMatches++;
                            continue;
                        }
                    }
                }
            }
            assert releaseMatches == 1;
            assert trackMatches == 4;

        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.getTransaction().commit();
        }
    }
}
