package org.socialmusicdiscovery.frontend;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.serialization.SerializationException;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.*;
import org.apache.pivot.wtkx.Bindable;
import org.apache.pivot.wtkx.WTKX;
import org.apache.pivot.wtkx.WTKXSerializer;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.Track;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EditReleaseWindow extends Window implements Bindable {
    private final DateFormat DATE_FORMAT_YEAR = new SimpleDateFormat("yyyy");
    private final DateFormat DATE_FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");

    @WTKX TextInput releaseNameTextInput;
    @WTKX TextInput yearTextInput;
    @WTKX TextInput composersTextInput;
    @WTKX TextInput conductorsTextInput;
    @WTKX TextInput performersTextInput;
    @WTKX TableView tracksTableView;
    @WTKX PushButton cancelButton;

    @WTKX PushButton searchComposersButton;
    @WTKX PushButton searchConductorsButton;
    @WTKX PushButton searchPerformersButton;

    public class TrackData {
        public Integer getNumber() {
            return number;
        }

        public void setNumber(Integer number) {
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

        private Integer number;
        private String title;
        private String composers;
        private String conductors;
        private String performers;
        private Track track;
    }
    private Release release;

    private List<TrackData> trackData = new ArrayList<TrackData>();

    private Resources resources;
    @Override
    public void initialize(Resources resources) {
        this.resources = resources;
    }

    public void open(Display display, Window owner, Release release) {
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
                    if(count==2) {
                        TrackData trackData = (TrackData) tracksTableView.getSelectedRow();
                        WTKXSerializer wtkxSerializer = new WTKXSerializer(resources);
                        EditTrackWindow window = (EditTrackWindow) wtkxSerializer.readObject(this, "EditTrackWindow.wtkx");
                        window.open(getDisplay(),getWindow(),trackData.track);
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
        if(release.getDate() != null) {
            yearTextInput.setText(DATE_FORMAT_DATE.format(release.getDate()));
        }

        updateContributors(release.getContributors());

        updateTracks(release.getTracks());

        super.open(display, owner);
    }

    private void updateTracks(java.util.List<Track> tracks) {
        trackData.clear();
        for (Track track : tracks) {
            TrackData trackData = new TrackData();
            trackData.setTrack(track);
            trackData.number = track.getNumber();
            if(track.getRecording().getName() != null) {
                trackData.title =track.getRecording().getName();
            }else if(track.getRecording().getWork()!= null) {
                trackData.title =track.getRecording().getWork().getName();
            }
            Set<Contributor> contributorSet = new HashSet<Contributor>(track.getRecording().getContributors());
            if(track.getRecording().getWork()!=null) {
                contributorSet.addAll(track.getRecording().getWork().getContributors());
            }
            Map<String,StringBuilder> contributors = getContributorMap(contributorSet);
            if(contributors.get("composer") != null) {
                trackData.composers = contributors.get("composer").toString();
            }
            if(contributors.get("conductor") != null) {
                trackData.conductors= contributors.get("conductor").toString();
            }
            if(contributors.get("performer") != null) {
                trackData.performers= contributors.get("performer").toString();
            }
            this.trackData.add(trackData);
        }
        System.out.println("Adding "+this.trackData.getLength()+" tracks");
        tracksTableView.setTableData(this.trackData);
    }
    private Map<String,StringBuilder> getContributorMap(Set<Contributor> contributorSet) {
        Map<String,StringBuilder> contributors = new HashMap<String,StringBuilder>();
        for (Contributor contributor : contributorSet) {
            if(!contributors.containsKey(contributor.getType())) {
                contributors.put(contributor.getType(),new StringBuilder());
            }
            StringBuilder contributorString = contributors.get(contributor.getType());
            if(contributorString.length()>0) {
                contributorString.append(", ");
            }
            contributorString.append(contributor.getArtist().getName());
        }
        return contributors;
    }
    private void updateContributors(Set<Contributor> contributorSet) {
        Map<String,StringBuilder> contributors = getContributorMap(contributorSet);
        if(contributors.get("composer") != null) {
            composersTextInput.setText(contributors.get("composer").toString());
        }
        if(contributors.get("conductor") != null) {
            conductorsTextInput.setText(contributors.get("conductor").toString());
        }
        if(contributors.get("performer") != null) {
            performersTextInput.setText(contributors.get("performer").toString());
        }

    }
}
