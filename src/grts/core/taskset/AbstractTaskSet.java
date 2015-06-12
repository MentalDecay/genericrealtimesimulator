package grts.core.taskset;

import grts.core.schedulable.AbstractRecurrentTask;
import grts.core.schedulable.ITask;

import java.util.List;

public abstract class AbstractTaskSet implements ITaskSet {

    AbstractTaskSet(List<ITask> tasks) {
        if(tasks.isEmpty()){
            throw new IllegalArgumentException("Can't create new task set with no recurrent tasks");
        }
        hyperPeriod = calculateHyperPeriod(tasks);
        this.tasks = tasks;
    }

    // Stolen from YARTISS
    private static long calculateHyperPeriod(List<ITask> tasks){
        long [] tasksPeriods = new long [tasks.size()];
        int i = 0;
        long p1 = 0;
        for (ITask task : tasks) {
            if(!(task instanceof  AbstractRecurrentTask)){
                continue;
            }
            AbstractRecurrentTask task1 = (AbstractRecurrentTask) task;
            tasksPeriods[i] = task1.getMinimumInterArrivalTime();
            p1 = task1.getMinimumInterArrivalTime();
            i++;
        }
        return lcm(p1,tasksPeriods);
    }

    // Stolen from YARTISS
    private static long  lcm (long nb1, long nb2) {
        long prod, rest, lcm;

        prod = nb1*nb2;
        rest   = nb1%nb2;
        while(rest != 0){
            nb1 = nb2;
            nb2 = rest;
            rest = nb1%nb2;
        }
        lcm = prod/nb2;
        return lcm;
    }

    // Stolen from YARTISS
    private static long lcm(long nb1, long ... nbs){
        long lcm = nb1;
        for (long nbi : nbs) {
            lcm = lcm(lcm, nbi);
        }
        return lcm;
    }



    private final List<ITask> tasks;

    private final long hyperPeriod;


    @Override
    public List<ITask> getTasks() {
        return tasks;
    }

    @Override
    public long getHyperPeriod() {
        return hyperPeriod;
    }
}
