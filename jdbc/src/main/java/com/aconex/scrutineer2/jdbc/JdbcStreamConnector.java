package com.aconex.scrutineer2.jdbc;

import com.aconex.scrutineer2.AbstractIdAndVersionStreamConnector;
import com.aconex.scrutineer2.ConnectorConfig;
import com.aconex.scrutineer2.IdAndVersionFactory;
import com.aconex.scrutineer2.IdAndVersionStream;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcStreamConnector extends AbstractIdAndVersionStreamConnector {
    private Connection connection;

    protected JdbcStreamConnector(ConnectorConfig connectorConfig, IdAndVersionFactory idAndVersionFactory) {
        super(connectorConfig, idAndVersionFactory);
    }

    public IdAndVersionStream fetchFromSource() {
        this.connection = initializeJdbcDriverAndConnection();
        return new JdbcIdAndVersionStream(connection, getConfig().getSql(), getIdAndVersionFactory());
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
            return DriverManager.getConnection(getConfig().getJdbcUrl(), getConfig().getUser(), getConfig().getPassword());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void validateDriverClass() {
        try {
            Class.forName(getConfig().getDriverClass()).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private JdbcConnectorConfig getConfig(){
        return (JdbcConnectorConfig) getConnectorConfig();
    }

}
