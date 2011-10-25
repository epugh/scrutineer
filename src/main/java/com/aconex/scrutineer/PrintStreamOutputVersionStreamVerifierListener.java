package com.aconex.scrutineer;

import java.io.PrintStream;

public class PrintStreamOutputVersionStreamVerifierListener implements IdAndVersionStreamVerifierListener {


    private final PrintStream printStream;

    public PrintStreamOutputVersionStreamVerifierListener(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public void onMissingInSecondaryStream(IdAndVersion idAndVersion) {
        printStream.println(String.format("NOTINSECONDARY\t%s\t%d",idAndVersion.getId(), idAndVersion.getVersion()));
    }

    @Override
    public void onMissingInPrimaryStream(IdAndVersion idAndVersion) {
        printStream.println(String.format("NOTINPRIMARY\t%s\t%d",idAndVersion.getId(), idAndVersion.getVersion()));
    }

    @Override
    public void onVersionMisMatch(IdAndVersion primaryItem, IdAndVersion secondaryItem) {
        printStream.println(String.format("MISMATCH\t%s\t%d\tsecondaryVersion=%d",primaryItem.getId(), primaryItem.getVersion(), secondaryItem.getVersion()));
    }
}
