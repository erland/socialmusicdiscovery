package org.socialmusicdiscovery.server.api.mediaimport;

/**
 * Callback interface that is used to keep track of the progress of an import operation
 */
public interface MediaImporterCallback {
    /**
     * Report current progress of an import operation
     * @param module The identity of the import module, this is the value returned from {@link MediaImporter#getId()}
     * @param currentDescription The description of the current state of the import operation
     * @param currentNo The number of the current item being imported
     * @param totalNo The total number of items being imported
     */
    void progress(String module, String currentDescription, Long currentNo, Long totalNo);

    /**
     * Report that the import operation has stopped due to an error
     * @param module The identity of the import module, this is the value returned from {@link MediaImporter#getId()}
     * @param error A description of the error that has occurred
     */
    void failed(String module, String error);

    /**
     * Report that the import operation has finished with successful result
     * @param module The identity of the import module, this is the value returned from {@link MediaImporter#getId()}
     */
    void finished(String module);

    /**
     * Report that the import operation has been aborted before it was completely finished
     * @param module The identity of the import module, this is the value returned from {@link MediaImporter#getId()}
     */
    void aborted(String module);
}