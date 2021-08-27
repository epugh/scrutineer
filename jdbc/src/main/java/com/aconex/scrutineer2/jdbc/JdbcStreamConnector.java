package com.aconex.scrutineer2.jdbc;

import com.aconex.scrutineer2.ConnectorConfig;
import com.aconex.scrutineer2.IdAndVersionFactory;
import com.aconex.scrutineer2.IdAndVersionStream;
import com.aconex.scrutineer2.IdAndVersionStreamConnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcStreamConnector implements IdAndVersionStreamConnector {
    private Connection connection;
    private final Config config;
    private IdAndVersionFactory idAndVersionFactory;

    public static class Config implements ConnectorConfig {
        private String jdbcUrl;
        private String driverClass;
        private String sql;
        private String user;
        private String password;

        public String getJdbcUrl() {
            return jdbcUrl;
        }

        public void setJdbcUrl(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
        }

        public String getDriverClass() {
            return driverClass;
        }

        public void setDriverClass(String driverClass) {
            this.driverClass = driverClass;
        }

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public IdAndVersionStreamConnector createConnector(IdAndVersionFactory idAndVersionFactory) {
            return new JdbcStreamConnector(this, idAndVersionFactory);
        }
    }
    public JdbcStreamConnector(Config config, IdAndVersionFactory idAndVersionFactory) {
        this.config = config;
        this.idAndVersionFactory = idAndVersionFactory;
    }

    @Override
    public IdAndVersionStream connect() {
        this.connection = initializeJdbcDriverAndConnection();
        return new JdbcIdAndVersionStream(connection, config.getSql(), idAndVersionFactory);
    }

    @Override
    public void close() {
        closeJdbcConnection();
    }

    private void closeJdbcConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection initializeJdbcDriverAndConnection() {
        validateDriverClass();
        try {
            return DriverManager.getConnection(config.getJdbcUrl(), config.getUser(), config.getPassword());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void validateDriverClass() {
        try {
            Class.forName(config.getDriverClass()).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
