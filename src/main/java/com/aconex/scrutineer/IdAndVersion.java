package com.aconex.scrutineer;

public class IdAndVersion implements Comparable<IdAndVersion> {

    private final long id;
    private final long version;

    public IdAndVersion(long id, long version) {
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
           (int)(id ^ (id >>> 32)) +
           (int)(version ^ (version >>> 32));
    }

    @Override
    public String toString() {
        return id + ":" + version;
    }

    public int compareTo(IdAndVersion other) {
        if (id == other.id && version == other.version) {
            return 0;
        }
        if (id < other.id) {
            return -1;
        }
        if (id > other.id) {
            return 1;
        }
        if (version < other.version){
            return -1;
        }
        return 1;
    }

    public long getId() {
        return id;
    }

    public long getVersion() {
        return version;
    }
}
