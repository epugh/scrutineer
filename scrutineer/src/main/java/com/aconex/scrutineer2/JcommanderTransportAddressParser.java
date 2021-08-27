package com.aconex.scrutineer2;

import java.util.List;

import com.aconex.scrutineer2.elasticsearch.v7.TransportAddressParser;
import com.beust.jcommander.IStringConverter;
import org.elasticsearch.common.transport.TransportAddress;

public class JcommanderTransportAddressParser implements IStringConverter<List<TransportAddress>> {
    @Override
    public List<TransportAddress> convert(String sourceValue) {
        return new TransportAddressParser().convert(sourceValue);
    }
}
