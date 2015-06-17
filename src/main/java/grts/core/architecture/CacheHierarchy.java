package grts.core.architecture;

import java.util.ArrayList;

public class CacheHierarchy {
    private final ArrayList<Cache> caches;

    /**
     * Creates a new Cache Hierarchy.
     * @param caches The arrayList of cache. The order of the arrayList matches to the order of cache levels.
     */
    public CacheHierarchy(ArrayList<Cache> caches) {
        this.caches = caches;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        caches.forEach(cache -> stringBuilder.append(cache).append("\n"));
        return stringBuilder.toString();
    }
}
