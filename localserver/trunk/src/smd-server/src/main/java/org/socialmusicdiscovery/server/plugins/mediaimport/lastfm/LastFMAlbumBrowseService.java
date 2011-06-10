package org.socialmusicdiscovery.server.plugins.mediaimport.lastfm;

import com.sun.jersey.api.client.Client;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.socialmusicdiscovery.server.business.model.core.ReleaseEntity;
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
 * Browse service that browses LastFM albums
 */
public class LastFMAlbumBrowseService extends AbstractLastFMBrowseService implements BrowseService<LastFMAlbum> {
    public LastFMAlbumBrowseService() {
    }

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
                JSONObject object = Client.create().resource(getLastFmUrl("album.search&album=" + URLEncoder.encode(entity.getName(), "utf8"))).accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
                List<ResultItem<LastFMAlbum>> albums = new ArrayList<ResultItem<LastFMAlbum>>();
                JSONArray array = object.getJSONObject("results").getJSONObject("albummatches").getJSONArray("album");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject element = array.getJSONObject(i);
                    String id;
                    if (element.has("mbid") && element.getString("mbid").length() > 0) {
                        id = "mbid:" + element.getString("mbid");
                    } else {
                        id = "artist:" + URLEncoder.encode(element.getString("artist"), "utf8") + ":album:" + URLEncoder.encode(element.getString("name"), "utf8");
                    }
                    String name = element.getString("name");
                    LastFMAlbum album = new LastFMAlbum(id, name);
                    ResultItem<LastFMAlbum> item = new ResultItem<LastFMAlbum>(album, false, false);
                    item.setType(album.getClass().getSimpleName());
                    albums.add(item);
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
                String query;
                String artistId = currentId.substring(13);
                if (artistId.startsWith("mbid")) {
                    query = "&mbid=" + artistId.substring(5);
                } else {
                    query = "&" + artistId.replaceAll("artist:", "artist=");
                }
                JSONObject object = Client.create().resource(getLastFmUrl("artist.gettopalbums" + query)).accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
                List<ResultItem<LastFMAlbum>> albums = new ArrayList<ResultItem<LastFMAlbum>>();
                if (object.getJSONObject("topalbums").has("album")) {
                    JSONArray array = object.getJSONObject("topalbums").getJSONArray("album");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject element = array.getJSONObject(i);
                        String id;
                        if (element.has("mbid") && element.getString("mbid").length() > 0) {
                            id = "mbid:" + element.getString("mbid");
                        } else {
                            id = "artist:" + URLEncoder.encode(element.getString("artist"), "utf8") + ":album:" + URLEncoder.encode(element.getString("name"), "utf8");
                        }
                        String name = element.getString("name");
                        LastFMAlbum album = new LastFMAlbum(id, name);
                        ResultItem<LastFMAlbum> item = new ResultItem<LastFMAlbum>(album, false, false);
                        item.setType(album.getClass().getSimpleName());
                        albums.add(item);
                    }
                }
                result.setCount((long) albums.size());
                result.setItems(albums);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public ResultItem<LastFMAlbum> findById(String id) {
        try {
            String query;
            if (id.startsWith("mbid")) {
                query = "&mbid=" + id.substring(5);
            } else {
                query = id.replaceAll(":album:", "&album=");
                query = query.replaceAll("artist:", "artist=");
                query = "&" + query;
            }
            JSONObject object = Client.create().resource(getLastFmUrl("album.getinfo" + query)).accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
            String name = object.getJSONObject("album").getString("name");
            LastFMAlbum album = new LastFMAlbum(id, name);
            ResultItem<LastFMAlbum> result = new ResultItem<LastFMAlbum>(album, false, false);
            result.setType(album.getClass().getSimpleName());
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getObjectType() {
        return LastFMAlbum.class.getSimpleName();
    }
}