package com.aconex.scrutineer;

import com.google.common.base.Function;
import org.elasticsearch.common.joda.time.DateTimeZone;
import org.elasticsearch.common.joda.time.format.DateTimeFormatter;
import org.elasticsearch.common.joda.time.format.ISODateTimeFormat;

public class TimestampFormatter implements Function<Long, Object> {

    private final DateTimeFormatter dateTimeFormatter;

    public TimestampFormatter() {
        this(DateTimeZone.getDefault());
    }

    public TimestampFormatter(DateTimeZone dateTimeZone) {
        this.dateTimeFormatter = ISODateTimeFormat.dateTime().withZone(dateTimeZone);
    }

    @Override
    public Object apply(Long timestamp) {
        String timestampFormatted = dateTimeFormatter.print(timestamp);
        return String.format("%d(%s)", timestamp, timestampFormatted);
    }
}
