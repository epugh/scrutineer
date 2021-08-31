package com.aconex.scrutineer.http;

import com.aconex.scrutineer.ConnectorConfig;
import com.aconex.scrutineer.IdAndVersionFactory;
import com.aconex.scrutineer.IdAndVersionStreamConnector;

public class HttpConnectorConfig extends ConnectorConfig {
    private static final int DEFAULT_TIMEOUT_IN_MILLISECOND = 1000;
    private String httpEndpointUrl;
    private int httpConnectionTimeoutInMillisecond = DEFAULT_TIMEOUT_IN_MILLISECOND;
    private int httpReadTimeoutInMillisecond = DEFAULT_TIMEOUT_IN_MILLISECOND;

    public String getHttpEndpointUrl() {
        return httpEndpointUrl;
    }

    public void setHttpEndpointUrl(String httpEndpointUrl) {
        this.httpEndpointUrl = httpEndpointUrl;
    }

    public int getHttpConnectionTimeoutInMillisecond() {
        return httpConnectionTimeoutInMillisecond;
    }

    public void setHttpConnectionTimeoutInMillisecond(int httpConnectionTimeoutInMillisecond) {
        this.httpConnectionTimeoutInMillisecond = httpConnectionTimeoutInMillisecond;
    }

    public int getHttpReadTimeoutInMillisecond() {
        return httpReadTimeoutInMillisecond;
    }

    public void setHttpReadTimeoutInMillisecond(int httpReadTimeoutInMillisecond) {
        this.httpReadTimeoutInMillisecond = httpReadTimeoutInMillisecond;
    }

    @Override
    public String toString() {
        return "Config{" +
                "httpEndpointUrl='" + httpEndpointUrl + '\'' +
                ", httpConnectionTimeoutInMillisecond=" + httpConnectionTimeoutInMillisecond +
                ", httpReadTimeoutInMillisecond=" + httpReadTimeoutInMillisecond +
                '}';
    }

    @Override
    public IdAndVersionStreamConnector createConnector(IdAndVersionFactory idAndVersionFactory) {
        return new JsonEncodedHttpEndpointSourceConnector(this, idAndVersionFactory);
    }
}
