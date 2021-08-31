package com.aconex.scrutineer.javautil;

public class SystemTimeSource implements TimeSource {

    @Override
    public long getCurrentTime() {
        return System.currentTimeMillis();
    }
}
