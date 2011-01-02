package org.socialmusicdiscovery.server.plugins.searchrelationupdater;

import org.socialmusicdiscovery.server.api.mediaimport.ProcessingStatusCallback;
import org.socialmusicdiscovery.server.api.plugin.AbstractPlugin;
import org.socialmusicdiscovery.server.api.plugin.Plugin;
import org.socialmusicdiscovery.server.api.plugin.PluginException;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.logic.SearchRelationPostProcessor;

public class SearchRelationUpdaterPlugin extends AbstractPlugin {
    @Override
    public int getStartPriority() {
        return Plugin.START_PRIORITY_EARLY + 1;
    }

    @Override
    public boolean start() throws PluginException {
        String database = InjectHelper.instanceWithName(String.class, "org.socialmusicdiscovery.server.database");

        String forcedUpdateOfSearchRelations = System.getProperty("org.socialmusicdiscovery.server.searchrelations");
        if ((database != null && (database.endsWith("-test"))) || (forcedUpdateOfSearchRelations != null && forcedUpdateOfSearchRelations.equalsIgnoreCase("true"))) {
            System.out.println("Starting to update search relations...");
            new SearchRelationPostProcessor().execute(new ProcessingStatusCallback() {
                public void progress(String module, String currentDescription, Long currentNo, Long totalNo) {
                    System.out.println(currentNo + " of " + totalNo + ": " + currentDescription);
                }

                public void failed(String module, String error) {
                    System.err.println("Failed with error: " + error);
                }

                public void finished(String module) {
                    System.out.println("Finish updating search relations");
                }

                public void aborted(String module) {
                }
            });
        }
        return false;
    }
}
