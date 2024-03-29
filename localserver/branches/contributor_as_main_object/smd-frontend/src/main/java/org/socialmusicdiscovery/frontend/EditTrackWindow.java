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
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.util.Resources;
import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;
import org.apache.pivot.wtk.*;
import org.socialmusicdiscovery.server.business.model.core.Contributor;
import org.socialmusicdiscovery.server.business.model.core.Track;
import org.socialmusicdiscovery.server.business.model.core.Work;

import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.net.URL;
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

    @BXML
    TextInput trackNumberTextInput;
    @BXML
    TextInput recordingNameTextInput;
    @BXML
    TextInput recordingYearTextInput;
    @BXML
    TextInput workNameTextInput;
    @BXML
    TableView contributorsTableView;
    @BXML
    PushButton cancelButton;
    @BXML
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
    public void initialize(org.apache.pivot.collections.Map<String, Object> stringObjectMap, URL url, Resources resources) {
        this.resources = resources;
        InjectHelper.injectMembers(this);
    }

    public void open(Display display, Window owner, Track track) {
        track = Client.create(config).resource("http://" + SMDSERVER + ":" + SMDSERVERPORT + "/tracks/" + track.getId()).accept(MediaType.APPLICATION_JSON).get(Track.class);
        track.getRecording().setContributors(new java.util.HashSet<Contributor>(Client.create(config).resource("http://" + SMDSERVER + ":" + SMDSERVERPORT + "/contributors?recording=" + track.getRecording().getId()).accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Contributor>>() {})));
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
        recordingNameTextInput.setText(track.getRecording().getName()!=null?track.getRecording().getName():"");
        Work work = null;
        if (track.getRecording().getWorks() != null && track.getRecording().getWorks().size()>0) {
            work = track.getRecording().getWorks().iterator().next();
        }
        if (work != null) {
            workNameTextInput.setText(work.getName());
        }
        if (track.getRecording().getDate() != null) {
            recordingYearTextInput.setText(DATE_FORMAT_DATE.format(this.track.getRecording().getDate()));
        }

        Set<Contributor> contributors = new HashSet<Contributor>(track.getRecording().getContributors());
        if (work != null) {
            contributors.addAll(work.getContributors());
        }
        updateContributors(contributors);

        // Add a suggestion popup on the work name field
        workNameTextInput.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
            @Override
            public void textInserted(TextInput textInput, int index, int count) {
                suggestWorks("work", textInput);
            }

            @Override
            public void textRemoved(TextInput textInput, int index, int count) {
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
                            suggestionPopup.setSuggestionData(suggestedArtists);
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
