package org.socialmusicdiscovery.frontend;

import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.*;
import org.apache.pivot.wtkx.WTKXSerializer;
import org.socialmusicdiscovery.frontend.injections.PropertiesModule;

import java.util.Locale;

public class SMDApplication implements Application {
    private SMDApplicationWindow window = null;

    public static final String CALLBACK_KEY = "dispatcher";
    public static final String LANGUAGE_PROPERTY_NAME = "language";

    @Override
    public void startup(Display display, Map<String, String> properties)
            throws Exception {

        String language = properties.get(LANGUAGE_PROPERTY_NAME);
        if (language != null) {
            Locale.setDefault(new Locale(language));
        }
        PropertiesModule.init(properties);
        Resources resources = new Resources(SMDApplication.class.getName());
        WTKXSerializer wtkxSerializer = new WTKXSerializer(resources);
        window = (SMDApplicationWindow) wtkxSerializer.readObject(this, "SMDApplicationWindow.wtkx");
        window.open(display);
    }

    @Override
    public boolean shutdown(boolean optional) {
        if (window != null) {
            window.close();
        }

        return false;
    }

    @Override
    public void suspend() {
    }

    @Override
    public void resume() {
    }

    public static void main(String[] args) {
        DesktopApplicationContext.main(SMDApplication.class, args);
    }
}