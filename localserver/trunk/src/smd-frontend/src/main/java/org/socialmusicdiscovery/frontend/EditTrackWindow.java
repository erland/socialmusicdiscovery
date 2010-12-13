package org.socialmusicdiscovery.frontend;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.config.ClientConfig;
import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.util.Resources;
import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;
import org.apache.pivot.wtk.*;
import org.apache.pivot.wtkx.Bindable;
import org.apache.pivot.wtkx.WTKX;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Track;
import org.socialmusicdiscovery.server.business.model.core.Work;

import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EditTrackWindow extends Window implements Bindable {
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

    @WTKX
    TextInput trackNumberTextInput;
    @WTKX
    TextInput recordingNameTextInput;
    @WTKX
    TextInput recordingYearTextInput;
    @WTKX
    TextInput workNameTextInput;
    @WTKX
    TableView contributorsTableView;
    @WTKX
    PushButton cancelButton;
    @WTKX
    PushButton searchWorksButton;

    public class ContributorData {
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Contributor getContributor() {
            return contributor;
        }

        public void setContributor(Contributor contributor) {
            this.contributor = contributor;
        }

        private String name;
        private String type;
        private Contributor contributor;
    }

    private Track track;

    private List<ContributorData> contributorData = new ArrayList<ContributorData>();

    /**
     * Map with background search tasks currently executing
     */
    private Map<String, Task<Void>> searchTasks = new ConcurrentHashMap<String, Task<Void>>();

    /**
     * Suggestion popup when searching for works
     */
    private SuggestionPopup suggestionPopup = new SuggestionPopup();

    private Resources resources;

    @Override
    public void initialize(Resources resources) {
        this.resources = resources;
        InjectHelper.injectMembers(this);
    }

    public void open(Display display, Window owner, Track track) {
        track = Client.create(config).resource("http://" + SMDSERVER + ":" + SMDSERVERPORT + "/tracks/" + track.getId()).accept(MediaType.APPLICATION_JSON).get(Track.class);
        this.track = track;

        searchWorksButton.getButtonPressListeners().add(new ButtonPressListener() {
            @Override
            public void buttonPressed(Button button) {
                Alert.alert("Not implemented yet", getWindow());
            }
        });

        cancelButton.getButtonPressListeners().add(new ButtonPressListener() {
            @Override
            public void buttonPressed(Button button) {
                getWindow().close();
            }
        });

        if (track.getNumber() != null) {
            trackNumberTextInput.setText(track.getNumber().toString());
        }
        recordingNameTextInput.setText(track.getRecording().getName());
        if (track.getRecording().getWork() != null) {
            workNameTextInput.setText(track.getRecording().getWork().getName());
        }
        if (track.getRecording().getDate() != null) {
            recordingYearTextInput.setText(DATE_FORMAT_DATE.format(this.track.getRecording().getDate()));
        }

        Set<Contributor> contributors = new HashSet<Contributor>(track.getRecording().getContributors());
        if (track.getRecording().getWork() != null) {
            contributors.addAll(track.getRecording().getWork().getContributors());
        }
        updateContributors(contributors);

        // Add a suggestion popup on the work name field
        workNameTextInput.getTextInputCharacterListeners().add(new TextInputCharacterListener() {
            @Override
            public void charactersInserted(TextInput textInput, int index, int count) {
                suggestWorks("work", textInput);
            }

            @Override
            public void charactersRemoved(TextInput textInput, int index, int count) {
                suggestWorks("work", textInput);
            }
        });

        super.open(display, owner);
    }


    private void suggestWorks(final String type, final TextInput textInput) {
        if (searchTasks.containsKey(type)) {
            // Search is already in progress
            return;
        }
        if (textInput.getText().length() > 2) {
            final String text = textInput.getText();
            final String searchParameters;

            try {
                searchParameters = "nameContains=" + URLEncoder.encode(text, "UTF8");
                searchTasks.put(type, new Task<Void>() {
                    @Override
                    public Void execute() throws TaskExecutionException {
                        Collection<Work> works = Client.create().resource("http://" + SMDSERVER + ":" + SMDSERVERPORT + "/works?" + searchParameters).accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Work>>() {
                        });
                        List<String> suggestedArtists = new ArrayList<String>();
                        for (Work work : works) {
                            suggestedArtists.add(work.getName());
                        }
                        if (suggestedArtists.getLength() > 0) {
                            suggestionPopup.setSuggestions(suggestedArtists);
                            suggestionPopup.open(textInput);
                        } else {
                            suggestionPopup.close();
                        }
                        if (!textInput.getText().equals(text)) {
                            if (textInput.getText().length() > 2) {
                                execute();
                                return null;
                            } else {
                                suggestionPopup.close();
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
        } else {
            suggestionPopup.close();
        }
    }

    private void updateContributors(java.util.Set<Contributor> contributors) {
        contributorData.clear();
        for (Contributor contributor : contributors) {
            ContributorData contributorData = new ContributorData();
            contributorData.setContributor(contributor);
            contributorData.name = contributor.getArtist().getName();
            contributorData.type = contributor.getType();
            this.contributorData.add(contributorData);
        }
        contributorsTableView.setTableData(this.contributorData);
    }
}
