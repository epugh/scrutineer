package com.aconex.scrutineer.http;

import com.aconex.scrutineer.IdAndVersionFactory;
import com.aconex.scrutineer.IdAndVersionStream;
import com.aconex.scrutineer.IdAndVersionStreamConnector;
import com.aconex.scrutineer.config.ConnectorConfig;
import com.aconex.scrutineer.javautil.JavaIteratorIdAndVersionStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class JsonEncodedHttpEndpointSourceConnector implements IdAndVersionStreamConnector {
    private final Logger logger = LoggerFactory.getLogger(JsonEncodedHttpEndpointSourceConnector.class);
    private HttpURLConnection httpConnection;
    private InputStream responseInputStream;
    private ConnectorConfig connectorConfig;

    @Override
    public void configure(Map<String, String> properties) {
        connectorConfig = new Config(properties);
    }

    @Override
    public IdAndVersionStream connect(IdAndVersionFactory idAndVersionFactory) {
        try{
            responseInputStream =sendRequest((Config)connectorConfig);
            return new JavaIteratorIdAndVersionStream (new JsonEncodedIdAndVersionInputStreamIterator(responseInputStream, idAndVersionFactory));
        } catch (Exception e){
            throw new RuntimeException("Failed to list entities from source endpoint: "+connectorConfig, e);
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
        connection.setConnectTimeout(config.getHttpConnectionTimeout());
        connection.setReadTimeout(config.getHttpReadTimeout());
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

    public static class Config extends ConnectorConfig {
        public static final String HTTP_ENDPOINT_URL="http.endpoint.url";
        public static final String HTTP_CONNECTION_TIMEOUT="http.connection.timeout";
        public static final String HTTP_READ_TIMEOUT="http.read.timeout";

        protected Config(Map<String, String> props) {
            super(props);
        }
        public String getHttpEndpointUrl(){
            return get(HTTP_ENDPOINT_URL);
        }
        public int getHttpConnectionTimeout(){
            return Integer.parseInt(get(HTTP_CONNECTION_TIMEOUT));
        }
        public int getHttpReadTimeout(){
            return Integer.parseInt(get(HTTP_READ_TIMEOUT));
        }
    }
}
