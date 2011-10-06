package com.aconex.scrutineer;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class HasIdAndVersionMatcher extends TypeSafeMatcher<IdAndVersion> {

    private final String id;

    private final long version;

    public HasIdAndVersionMatcher(String id, long version) {
        this.id = id;
        this.version = version;
    }

    @Override
    public boolean matchesSafely(IdAndVersion idAndVersion) {
        return idAndVersion.getId().endsWith(id) && idAndVersion.getVersion() == version;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(" had id and version "+id+":"+version);
    }

    @Factory
    public static <T>Matcher<IdAndVersion> hasIdAndVersion(String id, long version) {
        return new HasIdAndVersionMatcher(id,version);
    }
}
