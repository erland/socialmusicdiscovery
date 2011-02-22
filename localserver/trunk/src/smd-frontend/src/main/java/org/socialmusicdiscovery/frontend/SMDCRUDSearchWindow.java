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
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.config.ClientConfig;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.serialization.SerializationException;
import org.apache.pivot.util.Resources;
import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;
import org.apache.pivot.util.concurrent.TaskListener;
import org.apache.pivot.wtk.*;
import org.apache.pivot.wtkx.Bindable;
import org.apache.pivot.wtkx.WTKX;
import org.apache.pivot.wtkx.WTKXSerializer;
import org.socialmusicdiscovery.server.api.OperationStatus;
import org.socialmusicdiscovery.server.api.management.mediaimport.MediaImportStatus;
import org.socialmusicdiscovery.server.business.model.core.Artist;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.Work;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SMDCRUDSearchWindow extends Window implements Bindable {
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

    /**
     * Import module to use
     */
    private final String IMPORT_MODULE = "squeezeboxserver";
    /**
     * Background task for updating progress bar duing media imports
     */
    private Task<Void> importTask = null;
    /**
     * Map with background search tasks currently executing
     */
    private Map<String, Task<Void>> searchTasks = new ConcurrentHashMap<String, Task<Void>>();
    /**
     * Indicates the that import operation has been aborted
     */
    private boolean importAborted = false;

    @WTKX
    Meter importProgressMeter;
    @WTKX
    Label importProgressDescription;
    @WTKX
    PushButton importButton;

    @WTKX
    TextInput searchTextInput;
    @WTKX
    PushButton searchButton;
    @WTKX
    ActivityIndicator searchActivity;

    @WTKX
    TableView artistResultsTableView;
    @WTKX
    TableView releaseResultsTableView;
    @WTKX
    TableView workResultsTableView;

    @WTKX
    PushButton closeButton;

    private Resources resources;

    @Inject
    private ClientConfig config;

    @Override
    public void initialize(Resources resources) {
        this.resources = resources;
        InjectHelper.injectMembers(this);
        HOSTURL = "http://" + SMDSERVER + ":" + SMDSERVERPORT;
    }

    @Override
    public void open(Display display, Window owner) {
        super.open(display, owner);

        // Set focus to window
        display.requestFocus();

        // Set focus to search field
        searchTextInput.requestFocus();

        // Check if an import is in progress and refresh the progress bar if it is
        try {
            MediaImportStatus status = Client.create(config).resource(HOSTURL + "/mediaimportmodules/" + IMPORT_MODULE).accept(MediaType.APPLICATION_JSON).get(MediaImportStatus.class);
            if (status != null) {
                startImportProgressBar(IMPORT_MODULE);
            }
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() != 204) {
                throw e;
            }
        }

        // Setup listeners for Import/Abort button
        importButton.getButtonPressListeners().add(new ButtonPressListener() {
            @Override
            public void buttonPressed(Button button) {
                if (importTask != null) {
                    abortImport(IMPORT_MODULE);
                } else {
                    startImport(IMPORT_MODULE);
                }
            }
        });

        // Setup listeners for close button
        closeButton.getButtonPressListeners().add(new ButtonPressListener() {
             @Override
             public void buttonPressed(Button button) {
                 getWindow().close();
              }
        });

        // When search button is clicked, we want to search for both artists, releases and works
        searchButton.getButtonPressListeners().add(new ButtonPressListener() {
            @Override
            public void buttonPressed(Button button) {
                searchArtists(searchTextInput.getText(), null, null);
                searchReleases(searchTextInput.getText(), null, null);
                searchWorks(searchTextInput.getText(), null, null);
            }
        });

        // When search field is changed, we want to search for both artists, releases and works
        searchTextInput.getTextInputTextListeners().add(new TextInputTextListener() {
            @Override
            public void textChanged(TextInput textInput) {
                if (textInput.getText() != null && textInput.getText().length() > 2) {
                    searchArtists(searchTextInput.getText(), null, null);
                    searchReleases(searchTextInput.getText(), null, null);
                    searchWorks(searchTextInput.getText(), null, null);
                }
            }
        });

        // Selection changes in release results should trigger refresh of artists and works
        releaseResultsTableView.getTableViewSelectionListeners().add(new TableViewSelectionListener.Adapter() {
            @Override
            public void selectedRangesChanged(TableView tableView, Sequence<Span> spanSequence) {
                Release release = (Release) tableView.getSelectedRow();
                searchTextInput.setText("");
                searchArtists(null, null, release.getId());
                searchWorks(null, null, release.getId());
            }
        });

        // Selection changes in artist results should trigger refresh of releases and works
        artistResultsTableView.getTableViewSelectionListeners().add(new TableViewSelectionListener.Adapter() {
            @Override
            public void selectedRangesChanged(TableView tableView, Sequence<Span> spanSequence) {
                Artist artist = (Artist) tableView.getSelectedRow();
                searchTextInput.setText("");
                searchReleases(null, artist.getId(), null);
                searchWorks(null, artist.getId(), null);
            }
        });

        // Selection changes in work results should trigger refresh of artists and releases
        workResultsTableView.getTableViewSelectionListeners().add(new TableViewSelectionListener.Adapter() {
            @Override
            public void selectedRangesChanged(TableView tableView, Sequence<Span> spanSequence) {
                Work work = (Work) tableView.getSelectedRow();
                searchTextInput.setText("");
                searchReleases(null, null, work.getId());
                searchArtists(null, work.getId(), null);
            }
        });

        // Double click handler for releases
        releaseResultsTableView.getComponentMouseButtonListeners().add(new ComponentMouseButtonListener.Adapter() {
            @Override
            public boolean mouseClick(Component component, Mouse.Button button, int x, int y, int count) {
                try {
                    if (count == 2) {
                        Release release = (Release) releaseResultsTableView.getSelectedRow();
                        WTKXSerializer wtkxSerializer = new WTKXSerializer(resources);
                        EditReleaseWindow window = (EditReleaseWindow) wtkxSerializer.readObject(this, "EditReleaseWindow.wtkx");
                        window.open(getDisplay(), getWindow(), release);
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
    }


    /**
     * Trigger a background search for artists
     *
     * @param name      Partial artist name
     * @param workId    Identity of work
     * @param releaseId Identity of release
     */
    private void searchArtists(String name, String workId, String releaseId) {
        if (searchTasks.containsKey("artist")) {
            return;
        }
        String parameters = "";
        if (name != null && name.length() > 0) {
            try {
                parameters = "?nameContains=" + URLEncoder.encode(name, "UTF8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else if (workId != null && workId.length() > 0) {
            try {
                parameters = "?work=" + URLEncoder.encode(workId, "UTF8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else if (releaseId != null && releaseId.length() > 0) {
            try {
                parameters = "?release=" + URLEncoder.encode(releaseId, "UTF8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        searchActivity.setVisible(true);
        searchActivity.setActive(true);

        final String searchParameters = parameters;
        searchTasks.put("artist", new Task<Void>() {
            @Override
            public Void execute() throws TaskExecutionException {
                try {
                    Collection<Artist> artists = Client.create(config).resource(HOSTURL + "/artists" + searchParameters).accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Artist>>() {
                    });
                    artistResultsTableView.getTableData().clear();
                    for (Artist artist : artists) {
                        ((List<Artist>) artistResultsTableView.getTableData()).add(artist);
                    }
                    return null;
                } catch (Throwable t) {
                    t.printStackTrace();
                    throw new TaskExecutionException(t);
                }
            }
        });
        executeAndCleanupSearch(name, "artist");
    }


    /**
     * Trigger a background search for releases
     *
     * @param name     Partial release name
     * @param artistId Identity of artist
     * @param workId   Identity of work
     */
    private void searchReleases(String name, String artistId, String workId) {
        if (searchTasks.containsKey("release")) {
            return;
        }
        String parameters = "";
        if (name != null && name.length() > 0) {
            try {
                parameters = "?nameContains=" + URLEncoder.encode(name, "UTF8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else if (artistId != null && artistId.length() > 0) {
            try {
                parameters = "?artist=" + URLEncoder.encode(artistId, "UTF8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else if (workId != null && workId.length() > 0) {
            try {
                parameters = "?work=" + URLEncoder.encode(workId, "UTF8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        searchActivity.setVisible(true);
        searchActivity.setActive(true);

        final String searchParameters = parameters;
        searchTasks.put("release", new Task<Void>() {
            @Override
            public Void execute() throws TaskExecutionException {
                try {
                    Collection<Release> releases = Client.create(config).resource(HOSTURL + "/releases" + searchParameters).accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Release>>() {
                    });
                    releaseResultsTableView.getTableData().clear();
                    for (Release release : releases) {
                        ((List<Release>) releaseResultsTableView.getTableData()).add(release);
                    }
                    return null;
                } catch (Throwable t) {
                    t.printStackTrace();
                    throw new TaskExecutionException(t);
                }
            }
        });
        executeAndCleanupSearch(name, "release");
    }

    /**
     * Trigger a background search for works
     *
     * @param name      Partial work name
     * @param artistId  Identity of artist
     * @param releaseId Identity of release
     */
    private void searchWorks(String name, String artistId, String releaseId) {
        if (searchTasks.containsKey("work")) {
            return;
        }
        String parameters = "";
        if (name != null && name.length() > 0) {
            try {
                parameters = "?nameContains=" + URLEncoder.encode(name, "UTF8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else if (artistId != null && artistId.length() > 0) {
            try {
                parameters = "?artist=" + URLEncoder.encode(artistId, "UTF8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else if (releaseId != null && releaseId.length() > 0) {
            try {
                parameters = "?release=" + URLEncoder.encode(releaseId, "UTF8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        searchActivity.setVisible(true);
        searchActivity.setActive(true);

        final String searchParameters = parameters;
        searchTasks.put("work", new Task<Void>() {
            @Override
            public Void execute() throws TaskExecutionException {
                try {
                    Collection<Work> works = Client.create(config).resource(HOSTURL + "/works" + searchParameters).accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Work>>() {
                    });
                    workResultsTableView.getTableData().clear();
                    for (Work work : works) {
                        ((List<Work>) workResultsTableView.getTableData()).add(work);
                    }
                    return null;
                } catch (Throwable t) {
                    t.printStackTrace();
                    throw new TaskExecutionException(t);
                }
            }
        });
        executeAndCleanupSearch(name, "work");
    }

    /**
     * Execute specified search operation as a background thread
     *
     * @param name       The search text used, this is required to make it possible to automatically re-search if user has changed search field during operation
     * @param objectType The type of object to search for
     */
    private void executeAndCleanupSearch(final String name, final String objectType) {
        searchTasks.get(objectType).execute(new TaskAdapter<Void>(new TaskListener<Void>() {
            @Override
            public void taskExecuted(Task<Void> voidTask) {
                searchTasks.remove(objectType);
                if (name != null && !name.equals(searchTextInput.getText()) && searchTextInput.getText().length() > 2) {
                    if (objectType.equals("artist")) {
                        searchArtists(searchTextInput.getText(), null, null);
                    } else if (objectType.equals("release")) {
                        searchReleases(searchTextInput.getText(), null, null);
                    } else if (objectType.equals("work")) {
                        searchWorks(searchTextInput.getText(), null, null);
                    }
                }
                if (searchTasks.isEmpty()) {
                    searchActivity.setVisible(false);
                    searchActivity.setActive(false);
                }
            }

            @Override
            public void executeFailed(Task<Void> voidTask) {
                searchTasks.remove(objectType);
                if (searchTasks.isEmpty()) {
                    searchActivity.setVisible(false);
                    searchActivity.setActive(false);
                }
            }
        }));
    }

    /**
     * Start a media import
     *
     * @param module The import module to use
     */
    private void startImport(String module) {
        if (importTask == null) {
            importAborted = false;
            OperationStatus operationStatus = Client.create(config).resource(HOSTURL + "/mediaimportmodules/" + module).post(OperationStatus.class);
            Boolean status = operationStatus.getSuccess();
            if (status != null && status) {
                startImportProgressBar(module);
            }
        }
    }

    /**
     * Start a background thread responsible to update the progress bar for an import operation in progress
     *
     * @param module Import module to show in the progress bar
     */
    private void startImportProgressBar(final String module) {
        if (importTask == null) {
            importTask = new Task<Void>() {
                @Override
                public Void execute() throws TaskExecutionException {
                    try {
                        importButton.setButtonData(resources.getString("SMDCRUDSearchWindow.abortButton"));
                        importProgressMeter.setPercentage(0);
                        importProgressMeter.setText("");
                        importProgressMeter.setVisible(true);
                        MediaImportStatus status = Client.create(config).resource(HOSTURL + "/mediaimportmodules/" + module).accept(MediaType.APPLICATION_JSON).get(MediaImportStatus.class);
                        while (status != null) {
                            if (status.getTotalNumber() > 0) {
                                importProgressMeter.setPercentage((double) status.getCurrentNumber() / status.getTotalNumber());
                                importProgressMeter.setText(status.getCurrentNumber() + " of " + status.getTotalNumber());
                            }
                            importProgressDescription.setText(status.getCurrentDescription());
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                throw new TaskExecutionException(e);
                            }
                            status = Client.create(config).resource(HOSTURL + "/mediaimportmodules/" + module).accept(MediaType.APPLICATION_JSON).get(MediaImportStatus.class);
                        }
                    } catch (UniformInterfaceException e) {
                        if (e.getResponse().getStatus() != 204) {
                            throw e;
                        }
                    }
                    return null;
                }
            };
            importTask.execute(new TaskAdapter<Void>(new TaskListener<Void>() {
                @Override
                public void taskExecuted(Task<Void> task) {
                    importProgressMeter.setPercentage(0);
                    importProgressMeter.setText("");
                    importProgressMeter.setVisible(false);
                    importButton.setButtonData(resources.getString("SMDCRUDSearchWindow.importButton"));
                    if (importAborted) {
                        importProgressDescription.setText("Import aborted");
                    } else {
                        importProgressDescription.setText("Import finished");
                    }
                    importTask = null;
                }

                @Override
                public void executeFailed(Task<Void> task) {
                    importProgressMeter.setPercentage(0);
                    importProgressMeter.setText("");
                    importProgressMeter.setVisible(false);
                    importButton.setButtonData(resources.getString("SMDCRUDSearchWindow.importButton"));
                    importProgressDescription.setText("Import failed");
                    importTask = null;
                }
            }));
        }
    }

    /**
     * Abort an import operation in progress
     *
     * @param module Import module to abort
     */
    public void abortImport(String module) {
        if (importTask != null) {
            importAborted = true;
            Client.create(config).resource(HOSTURL + "/mediaimportmodules/" + module).delete();
        }
    }
}
