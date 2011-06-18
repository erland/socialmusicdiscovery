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

package org.socialmusicdiscovery.server.plugins.mediaimport.lastfm;

import com.sun.jersey.api.client.Client;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.socialmusicdiscovery.server.business.logic.InternetImageProvider;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.core.ReleaseEntity;
import org.socialmusicdiscovery.server.business.service.browse.BrowseService;
import org.socialmusicdiscovery.server.business.service.browse.Result;
import org.socialmusicdiscovery.server.business.service.browse.ResultItem;

import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Browse service that browses LastFM albums
 */
public class LastFMAlbumBrowseService extends AbstractLastFMBrowseService implements BrowseService<LastFMAlbum> {
    @Override
    public Result<LastFMAlbum> findChildren(Collection<String> criteriaList, Collection<String> sortCriteriaList, Integer firstItem, Integer maxItems, Boolean childCounters) {
        String currentId = "";
        for (String criteria : criteriaList) {
            currentId = criteria;
        }

        ReleaseEntity entity = null;
        if (criteriaList.size() == 1 && currentId.startsWith("Release:")) {
            BrowseService browseService = browseServiceManager.getBrowseService("Release");
            if (browseService != null) {
                ResultItem currentItem = browseService.findById(currentId.substring(currentId.indexOf(":") + 1));
                entity = (ReleaseEntity) currentItem.getItem();
            }
        }
        Result<LastFMAlbum> result = new Result<LastFMAlbum>();

        if (entity != null) {
            try {
                JSONObject object = Client.create().resource(getLastFmUrl("album.search&album=" + urlEncode(entity.getName()))).accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
                List<ResultItem<LastFMAlbum>> albums = new ArrayList<ResultItem<LastFMAlbum>>();
                JSONArray array = object.getJSONObject("results").getJSONObject("albummatches").getJSONArray("album");
                result.setCount((long)array.length());
                for (int i = 0; i < array.length(); i++) {
                    if((firstItem==null || i>=firstItem) && (maxItems==null || maxItems>albums.size())) {
                        albums.add(createFromJSON(array.getJSONObject(i)));
                    }
                }
                result.setCount((long) albums.size());
                result.setItems(albums);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (currentId.startsWith(LastFMArtist.class.getSimpleName() + ":")) {
            try {
                String artistId = currentId.substring(13);
                JSONObject object = Client.create().resource(getLastFmUrl("artist.gettopalbums" + createQueryFromId(artistId))).accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
                List<ResultItem<LastFMAlbum>> albums = new ArrayList<ResultItem<LastFMAlbum>>();
                if (object.getJSONObject("topalbums").has("album")) {
                    JSONArray array = object.getJSONObject("topalbums").getJSONArray("album");
                    result.setCount((long)array.length());
                    for (int i = 0; i < array.length(); i++) {
                        if((firstItem==null || i>=firstItem) && (maxItems==null || maxItems>albums.size())) {
                            albums.add(createFromJSON(array.getJSONObject(i)));
                        }
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
    public ResultItem<LastFMAlbum> findById(String id) {
        try {
            JSONObject object = Client.create().resource(getLastFmUrl("album.getinfo" + createQueryFromId(id))).accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
            return createFromJSON(object.getJSONObject("album"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ResultItem<LastFMAlbum> createFromJSON(JSONObject json) throws JSONException {
        try {
            String id;
            if (json.has("mbid") && json.getString("mbid").length() > 0) {
                id = "mbid:" + json.getString("mbid");
            } else {
                String artist;
                if(json.optJSONObject("artist")!=null) {
                    artist = json.getJSONObject("artist").getString("name");
                }else {
                    artist = json.getString("artist");
                }
                id = "artist:" + urlEncode(artist) + ":album:" + urlEncode(json.getString("name"));
            }
            String image = null;
            JSONArray images = json.optJSONArray("image");
            if(images!=null && images.length()>0) {
                image = images.getJSONObject(images.length()-1).getString("#text");
                if(image.length()==0) {
                    image = null;
                }
            }
            String name = json.getString("name");
            LastFMAlbum album = new LastFMAlbum(id, name, image);
            ResultItem<LastFMAlbum> item = new ResultItem<LastFMAlbum>(album, false, false);
            item.setImage(getImage(album));
            item.setType(album.getClass().getSimpleName());
            return item;
        } catch (UnsupportedEncodingException e) {
            throw new JSONException(e);
        }
    }

    @Override
    public String getObjectType() {
        return LastFMAlbum.class.getSimpleName();
    }

    @Override
    protected <T extends SMDIdentity> ResultItem.ResultItemImage getImage(T item) {
        if(((LastFMAlbum)item).getImage()!=null) {
            return new ResultItem.ResultItemImage(InternetImageProvider.PROVIDER_ID, null, ((LastFMAlbum)item).getImage());
        }
        return null;
    }
}