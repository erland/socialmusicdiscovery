package org.socialmusicdiscovery.server.business.logic;

public interface TransactionManager {
    void begin();
    void setRollbackOnly();
    void end();
}
