package com.aconex.scrutineer;

import org.apache.commons.lang.builder.CompareToBuilder;

public class IdAndVersion implements Comparable<IdAndVersion> {

    private final String id;
    private final long version;

    public IdAndVersion(String id, long version) {
        this.id = id;
        this.version = version;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IdAndVersion)) {
            return false;
        }
        IdAndVersion other = (IdAndVersion) obj;
        return this.compareTo(other) == 0;
    }

    @Override
    public int hashCode() {
        return 7 +
           id.hashCode() +
           (int)(version ^ (version >>> 32));
    }

    @Override
    public String toString() {
        return id + ":" + version;
    }

    public int compareTo(IdAndVersion other) {
        return new CompareToBuilder().append(id, other.id).append(version, other.version).toComparison();
    }

    public String getId() {
        return id;
    }

    public long getVersion() {
        return version;
    }
}
