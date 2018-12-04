package com.aconex.scrutineer;

import org.mockito.ArgumentMatcher;

public class HasIdAndVersionMatcher implements ArgumentMatcher<IdAndVersion> {

    private final String id;

    private final long version;

    public HasIdAndVersionMatcher(String id, long version) {
        this.id = id;
        this.version = version;
    }

    @Override
    public boolean matches(IdAndVersion idAndVersion) {
        return idAndVersion.getId().equals(id) && idAndVersion.getVersion() == version;
    }
}
