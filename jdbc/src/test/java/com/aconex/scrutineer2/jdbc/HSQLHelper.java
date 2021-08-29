package com.aconex.scrutineer2.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.hsqldb.jdbc.JDBCDataSource;

public class HSQLHelper {
    public static final String INMEM_TEST_DB = "mem:scrutineer";
    public static final String DB_USERNAME = "sa";
    public static final String DB_PASSWORD = "";

    public HSQLHelper() {
    }

    public DataSource setupHSQLDBDataSource() {
        JDBCDataSource dataSource = new JDBCDataSource();
        dataSource.setDatabase(INMEM_TEST_DB);
        dataSource.setUser(DB_USERNAME);
        dataSource.setPassword(DB_PASSWORD);
        return dataSource;
    }


    public void shutdownHSQL(DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
        shutdownHSQL(connection);
    }

    private void shutdownHSQL(Connection connection) throws SQLException {
        connection.createStatement().execute("SHUTDOWN");
    }


    // The following< code borrowed from http://stackoverflow.com/questions/1531324/is-there-any-way-for-dbunit-to-automatically-create-tables

    public void createHsqldbTables(IDataSet dataSet, Connection connection) throws DataSetException, SQLException {
        String[] tableNames = dataSet.getTableNames();

        String sql = "";
        for (String tableName : tableNames) {
            ITable table = dataSet.getTable(tableName);
            ITableMetaData metadata = table.getTableMetaData();
            Column[] columns = metadata.getColumns();

            sql += "create table " + tableName + "( ";
            boolean first = true;
            for (Column column : columns) {
                if (!first) {
                    sql += ", ";
                }
                String columnName = column.getColumnName();
                String type = resolveType((String) table.getValue(0, columnName));
                sql += columnName + " " + type;
                if (first) {
                    sql += " primary key";
                    first = false;
                }
            }
            sql += "); ";
        }
        PreparedStatement pp = connection.prepareStatement(sql);
        pp.executeUpdate();
    }

    String resolveType(String str) {
        try {
            if (new Double(str).toString().equals(str)) {
                return "double";
            }
        } catch (Exception e) {
        }

        try {
            if (new Integer(str).toString().equals(str)) {
                return "int";
            }
        } catch (Exception e) {
        }

        return "varchar";
    }
}