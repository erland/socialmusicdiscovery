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

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.Track;
import org.socialmusicdiscovery.server.business.repository.GlobalIdentityRepository;
import org.socialmusicdiscovery.server.business.repository.core.ArtistRepository;
import org.socialmusicdiscovery.server.business.repository.core.ReleaseRepository;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.Test;

import java.util.Date;

public class GlobalIdentityTest extends BaseTestCase {
    @Inject
    ReleaseRepository releaseRepository;
    @Inject
    ArtistRepository artistRepository;
    @Inject
    GlobalIdentityRepository globalIdentityRepository;

    @Test
    public void testModelGlobalIdentity() throws Exception {
        loadTestData(getClass().getPackage().getName(), "The Bodyguard.xml");
        em.getTransaction().begin();
        try {
            Artist artist = artistRepository.findByName("Whitney Houston").iterator().next();
            Release release = releaseRepository.findByName("The Bodyguard (Original Soundtrack Album)").iterator().next();

            GlobalIdentityEntity identity = new GlobalIdentityEntity();
            identity.setSource(GlobalIdentity.SOURCE_MUSICBRAINZ);
            identity.setEntityId(artist.getId());
            identity.setUri("0307edfc-437c-4b48-8700-80680e66a228");
            identity.setLastUpdated(new Date());
            identity.setLastUpdatedBy("JUnit");
            globalIdentityRepository.create(identity);

            identity = new GlobalIdentityEntity();
            identity.setSource(GlobalIdentity.SOURCE_MUSICBRAINZ);
            identity.setEntityId(release.getId());
            identity.setUri("11cafb9e-5fbc-49c7-b920-4ff754e03e93");
            identity.setLastUpdated(new Date());
            identity.setLastUpdatedBy("JUnit");
            globalIdentityRepository.create(identity);

            int identifiedTracks = 0;
            for (Track track : release.getTracks()) {
                identity = new GlobalIdentityEntity();
                identity.setSource(GlobalIdentity.SOURCE_MUSICBRAINZ);
                identity.setEntityId(track.getId());
                identity.setLastUpdated(new Date());
                identity.setLastUpdatedBy("JUnit");
                if (track.getNumber().equals(1)) {
                    identity.setUri("86cf33ac-5b7b-401b-9188-608bb2752063");
                } else if (track.getNumber().equals(5)) {
                    identity.setUri("bdd8624a-d0ac-480f-8fe5-253bd99b7d3f");
                } else if (track.getNumber().equals(9)) {
                    identity.setUri("f2ba4ef6-7017-4b93-9176-c079ed0a97e9");
                } else if (track.getNumber().equals(13)) {
                    identity.setUri("c3af5bb7-5711-495e-8fae-af8d730497cd");
                }
                if (identity.getUri() != null) {
                    identifiedTracks++;
                    globalIdentityRepository.create(identity);
                }
            }
            assert identifiedTracks == 4;

            identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_MUSICBRAINZ, artist);
            assert identity != null;
            assert identity.getUri().equals("0307edfc-437c-4b48-8700-80680e66a228");
            assert identity.getLastUpdated() != null;
            assert identity.getLastUpdatedBy() != null;

            identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_MUSICBRAINZ, release);
            assert identity != null;
            assert identity.getUri().equals("11cafb9e-5fbc-49c7-b920-4ff754e03e93");
            assert identity.getLastUpdated() != null;
            assert identity.getLastUpdatedBy() != null;

            for (Track track : release.getTracks()) {
                if (track.getNumber().equals(1)) {
                    identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_MUSICBRAINZ, track);
                    assert identity != null;
                    assert identity.getUri().equals("86cf33ac-5b7b-401b-9188-608bb2752063");
                    assert identity.getLastUpdated() != null;
                    assert identity.getLastUpdatedBy() != null;
                    identifiedTracks--;
                } else if (track.getNumber().equals(5)) {
                    identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_MUSICBRAINZ, track);
                    assert identity != null;
                    assert identity.getUri().equals("bdd8624a-d0ac-480f-8fe5-253bd99b7d3f");
                    assert identity.getLastUpdated() != null;
                    assert identity.getLastUpdatedBy() != null;
                    identifiedTracks--;
                } else if (track.getNumber().equals(9)) {
                    identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_MUSICBRAINZ, track);
                    assert identity != null;
                    assert identity.getUri().equals("f2ba4ef6-7017-4b93-9176-c079ed0a97e9");
                    assert identity.getLastUpdated() != null;
                    assert identity.getLastUpdatedBy() != null;
                    identifiedTracks--;
                } else if (track.getNumber().equals(13)) {
                    identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_MUSICBRAINZ, track);
                    assert identity != null;
                    assert identity.getUri().equals("c3af5bb7-5711-495e-8fae-af8d730497cd");
                    assert identity.getLastUpdated() != null;
                    assert identity.getLastUpdatedBy() != null;
                    identifiedTracks--;
                }
            }

            assert identifiedTracks == 0;
        } finally {
            em.getTransaction().commit();
        }
    }
}
