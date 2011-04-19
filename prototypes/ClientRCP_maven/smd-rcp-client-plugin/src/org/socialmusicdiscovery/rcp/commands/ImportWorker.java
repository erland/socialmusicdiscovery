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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.socialmusicdiscovery.rcp.Activator;
import org.socialmusicdiscovery.rcp.content.DataSource;
import org.socialmusicdiscovery.rcp.error.FatalApplicationException;
import org.socialmusicdiscovery.server.api.OperationStatus;
import org.socialmusicdiscovery.server.api.management.mediaimport.MediaImportStatus;

/**
 * Runs a server import. Designed to be called from some kind of {@link Job} or other 
 * task that runs a {@link IProgressMonitor}. 
 *  
 * @author Peer Törngren
 *
 */
public class ImportWorker {
	
	private final DataSource dataSource;
	private final String module;
	private final String taskName = "Importing media information";
	private final String name;
	
	public ImportWorker(String name, DataSource dataSource, String module) {
		this.dataSource = dataSource;
		this.module = module;
		this.name = name;
	}

	public IStatus run(IProgressMonitor monitor) {
		OperationStatus operationStatus = dataSource.startImport(module);
        Boolean startStatus = operationStatus.getSuccess();
        return startStatus!= null && startStatus ? monitorImport(monitor) : error();
	}
	
	private IStatus error() {
		RuntimeException e = new RuntimeException("Failed to start import. Is the server running? Is the server properly configured? Please check server log for details.");
		return new Status(Status.ERROR, Activator.PLUGIN_ID, name, e);
	}
	

	private IStatus monitorImport(IProgressMonitor monitor) {
    	try {
    		MediaImportStatus status = dataSource.getImportStatus(module);
			long totalWork = status.getTotalNumber().intValue();
			double lastPosition = 0;
			boolean isLoaded = false;
			
			String subTask = status.getCurrentDescription();
			monitor.beginTask("Initializing ...", IProgressMonitor.UNKNOWN);
			while (status != null) {
				totalWork = status.getTotalNumber();
				if (isLoaded) {
			        lastPosition = updateProgressCounter(monitor, status, lastPosition);
				} else if (totalWork>0) {
					isLoaded = startProgressCounter(monitor, totalWork);
				}
			    subTask = updateSubTask(monitor, status, lastPosition, subTask);
			    
			    pause(2000);
			    
			    if (monitor.isCanceled()) {
			    	dataSource.cancelImport(module);
					return Status.CANCEL_STATUS;
			    }
			    status = dataSource.getImportStatus(module);
			}
			monitor.done();
    	} finally {
    		dataSource.cancelImport(module);
    	}
        return Status.OK_STATUS;
	}

	private String updateSubTask(IProgressMonitor monitor, MediaImportStatus status, double position, String lastSubTask) {
		String currentSubTask = status.getCurrentDescription();
		
		if (currentSubTask.equals(lastSubTask)) {
			return lastSubTask;
		}
		
		int i = (int)position;
		monitor.subTask(i+": "+ currentSubTask);
		return currentSubTask;
	}

	private boolean startProgressCounter(IProgressMonitor monitor, long totalWork) {
		boolean isLoaded;
		isLoaded = true;
		String msg = taskName + " (" + totalWork +" entries)";
		monitor.beginTask(msg, (int) totalWork);
		return isLoaded;
	}

	private double updateProgressCounter(IProgressMonitor monitor, MediaImportStatus status, double lastPosition) {
		double currentPosition = status.getCurrentNumber();
		double worked = currentPosition - lastPosition;
		lastPosition = currentPosition;
		monitor.worked((int)worked);
		return lastPosition;
	}

	private void pause(long ms) {
		try {
		    Thread.sleep(ms);
		} catch (InterruptedException e) {
		    throw new FatalApplicationException(e);
		}
	}
}