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
 * Browse service that browses LastFM images
 */
public class LastFMImageBrowseService extends AbstractLastFMBrowseService implements BrowseService<LastFMImage> {
    @Override
    public Integer findChildrenCount(Collection<String> criteriaList) {
        Result<LastFMImage> result = findChildren(criteriaList,new ArrayList<String>(), null, null, false);
        return result.getCount();
    }

    @Override
    public Result<LastFMImage> findChildren(Collection<String> criteriaList, Collection<String> sortCriteriaList, Integer firstItem, Integer maxItems, Boolean childCounters) {
        String currentId = "";
        for (String criteria : criteriaList) {
            if(criteria.contains(":") && !criteria.startsWith("Folder:")) {
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
        Result<LastFMImage> result = new Result<LastFMImage>();

        if (entity != null) {
            try {
                JSONObject object = Client.create().resource(getLastFmUrl("artist.getimages&artist=" + urlEncode(entity.getName()))).accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
                List<ResultItem<LastFMImage>> images = new ArrayList<ResultItem<LastFMImage>>();
                if (object.getJSONObject("images").has("image")) {
                    JSONArray array = object.getJSONObject("images").getJSONArray("image");
                    result.setCount(array.length());
                    for (int i = 0; i < array.length(); i++) {
                        if((firstItem==null || i>=firstItem) && (maxItems==null || maxItems>images.size())) {
                            images.add(createFromJSON(array.getJSONObject(i)));
                        }
                    }
                }
                result.setItems(images);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (currentId.startsWith(LastFMArtist.class.getSimpleName() + ":")) {
            try {
                String artistId = currentId.substring(13);
                JSONObject object = Client.create().resource(getLastFmUrl("artist.getimages" + createQueryFromId(artistId))).accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
                List<ResultItem<LastFMImage>> images = new ArrayList<ResultItem<LastFMImage>>();
                if (object.getJSONObject("images").has("image")) {
                    JSONArray array = object.getJSONObject("images").getJSONArray("image");
                    result.setCount(array.length());
                    for (int i = 0; i < array.length(); i++) {
                        if((firstItem==null || i>=firstItem) && (maxItems==null || maxItems>images.size())) {
                            images.add(createFromJSON(array.getJSONObject(i)));
                        }
                    }
                }
                result.setItems(images);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public ResultItem<LastFMImage> findById(String id) {
        return null;
    }

    private ResultItem<LastFMImage> createFromJSON(JSONObject json) throws JSONException {
        try {
            String id = json.getString("url");
            if(id.startsWith("http://www.last.fm/music/")) {
                id = id.substring("http://www.last.fm/music/".length());
            }
            String name = json.getString("title");
            if(name.length()==0) {
                if(id.contains("/")) {
                    name = id.substring(id.lastIndexOf("/")+1);
                }else {
                    name = id;
                }
            }
            String image = null;
            JSONArray images = json.getJSONObject("sizes").optJSONArray("size");
            if(images!=null && images.length()>0) {
                image = images.getJSONObject(images.length()-1).getString("#text");
                if(image.length()==0) {
                    image = null;
                }
            }
            LastFMImage imageObject = new LastFMImage(urlEncode(id), name, image);
            ResultItem<LastFMImage> item = new ResultItem<LastFMImage>(imageObject, false, false);
            item.setImage(getImage(imageObject));
            item.setType(imageObject.getClass().getSimpleName());
            return item;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new JSONException(e);
        }
    }

    @Override
    public String getObjectType() {
        return LastFMImage.class.getSimpleName();
    }

    @Override
    protected <T extends SMDIdentity> ResultItem.ResultItemImage getImage(T item) {
        if(((LastFMImage)item).getImage()!=null) {
            return new ResultItem.ResultItemImage(InternetImageProvider.PROVIDER_ID, null, ((LastFMImage)item).getImage());
        }
        return null;
    }
}
