package org.socialmusicdiscovery.server.business.logic;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.socialmusicdiscovery.server.api.management.mediaimport.MediaImportStatus;
import org.socialmusicdiscovery.server.api.mediaimport.MediaImporter;
import org.socialmusicdiscovery.server.api.mediaimport.MediaImporterCallback;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class MediaImportManager {
    @Inject
    @Named("mediaimport")
    private ExecutorService executorService;

    /** Available media importer modules */
    private Map<String,MediaImporter> mediaImporters;

    /** Currently executing media importer modules */
    private Map<String, MediaImporterState> runningModules = new HashMap<String,MediaImporterState>();

    /** Synchronization object to ensure thread safety */
    private final Object RUNNING_MODULES = new Object();

    /**
     * Representation of the current state of an executing media importer module
     */
    class MediaImporterState {
        MediaImporter importer;
        Long started = System.currentTimeMillis();
        String currentDescription;
        Long currentNo;
        Long totalNo;
        public MediaImporterState(MediaImporter importer) {
            this.importer = importer;
            currentDescription = "";
            currentNo = 0L;
            totalNo = 0L;
        }
    }

    public MediaImportManager(Map<String,MediaImporter> mediaImporters) {
        this.mediaImporters = mediaImporters;
        InjectHelper.injectMembers(this);
    }

    /**
     * Get status of the specified media importer module
     * @param module The identity of the media importer module, same as returned from {@link MediaImporter#getId()}
     * @return The current status or null if the module isn't executing
     */
    public MediaImportStatus getModuleStatus(String module) {
        synchronized (RUNNING_MODULES) {
            MediaImporterState state = runningModules.get(module);
            if(state != null) {
                return new MediaImportStatus(state.importer.getId(),state.currentDescription,state.currentNo,state.totalNo);
            }
        }
        return null;
    }

    /**
     * Get a list of all media importer modules currently executing
     * @return A list of all executing media importer modules, an empty list of no modules are executing
     */
    public Collection<MediaImportStatus> getRunningModules() {
        List<MediaImportStatus> status = new ArrayList<MediaImportStatus>();
        synchronized (RUNNING_MODULES) {
            for (MediaImporterState mediaImporterState : runningModules.values()) {
                status.add(getModuleStatus(mediaImporterState.importer.getId()));
            }
        }
        return status;
    }

    /**
     * Start an import using the specified media importer module
     * @param module The identity of the media importer module, same as returned from {@link MediaImporter#getId()}
     * @return true if the module can be started, false if the module doesn't exist or is currently executing
     */
    public boolean startImport(final String module) {
        if(module == null || !mediaImporters.containsKey(module) || runningModules.containsKey(module)) {
            return false;
        }
        synchronized (RUNNING_MODULES) {
            runningModules.put(module, new MediaImporterState(mediaImporters.get(module)));
        }
        Future future = executorService.submit(new Runnable() {
            public void run() {
                try {
                    mediaImporters.get(module).execute(new MediaImporterCallback() {
                        public void progress(String module, String currentDescription, Long currentNo, Long totalNo) {
                            System.out.println(module+" ("+currentNo+" of "+totalNo+"): "+currentDescription);
                            synchronized (RUNNING_MODULES) {
                                runningModules.get(module).currentDescription = currentDescription;
                                runningModules.get(module).currentNo= currentNo;
                                runningModules.get(module).totalNo= totalNo;
                            }
                        }

                        public void failed(String module, String error) {
                            synchronized (RUNNING_MODULES) {
                                System.out.println(module+" failure after "+((System.currentTimeMillis()-runningModules.get(module).started)/1000)+" seconds");
                                runningModules.remove(module);
                            }
                        }

                        public void aborted(String module) {
                            synchronized (RUNNING_MODULES) {
                                System.out.println(module+" aborted after "+((System.currentTimeMillis()-runningModules.get(module).started)/1000)+" seconds");
                                runningModules.remove(module);
                            }
                        }

                        public void finished(String module) {
                            synchronized (RUNNING_MODULES) {
                                System.out.println(module+" finished after "+((System.currentTimeMillis()-runningModules.get(module).started)/1000)+" seconds");
                                runningModules.remove(module);
                            }
                        }
                    });
                }catch(Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        return true;
    }

    /**
     * Abort an import of the specified media importer module
     * @param module The identity of the media importer module, same as returned from {@link MediaImporter#getId()}
     * @return true if the import was successfully aborted, false if the media importer module doesn't exist
     */
    public boolean abortImport(final String module) {
        if(module == null || !mediaImporters.containsKey(module)) {
            return false;
        }
        synchronized (RUNNING_MODULES) {
            if(runningModules.containsKey(module)) {
                runningModules.get(module).importer.abort();
            }
        }
        return true;
    }
}
