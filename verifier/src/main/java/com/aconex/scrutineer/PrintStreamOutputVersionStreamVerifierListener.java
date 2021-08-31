package com.aconex.scrutineer;

import java.io.PrintStream;

import com.google.common.base.Function;

public class PrintStreamOutputVersionStreamVerifierListener implements IdAndVersionStreamVerifierListener {

    public static final Function<Long, Object> DEFAULT_FORMATTER = new DefaultVersionFormatter();

    private final PrintStream printStream;
    private Function<Long, Object> versionFormatter = DEFAULT_FORMATTER;


    public PrintStreamOutputVersionStreamVerifierListener(PrintStream printStream) {
        this(printStream,false);
    }

    public PrintStreamOutputVersionStreamVerifierListener(PrintStream printStream, boolean versionsAsTimestamps) {
        this.printStream = printStream;
        if (versionsAsTimestamps) {
            versionFormatter = new TimestampFormatter();
        }
    }

    @Override
    public void onMissingInSecondaryStream(IdAndVersion idAndVersion) {
        printStream.println(String.format("NOTINSECONDARY\t%s\t%s", idAndVersion.getId(), versionFormatter.apply(idAndVersion.getVersion())));
    }

    @Override
    public void onMissingInPrimaryStream(IdAndVersion idAndVersion) {
        printStream.println(String.format("NOTINPRIMARY\t%s\t%s", idAndVersion.getId(), versionFormatter.apply(idAndVersion.getVersion())));
    }

    @Override
    public void onVersionMisMatch(IdAndVersion primaryItem, IdAndVersion secondaryItem) {
        printStream.println(String.format("MISMATCH\t%s\t%s\tsecondaryVersion=%s", primaryItem.getId(), versionFormatter.apply(primaryItem.getVersion()), versionFormatter.apply(secondaryItem.getVersion())));
    }

    private static class DefaultVersionFormatter implements Function<Long, Object> {
        @Override
        public Object apply(Long aLong) {
            return aLong;
        }
    }
}
