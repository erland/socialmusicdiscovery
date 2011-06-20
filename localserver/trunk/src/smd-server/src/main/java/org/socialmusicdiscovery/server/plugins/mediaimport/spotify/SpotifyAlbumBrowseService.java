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
import org.socialmusicdiscovery.server.business.model.core.ReleaseEntity;
import org.socialmusicdiscovery.server.business.service.browse.*;

import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Browse service that browses Spotify albums
 */
public class SpotifyAlbumBrowseService extends AbstractBrowseService implements BrowseService<SpotifyAlbum>, OnlinePlayableElementService {
    @Override
    public Result<SpotifyAlbum> findChildren(Collection<String> criteriaList, Collection<String> sortCriteriaList, Integer firstItem, Integer maxItems, Boolean childCounters) {
        String currentId = "";
        for (String criteria : criteriaList) {
            if(criteria.contains(":")) {
                currentId = criteria;
            }
        }

        ReleaseEntity entity = null;
        if (criteriaList.size() == 1 && currentId.startsWith("Release:")) {
            BrowseService browseService = browseServiceManager.getBrowseService("Release");
            if (browseService != null) {
                ResultItem currentItem = browseService.findById(currentId.substring(currentId.indexOf(":") + 1));
                entity = (ReleaseEntity) currentItem.getItem();
            }
        }
        Result<SpotifyAlbum> result = new Result<SpotifyAlbum>();

        if (entity != null) {
            try {
                JSONObject object = Client.create().resource("http://ws.spotify.com/search/1/album.json?q=album:" + URLEncoder.encode(entity.getName(), "utf8")).accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
                result.setCount(object.getJSONObject("info").getLong("num_results"));
                List<ResultItem<SpotifyAlbum>> albums = new ArrayList<ResultItem<SpotifyAlbum>>();
                JSONArray array = object.getJSONArray("albums");
                for (int i = 0; i < array.length(); i++) {
                    ResultItem<SpotifyAlbum> item = createFromJSON(array.getJSONObject(i));
                    if (item != null) {
                        if((firstItem==null || result.getCount()>=firstItem) && (maxItems==null || maxItems>albums.size())) {
                            albums.add(item);
                        }
                        result.setCount(result.getCount()+1);
                    }
                }
                result.setItems(albums);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (currentId.startsWith(SpotifyArtist.class.getSimpleName() + ":")) {
            try {
                JSONObject object = Client.create().resource("http://ws.spotify.com/lookup/1/.json?uri=" + currentId.substring(14) + "&extras=album").accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
                List<ResultItem<SpotifyAlbum>> albums = new ArrayList<ResultItem<SpotifyAlbum>>();
                JSONArray array = object.getJSONObject("artist").getJSONArray("albums");
                for (int i = 0; i < array.length(); i++) {
                    ResultItem<SpotifyAlbum> item = createFromJSON(array.getJSONObject(i).getJSONObject("album"));
                    if (item != null) {
                        if((firstItem==null || result.getCount()>=firstItem) && (maxItems==null || maxItems>albums.size())) {
                            albums.add(item);
                        }
                        result.setCount(result.getCount()+1);
                    }
                }
                result.setItems(albums);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public ResultItem<SpotifyAlbum> findById(String id) {
        try {
            JSONObject object = Client.create().resource("http://ws.spotify.com/lookup/1/.json?uri=" + id).accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
            return createFromJSON(object.getJSONObject("album"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<OnlinePlayableElement> find(List<String> criteriaList) {
        List<OnlinePlayableElement> result = new ArrayList<OnlinePlayableElement>();
        for (String criteria : criteriaList) {
            if (criteria.startsWith(SpotifyAlbum.class.getSimpleName())) {
                JSONObject object = Client.create().resource("http://ws.spotify.com/lookup/1/.json?uri=" + criteria.substring(13) + "&extras=track").accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
                try {
                    JSONArray array = object.getJSONObject("album").getJSONArray("tracks");
                    for (int i = 0; i < array.length(); i++) {
                        result.add(new OnlinePlayableElement(array.getJSONObject(i).getString("href")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return result;
    }

    private ResultItem<SpotifyAlbum> createFromJSON(JSONObject json) throws JSONException {
        if (getConfiguration().getStringParameter("location") != null && json.getJSONObject("availability") != null) {
            String territories = json.getJSONObject("availability").getString("territories");
            if (territories != null && !territories.contains(getConfiguration().getStringParameter("location"))) {
                return null;
            }
        }
        String id = json.getString("href");
        String name = json.getString("name");
        SpotifyAlbum album = new SpotifyAlbum(id, name);
        ResultItem<SpotifyAlbum> item = new ResultItem<SpotifyAlbum>(album, false, false);
        item.setType(album.getClass().getSimpleName());
        return item;
    }

    @Override
    public String getObjectType() {
        return SpotifyAlbum.class.getSimpleName();
    }
}