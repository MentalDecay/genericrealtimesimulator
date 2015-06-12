package grts.core.taskset;


import grts.core.schedulable.Schedulable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class TaskSet implements Iterable<Schedulable> {

    private final ArrayList<Schedulable> schedulables = new ArrayList<>();


    public TaskSet(List<Schedulable> schedulables) {
        this.schedulables.addAll(schedulables);
    }

    public Stream<Schedulable> stream() {
        return schedulables.stream();
    }

    @Override
    public Iterator<Schedulable> iterator() {
        return schedulables.iterator();
    }

}
