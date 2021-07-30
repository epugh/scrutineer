package com.aconex.scrutineer;

import java.io.PrintStream;

import com.google.common.base.Function;

public class PrintStreamOutputVersionStreamVerifierListener implements IdAndVersionStreamVerifierListener {

    public static final Function<Long, Object> DEFAULT_FORMATTER = new DefaultVersionFormatter();

    private final PrintStream printStream;
    private final Function<Long, Object> versionFormatter;


    public PrintStreamOutputVersionStreamVerifierListener(PrintStream printStream) {
        this(printStream, DEFAULT_FORMATTER);
    }

    public PrintStreamOutputVersionStreamVerifierListener(PrintStream printStream, Function<Long, Object> versionFormatter) {
        this.printStream = printStream;
        this.versionFormatter = versionFormatter;
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
