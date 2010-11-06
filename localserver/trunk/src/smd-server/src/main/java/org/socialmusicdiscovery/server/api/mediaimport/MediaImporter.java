package org.socialmusicdiscovery.server.api.mediaimport;


/**
 * This interface has to be imported by all media importers.
 */
public interface MediaImporter {
    /**
     * Returns the unique identity of the import module, this is used when you want to issue a command to the import module
     * @return
     */
    String getId();

    /**
     * Called when the importer is supposed to execute its logic, the importer should use the provided callback interface to
     * report the progress of the operation
     * @param progressHandler A callback object which the import module should call to report the current status
     */
    void execute(MediaImporterCallback progressHandler);

    /**
     * Abort the current import operation in progress
     */
    void abort();
}
