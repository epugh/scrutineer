package com.aconex.scrutineer.jdbc;

import com.aconex.scrutineer.IdAndVersion;
import com.aconex.scrutineer.IdAndVersionStream;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

public class JdbcIdAndVersionStream implements IdAndVersionStream {

    private final DataSource dataSource;
    private Connection connection;

    public JdbcIdAndVersionStream(DataSource dataSource) {
        this.dataSource = dataSource;
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
        return null;  
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
