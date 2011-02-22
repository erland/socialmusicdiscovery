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

package org.socialmusicdiscovery.server.business.logic;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.socialmusicdiscovery.server.api.management.mediaimport.MediaImportStatus;
import org.socialmusicdiscovery.server.api.mediaimport.MediaImporter;
import org.socialmusicdiscovery.server.api.mediaimport.PostProcessor;
import org.socialmusicdiscovery.server.api.mediaimport.ProcessingModule;
import org.socialmusicdiscovery.server.api.mediaimport.ProcessingStatusCallback;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class MediaImportManager {
    @Inject
    @Named("mediaimport")
    private ExecutorService executorService;

    /** Available media importer modules */
    private Map<String,MediaImporter> mediaImporters;

    /** Available post processing modules */
    private Map<String, PostProcessor> postProcessors;

    /** Currently executing media importer modules */
    private Map<String, ProcessState> runningModules = new HashMap<String, ProcessState>();

    /** Synchronization object to ensure thread safety */
    private final Object RUNNING_MODULES = new Object();

    /**
     * Representation of the current state of an executing media importer module
     */
    static class ProcessState {
        ProcessingModule module;
        public enum Phase {
            EXECUTING,
            POSTPROCESSING,
        };
        Phase phase;
        String currentPostProcessingModule;
        Long started = System.currentTimeMillis();
        String currentDescription;
        Long currentNo;
        Long totalNo;
        public ProcessState(ProcessingModule module, Phase phase) {
            this.module = module;
            this.phase = phase;
            currentDescription = "";
            currentNo = 0L;
            totalNo = 0L;
        }
    }

    public MediaImportManager(Map<String,MediaImporter> mediaImporters, Map<String,PostProcessor> postProcessors) {
        this.mediaImporters = mediaImporters;
        this.postProcessors = postProcessors;
        InjectHelper.injectMembers(this);
    }

    /**
     * Get status of the specified media importer module
     * @param module The identity of the media importer module, same as returned from {@link MediaImporter#getId()}
     * @return The current status or null if the module isn't executing
     */
    public MediaImportStatus getModuleStatus(String module) {
        synchronized (RUNNING_MODULES) {
            ProcessState state = runningModules.get(module);
            if(state != null) {
                if(state.phase == ProcessState.Phase.EXECUTING) {
                    return new MediaImportStatus(state.module.getId(),state.currentDescription,state.currentNo,state.totalNo);
                }else if(state.phase == ProcessState.Phase.POSTPROCESSING) {
                    state = runningModules.get(state.currentPostProcessingModule);
                    return new MediaImportStatus(state.module.getId(),state.currentDescription,state.currentNo,state.totalNo);
                }
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
            for (ProcessState processState : runningModules.values()) {
                status.add(getModuleStatus(processState.module.getId()));
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
            runningModules.put(module, new ProcessState(mediaImporters.get(module), ProcessState.Phase.EXECUTING));
        }
        Future future = executorService.submit(new Runnable() {
            public void run() {
                try {
                    mediaImporters.get(module).execute(new ProcessingStatusCallback() {
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
                            boolean postProcessing = false;
                            ProcessState state = null;
                            synchronized (RUNNING_MODULES) {
                                state = runningModules.get(module);
                                if(!(state.module instanceof PostProcessor) && postProcessors.size()>0) {
                                    System.out.println(module+" aborted, started to execute post processors after "+((System.currentTimeMillis()-runningModules.get(module).started)/1000)+" seconds...");
                                    state.phase = ProcessState.Phase.POSTPROCESSING;
                                    postProcessing = true;
                                }else if(postProcessors.size()==0) {
                                    System.out.println(module+" aborted after "+((System.currentTimeMillis()-runningModules.get(module).started)/1000)+" seconds");
                                }
                            }
                            if(postProcessing) {
                                for (PostProcessor postProcessor : postProcessors.values()) {
                                    synchronized (RUNNING_MODULES) {
                                        state.currentPostProcessingModule = postProcessor.getId();
                                        runningModules.put(postProcessor.getId(), new ProcessState(postProcessor,ProcessState.Phase.EXECUTING));
                                    }
                                    postProcessor.execute(this);
                                }
                                System.out.println(module+" aborted after "+((System.currentTimeMillis()-runningModules.get(module).started)/1000)+" seconds");
                            }

                            synchronized (RUNNING_MODULES) {
                                runningModules.remove(module);
                            }
                        }

                        public void finished(String module) {
                            boolean postProcessing = false;
                            ProcessState state = null;
                            synchronized (RUNNING_MODULES) {
                                state = runningModules.get(module);
                                if(!(state.module instanceof PostProcessor) && postProcessors.size()>0) {
                                    System.out.println(module+" finished, started to execute post processors after "+((System.currentTimeMillis()-state.started)/1000)+" seconds...");
                                    state.phase = ProcessState.Phase.POSTPROCESSING;
                                    postProcessing = true;
                                }else if(postProcessors.size()==0) {
                                    System.out.println(module+" finished after "+((System.currentTimeMillis()-state.started)/1000)+" seconds");
                                }
                            }

                            if(postProcessing) {
                                for (PostProcessor postProcessor : postProcessors.values()) {
                                    synchronized (RUNNING_MODULES) {
                                        state.currentPostProcessingModule = postProcessor.getId();
                                        runningModules.put(postProcessor.getId(), new ProcessState(postProcessor,ProcessState.Phase.EXECUTING));
                                    }
                                    postProcessor.execute(this);
                                }
                                System.out.println(module+" finished after "+((System.currentTimeMillis()-state.started)/1000)+" seconds");
                            }

                            synchronized (RUNNING_MODULES) {
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
                if(runningModules.get(module).phase == ProcessState.Phase.EXECUTING) {
                    runningModules.get(module).module.abort();
                }else if(runningModules.get(module).phase == ProcessState.Phase.POSTPROCESSING) {
                    runningModules.get(runningModules.get(module).currentPostProcessingModule).module.abort();
                }
            }
        }
        return true;
    }
}
