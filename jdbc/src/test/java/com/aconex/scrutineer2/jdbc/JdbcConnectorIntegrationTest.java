package com.aconex.scrutineer2.jdbc;

import com.aconex.scrutineer2.IdAndVersion;
import com.aconex.scrutineer2.IdAndVersionFactory;
import com.aconex.scrutineer2.StringIdAndVersion;
import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Iterator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;


public class JdbcConnectorIntegrationTest extends DataSourceBasedDBTestCase {
    private static final String SQL = "Select id, version from test";
    private final HSQLHelper hqlHelper = new HSQLHelper();
	private final IdAndVersionFactory idAndVersionFactory = StringIdAndVersion.FACTORY;

    public void testShouldReturnTuplesInCorrectOrder() {
        JdbcConnectorConfig connectorConfig = new JdbcConnectorConfig();
        connectorConfig.setPresorted(true);
        connectorConfig.setDriverClass("org.hsqldb.jdbc.JDBCDriver");
        connectorConfig.setJdbcUrl(String.format("jdbc:hsqldb:%s", HSQLHelper.INMEM_TEST_DB));
        connectorConfig.setSql(SQL);
        connectorConfig.setUser(HSQLHelper.DB_USERNAME);
        connectorConfig.setPassword(HSQLHelper.DB_PASSWORD);

        JdbcStreamConnector jdbcStreamConnector = new JdbcStreamConnector(connectorConfig, idAndVersionFactory);
        Iterator<IdAndVersion> iterator = jdbcStreamConnector.connect().iterator();

        assertThat(iterator.next(), equalTo(new StringIdAndVersion("1",10)));
        assertThat(iterator.next(), equalTo(new StringIdAndVersion("2",20)));
        assertThat(iterator.next(), equalTo(new StringIdAndVersion("3",30)));

        jdbcStreamConnector.close();
    }

    @Override
    protected void setUp() throws Exception {
        hqlHelper.createHsqldbTables(getDataSet(), getDataSource().getConnection());
        super.setUp();

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        hqlHelper.shutdownHSQL(getDataSource());
    }

    @Override
    protected DataSource getDataSource() {
        return hqlHelper.setupHSQLDBDataSource();
    }


    @Override
    protected IDataSet getDataSet() throws Exception {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("dataset.xml");
        return new XmlDataSet(resourceAsStream);
    }
}
