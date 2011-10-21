package com.aconex.scrutineer.jdbc;

import com.aconex.scrutineer.IdAndVersion;
import com.aconex.scrutineer.IdAndVersionStream;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

public class JdbcIdAndVersionStream implements IdAndVersionStream {

    private final DataSource dataSource;
    private final String sql;
    private Connection connection;
    private Statement statement;

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
            return new IdAndVersionResultSetIterator(statement.executeQuery(sql));
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
