package com.aconex.scrutineer;

import java.io.PrintStream;

public class StdOutIdAndVersionStreamVerifierListener implements IdAndVersionStreamVerifierListener {


    private PrintStream printStream;

    public StdOutIdAndVersionStreamVerifierListener() {
        this(System.out);
    }

    public StdOutIdAndVersionStreamVerifierListener(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public void onMissingInSecondaryStream(IdAndVersion idAndVersion) {
        printIdAndVersionToStream(idAndVersion);
    }

    private void printIdAndVersionToStream(IdAndVersion idAndVersion) {
        printStream.println(idAndVersion.getId());
    }

    @Override
    public void onMissingInPrimaryStream(IdAndVersion idAndVersion) {
        printIdAndVersionToStream(idAndVersion);
    }

    @Override
    public void onVersionMisMatch(IdAndVersion primaryItem, IdAndVersion secondaryItem) {
        printIdAndVersionToStream(primaryItem);
    }
}
