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

package org.socialmusicdiscovery.frontend;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.config.ClientConfig;
import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.serialization.SerializationException;
import org.apache.pivot.util.Resources;
import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;
import org.apache.pivot.wtk.*;
import org.socialmusicdiscovery.server.business.model.core.*;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EditReleaseWindow extends Window implements Bindable {
    private final DateFormat DATE_FORMAT_YEAR = new SimpleDateFormat("yyyy");
    private final DateFormat DATE_FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");

    @Inject
    @Named("smd-server.host")
    private String SMDSERVER;

    @Inject
    @Named("org.socialmusicdiscovery.server.port")
    private String SMDSERVERPORT;

    @Inject
    private ClientConfig config;

    @BXML
    TextInput releaseNameTextInput;
    @BXML
    TextInput yearTextInput;
    @BXML
    TextInput composersTextInput;
    @BXML
    TextInput conductorsTextInput;
    @BXML
    TextInput performersTextInput;
    @BXML
    TableView tracksTableView;
    @BXML
    PushButton cancelButton;

    @BXML
    PushButton searchComposersButton;
    @BXML
    PushButton searchConductorsButton;
    @BXML
    PushButton searchPerformersButton;

    public class TrackData {
        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getComposers() {
            return composers;
        }

        public void setComposers(String composers) {
            this.composers = composers;
        }

        public String getConductors() {
            return conductors;
        }

        public void setConductors(String conductors) {
            this.conductors = conductors;
        }

        public String getPerformers() {
            return performers;
        }

        public void setPerformers(String performers) {
            this.performers = performers;
        }

        public Track getTrack() {
            return track;
        }

        public void setTrack(Track track) {
            this.track = track;
        }

        private String number;
        private String title;
        private String composers;
        private String conductors;
        private String performers;
        private Track track;
    }

    private Release release;

    private List<TrackData> trackData = new ArrayList<TrackData>();

    /**
     * Map with background search tasks currently executing
     */
    private Map<String, Task<Void>> searchTasks = new ConcurrentHashMap<String, Task<Void>>();

    private SuggestionPopup suggestionPopup = new SuggestionPopup();

    private Resources resources;

    @Override
    public void initialize(org.apache.pivot.collections.Map<String, Object> stringObjectMap, URL url, Resources resources) {
        this.resources = resources;
        InjectHelper.injectMembers(this);
    }

    public void open(Display display, Window owner, Release release) {
        release = Client.create(config).resource("http://" + SMDSERVER + ":" + SMDSERVERPORT + "/releases/" + release.getId()).accept(MediaType.APPLICATION_JSON).get(Release.class);
        release.setTracks(new java.util.ArrayList<Track>(Client.create(config).resource("http://" + SMDSERVER + ":" + SMDSERVERPORT + "/tracks?release=" + release.getId()).accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Track>>() {})));
        this.release = release;

        cancelButton.getButtonPressListeners().add(new ButtonPressListener() {
            @Override
            public void buttonPressed(Button button) {
                getWindow().close();
            }
        });

        searchComposersButton.getButtonPressListeners().add(new ButtonPressListener() {
            @Override
            public void buttonPressed(Button button) {
                Alert.alert("Not implemented yet", getWindow());
            }
        });

        searchConductorsButton.getButtonPressListeners().add(new ButtonPressListener() {
            @Override
            public void buttonPressed(Button button) {
                Alert.alert("Not implemented yet", getWindow());
            }
        });

        searchPerformersButton.getButtonPressListeners().add(new ButtonPressListener() {
            @Override
            public void buttonPressed(Button button) {
                Alert.alert("Not implemented yet", getWindow());
            }
        });

        // Double click handler for releases
        tracksTableView.getComponentMouseButtonListeners().add(new ComponentMouseButtonListener.Adapter() {
            @Override
            public boolean mouseClick(Component component, Mouse.Button button, int x, int y, int count) {
                try {
                    if (count == 2) {
                        TrackData trackData = (TrackData) tracksTableView.getSelectedRow();
                        BXMLSerializer wtkxSerializer = new BXMLSerializer();
                        EditTrackWindow window = (EditTrackWindow) wtkxSerializer.readObject(getClass().getResource("EditTrackWindow.bxml"),new Resources(resources, EditTrackWindow.class.getName()));
                        window.open(getDisplay(), getWindow(), trackData.track);
                        return true;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (SerializationException e) {
                    throw new RuntimeException(e);
                }
                return false;
            }
        });

        releaseNameTextInput.setText(release.getName());
        if (release.getDate() != null) {
            yearTextInput.setText(DATE_FORMAT_DATE.format(release.getDate()));
        }

        updateContributors(release.getContributors());

        updateTracks(release.getTracks());

        // Add suggestion popup for performer
        performersTextInput.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override
            public void textInserted(TextInput textInput, int index, int count) {
                suggestArtists("performer", textInput);
            }

            @Override
            public void textRemoved(TextInput textInput, int index, int count) {
                suggestArtists("performer", textInput);
            }
        });

        // Add suggestion popup for conductor
        conductorsTextInput.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override
            public void textInserted(TextInput textInput, int index, int count) {
                suggestArtists("conductor", textInput);
            }

            @Override
            public void textRemoved(TextInput textInput, int index, int count) {
                suggestArtists("conductor", textInput);
            }
        });

        // Add suggestion popup for composer
        composersTextInput.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override
            public void textInserted(TextInput textInput, int index, int count) {
                suggestArtists("composer", textInput);
            }

            @Override
            public void textRemoved(TextInput textInput, int index, int count) {
                suggestArtists("composer", textInput);
            }
        });

        super.open(display, owner);
    }

    private void suggestArtists(final String type, final TextInput textInput) {
        if (searchTasks.containsKey(type)) {
            // Search already in progress
            return;
        }

        if (textInput.getText().length() > 1) {
            final String text = textInput.getText();
            final String searchParameters;

            try {
                searchParameters = "nameContains=" + URLEncoder.encode(text, "UTF8");
                searchTasks.put(type, new Task<Void>() {
                    @Override
                    public Void execute() throws TaskExecutionException {
                        Collection<Artist> artists = Client.create().resource("http://" + SMDSERVER + ":" + SMDSERVERPORT + "/artists?" + searchParameters).accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Artist>>() {
                        });
                        List<String> suggestedArtists = new ArrayList<String>();
                        for (Artist artist : artists) {
                            suggestedArtists.add(artist.getName());
                        }
                        if (suggestedArtists.getLength() > 0) {
                            suggestionPopup.setSuggestionData(suggestedArtists);
                            suggestionPopup.open(textInput);
                        }
                        if (!textInput.getText().equals(text)) {
                            if (textInput.getText().length() > 1) {
                                execute();
                                return null;
                            }
                        }
                        searchTasks.remove(type);
                        return null;
                    }
                });
                searchTasks.get(type).execute();
            } catch (TaskExecutionException e) {
                e.printStackTrace();
                // Hide exception, this shouldn't happen and if it does we can just ignore it
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                // Hide exception, this shouldn't happen and if it does we can just ignore it
            }
        }
    }

    private void updateTracks(java.util.List<Track> tracks) {
        trackData.clear();
        for (Track track : tracks) {
            TrackData trackData = new TrackData();
            if(track.getMedium() != null) {
                if(track.getMedium().getName() != null) {
                    trackData.number = track.getMedium().getName();
                }else if(track.getMedium().getNumber() != null) {
                    trackData.number = track.getMedium().getNumber().toString();
                }
            }
            trackData.setTrack(track);
            if(track.getNumber() != null) {
                if(trackData.number != null) {
                    trackData.number = trackData.number+"-"+track.getNumber();
                }else {
                    trackData.number = track.getNumber().toString();
                }
            }
            Work work = null;
            if (track.getRecording().getWorks() != null && track.getRecording().getWorks().size()>0) {
                work = track.getRecording().getWorks().iterator().next();
            }

            if (track.getRecording().getName() != null) {
                trackData.title = track.getRecording().getName();
            } else if (work != null) {
                if(work.getParent() != null) {
                    trackData.title = work.getParent().getName()+": "+work.getName();
                }else {
                    trackData.title = work.getName();
                }
            }
            Set<Contributor> contributorSet = new HashSet<Contributor>(track.getRecording().getContributors());
            if (work != null) {
                contributorSet.addAll(work.getContributors());
            }
            Map<String, StringBuilder> contributors = getContributorMap(contributorSet);
            if (contributors.get("composer") != null) {
                trackData.composers = contributors.get("composer").toString();
            }
            if (contributors.get("conductor") != null) {
                trackData.conductors = contributors.get("conductor").toString();
            }
            if (contributors.get("performer") != null) {
                trackData.performers = contributors.get("performer").toString();
            }
            this.trackData.add(trackData);
        }
        tracksTableView.setTableData(this.trackData);
    }

    private Map<String, StringBuilder> getContributorMap(Set<Contributor> contributorSet) {
        Map<String, StringBuilder> contributors = new HashMap<String, StringBuilder>();
        for (Contributor contributor : contributorSet) {
            if (!contributors.containsKey(contributor.getType())) {
                contributors.put(contributor.getType(), new StringBuilder());
            }
            StringBuilder contributorString = contributors.get(contributor.getType());
            if (contributorString.length() > 0) {
                contributorString.append(", ");
            }
            contributorString.append(contributor.getArtist().getName());
        }
        return contributors;
    }

    private void updateContributors(Set<Contributor> contributorSet) {
        Map<String, StringBuilder> contributors = getContributorMap(contributorSet);
        if (contributors.get("composer") != null) {
            composersTextInput.setText(contributors.get("composer").toString());
        }
        if (contributors.get("conductor") != null) {
            conductorsTextInput.setText(contributors.get("conductor").toString());
        }
        if (contributors.get("performer") != null) {
            performersTextInput.setText(contributors.get("performer").toString());
        }

    }
}
