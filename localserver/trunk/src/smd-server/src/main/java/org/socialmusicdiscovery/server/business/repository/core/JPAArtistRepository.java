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

package org.socialmusicdiscovery.server.business.repository.core;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.ArtistEntity;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.ContributorEntity;
import org.socialmusicdiscovery.server.business.repository.AbstractJPASMDIdentityRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class JPAArtistRepository extends AbstractJPASMDIdentityRepository<ArtistEntity> implements ArtistRepository {
    private PersonRepository personRepository;
    private ContributorRepository contributorRepository;

    @Inject
    public JPAArtistRepository(EntityManager em, PersonRepository personRepository, ContributorRepository contributorRepository) {
        super(em);
        this.personRepository = personRepository;
        this.contributorRepository = contributorRepository;
    }

    public Collection<ArtistEntity> findByName(String name) {
        return findByNameWithRelations(name, null, null);
    }

    public Collection<ArtistEntity> findByNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " where lower(e.name)=:name order by e.name");
        query.setParameter("name", name.toLowerCase());
        return query.getResultList();
    }

    public Collection<ArtistEntity> findByPartialNameWithRelations(String name, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations) + " where lower(e.name) like :name order by e.name");
        query.setParameter("name", "%" + name.toLowerCase() + "%");
        return query.getResultList();
    }

    @Override
    public void create(ArtistEntity entity) {
        if (entity.getPerson() != null && !entityManager.contains(entity.getPerson())) {
            entity.setPerson(personRepository.findById(entity.getPerson().getId()));
        }
        if(entity.getSortAs()==null) {
            entity.setSortAsAutomatically();
        }
        super.create(entity);
    }

    @Override
    public ArtistEntity merge(ArtistEntity entity) {
        if (entity.getPerson() != null && !entityManager.contains(entity.getPerson())) {
            entity.setPerson(personRepository.findById(entity.getPerson().getId()));
        }
        if(entity.getSortAs()==null) {
            entity.setSortAsAutomatically();
        }
        return super.merge(entity);
    }

    public Collection<ArtistEntity> findByWorkWithRelations(String workId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(recordingQueryStringFor("e", "artist", "work", "relation", "searchRelation", mandatoryRelations, optionalRelations, true) + " WHERE searchRelation.reference=:work order by e.name");
        query.setParameter("work", workId);
        return query.getResultList();
    }

    public Collection<ArtistEntity> findByReleaseWithRelations(String releaseId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(recordingQueryStringFor("e", "artist", "release", "relation", "searchRelation", mandatoryRelations, optionalRelations, true) + " WHERE searchRelation.reference=:release order by e.name");
        query.setParameter("release", releaseId);
        return query.getResultList();
    }

    public Collection<ArtistEntity> findByPersonWithRelations(String personId, Collection<String> mandatoryRelations, Collection<String> optionalRelations) {
        Query query = entityManager.createQuery(queryStringFor("e", mandatoryRelations, optionalRelations, true) + " JOIN e.person as p WHERE p.id=:person");
        query.setParameter("person", personId);
        return query.getResultList();
    }

    public void remove(ArtistEntity entity) {
        Collection<ContributorEntity> contributors = contributorRepository.findByArtistWithRelations(entity.getId(),null,null);
        for (Contributor contributor : contributors) {
            contributorRepository.remove((ContributorEntity)contributor);
        }

        entityManager.createQuery("DELETE from RecordingArtistSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from ReleaseSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        entityManager.createQuery("DELETE from PersonSearchRelationEntity where reference=:id").setParameter("id",entity.getId()).executeUpdate();
        super.remove(entity);
    }

    public void refresh(ArtistEntity entity) {
        Collection<ContributorEntity> contributors = contributorRepository.findByArtistWithRelations(entity.getId(), null, null);
        for (ContributorEntity contributor : contributors) {
            contributorRepository.refresh(contributor);
        }
    }
}
