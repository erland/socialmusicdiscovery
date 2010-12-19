package org.socialmusicdiscovery.server.business.logic;

import com.google.inject.*;
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
     *
     * @param obj Object to inject attribute values in
     */
    public static void injectMembers(Object obj) {
        injector.injectMembers(obj);
    }

    /**
     * Get the instance represented by the specified name
     *
     * @param T    The interface of the instance to get
     * @param name The name of the instance
     * @return The instance represented by the specified name
     */
    public static <T> T instanceWithName(Class T, String name) {
        return (T) injector.getInstance(Key.get(T, Names.named(name)));
    }

    /**
     * Check if there are an instance represented by the specified name
     *
     * @param T    The interface of the instance to check
     * @param name The name of the instance
     * @return true if there is an instance with this name
     */
    public static <T> boolean existsWithName(Class T, String name) {
        List<Binding<T>> bindings = injector.findBindingsByType(TypeLiteral.get(T));
        for (Binding<T> binding : bindings) {
            if (binding.getKey().equals(Key.get(T, Names.named(name)))) {
                return true;
            }
        }
        return false;
    }
}
