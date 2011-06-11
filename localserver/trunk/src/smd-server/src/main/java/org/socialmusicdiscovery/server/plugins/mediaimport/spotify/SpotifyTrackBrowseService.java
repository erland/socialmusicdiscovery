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

import com.sun.jersey.api.client.Client;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.socialmusicdiscovery.server.business.model.core.TrackEntity;
import org.socialmusicdiscovery.server.business.service.browse.AbstractBrowseService;
import org.socialmusicdiscovery.server.business.service.browse.BrowseService;
import org.socialmusicdiscovery.server.business.service.browse.Result;
import org.socialmusicdiscovery.server.business.service.browse.ResultItem;

import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Browse service that browses Spotify tracks
 */
public class SpotifyTrackBrowseService extends AbstractBrowseService implements BrowseService<SpotifyTrack> {
    @Override
    public Result<SpotifyTrack> findChildren(Collection<String> criteriaList, Collection<String> sortCriteriaList, Integer firstItem, Integer maxItems, Boolean childCounters) {
        String currentId = "";
        for (String criteria : criteriaList) {
            currentId = criteria;
        }

        TrackEntity entity = null;
        if (criteriaList.size() == 1 && currentId.startsWith("Track:")) {
            BrowseService browseService = browseServiceManager.getBrowseService("Track");
            if (browseService != null) {
                ResultItem currentItem = browseService.findById(currentId.substring(currentId.indexOf(":") + 1));
                entity = (TrackEntity) currentItem.getItem();
            }
        }
        Result<SpotifyTrack> result = new Result<SpotifyTrack>();

        if (entity != null) {
            try {
                JSONObject object = Client.create().resource("http://ws.spotify.com/search/1/track.json?q=track:" + URLEncoder.encode(entity.getRecording().getWorks().iterator().next().getName(), "utf8")).accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
                result.setCount(object.getJSONObject("info").getLong("num_results"));
                List<ResultItem<SpotifyTrack>> tracks = new ArrayList<ResultItem<SpotifyTrack>>();
                JSONArray array = object.getJSONArray("tracks");
                for (int i = 0; i < array.length(); i++) {
                    tracks.add(createFromJSON(array.getJSONObject(i)));
                }
                result.setItems(tracks);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (currentId.startsWith(SpotifyAlbum.class.getSimpleName() + ":")) {
            try {
                JSONObject object = Client.create().resource("http://ws.spotify.com/lookup/1/.json?uri=" + currentId.substring(13) + "&extras=track").accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
                List<ResultItem<SpotifyTrack>> tracks = new ArrayList<ResultItem<SpotifyTrack>>();
                JSONArray array = object.getJSONObject("album").getJSONArray("tracks");
                for (int i = 0; i < array.length(); i++) {
                    tracks.add(createFromJSON(array.getJSONObject(i)));
                }
                result.setCount((long) tracks.size());
                result.setItems(tracks);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public ResultItem<SpotifyTrack> findById(String id) {
        try {
            JSONObject object = Client.create().resource("http://ws.spotify.com/lookup/1/.json?uri=" + id).accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
            return createFromJSON(object.getJSONObject("track"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ResultItem<SpotifyTrack> createFromJSON(JSONObject json) throws JSONException {
        String id = json.getString("href");
        String name = json.getString("name");
        Integer number = null;
        if (json.has("track-number")) {
            number = json.getInt("track-number");
        }
        SpotifyTrack track = new SpotifyTrack(id, number, name);
        ResultItem<SpotifyTrack> item = new ResultItem<SpotifyTrack>(track, true, false);
        item.setType(track.getClass().getSimpleName());
        return item;
    }

    @Override
    public String getObjectType() {
        return SpotifyTrack.class.getSimpleName();
    }

    @Override
    protected Boolean getPlayable() {
        return true;
    }
}