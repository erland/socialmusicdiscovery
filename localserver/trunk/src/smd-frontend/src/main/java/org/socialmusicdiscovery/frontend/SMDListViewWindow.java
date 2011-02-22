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
import com.sun.jersey.api.client.config.ClientConfig;
import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.util.Resources;
import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;
import org.apache.pivot.util.concurrent.TaskListener;
import org.apache.pivot.wtk.*;
import org.apache.pivot.wtk.content.ListItem;
import org.apache.pivot.wtkx.Bindable;
import org.apache.pivot.wtkx.WTKX;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.socialmusicdiscovery.frontend.injections.ClientConfigModule;
import org.socialmusicdiscovery.server.business.model.classification.Classification;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationEntity;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.model.core.Label;
import org.socialmusicdiscovery.server.support.format.TitleFormat;

import javax.ws.rs.core.MediaType;
import java.util.*;

public class SMDListViewWindow extends Window implements Bindable {
    @Inject
    @Named("smd-server.host")
    private String SMDSERVER;

    @Inject
    @Named("org.socialmusicdiscovery.server.port")
    private String SMDSERVERPORT;

    /**
     * URL to SMD Server application
     */
    private String HOSTURL = null;

    @WTKX
    PushButton closeButton;

    @WTKX
    ActivityIndicator searchActivity;

    @WTKX
    ListButton list1Button;
    @WTKX
    ListButton list2Button;
    @WTKX
    ListButton list3Button;

    @WTKX
    TableView list1TableView;
    @WTKX
    TableView list2TableView;
    @WTKX
    TableView list3TableView;

    @WTKX
    TableView tracksTableView;

    @Inject
    ClientConfigModule.JSONProvider jsonProvider;

    /**
     * List of ListButton objects which contains search criteras which a specific ListButton should base its search on
     */
    private Map<ListButton, List<ListButton>> previousListButtons = new HashMap<ListButton, List<ListButton>>();

    /**
     * The ListButton object that follows a specific ListButton, this is used to know which list to update when the current one is finished
     */
    private Map<ListButton, ListButton> followingListButton = new HashMap<ListButton, ListButton>();

    /**
     * Mapping between ListButton objects and TableView to know which ListButton is related to which TableView
     */
    private Map<ListButton, TableView> viewMap = new HashMap<ListButton, TableView>();

    /**
     * Browser implementation for each main object type
     */
    private Map<String, AbstractBrowser> browserMap = new HashMap<String, AbstractBrowser>();

    /**
     * Container with the search criteras that has been used to fill a specific TableView
     */
    private Map<TableView, String> previousCriterias = new HashMap<TableView, String>();

    private Resources resources;

    @Inject
    private ClientConfig config;

    /**
     * Representation of a single row in one of the search result tables
     */
    public static class TableViewItem {
        private String id;
        private String name;

        public TableViewItem(String id, String name, Object item) {
            this.id = id;
            this.name = new TitleFormat(name).format(item);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Override
    public void initialize(Resources resources) {
        this.resources = resources;
        InjectHelper.injectMembers(this);
        HOSTURL = "http://" + SMDSERVER + ":" + SMDSERVERPORT;
    }

    @Override
    public void open(Display display, Window owner) {
        super.open(display, owner);

        try {
            viewMap.put(list1Button, list1TableView);
            viewMap.put(list2Button, list2TableView);
            viewMap.put(list3Button, list3TableView);
            previousListButtons.put(list1Button, new ArrayList<ListButton>(new ListButton[0]));
            previousListButtons.put(list2Button, new ArrayList<ListButton>(Arrays.asList(list1Button).toArray(new ListButton[0])));
            previousListButtons.put(list3Button, new ArrayList<ListButton>(Arrays.asList(list1Button, list2Button).toArray(new ListButton[0])));
            followingListButton.put(list1Button, list2Button);
            followingListButton.put(list2Button, list3Button);
            followingListButton.put(list3Button, null);

            browserMap.put("Artist", new AbstractBrowser<Artist>(ArtistEntity.class) {
                @Override
                public TableViewItem getTableViewItem(Artist artist) {
                    return new TableViewItem(artist.getId(), "%object.name", artist);
                }
            });
            browserMap.put("Release", new AbstractBrowser<Release>(ReleaseEntity.class) {
                @Override
                public TableViewItem getTableViewItem(Release release) {
                    return new TableViewItem(release.getId(), "%object.name", release);
                }
            });
            browserMap.put("Work", new AbstractBrowser<Work>(WorkEntity.class) {
                @Override
                public TableViewItem getTableViewItem(Work work) {
                    return new TableViewItem(work.getId(), "%object.parent.name||[%object.parent,: ]||%object.name", work);
                }
            });
            browserMap.put("Classification", new AbstractBrowser<Classification>(ClassificationEntity.class) {
                @Override
                public TableViewItem getTableViewItem(Classification classification) {
                    return new TableViewItem(classification.getId(), "%object.name", classification);
                }
            });
            browserMap.put("Label", new AbstractBrowser<Label>(LabelEntity.class) {
                @Override
                public TableViewItem getTableViewItem(Label label) {
                    return new TableViewItem(label.getId(), "%object.name", label);
                }
            });
            browserMap.put("Track", new AbstractBrowser<Track>(TrackEntity.class) {
                @Override
                public TableViewItem getTableViewItem(Track track) {
                    return new TableViewItem(track.getId(), "(%object.medium.name|%object.medium.number)||[%object.medium,-]||%object.number||. ||%object.recording.works.parent.name||[%object.recording.works.parent,: ]||%object.recording.works.name", track);
                }
            });

            JSONArray array = Client.create().resource(HOSTURL + "/browse").accept(MediaType.APPLICATION_JSON).get(JSONArray.class);
            Set<ListItem> typeSet = new TreeSet<ListItem>(new Comparator<ListItem>() {
                @Override
                public int compare(ListItem item1, ListItem item2) {
                    return item1.getText().compareTo(item2.getText());
                }
            });
            for (int i = 0; i < array.length(); i++) {
                String type = array.getJSONObject(i).getString("id");
                if (type.contains(".")) {
                    String mainType = type.substring(0, type.indexOf("."));
                    String subType = type.substring(type.indexOf(".") + 1);
                    ListItem mainItem = new ListItem(mainType);
                    mainItem.setUserData(mainType);
                    typeSet.add(mainItem);
                    ListItem subItem = new ListItem(subType.substring(0, 1).toUpperCase() + subType.substring(1));
                    subItem.setUserData(type);
                    typeSet.add(subItem);
                } else {
                    ListItem item = new ListItem(type);
                    item.setUserData(type);
                    typeSet.add(item);
                }
            }
            List<ListItem> typeList = new ArrayList(typeSet.toArray());
            list1Button.setListData(typeList);
            list2Button.setListData(typeList);
            list3Button.setListData(typeList);

            int genreIndex = -1;
            int artistIndex = -1;
            int releaseIndex = -1;
            for (int i = 0; i < typeList.getLength(); i++) {
                if (typeList.get(i).getText().equals("Genre")) {
                    genreIndex = i;
                } else if (typeList.get(i).getText().equals("Release")) {
                    releaseIndex = i;
                } else if (typeList.get(i).getText().equals("Artist")) {
                    artistIndex = i;
                }
            }
            list1Button.getListButtonSelectionListeners().add(new ListButtonSelectionListener() {
                public void selectedIndexChanged(ListButton listButton, int i) {
                    updateSearchList(listButton);
                }
            });
            list2Button.getListButtonSelectionListeners().add(new ListButtonSelectionListener() {
                public void selectedIndexChanged(ListButton listButton, int i) {
                    updateSearchList(listButton);
                }
            });
            list3Button.getListButtonSelectionListeners().add(new ListButtonSelectionListener() {
                public void selectedIndexChanged(ListButton listButton, int i) {
                    updateSearchList(listButton);
                }
            });
            list1Button.setSelectedIndex(genreIndex);
            list2Button.setSelectedIndex(artistIndex);
            list3Button.setSelectedIndex(releaseIndex);

            list1TableView.getTableViewSelectionListeners().add(new TableViewSelectionListener.Adapter() {
                public void selectedRangesChanged(TableView tableView, Sequence<Span> previousSelectedRanges) {
                    updateSearchList(list2Button);
                }
            });
            list2TableView.getTableViewSelectionListeners().add(new TableViewSelectionListener.Adapter() {
                public void selectedRangesChanged(TableView tableView, Sequence<Span> previousSelectedRanges) {
                    updateSearchList(list3Button);
                }
            });
            list3TableView.getTableViewSelectionListeners().add(new TableViewSelectionListener.Adapter() {
                public void selectedRangesChanged(TableView tableView, Sequence<Span> previousSelectedRanges) {
                    updateSearchList(null);
                }
            });

            // Setup listeners for close button
            closeButton.getButtonPressListeners().add(new ButtonPressListener() {
                @Override
                public void buttonPressed(Button button) {
                    getWindow().close();
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    private void updateSearchList(final ListButton listButton) {
        String type = "Track";
        if (listButton != null) {
            type = ((ListItem) listButton.getSelectedItem()).getUserData().toString();
        }
        String mainType = type;
        if (type.contains(".")) {
            // We should retrieve objects of type "Artist" if type is "Artist.composer"
            mainType = type.substring(0, type.indexOf("."));
        }
        List<ListButton> previousButtons;
        if (listButton != null) {
            previousButtons = previousListButtons.get(listButton);
        } else {
            previousButtons = new ArrayList<ListButton>(Arrays.asList(list1Button, list2Button, list3Button).toArray(new ListButton[0]));
        }

        // Build list of search criteras based on selected items
        List<String> searchCriterias = new ArrayList<String>();
        for (ListButton button : previousButtons) {
            if (button.getSelectedIndex() >= 0) {
                ListItem selectedItem = (ListItem) button.getSelectedItem();
                String criteriaType = selectedItem.getUserData().toString();
                TableView tableView = viewMap.get(button);
                if (tableView.getSelectedIndex() >= 0) {
                    TableViewItem selectedViewItem = (TableViewItem) tableView.getTableData().get(tableView.getSelectedIndex());
                    if (selectedViewItem.getId() != null) {
                        searchCriterias.add(criteriaType + ":" + selectedViewItem.getId());
                    }
                }
            }
        }
        Task task = null;
        if (listButton != null) {
            task = browserMap.get(mainType).createTask(viewMap.get(listButton), type, searchCriterias);
        } else {
            task = browserMap.get(mainType).createTask(tracksTableView, type, searchCriterias);
        }

        // Activate search indicator
        searchActivity.setActive(true);
        searchActivity.setVisible(true);
        task.execute(new TaskAdapter<Void>(new TaskListener<Void>() {
            public void executeFailed(Task<Void> voidTask) {
                taskExecuted(voidTask);
            }

            public void taskExecuted(Task<Void> voidTask) {
                // If this isn't the track list (which doesn't have any following lists)
                if (listButton != null) {
                    // Update the next list in sequence based on the updated search criteras
                    ListButton nextButton = followingListButton.get(listButton);
                    if (nextButton != null) {
                        updateSearchList(nextButton);
                    } else {
                        // This is the track list which should be updated last
                        updateSearchList(null);
                    }
                } else {
                    // Update is finished, let's deactivate the search indicator
                    searchActivity.setVisible(false);
                    searchActivity.setActive(false);
                }
            }
        }));
    }

    /**
     * Abstract browser class which is used to browse objects using the "browse" API provided by SMD server
     * @param <T> The interface the object browsed is implementing
     */
    public abstract class AbstractBrowser<T> {
        // The implementation class which JSON should be serialized to
        Class<? extends T> itemClass;


        public AbstractBrowser(Class<? extends T> itemClass) {
            this.itemClass = itemClass;
        }

        /**
         * Should be implemented by concrete class so it returns a {@link TableViewItem} instance representing the object
         * @param object The object a {@link TableViewItem} instance should be created for
         * @return A newly create TableViewItem instance
         */
        public abstract TableViewItem getTableViewItem(T object);

        /**
         * Creates a new {@link Task] that will execute }the browse query towards the server and update the provided table view
         * @param tableView TableView which should be updated with the retrieved content
         * @param type Type of object to browse, for example "Artist.composer"
         * @param searchCriterias   List of search criterias besided the type of object that should be used, should be in format "Artist:xxxx"
         * @return A new Task object, you need to call the execute method to actually execute the task
         */
        public Task createTask(final TableView tableView, final String type, final List<String> searchCriterias) {
            Task task = new Task<Void>() {
                @Override
                public Void execute() throws TaskExecutionException {
                    if (tableView == tracksTableView && searchCriterias.getLength() == 0) {
                        tableView.getTableData().clear();
                        ((List<TableViewItem>) tableView.getTableData()).add(new TableViewItem(null, "(Too many matching tracks, select some criterias)", null));
                        return null;
                    }
                    try {
                        // Build search critera string
                        String criteriaString = "";
                        for (String criteria : searchCriterias) {
                            if (criteriaString.length() > 0) {
                                criteriaString += "&";
                            }
                            criteriaString += "criteria=" + criteria;
                        }
                        // Only update if search criteras have changed
                        if (previousCriterias.get(tableView) == null ||
                                !previousCriterias.get(tableView).equals(type + ":" + criteriaString)) {

                            // Execute query towards server
                            JSONObject object = Client.create().resource(HOSTURL + "/browse/" + type + "?" + criteriaString).accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
                            JSONArray objects = object.getJSONArray("items");
                            TableViewItem previousSelectedItem = null;

                            // Make sure we remember previous selection so we can re-select that item if it exists in the new search result
                            int previousSelectedIndex = -1;
                            if (tableView.getSelectedIndex() >= 0) {
                                previousSelectedItem = (TableViewItem) tableView.getTableData().get(tableView.getSelectedIndex());
                            }
                            tableView.getTableData().clear();
                            if (tableView != tracksTableView) {
                                // Add special "(All)" element at the top unless this is the tracks result list
                                ((List<TableViewItem>) tableView.getTableData()).add(new TableViewItem(null, "(All)", null));
                            }

                            // Convert each object from JSON to Java and add it to the table view
                            for (int i = 0; i < objects.length(); i++) {
                                T item = jsonProvider.fromJson(objects.getJSONObject(i).getJSONObject("item").toString(), itemClass);
                                TableViewItem tableViewItem = getTableViewItem(item);
                                int index = ((List<TableViewItem>) tableView.getTableData()).add(tableViewItem);

                                // If this is the previously selected object, make sure we remember its index
                                if (previousSelectedItem != null && tableViewItem.getId().equals(previousSelectedItem.getId())) {
                                    previousSelectedIndex = index;
                                }
                            }

                            // Remember search criterias which has been used for this request
                            previousCriterias.put(tableView, type + ":" + criteriaString);

                            // Re-select previously selected item if it exists in the new result
                            if (previousSelectedIndex >= 0) {
                                tableView.setSelectedIndex(previousSelectedIndex);
                            }
                        }
                        return null;
                    } catch (Throwable t) {
                        t.printStackTrace();
                        throw new TaskExecutionException(t);
                    }
                }
            };
            return task;
        }
    }
}
