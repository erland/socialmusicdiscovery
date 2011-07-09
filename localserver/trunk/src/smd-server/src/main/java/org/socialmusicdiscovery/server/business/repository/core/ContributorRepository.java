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

import com.google.inject.ImplementedBy;
import org.socialmusicdiscovery.server.business.model.core.ContributorEntity;
import org.socialmusicdiscovery.server.business.repository.SMDIdentityRepository;

import java.util.Collection;

@ImplementedBy(JPAContributorRepository.class)
public interface ContributorRepository extends SMDIdentityRepository<ContributorEntity> {
    Collection<ContributorEntity> findByArtistWithRelations(String artistId, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
    Collection<ContributorEntity> findByReleaseWithRelations(String releaseId, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
    Collection<ContributorEntity> findByWorkWithRelations(String workId, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
    Collection<ContributorEntity> findByRecordingWithRelations(String recordingId, Collection<String> mandatoryRelations, Collection<String> optionalRelations);
    Collection<ContributorEntity> findByRecordingSessionWithRelations(String recordingSessionId, Collection<String> mandatoryRelations, Collection<String> optionalRelations);

    /**
     * Refresh search relations for the specified entity, this method should be called after all modifications has been done on the entity
     * @param entity The entity that has been changed that might affect search relations
     */
    void refresh(ContributorEntity entity);
}
