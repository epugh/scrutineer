package com.aconex.scrutineer.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import com.aconex.scrutineer.IdAndVersionFactory;
import com.aconex.scrutineer.IdAndVersionStream;
import com.aconex.scrutineer.IdAndVersionStreamConnector;

public class JdbcStreamConnector implements IdAndVersionStreamConnector {
    private Connection connection;
    private JdbcConnectorConfig configs;

    @Override
    public void configure(Map<String, String> props) {
        this.configs = new JdbcConnectorConfig(props);
        tryInstantiateDriverClass(); // validation only
    }

    @Override
    public IdAndVersionStream connect(IdAndVersionFactory idAndVersionFactory) {
        this.connection = initializeJdbcDriverAndConnection();
        return new JdbcIdAndVersionStream(connection, configs.getSql(), idAndVersionFactory);
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
        tryInstantiateDriverClass();
        try {
            return DriverManager.getConnection(configs.getUrl(), configs.getUser(), configs.getPassword());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void tryInstantiateDriverClass() {
        try {
            Class.forName(configs.getDriverClass()).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
