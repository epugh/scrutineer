package com.aconex.scrutineer.jdbc;

import com.aconex.scrutineer.IdAndVersion;
import com.aconex.scrutineer.IdAndVersionStream;
import com.aconex.scrutineer.LogUtils;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

public class JdbcIdAndVersionStream implements IdAndVersionStream {

    private final DataSource dataSource;
    private final String sql;
    private Connection connection;
    private Statement statement;
    private static final Logger LOG = LogUtils.loggerForThisClass();
    private ResultSet resultSet;

    public JdbcIdAndVersionStream(DataSource dataSource, String sql) {
        this.dataSource = dataSource;
        this.sql = sql;
    }

    @Override
    public void open() {
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterator<IdAndVersion> iterator() {
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            return new IdAndVersionResultSetIterator(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void close() {

        SQLException sqlException = null;
        
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                sqlException = e;
                LogUtils.error(LOG, "Cannot close resultset",e);
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                sqlException = e;
                LogUtils.error(LOG, "Cannot close statement",e);
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                sqlException = e;
                LogUtils.error(LOG, "Cannot close connection",e);
            }
        }

        if (sqlException != null) {
            throw new RuntimeException(sqlException);
        }
    }

}
