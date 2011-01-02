package org.socialmusicdiscovery.server.plugins.json;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
import org.socialmusicdiscovery.server.api.plugin.AbstractPlugin;
import org.socialmusicdiscovery.server.api.plugin.PluginException;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class JSONPlugin extends AbstractPlugin {
    @Inject
    @Named("org.socialmusicdiscovery.server.port")
    String serverPort;

    /**
     * Web server thread
     */
    private SelectorThread threadSelector = null;

    @Override
    public boolean start() throws PluginException {
        if (serverPort == null) {
            InjectHelper.injectMembers(this);
        }
        Map<String, String> initParams = new HashMap<String, String>();
        initParams.put("com.sun.jersey.config.property.packages", "org.socialmusicdiscovery.server.api;org.socialmusicdiscovery.server.business.logic.jersey");
        initParams.put("com.sun.jersey.config.property.WadlGeneratorConfig", "org.socialmusicdiscovery.server.business.logic.jersey.SMDWadlGeneratorConfig");

        System.out.println("Starting grizzly...");
        URI uri = UriBuilder.fromUri("http://localhost/").port(Integer.parseInt(serverPort)).build();
        try {
            threadSelector = GrizzlyWebContainerFactory.create(uri, initParams);
        } catch (IOException e) {
            throw new PluginException(e);
        }
        System.out.println(String.format("Try out %sapplication.wadl", uri));
        return true;
    }

    @Override
    public void stop() throws PluginException {
        threadSelector.stopEndpoint();
    }
}
