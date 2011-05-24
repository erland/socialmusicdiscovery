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

package org.socialmusicdiscovery.server.business.model.search;

import org.socialmusicdiscovery.server.business.model.classification.Classification;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationEntity;
import org.socialmusicdiscovery.server.business.model.core.Recording;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Represents a search relation between a {@link Recording} and a {@link Classification}, see {@link SearchRelationEntity} for more information.
 * This entity contains the aggregated list of all
 * {@link Classification} instances directly or indirectly tied to the {@link Recording}
 */
@Entity
@Table(name = "recording_classifications_search_relations")
public class RecordingClassificationSearchRelationEntity extends RecordingSearchRelationEntity {
    @ManyToOne
    @JoinColumn(name = "reference", insertable = false, updatable = false)
    private ClassificationEntity classification;

    public RecordingClassificationSearchRelationEntity() {
    }

    /**
     * Constructs a search relation related to a {@link Classification}, this will fill the {@link #type} field with the
     * value from {@link org.socialmusicdiscovery.server.business.model.classification.Classification#getType()}.
     * @param recording The owner
     * @param classification The classification
     */
    public RecordingClassificationSearchRelationEntity(Recording recording, Classification classification) {
        super(recording, classification);
    }
}
