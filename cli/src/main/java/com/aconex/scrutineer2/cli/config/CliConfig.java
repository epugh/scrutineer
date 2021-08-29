package com.aconex.scrutineer2.cli.config;

import com.aconex.scrutineer2.ConnectorConfig;

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