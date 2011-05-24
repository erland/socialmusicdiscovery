package org.socialmusicdiscovery.server.business.logic.injections;

/**
 * Interface of a statistics logger which is able to collect statistics over a period and log the result at the end of the period
 */
public interface StatisticsLogger {
    /**
     * Starts collection of statistics
     */
    void start();

    /**
     * Finish collection of statistics and log the result appropriately
     */
    void finish();
}
