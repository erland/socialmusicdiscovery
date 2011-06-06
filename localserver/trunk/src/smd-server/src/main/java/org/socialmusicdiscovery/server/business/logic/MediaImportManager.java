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
import org.socialmusicdiscovery.server.business.logic.config.MappedConfigurationContext;
import org.socialmusicdiscovery.server.business.logic.config.MemoryConfigurationManager;
import org.socialmusicdiscovery.server.business.logic.config.MergedConfigurationManager;
import org.socialmusicdiscovery.server.business.logic.config.PersistentConfigurationManager;
import org.socialmusicdiscovery.server.business.logic.injections.StatisticsLogger;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameter;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameterEntity;

import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * The media import manager is a singleton that manage all media import and post processing modules, keeps their current state and offers
 * function to launch a specific importer.
 */
public class MediaImportManager {
    @Inject
    @Named("mediaimport")
    private ExecutorService executorService;

    @Inject
    @Named("default-value")
    private MemoryConfigurationManager defaultValueConfigurationManager;

    @Inject
    private StatisticsLogger statisticsLogger;

    /** Available media importer modules */
    private Map<String,MediaImporter> mediaImporters;

    /** Available post processing modules */
    private Map<String, PostProcessor> postProcessors;

    /** Currently executing media importer modules */
    private Map<String, ProcessState> runningModules = new HashMap<String, ProcessState>();

    /** Currently executing media importer modules which have been requested to be aborted */
    private Map<String, ProcessState> abortingModules = new HashMap<String, ProcessState>();

    /** Previously executing media importer modules which have failed */
    private Map<String, ProcessState> failedModules = new HashMap<String, ProcessState>();

    /** Previously executing media importer modules which have finished */
    private Map<String, ProcessState> finishedModules = new HashMap<String, ProcessState>();

    /** Synchronization object to ensure thread safety */
    private final Object RUNNING_MODULES = new Object();

    /**
     * Representation of the current state of an executing media importer module
     */
    static class ProcessState {
        ProcessingModule module;
        enum Phase {
            EXECUTING,
            POSTPROCESSING,
        };
        Phase phase;
        String currentPostProcessingModule;
        Long currentPostProcessingModuleNo=0L;
        Long started = System.currentTimeMillis();
        String currentDescription;
        Long lastUpdated = null;
        List<Long> processingTimes = new ArrayList<Long>(100);
        Long currentNo;
        Long totalNo;
        public ProcessState(ProcessState state) {
            this.module = state.module;
            this.phase = state.phase;
            this.currentPostProcessingModule = state.currentPostProcessingModule;
            this.currentPostProcessingModuleNo = state.currentPostProcessingModuleNo;
            this.started = state.started;
            this.currentDescription = state.currentDescription;
            this.lastUpdated = state.lastUpdated;
            this.processingTimes = new ArrayList<Long>(state.processingTimes);
            this.currentNo = state.currentNo;
            this.totalNo = state.totalNo;
        }
        public ProcessState(ProcessingModule module, Phase phase) {
            this.module = module;
            this.phase = phase;
            currentDescription = "";
            currentNo = 0L;
            totalNo = 0L;
            currentPostProcessingModuleNo=0L;
        }
    }

    /**
     * Constructs a new media import manager, this constructor should never be called directly instead you should create a member variable with
     * an {@link @Inject} annotation which will give you the one and only singleton instance
     * @param mediaImporters The list of media importers which should be managed
     * @param postProcessors The list of post processing modules which should be managed
     */
    public MediaImportManager(Map<String,MediaImporter> mediaImporters, Map<String,PostProcessor> postProcessors) {
        this.mediaImporters = mediaImporters;
        this.postProcessors = postProcessors;
        InjectHelper.injectMembers(this);

        for (MediaImporter mediaImporter : mediaImporters.values()) {
            Collection<ConfigurationParameter> defaultPluginConfiguration = mediaImporter.getDefaultConfiguration();
            String pluginConfigurationPath = "org.socialmusicdiscovery.server.plugins.mediaimport."+mediaImporter.getId()+".";

            Set<ConfigurationParameter> defaultConfiguration = new HashSet<ConfigurationParameter>();
            for (ConfigurationParameter parameter : defaultPluginConfiguration) {
                ConfigurationParameterEntity entity = new ConfigurationParameterEntity(parameter);
                if(!entity.getId().startsWith(pluginConfigurationPath)) {
                    entity.setId(pluginConfigurationPath+entity.getId());
                }
                entity.setDefaultValue(true);
                defaultConfiguration.add(entity);
            }
            defaultValueConfigurationManager.setParametersForPath(pluginConfigurationPath, defaultConfiguration);

            mediaImporter.setConfiguration(new MappedConfigurationContext(pluginConfigurationPath, new MergedConfigurationManager(new PersistentConfigurationManager())));
        }
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
                    return new MediaImportStatus(state.module.getId(),state.currentDescription,state.currentNo,state.totalNo, 1L, 1L+postProcessors.size(),MediaImportStatus.Status.Running);
                }else if(state.phase == ProcessState.Phase.POSTPROCESSING) {
                    ProcessState postProcessState = runningModules.get(state.currentPostProcessingModule);
                    if(abortingModules.containsKey(module)) {
                        return new MediaImportStatus(postProcessState.module.getId(),postProcessState.currentDescription,postProcessState.currentNo,postProcessState.totalNo, 1L+state.currentPostProcessingModuleNo, 1L+postProcessors.size(), MediaImportStatus.Status.Aborting);
                    }else {
                        return new MediaImportStatus(postProcessState.module.getId(),postProcessState.currentDescription,postProcessState.currentNo,postProcessState.totalNo, 1L+state.currentPostProcessingModuleNo,1L+postProcessors.size(),MediaImportStatus.Status.Running);
                    }
                }
            }else {
                if(failedModules.containsKey(module)) {
                    ProcessState failedState = failedModules.get(module);
                    return new MediaImportStatus(failedState.module.getId(),failedState.currentDescription, failedState.currentNo, failedState.totalNo,1L+failedState.currentPostProcessingModuleNo, 1L+postProcessors.size(),MediaImportStatus.Status.Failed);
                }else if(finishedModules.containsKey(module)) {
                    ProcessState finishedState = finishedModules.get(module);
                    return new MediaImportStatus(finishedState.module.getId(), finishedState.currentDescription, finishedState.currentNo, finishedState.totalNo, 1L+postProcessors.size(),1L+postProcessors.size(), MediaImportStatus.Status.FinishedOk);
                }else {
                    return new MediaImportStatus(module, "", 0L, 0L, 0L, 1L+postProcessors.size(),MediaImportStatus.Status.FinishedOk);
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
    public boolean startImport(final String module, final Map<String,String> parameters) {
        if(module == null || !mediaImporters.containsKey(module) || runningModules.containsKey(module)) {
            return false;
        }
        synchronized (RUNNING_MODULES) {
            runningModules.put(module, new ProcessState(mediaImporters.get(module), ProcessState.Phase.EXECUTING));
            abortingModules.remove(module);
            failedModules.remove(module);
        }
        executorService.submit(new Runnable() {
            public void run() {
                try {
                    statisticsLogger.start();
                    mediaImporters.get(module).init(parameters);
                    mediaImporters.get(module).execute(new ProcessingStatusCallback() {
                        public void progress(String module, String currentDescription, Long currentNo, Long totalNo) {
                            long processingTime;
                            long avgProcessingTime;
                            long currentTime = System.currentTimeMillis();
                            synchronized (RUNNING_MODULES) {
                                if(runningModules.get(module).lastUpdated == null) {
                                    processingTime = currentTime-runningModules.get(module).started;
                                }else {
                                    processingTime = currentTime-runningModules.get(module).lastUpdated;
                                }
                                if(runningModules.get(module).processingTimes.size()<=currentNo%100) {
                                    runningModules.get(module).processingTimes.add(processingTime);
                                }else {
                                    runningModules.get(module).processingTimes.set((int)(currentNo%100),processingTime);
                                }
                                avgProcessingTime = 0;
                                int count = 0;
                                for (Long time : runningModules.get(module).processingTimes) {
                                    if(time==null) {
                                        break;
                                    }
                                    avgProcessingTime += time;
                                    count++;
                                }
                                avgProcessingTime = avgProcessingTime/count;
                                runningModules.get(module).lastUpdated = currentTime;
                                runningModules.get(module).currentDescription = currentDescription;
                                runningModules.get(module).currentNo= currentNo;
                                runningModules.get(module).totalNo= totalNo;
                            }
                            if(totalNo>10000 && currentNo>50 && avgProcessingTime<10000/100) {
                                if(currentNo%100==0 || currentNo.equals(totalNo)) {
                                    System.out.println(module+" ("+currentNo+" of "+totalNo+", "+avgProcessingTime+" msec/item): "+currentDescription);
                                }
                            }else if(totalNo>1000 && currentNo>5 && avgProcessingTime<10000/10) {
                                if(currentNo%10==0 || currentNo.equals(totalNo)) {
                                    System.out.println(module+" ("+currentNo+" of "+totalNo+", "+avgProcessingTime+" msec/item): "+currentDescription);
                                }
                            }else {
                                System.out.println(module+" ("+currentNo+" of "+totalNo+", "+avgProcessingTime+" msec/item): "+currentDescription);
                            }
                        }

                        public void failed(String module, String error) {
                            statisticsLogger.finish();
                            synchronized (RUNNING_MODULES) {
                                System.out.println(module+" failure after "+((System.currentTimeMillis()-runningModules.get(module).started)/1000)+" seconds");
                                failedModules.put(module, runningModules.get(module));
                                runningModules.remove(module);
                            }
                        }

                        public void aborted(String module) {
                            statisticsLogger.finish();
                            boolean postProcessing = false;
                            ProcessState state = null;
                            synchronized (RUNNING_MODULES) {
                                state = runningModules.get(module);
                                if(!(state.module instanceof PostProcessor) && postProcessors.size()>0) {
                                    abortingModules.put(module, new ProcessState(state));
                                    System.out.println(module+" aborted, started to execute post processors after "+((System.currentTimeMillis()-runningModules.get(module).started)/1000)+" seconds...");
                                    state.phase = ProcessState.Phase.POSTPROCESSING;
                                    postProcessing = true;
                                }else if(postProcessors.size()==0) {
                                    failedModules.put(module, new ProcessState(state));
                                    System.out.println(module+" aborted after "+((System.currentTimeMillis()-runningModules.get(module).started)/1000)+" seconds");
                                }
                            }
                            if(postProcessing) {
                                state.currentPostProcessingModuleNo = 0L;
                                for (PostProcessor postProcessor : postProcessors.values()) {
                                    synchronized (RUNNING_MODULES) {
                                        state.currentPostProcessingModule = postProcessor.getId();
                                        state.currentPostProcessingModuleNo++;
                                        runningModules.put(postProcessor.getId(), new ProcessState(postProcessor,ProcessState.Phase.EXECUTING));
                                    }
                                    statisticsLogger.start();
                                    postProcessor.init(parameters);
                                    postProcessor.execute(this);
                                }
                                failedModules.put(module, abortingModules.remove(module));
                                System.out.println(module+" aborted after "+((System.currentTimeMillis()-runningModules.get(module).started)/1000)+" seconds");
                            }

                            synchronized (RUNNING_MODULES) {
                                runningModules.remove(module);
                            }
                        }

                        public void finished(String module) {
                            statisticsLogger.finish();
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
                                    state.currentPostProcessingModuleNo = 0L;
                                    synchronized (RUNNING_MODULES) {
                                        state.currentPostProcessingModule = postProcessor.getId();
                                        state.currentPostProcessingModuleNo++;
                                        runningModules.put(postProcessor.getId(), new ProcessState(postProcessor,ProcessState.Phase.EXECUTING));
                                    }
                                    statisticsLogger.start();
                                    postProcessor.init(parameters);
                                    postProcessor.execute(this);
                                }
                                System.out.println(module+" finished after "+((System.currentTimeMillis()-state.started)/1000)+" seconds");
                            }

                            synchronized (RUNNING_MODULES) {
                                finishedModules.put(module, runningModules.remove(module));
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
