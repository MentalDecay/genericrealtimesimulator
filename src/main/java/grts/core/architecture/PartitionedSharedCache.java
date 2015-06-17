package grts.core.architecture;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PartitionedSharedCache extends SharedCache {

    private final int nbPartitions;
    private ArrayList<LinkedList<Integer>> assignations;

    /**
     * Creates a new Partitioned Shared Cache.
     * @param size The size of the cache.
     * @param sharedWith The list of processor ids with which the cache is shared.
     * @param nbPartitions The number of partitions.
     * @param assignations The list (with a size of nbPartitions) of list of processors id with which the partition is shared.
     */
    public PartitionedSharedCache(long size, List<Integer> sharedWith, int nbPartitions, ArrayList<LinkedList<Integer>> assignations) {
        super(size, sharedWith);
        this.nbPartitions = nbPartitions;
        if(assignations.size() != nbPartitions){
            throw new IllegalArgumentException("Wrong assignations list");
        }
        this.assignations = assignations;
    }

    /**
     * Creates a new Partitioned Shared Cache.
     * @param size The size of the cache.
     * @param sharedWith The list of processor ids with which the cache is shared.
     * @param nbPartitions The number of partitions.
     * With this constructor, the assignation of processors with each partition must be set by an offline algorithm.
     */
    public PartitionedSharedCache(long size, List<Integer> sharedWith, int nbPartitions) {
        super(size, sharedWith);
        this.nbPartitions = nbPartitions;
        assignations = new ArrayList<>(nbPartitions);
    }

    /**
     * Get the number of partitions of the cache.
     * @return the number of partitions of the cache.
     */
    public int getNbPartitions() {
        return nbPartitions;
    }

    /**
     * Set the assignations of each partition of the cache.
     * @param assignations The new list of assignations. This list must have the size of the number of partitions.
     */
    public void setAssignations(ArrayList<LinkedList<Integer>> assignations) {
        if(assignations.size() != nbPartitions){
            throw new IllegalArgumentException("Wrong size of assignations");
        }
        this.assignations = assignations;
    }

    @Override
    public String toString() {
        return super.toString() + " with " + nbPartitions + " partitions assigned to : " + assignations;
    }
}
