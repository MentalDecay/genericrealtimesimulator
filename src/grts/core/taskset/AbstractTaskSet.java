package grts.core.taskset;

import grts.core.schedulable.AbstractRecurrentTask;

import java.util.List;

public abstract class AbstractTaskSet implements ITaskSet {

    AbstractTaskSet(List<AbstractRecurrentTask> recurrentTasks) {
        if(recurrentTasks.isEmpty()){
            throw new IllegalArgumentException("Can't create new task set with no recurrent tasks");
        }
        hyperPeriod = calculateHyperPeriod(recurrentTasks);
        this.recurrentTasks = recurrentTasks;
    }

    // Stolen from YARTISS
    private static long calculateHyperPeriod(List<AbstractRecurrentTask> tasks){
        long [] tasksPeriods = new long [tasks.size()];
        int i = 0;
        long p1 = 0;
        for (AbstractRecurrentTask task : tasks) {
            tasksPeriods[i] = task.getMinimumInterArrivalTime();
            p1 = task.getMinimumInterArrivalTime();
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



    private final List<AbstractRecurrentTask> recurrentTasks;

    private final long hyperPeriod;


    @Override
    public List<AbstractRecurrentTask> getRecurrentTasks() {
        return recurrentTasks;
    }

    @Override
    public long getHyperPeriod() {
        return hyperPeriod;
    }
}
