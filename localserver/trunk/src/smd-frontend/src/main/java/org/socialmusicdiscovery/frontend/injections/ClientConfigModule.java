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

package org.socialmusicdiscovery.frontend.injections;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.socialmusicdiscovery.server.business.model.GlobalIdentity;
import org.socialmusicdiscovery.server.business.model.GlobalIdentityEntity;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReference;
import org.socialmusicdiscovery.server.business.model.SMDIdentityReferenceEntity;
import org.socialmusicdiscovery.server.business.model.classification.Classification;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationEntity;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.model.subjective.*;
import org.socialmusicdiscovery.server.support.json.AbstractJSONProvider;

import java.util.HashMap;
import java.util.Map;

public class ClientConfigModule extends AbstractModule {
    private static ClientConfig clientConfig;

    public static class JSONProvider extends AbstractJSONProvider {
        public JSONProvider() {
            super(true);
        }

        @Override
        protected Map<Class, Class> getConversionMap() {
            Map<Class, Class> converters = new HashMap<Class,Class>();

            converters.put(Label.class, LabelEntity.class);
            converters.put(Release.class, ReleaseEntity.class);
            converters.put(Contributor.class, ContributorEntity.class);
            converters.put(Artist.class, ArtistEntity.class);
            converters.put(Person.class, PersonEntity.class);
            converters.put(Medium.class, MediumEntity.class);
            converters.put(Track.class, TrackEntity.class);
            converters.put(PlayableElement.class, PlayableElementEntity.class);
            converters.put(RecordingSession.class, RecordingSessionEntity.class);
            converters.put(Recording.class, RecordingEntity.class);
            converters.put(Work.class, WorkEntity.class);
            converters.put(SMDIdentityReference.class, SMDIdentityReferenceEntity.class);
            converters.put(Classification.class, ClassificationEntity.class);
            converters.put(GlobalIdentity.class, GlobalIdentityEntity.class);
            converters.put(Relation.class, SMDIdentityReferenceEntity.class);
            converters.put(Credit.class, CreditEntity.class);
            converters.put(Series.class, SeriesEntity.class);
            converters.put(PlayableElement.class, PlayableElementEntity.class);

            return converters;
        }

    }
    @Override
    protected void configure() {
    }

    @Provides
    public ClientConfig provideClientConfig() {
        if (clientConfig == null) {
            clientConfig = new DefaultClientConfig();
            clientConfig.getClasses().add(JSONProvider.class);
        }
        return clientConfig;
    }
}

