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

package org.socialmusicdiscovery.server.business.repository.classification;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationEntity;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationReference;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationReferenceEntity;
import org.socialmusicdiscovery.server.business.repository.AbstractJPASMDIdentityRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class JPAClassificationRepository extends AbstractJPASMDIdentityRepository<ClassificationEntity> implements ClassificationRepository {
    private ClassificationReferenceRepository classificationReferenceRepository;

    @Inject
    public JPAClassificationRepository(EntityManager em, ClassificationReferenceRepository classificationReferenceRepository) {
        super(em);
        this.classificationReferenceRepository = classificationReferenceRepository;
    }

    public Collection<ClassificationEntity> findByNameAndType(String name, String type) {
        Query query = entityManager.createQuery("from ClassificationEntity where name=:name and type=:type");
        query.setParameter("name",name);
        query.setParameter("type",type);
        return query.getResultList();
    }

    public Collection<ClassificationEntity> findByReference(String reference) {
        Query query = entityManager.createQuery("select e from ClassificationEntity as e JOIN e.references as r JOIN r.referenceTo as ref where ref.id=:reference");
        query.setParameter("reference",reference);
        return query.getResultList();
    }

    public Collection<ClassificationEntity> findByTypeAndReference(String type, String reference) {
        Query query = entityManager.createQuery("from ClassificationEntity JOIN references as r JOIN r.referenceTo as ref where ref.type=:type and ref.id=:reference");
        query.setParameter("type",type);
        query.setParameter("reference",reference);
        return query.getResultList();
    }

    @Override
    public void create(ClassificationEntity entity) {
        if(entity.getSortAs()==null) {
            entity.setSortAsAutomatically();
        }
        super.create(entity);
        for (ClassificationReference classificationReference : entity.getReferences()) {
            if(!entityManager.contains(classificationReference)) {
                if(((ClassificationReferenceEntity)classificationReference).getLastUpdated()==null) {
                    ((ClassificationReferenceEntity)classificationReference).setLastUpdated(entity.getLastUpdated());
                }
                if(((ClassificationReferenceEntity)classificationReference).getLastUpdatedBy()==null) {
                    ((ClassificationReferenceEntity)classificationReference).setLastUpdatedBy(entity.getLastUpdatedBy());
                }
                ((ClassificationReferenceEntity) classificationReference).setClassification(entity);
                classificationReferenceRepository.create((ClassificationReferenceEntity) classificationReference);
            }
        }
    }

    @Override
    public ClassificationEntity merge(ClassificationEntity entity) {
        for (ClassificationReference classificationReference : entity.getReferences()) {
            if(!entityManager.contains(classificationReference)) {
                if(((ClassificationReferenceEntity)classificationReference).getLastUpdated()==null) {
                    ((ClassificationReferenceEntity)classificationReference).setLastUpdated(entity.getLastUpdated());
                }
                if(((ClassificationReferenceEntity)classificationReference).getLastUpdatedBy()==null) {
                    ((ClassificationReferenceEntity)classificationReference).setLastUpdatedBy(entity.getLastUpdatedBy());
                }
                ((ClassificationReferenceEntity) classificationReference).setClassification(entity);
                classificationReferenceRepository.merge((ClassificationReferenceEntity) classificationReference);
            }
        }
        if(entity.getSortAs()==null) {
            entity.setSortAsAutomatically();
        }
        return super.merge(entity);
    }

    @Override
    public void remove(ClassificationEntity entity) {
        entityManager.refresh(entity);
        entityManager.createNativeQuery("DELETE from classification_references where classification_id=:id").setParameter("id",entity.getId()).executeUpdate();

        super.remove(entity);
    }
}
