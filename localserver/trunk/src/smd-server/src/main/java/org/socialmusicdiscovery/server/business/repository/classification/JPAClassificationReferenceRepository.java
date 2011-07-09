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
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationReferenceEntity;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.repository.AbstractJPASMDIdentityRepository;
import org.socialmusicdiscovery.server.business.repository.core.*;

import javax.persistence.EntityManager;

public class JPAClassificationReferenceRepository extends AbstractJPASMDIdentityRepository<ClassificationReferenceEntity> implements ClassificationReferenceRepository {
    private LabelRepository labelRepository;
    private ReleaseRepository releaseRepository;
    private TrackRepository trackRepository;
    private RecordingRepository recordingRepository;
    private RecordingSessionRepository recordingSessionRepository;
    private WorkRepository workRepository;
    private ArtistRepository artistRepository;
    private PersonRepository personRepository;

    @Inject
    public JPAClassificationReferenceRepository(EntityManager em, LabelRepository labelRepository, ReleaseRepository releaseRepository, TrackRepository trackRepository,
    RecordingRepository recordingRepository, RecordingSessionRepository recordingSessionRepository, WorkRepository workRepository, ArtistRepository artistRepository, PersonRepository personRepository) {
        super(em);
        this.labelRepository = labelRepository;
        this.releaseRepository = releaseRepository;
        this.trackRepository = trackRepository;
        this.recordingRepository = recordingRepository;
        this.recordingSessionRepository = recordingSessionRepository;
        this.workRepository = workRepository;
        this.artistRepository = artistRepository;
        this.personRepository = personRepository;
    }

    public void refresh(ClassificationReferenceEntity entity) {

        if(entity.getReferenceTo().getType().equals(SMDIdentityReferenceEntity.typeForClass(LabelEntity.class))) {
            labelRepository.refresh(labelRepository.findById(entity.getReferenceTo().getId()));
        }else if(entity.getReferenceTo().getType().equals(SMDIdentityReferenceEntity.typeForClass(ReleaseEntity.class))) {
            releaseRepository.refresh(releaseRepository.findById(entity.getReferenceTo().getId()));
        }else if(entity.getReferenceTo().getType().equals(SMDIdentityReferenceEntity.typeForClass(TrackEntity.class))) {
            trackRepository.refresh(trackRepository.findById(entity.getReferenceTo().getId()));
        }else if(entity.getReferenceTo().getType().equals(SMDIdentityReferenceEntity.typeForClass(RecordingEntity.class))) {
            recordingRepository.refresh(recordingRepository.findById(entity.getReferenceTo().getId()));
        }else if(entity.getReferenceTo().getType().equals(SMDIdentityReferenceEntity.typeForClass(RecordingSessionEntity.class))) {
            recordingSessionRepository.refresh(recordingSessionRepository.findById(entity.getReferenceTo().getId()));
        }else if(entity.getReferenceTo().getType().equals(SMDIdentityReferenceEntity.typeForClass(WorkEntity.class))) {
            workRepository.refresh(workRepository.findById(entity.getReferenceTo().getId()));
        }else if(entity.getReferenceTo().getType().equals(SMDIdentityReferenceEntity.typeForClass(ArtistEntity.class))) {
            artistRepository.refresh(artistRepository.findById(entity.getReferenceTo().getId()));
        }else if(entity.getReferenceTo().getType().equals(SMDIdentityReferenceEntity.typeForClass(PersonEntity.class))) {
            personRepository.refresh(personRepository.findById(entity.getReferenceTo().getId()));
        }

    }
}
