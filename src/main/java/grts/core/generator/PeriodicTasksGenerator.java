package grts.core.generator;

import grts.core.schedulable.PeriodicTask;
import grts.core.schedulable.Schedulable;
import grts.core.taskset.TaskSet;

import java.util.*;

public class PeriodicTasksGenerator {
    private final int n;
    private final int m;
    private final double u;
    private final Random random = new Random();

    public PeriodicTasksGenerator(int n, int m, double u) {
        if(n < 1 || m < 1 || u <= 0 || u > 1){
            throw new IllegalArgumentException("Can't create a new PeriodicTasksGenerator with this parameters : n :" + n + " m : " + m + " u : " + u);
        }
        this.n = n;
        this.m = m;
        this.u = u;
    }

    /**
     * Creates a double array which contains n utilizations of the n tasks.
     * @return An array of double.
     */
    private double[] generateUtilizations(){
        double[] utilizations = new double[n];
        boolean discard = true;
        double mU = m * u;
        double sumU;
        while(discard) {
            sumU = mU;
            double nextSumU;
            discard = false;
            for (int i = 0; i < n - 1; i++) {
                nextSumU = sumU * Math.pow(random.nextDouble(), 1. / ((double) n - (double) i));
                utilizations[i] = sumU - nextSumU;
                sumU = nextSumU;
                if (utilizations[i] > 1) {
                    discard = true;
                }
            }
            if(sumU > 1){
                discard = true;
                continue;
            }
            utilizations[n-1] = sumU;
        }
        return utilizations;
    }

    /**
     * Generates the ImplicitPeriodicTaskSet according to the PeriodicTasksGenerator.
     * @param maxPeriod The maximum period of the task generated.
     * @param threshold The threshold of the total utilization.
     * @return A new ImplicitPeriodicTaskSet randomly created.
     */
    public TaskSet generateImplicitPeriodicTaskSet(int maxPeriod, double threshold){
        return new TaskSet(createsPeriodicImplicitTasks(maxPeriod, threshold));

    }

    /**
     * Creates an array of Schedulable with a total utilization which fits according to the threshold and the number of processors.
     * @param maxPeriod The maximum period of a task.
     * @param threshold The threshold of the total utilization.
     * @return An array of Schedulable.
     */
    private List<Schedulable> createsPeriodicImplicitTasks(int maxPeriod, double threshold){
        double um = u * m;
        while(true){
            LinkedList<Schedulable> schedulables = new LinkedList<>();
            double[] utilizations = generateUtilizations();
            List<Long> periods = new ArrayList<>();
            random.longs(utilizations.length, 1, maxPeriod).forEach(periods::add);
            for(int i = 0; i < utilizations.length; i++){
                long wcet = Math.max((long) Math.floor(utilizations[i] * (double) periods.get(i)), 1);
                schedulables.add(new PeriodicTask(periods.get(i), wcet, periods.get(i), 0, "t" + (i + 1)));
            }
            double totalUtilization = schedulables.stream().mapToDouble(schedulable -> {
                PeriodicTask task = (PeriodicTask) schedulable;
                return (double) task.getWcet() / (double) task.getPeriod();
            }).sum();
            if(totalUtilization <= um && (um - threshold <= totalUtilization && totalUtilization <= um + threshold)){
                return schedulables;
            }
        }
    }
}
