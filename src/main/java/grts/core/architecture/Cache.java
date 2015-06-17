package grts.core.architecture;

public class Cache {

    private final long size;

    public Cache(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Cache of : " + size;
    }
}
