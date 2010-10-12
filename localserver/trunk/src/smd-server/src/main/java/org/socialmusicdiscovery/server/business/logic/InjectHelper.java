package org.socialmusicdiscovery.server.business.logic;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class InjectHelper {
    private static Injector injector = Guice.createInjector(new JPAModule());
    public static void injectMembers(Object obj) {
        injector.injectMembers(obj);
    }
}
