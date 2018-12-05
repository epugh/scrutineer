package com.aconex.scrutineer.elasticsearch;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.IStringConverter;
import org.elasticsearch.common.transport.TransportAddress;

public class TransportAddressParser implements IStringConverter<List<TransportAddress>> {

    private static final int DEFAULT_ELASTICSEARCH_PORT = 9300;
    private final InetAddressResolver inetAddressResolver;

    public TransportAddressParser(){
        this(new InetAddressResolver() {
            @Override
            public InetAddress resolveInetAddress(String hostByName) throws UnknownHostException {
                return InetAddress.getByName(hostByName);
            }
        });
    }
    TransportAddressParser(InetAddressResolver resolver) {
        this.inetAddressResolver = resolver;
    }

    @Override
    public List<TransportAddress> convert(String sourceValue) {
        String[] values = sourceValue.split(",");
        List<TransportAddress> transportAddresses = new ArrayList<>();
        for (String value : values) {
            parseHostPortPair(transportAddresses, value);
        }
        return transportAddresses;
    }

    private void parseHostPortPair(List<TransportAddress> transportAddresses, String value) {
        String[] hostPortPair = value.split(":");
        String host = hostPortPair[0];
        int port = hostPortPair.length < 2 ? DEFAULT_ELASTICSEARCH_PORT : Integer.valueOf(hostPortPair[1]);
        try {
            transportAddresses.add(new TransportAddress(inetAddressResolver.resolveInetAddress(host), port));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    interface InetAddressResolver {
        InetAddress resolveInetAddress(String hostByName) throws UnknownHostException;
    }
}
