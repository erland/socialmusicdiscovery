package org.socialmusicdiscovery.rcp.prefs;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.socialmusicdiscovery.rcp.Activator;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class LocalServer
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public LocalServer() {
		super(GRID);
		setMessage("Local Server Configuration");
		setTitle("Local Server");
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Define how to launch a local (test) server. Working directory is the \"home\" of the server, and the DB is expected to be located in this directory. The \"DB property\" is set as the system 'DB' property (currently 'org.socialmusicdiscovery.server.database').");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		DirectoryFieldEditor pathEditor = new DirectoryFieldEditor(PreferenceConstants.P_LOCALSERVER_DIRECTORY, "Working &Directory", getFieldEditorParent());
		addField(pathEditor);
		addField(new StringFieldEditor(PreferenceConstants.P_LOCALSERVER_DB, "DB property:", -1, StringFieldEditor.VALIDATE_ON_KEY_STROKE, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_LOCALSERVER_AUTOLAUNCH, "Auto-launch when client starts", BooleanFieldEditor.DEFAULT, getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	public static String getString(String key) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getString(key);
	}
	
}