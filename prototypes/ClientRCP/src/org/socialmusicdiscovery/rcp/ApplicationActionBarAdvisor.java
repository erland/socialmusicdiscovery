package org.socialmusicdiscovery.rcp;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.socialmusicdiscovery.rcp.actions.DataSourceLoadAction;
import org.socialmusicdiscovery.rcp.actions.LaunchLocalServerAction;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    private IWorkbenchAction introAction;
	private IWorkbenchAction preferenceAction;
	private IWorkbenchAction reloadAction;
	private IWorkbenchAction aboutAction;
	private LaunchLocalServerAction launchAction;
    
	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(IWorkbenchWindow window) {
		reloadAction = new DataSourceLoadAction();
		register(reloadAction);

		launchAction = new LaunchLocalServerAction();
		register(launchAction);
		
		preferenceAction = ActionFactory.PREFERENCES.create(window);
		register(preferenceAction);

		introAction = ActionFactory.INTRO.create(window);
		register(introAction);
		
		aboutAction = ActionFactory.ABOUT.create(window);
		register(aboutAction);

	}

	protected void fillMenuBar(IMenuManager menuBar) {
		{ // Server 
			MenuManager menu = new MenuManager("&Server", "Server");
			menuBar.add(menu);
			menu.add(reloadAction);
			menu.add(launchAction);
		}
		{ // Tools 
			MenuManager menu = new MenuManager("&Tools", "Tools");
			menuBar.add(menu);
			menu.add(preferenceAction);
			}
		{ // Help
			MenuManager menu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
			menuBar.add(menu);
			menu.add(introAction);
			menu.add(aboutAction);
		}
	}

}
