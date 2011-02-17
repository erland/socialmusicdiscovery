package org.socialmusicdiscovery.rcp.prefs;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import org.socialmusicdiscovery.rcp.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_AUTOCONNECT, true);
		store.setDefault(PreferenceConstants.P_PORT, "9998");
		store.setDefault(PreferenceConstants.P_HOSTNAME, "localhost");
		store.setDefault(PreferenceConstants.P_LOCALSERVER_DIRECTORY, "smd-server-1.0-SNAPSHOT.jar");
		store.setDefault(PreferenceConstants.P_LOCALSERVER_DB, "derby-memory-test"); // -Dorg.socialmusicdiscovery.server.database=derby-memory-test
		store.setDefault(PreferenceConstants.P_LOCALSERVER_AUTOLAUNCH, false);
	}

}
