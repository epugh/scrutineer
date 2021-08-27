package com.aconex.scrutineer2.v2.configconverter;

import com.aconex.scrutineer2.jdbc.JdbcStreamConnector;

import java.util.Map;

public class JdbcConfigConverter extends ConnectorConfigConverter{
        private static final String CONFIG_JDBC_DRIVER_CLASS = "jdbc.driver.class";
        private static final String CONFIG_JDBC_URL = "jdbc.url";
        private static final String CONFIG_JDBC_SQL = "jdbc.sql";
        private static final String CONFIG_JDBC_USER = "jdbc.user";
        private static final String CONFIG_JDBC_PASSWORD = "jdbc.password";

        @SuppressWarnings("PMD.NcssMethodCount")
        JdbcStreamConnector.Config convert(Map<String, String> props) {
            JdbcStreamConnector.Config config = new JdbcStreamConnector.Config();
            config.setDriverClass(getRequiredProperty(props, CONFIG_JDBC_DRIVER_CLASS));
            config.setJdbcUrl(getRequiredProperty(props, CONFIG_JDBC_URL));
            config.setSql(getRequiredProperty(props, CONFIG_JDBC_SQL));
            config.setUser(props.get(CONFIG_JDBC_USER));
            config.setPassword(props.get(CONFIG_JDBC_PASSWORD));
            return config;
        }
    }
