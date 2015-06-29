package grts.core.taskset;

import grts.core.schedulable.AbstractRecurrentTask;
import grts.core.schedulable.Schedulable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
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

    public boolean isConstrained(){
        return !schedulables.stream().allMatch(schedulable -> {
            if (!(schedulable instanceof AbstractRecurrentTask)) {
                return false;
            }
            AbstractRecurrentTask task = (AbstractRecurrentTask) schedulable;
            return task.getDeadline() <= task.getMinimumInterArrivalTime();
        });
    }

    public boolean isRecurrent(){
        return !schedulables.stream().anyMatch(schedulable -> !(schedulable instanceof AbstractRecurrentTask));
    }

    public TaskSet copy(){
        List<Schedulable> copySchedulables = new LinkedList<>();
        schedulables.stream().forEach(schedulable -> copySchedulables.add(schedulable.copy()));
        return new TaskSet(copySchedulables);
    }

}
