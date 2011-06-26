package org.socialmusicdiscovery.server.business.service.browse;

import java.util.List;

/**
 * Represents a command implementation which can be executed through context browse menus
 */
public interface Command {
    /**
     * Execute the command on the specified object
     *
     * @param parameters The parameters for the command
     * @return The result of the executed command
     */
    CommandResult executeCommand(List<String> parameters);
}
