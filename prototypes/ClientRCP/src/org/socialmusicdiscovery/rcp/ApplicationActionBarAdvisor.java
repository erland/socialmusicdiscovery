package org.socialmusicdiscovery.rcp;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    private IWorkbenchAction introAction;
	private IWorkbenchAction preferenceAction;
    
	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(IWorkbenchWindow window) {
		// built-in commands/actions needs to be created here? Cannot create thru extension point?
		// see http://www.eclipsezone.com/eclipse/forums/t113363.html

		preferenceAction = ActionFactory.PREFERENCES.create(window);
		register(preferenceAction);

		introAction = ActionFactory.INTRO.create(window);
		register(introAction);
	}

	protected void fillMenuBar(IMenuManager menuBar) {
		// menu is defined thru menu extension points in plugin.xml 
	}

}
