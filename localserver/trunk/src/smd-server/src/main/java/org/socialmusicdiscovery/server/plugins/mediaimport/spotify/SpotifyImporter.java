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
import com.google.inject.name.Named;
import com.sun.jersey.api.client.Client;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.socialmusicdiscovery.server.api.mediaimport.MediaImporter;
import org.socialmusicdiscovery.server.api.mediaimport.ProcessingStatusCallback;
import org.socialmusicdiscovery.server.business.logic.ImageProviderManager;
import org.socialmusicdiscovery.server.business.model.GlobalIdentity;
import org.socialmusicdiscovery.server.business.model.GlobalIdentityEntity;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.repository.GlobalIdentityRepository;
import org.socialmusicdiscovery.server.plugins.mediaimport.AbstractTagImporter;
import org.socialmusicdiscovery.server.plugins.mediaimport.TagData;
import org.socialmusicdiscovery.server.plugins.mediaimport.TrackData;
import org.socialmusicdiscovery.server.plugins.mediaimport.squeezeboxserver.SqueezeboxServerImageProvider;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.*;

public class SpotifyImporter extends AbstractTagImporter implements MediaImporter {
    @Inject
    private ImageProviderManager imageProviderManager;

    @Inject
    @Named("squeezeboxserver.username")
    private String squeezeboxServerUsername;

    @Inject
    @Named("squeezeboxserver.password")
    private String squeezeboxServerPassword;

    @Inject
    @Named("squeezeboxserver.passwordhash")
    private String squeezeboxServerPasswordHash;

    @Inject
    @Named("squeezeboxserver.host")
    private String squeezeboxServerHost;

    @Inject
    @Named("squeezeboxserver.port")
    private String squeezeboxServerPort;

    @Inject
    @Named("spotifyd.host")
    private String spotifyDaemonHost;

    @Inject
    @Named("spotifyd.port")
    private String spotifyDaemonPort;

    @Inject
    private GlobalIdentityRepository globalIdentityRepository;

    @Override
    public String getId() {
        return "spotify";
    }

    @Override
    protected void executeImport(ProcessingStatusCallback progressHandler) {
        List<SpotifyPlaylistData> playlists = new ArrayList<SpotifyPlaylistData>();
        final String SERVICE_URL = "http://" + spotifyDaemonHost + ":" + spotifyDaemonPort;

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Client c = Client.create();

        List<String> playlistUris = new ArrayList<String>();
        playlistUris.add("inbox");
        playlistUris.add("starred");

        try {
            System.out.println("Making call to: " + SERVICE_URL + "/playlists.json");
            JSONObject response = c.resource(SERVICE_URL + "/playlists.json").accept("application/json").get(JSONObject.class);
            try {
                JSONArray playlistArray = response.getJSONArray("playlists");
                for (int i = 0; i < playlistArray.length(); i++) {
                    try {
                        playlistUris.add(playlistArray.getJSONObject(i).getString("uri"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        //TODO: Error handling ?
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                //TODO: Error handling ?
            }

            int noOfTracks = 0;
            for (String playlistUri : playlistUris) {
                response = c.resource(SERVICE_URL + "/" + playlistUri + "/playlists.json").accept("application/json").get(JSONObject.class);

                try {
                    SpotifyPlaylistData playlistData = mapper.readValue(response.toString(), SpotifyPlaylistData.class);
                    playlists.add(playlistData);
                    noOfTracks += playlistData.getTracks().size();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                    //TODO: Error handling ?
                } catch (JsonParseException e) {
                    e.printStackTrace();
                    //TODO: Error handling ?
                } catch (IOException e) {
                    e.printStackTrace();
                    //TODO: Error handling ?
                }
            }


            long i = 0;
            long offset = 0;
            final long CHUNK_SIZE = 10;
            for (SpotifyPlaylistData playlist : playlists) {
                for (SpotifyPlaylistTrackData trackData : playlist.getTracks()) {
                    if (!isAborted()) {
                        if (i == 0) {
                            entityManager.getTransaction().begin();
                        }

                        TrackData track = new TrackData();
                        track.setFile(trackData.getUri());
                        track.setFormat("spotify");
                        track.setSmdID(trackData.getUri());
                        track.setUrl(trackData.getUri());
                        track.setTags(new ArrayList<TagData>());
                        track.getTags().add(new TagData(TagData.ALBUM, trackData.getAlbum()));
                        track.getTags().add(new TagData(TagData.TITLE, trackData.getName()));
                        track.getTags().add(new TagData(TagData.SPOTIFY_TRACK_ID, trackData.getUri()));
                        track.getTags().add(new TagData("SPOTIFYIMAGE", trackData.getCover()));
                        track.getTags().add(new TagData("DURATION", "" + trackData.getDuration()));

                        if (trackData.getIndex() != null) {
                            if (trackData.getIndex() != null && trackData.getIndex() != 0) {
                                track.getTags().add(new TagData(TagData.TRACKNUM, "" + trackData.getIndex()));
                            }
                            if (trackData.getDisc() != null && trackData.getDisc() != 0) {
                                track.getTags().add(new TagData(TagData.DISC, "" + trackData.getDisc()));
                            }
                            if (trackData.getAlbumuri() != null) {
                                track.getTags().add(new TagData(TagData.SPOTIFY_ALBUM_ID, trackData.getAlbumuri()));
                            }
                            if (trackData.getArtists() != null) {
                                for (SpotifyArtistData artistData : trackData.getArtists()) {
                                    track.getTags().add(new TagData(TagData.ARTIST, artistData.getName()));
                                    track.getTags().add(new TagData(TagData.SPOTIFY_ARTIST_ID, artistData.getUri()));
                                }
                            }
                        } else {
                            //TODO: Remove this as soon as 2.2.7 of spotifyd has been officially released
                            response = c.resource(SERVICE_URL + "/" + trackData.getUri() + "/browse.json").accept("application/json").get(JSONObject.class);
                            try {
                                if (response.has("index") && !response.getString("index").equals("0")) {
                                    track.getTags().add(new TagData(TagData.TRACKNUM, response.getString("index")));
                                }
                                if (response.has("disc") && !response.getString("disc").equals("0")) {
                                    track.getTags().add(new TagData(TagData.DISC, response.getString("disc")));
                                }
                                if (response.has("albumuri") && response.getString("albumuri").length() > 0) {
                                    track.getTags().add(new TagData(TagData.SPOTIFY_ALBUM_ID, response.getString("albumuri")));
                                }
                                if (response.has("artists")) {
                                    JSONArray artistsArray = response.getJSONArray("artists");
                                    for (int j = 0; j < artistsArray.length(); j++) {
                                        JSONObject artistObject = artistsArray.getJSONObject(j);
                                        track.getTags().add(new TagData(TagData.ARTIST, artistObject.getString("name")));
                                        track.getTags().add(new TagData(TagData.SPOTIFY_ARTIST_ID, artistObject.getString("uri")));
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                //TODO: Implement error handling
                            }
                        }

                        progressHandler.progress(getId(), trackData.getAlbum() + "/" + trackData.getName(), offset + 1, (long) noOfTracks);
                        if (track != null) {
                            try {
                                importNewPlayableElement(track);
                            } catch (ConstraintViolationException e) {
                                //TODO: Change this so it uses the logging framework
                                System.err.println("ERROR when importing: " + track.getFile() + ": ");
                                for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                                    System.err.println("- " + violation.getLeafBean().getClass().getSimpleName() + "." + violation.getPropertyPath().toString() + ": " + violation.getMessage());
                                }
                            }
                        } else {
                            System.err.println("ERROR when importing: " + trackData.getName() + " by " + trackData.getArtist() + " with identity " + trackData.getUri() + ": ");
                            System.err.println("- Unable to read tags");
                        }

                        i++;
                        if (i >= CHUNK_SIZE) {
                            entityManager.flush();
                            entityManager.clear();
                            entityManager.getTransaction().commit();
                            i = 0;
                        }
                    } else {
                        progressHandler.progress(getId(), trackData.getAlbum() + "/" + trackData.getName(), offset + 1, (long) noOfTracks);
                    }
                    offset++;
                }
            }
            if (entityManager.getTransaction().isActive()) {
                entityManager.flush();
                entityManager.clear();
                entityManager.getTransaction().commit();
            }
            progressHandler.finished(getId());
        } catch (Throwable t) {
            progressHandler.failed(getId(), t.getLocalizedMessage());
            t.printStackTrace();
            //TODO: Add some error handling
        }
    }

    @Override
    protected ImageEntity getReleaseImage(TrackData data) {
        String spotifyImageId = null;
        for (TagData tagData : data.getTags()) {
            if (tagData.getName().equalsIgnoreCase("SPOTIFYIMAGE")) {
                spotifyImageId = tagData.getValue();
                break;
            }
        }
        if (spotifyImageId != null) {
            ImageEntity defaultImage = new ImageEntity();
            defaultImage.setProviderId(SpotifyImageProvider.PROVIDER_ID);
            defaultImage.setProviderImageId(spotifyImageId);
            defaultImage.setType(Image.TYPE_COVER_FRONT);
            defaultImage.setUri(imageProviderManager.getProvider(SqueezeboxServerImageProvider.PROVIDER_ID).getImageURL(defaultImage));
            return defaultImage;
        }
        return null;
    }

    @Override
    protected void handleTrackIdentities(Map<String, Collection<String>> tags, TrackEntity track) {
        if (tags.containsKey(TagData.SPOTIFY_TRACK_ID)) {
            GlobalIdentityEntity identity = new GlobalIdentityEntity();
            identity.setSource(GlobalIdentity.SOURCE_SPOTIFY);
            identity.setEntityId(track.getId());
            identity.setUri(tags.get(TagData.SPOTIFY_TRACK_ID).iterator().next());
            identity.setLastUpdated(new Date());
            identity.setLastUpdatedBy(getId());
            validate(identity);
            globalIdentityRepository.create(identity);
        }
        super.handleTrackIdentities(tags, track);
    }

    @Override
    protected void handleArtistIdentities(Map<String, Collection<String>> tags, Set<Contributor> artistContributors) {
        if (artistContributors.size() == 1 && tags.containsKey(TagData.SPOTIFY_ARTIST_ID)) {
            String artistId = tags.get(TagData.SPOTIFY_ARTIST_ID).iterator().next();
            Artist artist = artistContributors.iterator().next().getArtist();
            GlobalIdentityEntity identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_SPOTIFY, artist);
            if (identity == null) {
                identity = new GlobalIdentityEntity();
                identity.setSource(GlobalIdentity.SOURCE_SPOTIFY);
                identity.setEntityId(artist.getId());
                identity.setUri(artistId);
                identity.setLastUpdated(new Date());
                identity.setLastUpdatedBy(getId());
                validate(identity);
                globalIdentityRepository.create(identity);
            }
        }
        super.handleArtistIdentities(tags, artistContributors);
    }

    @Override
    protected void handleReleaseIdentities(Map<String, Collection<String>> tags, ReleaseEntity release) {
        if (tags.containsKey(TagData.SPOTIFY_ALBUM_ID)) {
            String releaseId = tags.get(TagData.SPOTIFY_ALBUM_ID).iterator().next();
            GlobalIdentityEntity identity = globalIdentityRepository.findBySourceAndEntity(GlobalIdentity.SOURCE_SPOTIFY, release);
            if (identity == null) {
                identity = new GlobalIdentityEntity();
                identity.setSource(GlobalIdentity.SOURCE_SPOTIFY);
                identity.setEntityId(release.getId());
                identity.setUri(releaseId);
                identity.setLastUpdated(new Date());
                identity.setLastUpdatedBy(getId());
                validate(identity);
                globalIdentityRepository.create(identity);
            }
        }
    }
}
