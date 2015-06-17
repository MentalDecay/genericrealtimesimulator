package grts.core.architecture;

public class Cache {

    private final long size;

    /**
     * Creates a new basic Cache.
     * @param size The size of the cache.
     */
    public Cache(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Cache of : " + size;
    }
}
