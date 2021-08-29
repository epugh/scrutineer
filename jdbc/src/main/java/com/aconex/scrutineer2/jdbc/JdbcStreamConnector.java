package com.aconex.scrutineer2.jdbc;

import com.aconex.scrutineer2.AbstractIdAndVersionStreamConnector;
import com.aconex.scrutineer2.ConnectorConfig;
import com.aconex.scrutineer2.IdAndVersion;
import com.aconex.scrutineer2.IdAndVersionFactory;
import com.aconex.scrutineer2.IdAndVersionStream;
import com.aconex.scrutineer2.LogUtils;
import com.aconex.scrutineer2.javautil.JavaIteratorIdAndVersionStream;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

public class JdbcStreamConnector extends AbstractIdAndVersionStreamConnector {
    private static final Logger LOG = LogUtils.loggerForThisClass();
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    protected JdbcStreamConnector(ConnectorConfig connectorConfig, IdAndVersionFactory idAndVersionFactory) {
        super(connectorConfig, idAndVersionFactory);
    }

    public IdAndVersionStream fetchFromSource() {
        this.connection = initializeJdbcDriverAndConnection();
        long begin = System.currentTimeMillis();
        Iterator<IdAndVersion> iterator = createIterator();
        LogUtils.info(LOG, "Executed JDBC query in %dms", (System.currentTimeMillis() - begin));
        return new JavaIteratorIdAndVersionStream(iterator);
    }

    private Iterator<IdAndVersion> createIterator() {
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(getConfig().getSql());
            return new IdAndVersionResultSetIterator(resultSet, getIdAndVersionFactory());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        closeQuietly(resultSet);
        closeQuietly(statement);
        closeQuietly(connection);
    }

    private void closeQuietly(AutoCloseable resource) {
        try {
            if (resource != null) {
                resource.close();
            }
        } catch (Exception e) {
            LOG.warn("Failed to close resource: "+resource);
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
