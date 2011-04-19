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

package org.socialmusicdiscovery.rcp.commands;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.socialmusicdiscovery.rcp.content.DataSource;
import org.socialmusicdiscovery.rcp.error.FatalApplicationException;
import org.socialmusicdiscovery.server.api.OperationStatus;
import org.socialmusicdiscovery.server.api.management.mediaimport.MediaImportStatus;

/**
 * Runs a server import task. 
 * TODO run as Job, allow background mode 
 *  
 * @author Peer Törngren
 *
 */
public class ImportJob implements IRunnableWithProgress {
	
	private final DataSource dataSource;
	private final String module;
	private final String taskName = "Importing media information";
	
	public ImportJob(DataSource dataSource, String module) {
		this.dataSource = dataSource;
		this.module = module;
	}

	@Override
	public void run(IProgressMonitor monitor) {
        OperationStatus operationStatus = dataSource.startImport(module);
        Boolean startStatus = operationStatus.getSuccess();
        if (startStatus != null && startStatus) {
            monitorImport(monitor);
        } else {
        	throw new RuntimeException("Failed to start import. Is the server running? Is the server properly configured? Please check server log for details.");
        }
	}
	
	private void monitorImport(IProgressMonitor monitor) {
		MediaImportStatus status = dataSource.getImportStatus(module);
		long totalWork = status.getTotalNumber().intValue();
		double lastPosition = 0;
        boolean isLoaded = false;
        String subTask = status.getCurrentDescription();
        
		monitor.beginTask("Initializing ...", IProgressMonitor.UNKNOWN);
		while (status != null) {
			totalWork = status.getTotalNumber();
			
			if (isLoaded) {
	            double currentPosition = status.getCurrentNumber();
	            double worked = currentPosition - lastPosition;
	            lastPosition = currentPosition;
		        monitor.worked((int)worked);
			} else if (totalWork>0) {
				isLoaded = true;
				String msg = taskName + " (" + totalWork +" entries)";
				monitor.beginTask(msg, (int) totalWork);
			}
			
	        if (!subTask.equals(status.getCurrentDescription())) {
	        	subTask = status.getCurrentDescription();
	        	int i = (int)lastPosition;
				monitor.subTask(i+": "+ subTask);
		    }
	        
		    try {
		        Thread.sleep(2000);
		    } catch (InterruptedException e) {
		        throw new FatalApplicationException(e);
		    }
		    if (monitor.isCanceled()) {
		    	dataSource.cancelImport(module);
		    	break;
		    }
		    status = dataSource.getImportStatus(module);
		}
		monitor.done();
	}


//	/**
//     * Start a media import
//     *
//     * @param module The import module to use
//     */
//    private void startImport() {
////        if (importTask == null) {
////            importAborted = false;
//            OperationStatus operationStatus = dataSource.startImport();
//            Boolean startStatus = operationStatus.getSuccess();
//			if (startStatus!= null && startStatus.booleanValue()) {
//                MediaImportStatus status = dataSource.getImportStatus();
//                while (status != null) {
//                    if (status.getTotalNumber() > 0) {
////                        importProgressMeter.setPercentage((double) status.getCurrentNumber() / status.getTotalNumber());
////                        importProgressMeter.setText(status.getCurrentNumber() + " of " + status.getTotalNumber());
//                    }
////                    importProgressDescription.setText(status.getCurrentDescription());
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        throw new FatalApplicationException(e);
//                    }
//                    status = dataSource.getImportStatus();
//                }
//            }
////        }
//    }
//
//    /**
//     * Start a background thread responsible to update the progress bar for an import operation in progress
//     *
//     * @param module Import module to show in the progress bar
//     */
//    private void startImportProgressBar(final String module) {
//        if (importTask == null) {
//            importTask = new Task<Void>() {
//                @Override
//                public Void execute() throws TaskExecutionException {
//                    try {
//                        importButton.setButtonData(resources.getString("SMDCRUDSearchWindow.abortButton"));
//                        importProgressMeter.setPercentage(0);
//                        importProgressMeter.setText("");
//                        importProgressMeter.setVisible(true);
//                        MediaImportStatus status = Client.create(config).resource(HOSTURL + "/mediaimportmodules/" + module).accept(MediaType.APPLICATION_JSON).get(MediaImportStatus.class);
//                        while (status != null) {
//                            if (status.getTotalNumber() > 0) {
//                                importProgressMeter.setPercentage((double) status.getCurrentNumber() / status.getTotalNumber());
//                                importProgressMeter.setText(status.getCurrentNumber() + " of " + status.getTotalNumber());
//                            }
//                            importProgressDescription.setText(status.getCurrentDescription());
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                throw new TaskExecutionException(e);
//                            }
//                            status = Client.create(config).resource(HOSTURL + "/mediaimportmodules/" + module).accept(MediaType.APPLICATION_JSON).get(MediaImportStatus.class);
//                        }
//                    } catch (UniformInterfaceException e) {
//                        if (e.getResponse().getStatus() != 204) {
//                            throw e;
//                        }
//                    }
//                    return null;
//                }
//            };
//            importTask.execute(new TaskAdapter<Void>(new TaskListener<Void>() {
//                @Override
//                public void taskExecuted(Task<Void> task) {
//                    importProgressMeter.setPercentage(0);
//                    importProgressMeter.setText("");
//                    importProgressMeter.setVisible(false);
//                    importButton.setButtonData(resources.getString("SMDCRUDSearchWindow.importButton"));
//                    if (importAborted) {
//                        importProgressDescription.setText("Import aborted");
//                    } else {
//                        importProgressDescription.setText("Import finished");
//                    }
//                    importTask = null;
//                }
//
//                @Override
//                public void executeFailed(Task<Void> task) {
//                    importProgressMeter.setPercentage(0);
//                    importProgressMeter.setText("");
//                    importProgressMeter.setVisible(false);
//                    importButton.setButtonData(resources.getString("SMDCRUDSearchWindow.importButton"));
//                    importProgressDescription.setText("Import failed");
//                    importTask = null;
//                }
//            }));
//        }
//    }

//    /**
//     * Abort an import operation in progress
//     *
//     * @param module Import module to abort
//     */
//    public void abortImport(String module) {
//        if (importTask != null) {
//            importAborted = true;
//            Client.create(config).resource(HOSTURL + "/mediaimportmodules/" + module).delete();
//        }
//    }

}
