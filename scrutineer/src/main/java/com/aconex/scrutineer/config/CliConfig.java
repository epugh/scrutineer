package com.aconex.scrutineer.config;

import com.aconex.scrutineer.ConnectorConfig;

/**
 * Extension point for different implementations of scrutineer (and streams) configurations
 */
public interface CliConfig {
    default boolean numeric() {return false;}
    default boolean versionsAsTimestamps() { return false;}
    default boolean ignoreTimestampsDuringRun() {
        return false;
    }

    ConnectorConfig getPrimaryConnectorConfig();
    ConnectorConfig getSecondaryConnectorConfig();
}