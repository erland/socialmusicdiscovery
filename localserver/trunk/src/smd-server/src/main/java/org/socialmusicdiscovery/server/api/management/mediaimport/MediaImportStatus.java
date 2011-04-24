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

package org.socialmusicdiscovery.server.api.management.mediaimport;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class MediaImportStatus {
    public enum Status {
        /** Import module is currently executing */
        Running,
        /** Import module is still executing but is trying to abort, will change state to {@link #Failed} when aborted successfully */
        Aborting,
        /** Import module is not running or have finished successfully */
        FinishedOk,
        /** Import module has failed or been aborted and is no longer executing */
        Failed,
    }

    /** Identity of import module/phase currently executing */
    @Expose
    private String module;

    /** Description about currently processed item */
    @Expose
    private String currentDescription;

    /** Sequence number of currently processed item within the current phase */
    @Expose
    private Long currentNumber;

    /** Total number of items which are going to be processed within the current phase */
    @Expose
    private Long totalNumber;

    /** The sequence number of currently executing import phase */
    @Expose
    Long currentPhaseNo;

    /** Total number of phases which are going to be executed */
    @Expose
    Long totalPhaseNo;

    /** Current status of this import */
    @Expose
    private Status status;

    public MediaImportStatus() {}
    public MediaImportStatus(String module, String currentDescription, Long currentNumber, Long totalNumber, Long currentPhaseNo, Long totalPhaseNo, Status status) {
        this.module = module;
        this.currentDescription = currentDescription;
        this.currentNumber = currentNumber;
        this.totalNumber = totalNumber;
        this.status = status;
        this.currentPhaseNo = currentPhaseNo;
        this.totalPhaseNo= totalPhaseNo;
    }
    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getCurrentDescription() {
        return currentDescription;
    }

    public void setCurrentDescription(String currentDescription) {
        this.currentDescription = currentDescription;
    }

    public Long getCurrentNumber() {
        return currentNumber;
    }

    public void setCurrentNumber(Long currentNumber) {
        this.currentNumber = currentNumber;
    }

    public Long getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(Long totalNumber) {
        this.totalNumber = totalNumber;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
