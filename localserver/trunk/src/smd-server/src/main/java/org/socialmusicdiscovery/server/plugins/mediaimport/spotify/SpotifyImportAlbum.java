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

package org.socialmusicdiscovery.server.plugins.mediaimport.spotify;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.model.GlobalIdentityEntity;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.repository.GlobalIdentityRepository;
import org.socialmusicdiscovery.server.business.repository.core.*;
import org.socialmusicdiscovery.server.business.service.browse.Command;
import org.socialmusicdiscovery.server.business.service.browse.CommandResult;

import javax.ws.rs.core.MediaType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class SpotifyImportAlbum implements Command {
    private static final String SPOTIFY_SOURCE = "spotify";
    @Inject
    ReleaseRepository releaseRepository;
    @Inject
    TrackRepository trackRepository;
    @Inject
    PlayableElementRepository playableElementRepository;
    @Inject
    RecordingRepository recordingRepository;
    @Inject
    WorkRepository workRepository;
    @Inject
    ContributorRepository contributorRepository;
    @Inject
    ArtistRepository artistRepository;
    @Inject
    GlobalIdentityRepository globalIdentityRepository;


    public SpotifyImportAlbum() {
        InjectHelper.injectMembers(this);
    }

    @Override
    public CommandResult executeCommand(List<String> parameters) {
        Date currentTime = new Date();
        if (parameters.size() == 0) {
            return new CommandResult(false, "Missing parameters");
        }
        String objectId = parameters.get(0);
        if (!objectId.startsWith(SpotifyAlbum.class.getSimpleName() + ":")) {
            return new CommandResult(false, "Invalid object, needs album identity");
        }
        try {
            objectId = objectId.substring(SpotifyAlbum.class.getSimpleName().length() + 1);
            JSONObject object = Client.create().resource("http://ws.spotify.com/lookup/1/.json?uri=" + objectId + "&extras=trackdetail").accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
            JSONObject jsonAlbum = object.getJSONObject("album");

            ReleaseEntity release = new ReleaseEntity();
            release.setName(jsonAlbum.getString("name"));
            if (jsonAlbum.has("released")) {
                release.setDate(new SimpleDateFormat("yyyy").parse(jsonAlbum.getString("released")));
            }
            release.setLastUpdated(currentTime);
            release.setLastUpdatedBy(SPOTIFY_SOURCE);
            releaseRepository.create(release);

            GlobalIdentityEntity globalIdentity = new GlobalIdentityEntity();
            globalIdentity.setSource(SPOTIFY_SOURCE);
            globalIdentity.setUri(jsonAlbum.getString("href"));
            globalIdentity.setEntityId(release.getId());
            globalIdentity.setLastUpdated(currentTime);
            globalIdentity.setLastUpdatedBy(SPOTIFY_SOURCE);
            globalIdentityRepository.create(globalIdentity);

            JSONArray array = jsonAlbum.getJSONArray("tracks");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonTrack = array.getJSONObject(i);

                WorkEntity work = new WorkEntity();
                work.setName(jsonTrack.getString("name"));
                work.setLastUpdated(currentTime);
                work.setLastUpdatedBy(SPOTIFY_SOURCE);
                workRepository.create(work);

                RecordingEntity recording = new RecordingEntity();
                recording.setLastUpdated(currentTime);
                recording.setLastUpdatedBy(SPOTIFY_SOURCE);
                recordingRepository.create(recording);
                recording.getWorks().add(work);

                TrackEntity track = new TrackEntity();
                if (jsonTrack.has("track-number")) {
                    track.setNumber(jsonTrack.getInt("track-number"));
                }
                track.setRecording(recording);
                release.addTrack(track);
                track.setLastUpdated(currentTime);
                track.setLastUpdatedBy(SPOTIFY_SOURCE);
                trackRepository.create(track);

                PlayableElementEntity playableElement = new PlayableElementEntity();
                playableElement.setSmdID(jsonTrack.getString("href"));
                playableElement.setUri(jsonTrack.getString("href"));
                playableElement.setFormat(SPOTIFY_SOURCE);
                playableElement.setLastUpdated(currentTime);
                playableElement.setLastUpdatedBy(SPOTIFY_SOURCE);
                playableElementRepository.create(playableElement);
                track.getPlayableElements().add(playableElement);

                JSONArray jsonArtists = jsonTrack.optJSONArray("artists");
                if (jsonArtists != null && jsonArtists.length() > 0) {
                    for (int j = 0; j < jsonArtists.length(); j++) {
                        JSONObject jsonArtist = jsonArtists.getJSONObject(j);
                        String name = jsonArtist.getString("name");
                        Collection<ArtistEntity> artists = artistRepository.findByName(name);
                        if (artists.size() == 0) {
                            artists = new ArrayList<ArtistEntity>();
                            ArtistEntity artist = new ArtistEntity();
                            artist.setName(name);
                            artist.setLastUpdated(currentTime);
                            artist.setLastUpdatedBy(SPOTIFY_SOURCE);
                            artistRepository.create(artist);
                            artists.add(artist);

                            globalIdentity = new GlobalIdentityEntity();
                            globalIdentity.setSource(SPOTIFY_SOURCE);
                            globalIdentity.setUri(jsonArtist.getString("href"));
                            globalIdentity.setEntityId(artist.getId());
                            globalIdentity.setLastUpdated(currentTime);
                            globalIdentity.setLastUpdatedBy(SPOTIFY_SOURCE);
                            globalIdentityRepository.create(globalIdentity);
                        }
                        for (ArtistEntity artist : artists) {
                            ContributorEntity contributor = new ContributorEntity(artist, Contributor.PERFORMER);
                            recording.addContributor(contributor);
                            contributor.setLastUpdated(currentTime);
                            contributor.setLastUpdatedBy(SPOTIFY_SOURCE);
                            contributorRepository.create(contributor);
                        }
                    }
                }
                recordingRepository.refresh(recording);
            }

            return new CommandResult(true, "Added " + release.getName());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
