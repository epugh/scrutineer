package com.aconex.scrutineer2.jdbc;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Iterator;
import javax.sql.DataSource;

import com.aconex.scrutineer2.IdAndVersion;
import com.aconex.scrutineer2.IdAndVersionFactory;
import com.aconex.scrutineer2.StringIdAndVersion;
import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;


public class JdbcIdAndVersionStreamIntegrationTest extends DataSourceBasedDBTestCase {

    private static final String SQL = "Select id, version from test";
    private final HSQLHelper HSQLHelper = new HSQLHelper();
	private IdAndVersionFactory idAndVersionFactory = StringIdAndVersion.FACTORY;


    public void testShouldReturnTuplesInCorrectOrder() throws SQLException {

        JdbcIdAndVersionStream jdbcIdAndVersionStream = new JdbcIdAndVersionStream(getDataSource().getConnection(), SQL, idAndVersionFactory);

        jdbcIdAndVersionStream.open();

        Iterator<IdAndVersion> iterator = jdbcIdAndVersionStream.iterator();

        assertThat(iterator.next(), equalTo(new StringIdAndVersion("1",10)));
        assertThat(iterator.next(), equalTo(new StringIdAndVersion("2",20)));
        assertThat(iterator.next(), equalTo(new StringIdAndVersion("3",30)));

        jdbcIdAndVersionStream.close();

    }

    @Override
    protected void setUp() throws Exception {
        HSQLHelper.createHsqldbTables(getDataSet(), getDataSource().getConnection());
        super.setUp();

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        getDataSource().getConnection().createStatement().execute("SHUTDOWN");
    }

    @Override
    protected DataSource getDataSource() {
        return HSQLHelper.setupHSQLDBDataSource();
    }


    @Override
    protected IDataSet getDataSet() throws Exception {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("dataset.xml");
        return new XmlDataSet(resourceAsStream);
    }


    private DataSource setupHSQLDBDataSource() {
        return HSQLHelper.setupHSQLDBDataSource();
    }

}
