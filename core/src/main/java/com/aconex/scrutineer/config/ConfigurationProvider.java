package com.aconex.scrutineer.config;

import java.util.Map;

/**
 * Extension point for different implementations of scrutineer (and streams) configurations
 */
public interface ConfigurationProvider {
    default boolean numeric() {return false;}
    default boolean versionsAsTimestamps() { return false;}
    default boolean ignoreTimestampsDuringRun() {
        return false;
    }

    Map<String, String> getPrimaryConnectorConfigs();
    Map<String, String> getSecondaryConnectorConfigs();
}