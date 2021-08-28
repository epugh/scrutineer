package com.aconex.scrutineer2.http;

import com.aconex.scrutineer2.ConnectorConfig;
import com.aconex.scrutineer2.IdAndVersionFactory;
import com.aconex.scrutineer2.IdAndVersionStreamConnector;

public class HttpConnectorConfig implements ConnectorConfig {
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
