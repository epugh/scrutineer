package com.aconex.scrutineer.v2.configconverter;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class ConnectorConfigConverter {
    protected String getRequiredProperty(Map<String, String> props, String propertyName) {
        return checkNotNull(props.get(propertyName), "Configuration for %s is required", propertyName);
    }
}
