package org.socialmusicdiscovery.server.business.logic;

import org.socialmusicdiscovery.server.business.service.browse.MenuLevelCommand;
import org.testng.annotations.Test;

public class MenuLevelTest {

    @Test
    public void testClientTypesNotDefined() {
        MenuLevelCommand level = new MenuLevelCommand("test","Test");
        assert level.isVisibleForClientType("computer");
        assert level.isVisibleForClientType(null);
    }

    @Test
    public void testClientTypesIncluded() {
        MenuLevelCommand level = new MenuLevelCommand("test","Test");
        level.getIncludedClientTypes().add("computer.web");

        assert !level.isVisibleForClientType("squeezebox");
        assert !level.isVisibleForClientType("computer");
        assert !level.isVisibleForClientType(null);
        assert level.isVisibleForClientType("computer.web");
        assert level.isVisibleForClientType("computer.web.default");
    }

    @Test
    public void testClientTypesExcluded() {
        MenuLevelCommand level = new MenuLevelCommand("test","Test");
        level.getExcludedClientTypes().add("computer.web");

        assert level.isVisibleForClientType("squeezebox");
        assert level.isVisibleForClientType("computer");
        assert level.isVisibleForClientType(null);
        assert !level.isVisibleForClientType("computer.web");
        assert !level.isVisibleForClientType("computer.web.default");
    }

    @Test
    public void testClientTypesIncludedWithExceptions() {
        MenuLevelCommand level = new MenuLevelCommand("test","Test");
        level.getIncludedClientTypes().add("computer");
        level.getExcludedClientTypes().add("computer.web");

        assert !level.isVisibleForClientType("squeezebox");
        assert level.isVisibleForClientType("computer");
        assert level.isVisibleForClientType("computer.rich");
        assert !level.isVisibleForClientType(null);
        assert !level.isVisibleForClientType("computer.web");
        assert !level.isVisibleForClientType("computer.web.default");
    }

    @Test
    public void testClientTypesExcludedWithExceptions() {
        MenuLevelCommand level = new MenuLevelCommand("test","Test");
        level.getExcludedClientTypes().add("computer");
        level.getIncludedClientTypes().add("computer.web");

        assert !level.isVisibleForClientType("squeezebox");
        assert !level.isVisibleForClientType("computer");
        assert !level.isVisibleForClientType("computer.rich");
        assert !level.isVisibleForClientType(null);
        assert level.isVisibleForClientType("computer.web");
        assert level.isVisibleForClientType("computer.web.default");
    }
}
