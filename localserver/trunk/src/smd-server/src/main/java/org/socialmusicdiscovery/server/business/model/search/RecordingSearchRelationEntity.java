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

import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.classification.Classification;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Recording;

/**
 * Abstract class which represents a search relation between a {@link Recording} and some other entity, see {@link SearchRelationEntity} for more information
 */
public abstract class RecordingSearchRelationEntity extends SearchRelationEntity {
    public RecordingSearchRelationEntity() {
    }

    /**
     * Constructs a search relation related to the specific {@link SMDIdentity}, this will leave the {@link #type} field empty
     * @param recording The owner
     * @param reference The entity which the search relation should be related to
     */
    public RecordingSearchRelationEntity(Recording recording, SMDIdentity reference) {
        super(recording, reference);
    }

    /**
     * Constructs a search relation related to a {@link Contributor}, this will fill the {@link #type} field with the
     * value from {@link org.socialmusicdiscovery.server.business.model.core.Contributor#getType()}.
     * The {@link org.socialmusicdiscovery.server.business.model.core.Contributor#getArtist()} will be used to fill the {@link #reference} field.
     * @param recording The owner
     * @param contributor The contributor
     */
    public RecordingSearchRelationEntity(Recording recording, Contributor contributor) {
        super(recording, contributor);
    }

    /**
     * Constructs a search relation related to a {@link Classification}, this will fill the {@link #type} field with the
     * value from {@link org.socialmusicdiscovery.server.business.model.classification.Classification#getType()}.
     * @param recording The owner
     * @param classification The classification
     */
    public RecordingSearchRelationEntity(Recording recording, Classification classification) {
        super(recording, classification);
    }
}
