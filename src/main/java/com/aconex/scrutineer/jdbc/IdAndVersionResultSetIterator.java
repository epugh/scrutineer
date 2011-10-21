package com.aconex.scrutineer.jdbc;

import com.aconex.scrutineer.IdAndVersion;
import org.apache.commons.lang.NotImplementedException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class IdAndVersionResultSetIterator implements Iterator<IdAndVersion> {

    private final ResultSet resultSet;

    private IdAndVersion current;

    public IdAndVersionResultSetIterator(ResultSet resultSet) {
        this.resultSet = resultSet;
        nextRow();
    }

    @Override
    public boolean hasNext() {
        return current != null;
    }

    @Override
    public IdAndVersion next() {
        try {
            return current;
        }
        finally {
            nextRow();
        }
    }

    @Override
    public void remove() {
        throw new NotImplementedException();
    }

    private void nextRow() {
        try {
            if (resultSet.next()) {
                current = new IdAndVersion(resultSet.getString(1), resultSet.getLong(2));
            }
            else {
                current = null;
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }

    }

    ResultSet getResultSet() {
        return resultSet;
    }
}
