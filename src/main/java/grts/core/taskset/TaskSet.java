package grts.core.taskset;


import grts.core.schedulable.Schedulable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class TaskSet implements Iterable<Schedulable> {

    private final ArrayList<Schedulable> schedulables = new ArrayList<>();


    /**
     * Creates a new tasks set.
     * @param schedulables A list of schedulables to be added to the tasks set.
     */
    public TaskSet(List<Schedulable> schedulables) {
        this.schedulables.addAll(schedulables);
    }

    /**
     * Creates a new stream of schedulables from the schedulables associated to the tasks set.
     * @return a stream of Schedulable.
     */
    public Stream<Schedulable> stream() {
        return schedulables.stream();
    }

    @Override
    public Iterator<Schedulable> iterator() {
        return schedulables.iterator();
    }

    /**
     * Get the number of schedulables in the tasks set.
     * @return The number of schedulables.
     */
    public int getSchedulablesNumber(){
        return schedulables.size();
    }

}
