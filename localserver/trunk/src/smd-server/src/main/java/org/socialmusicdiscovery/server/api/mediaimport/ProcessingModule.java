package org.socialmusicdiscovery.server.api.mediaimport;

public interface ProcessingModule {
    /**
     * Returns the unique identity of the processing module, this is used when you want to issue a command to the processing module
     * @return
     */
    String getId();

    /**
     * Called when the processing module is supposed to execute its logic, the module should use the provided callback interface to
     * report the progress of the operation
     * @param progressHandler A callback object which the processing module should call to report the current status
     */
    void execute(ProcessingStatusCallback progressHandler);

    /**
     * Abort the current processing operation in progress
     */
    void abort();
}
