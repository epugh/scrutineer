package com.aconex.scrutineer.javautil;

public class ControlledTimeSource implements TimeSource {
    private long expectedTime;

    public ControlledTimeSource(long expectedTime) {
        this.expectedTime = expectedTime;
    }

    @Override
    public long getCurrentTime() {
        return expectedTime;
    }
}
