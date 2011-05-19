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

package org.socialmusicdiscovery.server.api.mediaimport;

/**
 * Callback interface that is used to keep track of the progress of an import operation
 */
public interface ProcessingStatusCallback {
    /**
     * Report current progress of a processing operation
     * @param module The identity of the processing module, this is the value returned from {@link MediaImporter#getId()} and {@link PostProcessor#getId()}
     * @param currentDescription The description of the current state of the import operation
     * @param currentNo The number of the current item being imported
     * @param totalNo The total number of items being imported
     */
    void progress(String module, String currentDescription, Long currentNo, Long totalNo);

    /**
     * Report that the processing operation has stopped due to an error
     * @param module The identity of the processing module, this is the value returned from {@link MediaImporter#getId()} and {@link PostProcessor#getId()}
     * @param error A description of the error that has occurred
     */
    void failed(String module, String error);

    /**
     * Report that the processing operation has finished with successful result
     * @param module The identity of the processing module, this is the value returned from {@link MediaImporter#getId()} and {@link PostProcessor#getId()}
     */
    void finished(String module);

    /**
     * Report that the processing operation has been aborted before it was completely finished
     * @param module The identity of the processing module, this is the value returned from {@link MediaImporter#getId()} and {@link PostProcessor#getId()}
     */
    void aborted(String module);
}
