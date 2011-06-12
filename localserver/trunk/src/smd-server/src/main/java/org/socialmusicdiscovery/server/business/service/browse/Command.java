package org.socialmusicdiscovery.server.business.service.browse;

/**
 * Represents a command implementation which can be executed through context browse menus
 */
public interface Command {
    /**
     * Execute the command on the specified object
     *
     * @param objectType Type of object
     * @param objectId   Identity of object
     * @return The result of the executed command
     */
    CommandResult executeCommand(String objectType, String objectId);
}
