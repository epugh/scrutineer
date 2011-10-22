package com.aconex.scrutineer.jdbc;

import static com.aconex.scrutineer.HasIdAndVersionMatcher.hasIdAndVersion;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

import com.aconex.scrutineer.IdAndVersion;
import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.xml.XmlDataSet;
import org.hsqldb.jdbc.JDBCDataSource;


public class JdbcIdAndVersionStreamIntegrationTest extends DataSourceBasedDBTestCase {

    private static final String SQL = "Select id, version from test";


    public void testShouldReturnTuplesInCorrectOrder() throws SQLException {

        JdbcIdAndVersionStream jdbcIdAndVersionStream = new JdbcIdAndVersionStream(getDataSource(), SQL);

        jdbcIdAndVersionStream.open();

        Iterator<IdAndVersion> iterator = jdbcIdAndVersionStream.iterator();

        assertThat(iterator.next(), hasIdAndVersion("1", 10));
        assertThat(iterator.next(), hasIdAndVersion("2", 20));
        assertThat(iterator.next(), hasIdAndVersion("3", 30));

        jdbcIdAndVersionStream.close();

    }

    @Override
    protected void setUp() throws Exception {
        createHsqldbTables(getDataSet(), getDataSource().getConnection());
        super.setUp();

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        getDataSource().getConnection().createStatement().execute("SHUTDOWN");
    }

    @Override
    protected DataSource getDataSource() {
        JDBCDataSource dataSource = new JDBCDataSource();
        dataSource.setDatabase("mem:aname");
        dataSource.setUser("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    @Override
    protected IDataSet getDataSet() throws Exception {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("dataset.xml");
        return new XmlDataSet(resourceAsStream);
    }


    // The following< code borrowed from http://stackoverflow.com/questions/1531324/is-there-any-way-for-dbunit-to-automatically-create-tables

    private void createHsqldbTables(IDataSet dataSet, Connection connection) throws DataSetException, SQLException {
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

    private String resolveType(String str) {
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
