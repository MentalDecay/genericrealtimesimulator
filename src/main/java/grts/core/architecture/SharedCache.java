package grts.core.architecture;

import java.util.Collections;
import java.util.List;

public class SharedCache extends Cache {

    private final List<Integer> sharedWith;

    /**
     * Creates a new Shared Cache.
     * @param size The size of the cache.
     * @param sharedWith The list of processor ids with which the cache is shared.
     */
    public SharedCache(long size, List<Integer> sharedWith) {
        super(size);
        this.sharedWith = sharedWith;
    }

    /**
     * Get the sharedWith list. This list is unmodifiable.
     * @return An unmodifiable representation of the sharedWith list.
     */
    public List<Integer> getSharedWith() {
        return Collections.unmodifiableList(sharedWith);
    }

    @Override
    public String toString() {
        return super.toString() + " shared with : " + sharedWith;
    }
}
