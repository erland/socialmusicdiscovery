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

package org.socialmusicdiscovery.server.business.model;

import org.socialmusicdiscovery.server.business.model.classification.Classification;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationEntity;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationReference;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationReferenceEntity;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.Track;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;

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
            ClassificationReferenceEntity classificationReference = new ClassificationReferenceEntity();
            classificationReference.setReferenceTo(SMDIdentityReferenceEntity.forEntity(release));
            classificationReference.setLastUpdated(new Date());
            classificationReference.setLastUpdatedBy("JUnit");
            classification.addReference(classificationReference);
            classification.setLastUpdated(new Date());
            classification.setLastUpdatedBy("JUnit");

            for (Track track : release.getTracks()) {
                classificationReference = new ClassificationReferenceEntity();
                classificationReference.setReferenceTo(SMDIdentityReferenceEntity.forEntity(track));
                classificationReference.setLastUpdated(new Date());
                classificationReference.setLastUpdatedBy("JUnit");
                classification.addReference(classificationReference);
            }
            classificationRepository.create(classification);

            Collection<ClassificationEntity> classifications = classificationRepository.findByNameAndType("Pop", Classification.GENRE);
            assert classifications != null;
            assert classifications.size() == 1;
            classification = classifications.iterator().next();
            assert classification.getLastUpdated() != null;
            assert classification.getLastUpdatedBy() != null;
            Collection<ClassificationReference> references = classification.getReferences();
            assert references != null;
            assert references.size() == 5;
            int releaseMatches = 0;
            int trackMatches = 0;
            for (ClassificationReference reference : references) {
                assert ((ClassificationReferenceEntity)reference).getLastUpdated()!=null;
                assert ((ClassificationReferenceEntity)reference).getLastUpdatedBy()!=null;
                if (reference.getReferenceTo().getId().equals(release.getId())) {
                    releaseMatches++;
                    continue;
                } else {
                    for (Track track : release.getTracks()) {
                        if (reference.getReferenceTo().getId().equals(track.getId())) {
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
            throw e;
        } finally {
            if(em.getTransaction().isActive()) {
                em.getTransaction().commit();
            }
        }
    }
}
