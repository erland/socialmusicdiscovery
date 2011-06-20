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
import org.socialmusicdiscovery.server.business.model.core.ArtistEntity;
import org.socialmusicdiscovery.server.business.service.browse.BrowseService;
import org.socialmusicdiscovery.server.business.service.browse.Result;
import org.socialmusicdiscovery.server.business.service.browse.ResultItem;

import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Browse service that browses LastFM artists
 */
public class LastFMArtistBrowseService extends AbstractLastFMBrowseService implements BrowseService<LastFMArtist> {
    @Override
    public Result<LastFMArtist> findChildren(Collection<String> criteriaList, Collection<String> sortCriteriaList, Integer firstItem, Integer maxItems, Boolean childCounters) {
        String currentId = "";
        for (String criteria : criteriaList) {
            if(criteria.contains(":")) {
                currentId = criteria;
            }
        }

        ArtistEntity entity = null;
        if (criteriaList.size() == 1 && (currentId.startsWith("Artist:") || currentId.startsWith("Artist."))) {
            BrowseService browseService = browseServiceManager.getBrowseService("Artist");
            if (browseService != null) {
                ResultItem currentItem = browseService.findById(currentId.substring(currentId.indexOf(":") + 1));
                entity = (ArtistEntity) currentItem.getItem();
            }
        }
        Result<LastFMArtist> result = new Result<LastFMArtist>();

        if (entity != null) {
            try {
                JSONObject object = Client.create().resource(getLastFmUrl("artist.search&artist=" + urlEncode(entity.getName()))).accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
                List<ResultItem<LastFMArtist>> artists = new ArrayList<ResultItem<LastFMArtist>>();
                JSONArray array = object.getJSONObject("results").getJSONObject("artistmatches").getJSONArray("artist");
                result.setCount((long)array.length());
                for (int i = 0; i < array.length(); i++) {
                    if((firstItem==null || i>=firstItem) && (maxItems==null || maxItems>artists.size())) {
                        artists.add(createFromJSON(array.getJSONObject(i)));
                    }
                }
                result.setItems(artists);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public ResultItem<LastFMArtist> findById(String id) {
        try {
            JSONObject object = Client.create().resource(getLastFmUrl("artist.getinfo" + createQueryFromId(id))).accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
            return createFromJSON(object.getJSONObject("artist"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ResultItem<LastFMArtist> createFromJSON(JSONObject json) throws JSONException {
        try {
            String id;
            if (json.has("mbid") && json.getString("mbid").length() > 0) {
                id = "mbid:" + json.getString("mbid");
            } else {
                id = "artist:" + urlEncode(json.getString("name"));
            }
            String name = json.getString("name");
            String image = null;
            JSONArray images = json.optJSONArray("image");
            if(images!=null && images.length()>0) {
                image = images.getJSONObject(images.length()-1).getString("#text");
                if(image.length()==0) {
                    image = null;
                }
            }
            LastFMArtist artist = new LastFMArtist(id, name, image);
            ResultItem<LastFMArtist> item = new ResultItem<LastFMArtist>(artist, false, false);
            item.setImage(getImage(artist));
            item.setType(artist.getClass().getSimpleName());
            return item;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new JSONException(e);
        }
    }

    @Override
    public String getObjectType() {
        return LastFMArtist.class.getSimpleName();
    }

    @Override
    protected <T extends SMDIdentity> ResultItem.ResultItemImage getImage(T item) {
        if(((LastFMArtist)item).getImage()!=null) {
            return new ResultItem.ResultItemImage(InternetImageProvider.PROVIDER_ID, null, ((LastFMArtist)item).getImage());
        }
        return null;
    }
}
