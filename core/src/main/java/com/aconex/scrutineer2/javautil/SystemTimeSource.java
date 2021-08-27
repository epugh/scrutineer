package com.aconex.scrutineer2.javautil;

public class SystemTimeSource implements TimeSource {

    @Override
    public long getCurrentTime() {
        return System.currentTimeMillis();
    }
}
