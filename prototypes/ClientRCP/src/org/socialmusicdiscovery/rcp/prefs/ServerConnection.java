package org.socialmusicdiscovery.rcp.prefs;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.socialmusicdiscovery.rcp.Activator;
import org.eclipse.swt.widgets.Composite;

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

public class ServerConnection
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public ServerConnection() {
		super(GRID);
		setMessage("SMD Server Configuration");
		setTitle("SMD (Social Music Discovery)");
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Server connection setup.");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		{
			Composite composite = getFieldEditorParent();
			StringFieldEditor stringFieldEditor = new StringFieldEditor(PreferenceConstants.P_HOSTNAME, "&Host:", composite);
			stringFieldEditor.setErrorMessage("Must have a valid hostname.");
			stringFieldEditor.setEmptyStringAllowed(false);
			stringFieldEditor.getLabelControl(composite).setToolTipText("Set hostname where SMD server runs. Can be a name like 'localhost' or 'global.socialmusicdiscovery.org', or an IP number like '192.168.1.68'.");
			addField(
				stringFieldEditor);
		}
		{
			Composite composite = getFieldEditorParent();
			IntegerFieldEditor integerFieldEditor = new IntegerFieldEditor(PreferenceConstants.P_PORT, "&Port:", composite);
			integerFieldEditor.getLabelControl(composite).setToolTipText("Set port number that the SMD server listens to. Must be an integer number within the recommended range (1024-49151, see details on http://www.iana.org/assignments/port-numbers).");
			integerFieldEditor.setValidRange(1024, 49151);
			integerFieldEditor.setTextLimit(5);
			addField(integerFieldEditor);
		}
		addField(
			new BooleanFieldEditor(
				PreferenceConstants.P_AUTOCONNECT,
				"&Autoconnect",
				getFieldEditorParent()));
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