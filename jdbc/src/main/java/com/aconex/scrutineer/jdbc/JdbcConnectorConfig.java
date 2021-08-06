package com.aconex.scrutineer.jdbc;

import java.util.Map;

import com.aconex.scrutineer.config.ConnectorConfig;

public class JdbcConnectorConfig extends ConnectorConfig {
    public static final String CONFIG_JDBC_DRIVER_CLASS = "jdbc.driver.class";
    public static final String CONFIG_JDBC_URL = "jdbc.url";
    public static final String CONFIG_JDBC_SQL = "jdbc.sql";
    public static final String CONFIG_JDBC_USER = "jdbc.user";
    public static final String CONFIG_JDBC_PASSWORD = "jdbc.password";

    public JdbcConnectorConfig(Map<String, String> props) {
        super(props);
    }

    public String getSql() {
        return get(CONFIG_JDBC_SQL);
    }

    public String getUrl() {
        return get(CONFIG_JDBC_URL);
    }

    public String getUser() {
        return get(CONFIG_JDBC_USER);
    }

    public String getPassword() {
        return get(CONFIG_JDBC_PASSWORD);
    }

    public String getDriverClass() {
        return get(CONFIG_JDBC_DRIVER_CLASS);
    }
}
