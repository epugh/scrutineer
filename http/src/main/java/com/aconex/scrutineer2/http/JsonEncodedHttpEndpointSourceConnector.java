package com.aconex.scrutineer2.http;

import com.aconex.scrutineer2.ConnectorConfig;
import com.aconex.scrutineer2.IdAndVersionFactory;
import com.aconex.scrutineer2.IdAndVersionStream;
import com.aconex.scrutineer2.IdAndVersionStreamConnector;
import com.aconex.scrutineer2.javautil.JavaIteratorIdAndVersionStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class JsonEncodedHttpEndpointSourceConnector implements IdAndVersionStreamConnector {
    private final Logger logger = LoggerFactory.getLogger(JsonEncodedHttpEndpointSourceConnector.class);
    private HttpURLConnection httpConnection;
    private InputStream responseInputStream;
    private final Config config;
    private IdAndVersionFactory idAndVersionFactory;

    public JsonEncodedHttpEndpointSourceConnector(Config config, IdAndVersionFactory idAndVersionFactory) {
        this.config = config;
        this.idAndVersionFactory = idAndVersionFactory;
    }

    @Override
    public IdAndVersionStream connect() {
        try{
            responseInputStream =sendRequest(config);
            return new JavaIteratorIdAndVersionStream (new JsonEncodedIdAndVersionInputStreamIterator(responseInputStream, idAndVersionFactory));
        } catch (Exception e){
            throw new RuntimeException("Failed to list entities from source endpoint: "+ config, e);
        }
    }

    private InputStream sendRequest(Config config) throws IOException {
        String queryUrl = config.getHttpEndpointUrl();
        logger.info("Querying http endpoint: {}", queryUrl);

        httpConnection = prepareConnection(config, queryUrl);
        httpConnection.connect();
        return new BufferedInputStream(httpConnection.getInputStream());
    }

    private HttpURLConnection prepareConnection(Config config, String queryUrl) throws IOException {
        HttpURLConnection connection =  (HttpURLConnection) new URL(queryUrl).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(config.getHttpConnectionTimeoutInMillisecond());
        connection.setReadTimeout(config.getHttpReadTimeoutInMillisecond());
        return connection;
    }

    @Override
    public void close() {
        closeResponseInputStream();
        closeHttpConnection();
    }

    private void closeResponseInputStream() {
        if(responseInputStream!=null){
            try{
                responseInputStream.close();
            } catch (IOException e) {
                logger.warn("Failed to close the http response http stream", e);
            }
        }
    }

    private void closeHttpConnection() {
        if(httpConnection !=null){
            try{
                httpConnection.disconnect();
            }catch (Exception e){
                logger.warn("Failed to disconnect http url connection", e);
            }
        }
    }

    public static class Config implements ConnectorConfig {
        private static final int DEFAULT_TIMEOUT_IN_MILLISECOND=1000;
        private String httpEndpointUrl;
        private int httpConnectionTimeoutInMillisecond =DEFAULT_TIMEOUT_IN_MILLISECOND;
        private int httpReadTimeoutInMillisecond =DEFAULT_TIMEOUT_IN_MILLISECOND;

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
}
