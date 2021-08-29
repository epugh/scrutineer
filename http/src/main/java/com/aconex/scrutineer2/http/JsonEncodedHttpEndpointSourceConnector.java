package com.aconex.scrutineer2.http;

import com.aconex.scrutineer2.AbstractIdAndVersionStreamConnector;
import com.aconex.scrutineer2.ConnectorConfig;
import com.aconex.scrutineer2.IdAndVersion;
import com.aconex.scrutineer2.IdAndVersionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class JsonEncodedHttpEndpointSourceConnector extends AbstractIdAndVersionStreamConnector {
    private final Logger logger = LoggerFactory.getLogger(JsonEncodedHttpEndpointSourceConnector.class);
    private HttpURLConnection httpConnection;
    private InputStream responseInputStream;

    protected JsonEncodedHttpEndpointSourceConnector(ConnectorConfig connectorConfig, IdAndVersionFactory idAndVersionFactory) {
        super(connectorConfig, idAndVersionFactory);
    }

    @Override
    public void open() {
        try{
            responseInputStream =sendRequest(getConfig());
        } catch (Exception e){
            throw new RuntimeException("Failed to list entities from source endpoint: "+ getConfig(), e);
        }
    }
    public Iterator<IdAndVersion> fetchFromSource() {
        return new JsonEncodedIdAndVersionInputStreamIterator(responseInputStream, getIdAndVersionFactory());
    }

    private InputStream sendRequest(HttpConnectorConfig config) throws IOException {
        String queryUrl = config.getHttpEndpointUrl();
        logger.info("Querying http endpoint: {}", queryUrl);

        httpConnection = prepareConnection(config, queryUrl);
        httpConnection.connect();
        return new BufferedInputStream(httpConnection.getInputStream());
    }

    private HttpURLConnection prepareConnection(HttpConnectorConfig config, String queryUrl) throws IOException {
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
    private HttpConnectorConfig getConfig(){
        return (HttpConnectorConfig) getConnectorConfig();
    }
}
