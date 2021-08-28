package com.aconex.scrutineer2.jdbc;

import com.aconex.scrutineer2.IdAndVersionFactory;
import com.aconex.scrutineer2.IdAndVersionStream;
import com.aconex.scrutineer2.IdAndVersionStreamConnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcStreamConnector implements IdAndVersionStreamConnector {
    private Connection connection;
    private final JdbcConnectorConfig config;
    private IdAndVersionFactory idAndVersionFactory;

    public JdbcStreamConnector(JdbcConnectorConfig config, IdAndVersionFactory idAndVersionFactory) {
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
