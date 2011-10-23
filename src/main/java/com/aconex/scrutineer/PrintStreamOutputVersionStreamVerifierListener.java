package com.aconex.scrutineer;

import java.io.PrintStream;

public class PrintStreamOutputVersionStreamVerifierListener implements IdAndVersionStreamVerifierListener {


    private final PrintStream printStream;

    public PrintStreamOutputVersionStreamVerifierListener(PrintStream printStream) {
        this.printStream = printStream;
    }


    // TODO This class needs to print out more context on the line, whether it's a DELETE needed on the index, and time difference information when mismatched (if it is indeed a timestamp though.. right..?)

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
