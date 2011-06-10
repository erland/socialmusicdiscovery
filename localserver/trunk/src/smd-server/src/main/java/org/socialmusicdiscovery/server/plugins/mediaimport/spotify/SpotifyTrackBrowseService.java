package org.socialmusicdiscovery.server.plugins.mediaimport.spotify;

import com.sun.jersey.api.client.Client;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.socialmusicdiscovery.server.business.model.core.PlayableElementEntity;
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
                    JSONObject element = array.getJSONObject(i);
                    String id = element.getString("href");
                    String name = element.getString("name");
                    Integer number = null;
                    if (element.has("track-number")) {
                        number = element.getInt("track-number");
                    }
                    SpotifyTrack track = new SpotifyTrack(id, number, name);
                    PlayableElementEntity playableElement = new PlayableElementEntity();
                    playableElement.setUri(id);
                    track.getPlayableElements().add(playableElement);
                    ResultItem<SpotifyTrack> item = new ResultItem<SpotifyTrack>(track, true, false);
                    item.setType(track.getClass().getSimpleName());
                    item.setId(item.getType() + ":" + id);
                    item.setName(track.getName());
                    tracks.add(item);
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
                    JSONObject element = array.getJSONObject(i);
                    String id = element.getString("href");
                    String name = element.getString("name");
                    Integer number = null;
                    if (element.has("track-number")) {
                        number = element.getInt("track-number");
                    }
                    SpotifyTrack track = new SpotifyTrack(id, number, name);
                    PlayableElementEntity playableElement = new PlayableElementEntity();
                    playableElement.setUri(id);
                    track.getPlayableElements().add(playableElement);
                    ResultItem<SpotifyTrack> item = new ResultItem<SpotifyTrack>(track, true, false);
                    item.setType(track.getClass().getSimpleName());
                    tracks.add(item);
                }
                result.setCount(Long.valueOf(tracks.size()));
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
            String name = object.getJSONObject("track").getString("name");
            Integer number = null;
            if (object.getJSONObject("track").has("track-number")) {
                number = object.getJSONObject("track").getInt("track-number");
            }
            SpotifyTrack track = new SpotifyTrack(id, number, name);
            PlayableElementEntity playableElement = new PlayableElementEntity();
            playableElement.setUri(id);
            track.getPlayableElements().add(playableElement);
            ResultItem<SpotifyTrack> result = new ResultItem<SpotifyTrack>(track, true, false);
            result.setType(track.getClass().getSimpleName());
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
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