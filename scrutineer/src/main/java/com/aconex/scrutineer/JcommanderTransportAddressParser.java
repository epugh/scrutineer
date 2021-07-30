package com.aconex.scrutineer;

import java.util.List;

import com.aconex.scrutineer.elasticsearch.TransportAddressParser;
import com.beust.jcommander.IStringConverter;
import org.elasticsearch.common.transport.TransportAddress;

public class JcommanderTransportAddressParser implements IStringConverter<List<TransportAddress>> {
    @Override
    public List<TransportAddress> convert(String sourceValue) {
        return new TransportAddressParser().convert(sourceValue);
    }
}
