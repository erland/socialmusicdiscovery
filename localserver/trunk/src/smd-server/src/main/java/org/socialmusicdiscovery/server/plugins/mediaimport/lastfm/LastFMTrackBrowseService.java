package org.socialmusicdiscovery.server.plugins.mediaimport.lastfm;

import com.sun.jersey.api.client.Client;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.socialmusicdiscovery.server.business.model.core.TrackEntity;
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
 * Browse service that browses LastFM tracks
 */
public class LastFMTrackBrowseService extends AbstractLastFMBrowseService implements BrowseService<LastFMTrack> {
    @Override
    public Result<LastFMTrack> findChildren(Collection<String> criteriaList, Collection<String> sortCriteriaList, Integer firstItem, Integer maxItems, Boolean childCounters) {
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
        Result<LastFMTrack> result = new Result<LastFMTrack>();

        if (entity != null) {
            try {
                JSONObject object = Client.create().resource(getLastFmUrl("track.search&track=" + URLEncoder.encode(entity.getRecording().getWorks().iterator().next().getName(), "utf8"))).accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
                List<ResultItem<LastFMTrack>> tracks = new ArrayList<ResultItem<LastFMTrack>>();
                JSONArray array = object.getJSONObject("results").getJSONObject("trackmatches").getJSONArray("track");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject element = array.getJSONObject(i);
                    String id;
                    if (element.has("mbid") && element.getString("mbid").length() > 0) {
                        id = "mbid:" + element.getString("mbid");
                    } else {
                        id = "artist:" + URLEncoder.encode(element.getString("artist"), "utf8") + ":track:" + URLEncoder.encode(element.getString("name"), "utf8");
                    }
                    String name = element.getString("name");
                    LastFMTrack track = new LastFMTrack(id, null, name);
                    ResultItem<LastFMTrack> item = new ResultItem<LastFMTrack>(track, false, false);
                    item.setType(track.getClass().getSimpleName());
                    tracks.add(item);
                }
                result.setCount((long) tracks.size());
                result.setItems(tracks);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (currentId.startsWith(LastFMAlbum.class.getSimpleName() + ":")) {
            try {
                String query;
                String artistId = currentId.substring(12);
                if (artistId.startsWith("mbid")) {
                    query = "&mbid=" + artistId.substring(5);
                } else {
                    query = artistId.replaceAll(":album:", "&album=");
                    query = query.replaceAll("artist:", "artist=");
                    query = "&" + query;
                }
                JSONObject object = Client.create().resource(getLastFmUrl("album.getinfo" + query)).accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
                List<ResultItem<LastFMTrack>> tracks = new ArrayList<ResultItem<LastFMTrack>>();
                if (object.getJSONObject("album").getJSONObject("tracks").has("track")) {
                    JSONArray array = object.getJSONObject("album").getJSONObject("tracks").getJSONArray("track");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject element = array.getJSONObject(i);
                        String id;
                        if (element.has("mbid") && element.getString("mbid").length() > 0) {
                            id = "mbid:" + element.getString("mbid");
                        } else {
                            id = "artist:" + URLEncoder.encode(element.getJSONObject("artist").getString("name"), "utf8") + ":track:" + URLEncoder.encode(element.getString("name"), "utf8");
                        }
                        String name = element.getString("name");
                        LastFMTrack track = new LastFMTrack(id, null, name);
                        ResultItem<LastFMTrack> item = new ResultItem<LastFMTrack>(track, false, false);
                        item.setType(track.getClass().getSimpleName());
                        tracks.add(item);
                    }
                }
                result.setCount((long) tracks.size());
                result.setItems(tracks);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public ResultItem<LastFMTrack> findById(String id) {
        try {
            String query;
            if (id.startsWith("mbid")) {
                query = "&mbid=" + id.substring(5);
            } else {
                query = id.replaceAll(":album:", "&album=");
                query = query.replaceAll("artist:", "artist=");
                query = "&" + query;
            }
            JSONObject object = Client.create().resource(getLastFmUrl("track.getinfo" + query)).accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
            String name = object.getJSONObject("track").getString("name");
            LastFMTrack track = new LastFMTrack(id, null, name);
            ResultItem<LastFMTrack> result = new ResultItem<LastFMTrack>(track, false, false);
            result.setType(track.getClass().getSimpleName());
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getObjectType() {
        return LastFMTrack.class.getSimpleName();
    }
}