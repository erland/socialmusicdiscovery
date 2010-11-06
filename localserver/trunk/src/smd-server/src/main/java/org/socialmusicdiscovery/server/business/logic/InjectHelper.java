package org.socialmusicdiscovery.server.business.logic;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

public class InjectHelper {
    private static Injector injector = null;
    static {
        Iterator<AbstractModule> moduleIterator = ServiceLoader.load(AbstractModule.class).iterator();
        List<AbstractModule> modules = new ArrayList<AbstractModule>();
        while (moduleIterator.hasNext()) {
            modules.add(moduleIterator.next());
        }
        injector = Guice.createInjector(modules);
    }

    /**
     * Inject member variables in the specified object
     * @param obj Object to inject attribute values in
     */
    public static void injectMembers(Object obj) {
        injector.injectMembers(obj);
    }

    /**
     * Get the instance represented by the specified name
     * @param T The interface of the instance to get
     * @param name The name of the instance
     * @return The instance represented by the specified name
     */
    public static <T> T instanceWithName(Class T, String name) {
        return (T)injector.getInstance(Key.get(T, Names.named(name)));
    }
}
