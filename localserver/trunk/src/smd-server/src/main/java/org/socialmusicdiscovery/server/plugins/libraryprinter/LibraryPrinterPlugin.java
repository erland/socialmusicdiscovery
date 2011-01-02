package org.socialmusicdiscovery.server.plugins.libraryprinter;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.api.plugin.AbstractPlugin;
import org.socialmusicdiscovery.server.api.plugin.Plugin;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.model.core.ReleaseEntity;
import org.socialmusicdiscovery.server.business.repository.core.ReleaseRepository;

import java.util.Collection;

public class LibraryPrinterPlugin extends AbstractPlugin {
    @Inject
    ReleaseRepository releaseRepository;

    @Override
    public int getStartPriority() {
        return Plugin.START_PRIORITY_LATE - 1;
    }

    @Override
    public boolean start() {
        if (releaseRepository == null) {
            InjectHelper.injectMembers(this);
        }
        Collection<ReleaseEntity> releases = releaseRepository.findAll();
        if (releases.size() > 0) {
            System.out.println("\nFound " + releases.size() + " releases in database");
            //System.out.println("\nPrinting all available releases in database, please wait...\n");
        } else {
            System.out.println("\nNo releases available in database!");
        }
        return false;
    }
}
