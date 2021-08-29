package com.aconex.scrutineer2.jdbc;

import com.aconex.scrutineer2.ConnectorConfig;
import com.aconex.scrutineer2.IdAndVersionFactory;
import com.aconex.scrutineer2.IdAndVersionStreamConnector;

public class JdbcConnectorConfig extends ConnectorConfig {
    private String jdbcUrl;
    private String driverClass;
    private String sql;
    private String user;
    private String password;

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public IdAndVersionStreamConnector createConnector(IdAndVersionFactory idAndVersionFactory) {
        return new JdbcStreamConnector(this, idAndVersionFactory);
    }
}
