package grts.core.generator;

import grts.core.schedulable.PeriodicTask;
import grts.core.schedulable.Schedulable;
import grts.core.taskset.TaskSet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class UUnifastMonoProc {
    private final int n;
    private final double averageU;
    private final Random random = new Random();

    public UUnifastMonoProc(int n, double averageU) {
        if(n < 1 || averageU <= 0 || averageU > 1){
            throw new IllegalArgumentException("Can't create a new UUnifastMonoProc with this parameters : n :" + n + " averageU : " + averageU);
        }
        this.n = n;
        this.averageU = averageU;
    }

    public TaskSet generateUUnifastMonoProc(long maxPeriod, double threshold){
        return new TaskSet(createsPeriodicImplicitTasks(maxPeriod, threshold));
    }

    private List<Schedulable> createsPeriodicImplicitTasks(long maxPeriod, double threshold) {
        while(true) {
            double[] utilizations = generateUtilizations();
            LinkedList<Schedulable> schedulables = new LinkedList<>();
            List<Long> periods = new ArrayList<>();
            random.longs(utilizations.length, 1, maxPeriod).forEach(periods::add);
            for (int i = 0; i < utilizations.length; i++) {
                long wcet = Math.max((long) Math.floor(utilizations[i] * (double) periods.get(i)), 1);
                schedulables.add(new PeriodicTask(periods.get(i), wcet, periods.get(i), 0, "t" + (i + 1)));
            }
            double totalUtilization = schedulables.stream().mapToDouble(schedulable -> {
                PeriodicTask task = (PeriodicTask) schedulable;
                return (double) task.getWcet() / (double) task.getPeriod();
            }).sum();
            if(totalUtilization < 1 && (averageU - threshold <= totalUtilization && totalUtilization <= averageU + threshold)){
//                System.out.println("Total Utilization : " + totalUtilization);
                return schedulables;
            }
        }
    }

    private double[] generateUtilizations(){
        double[] utilizations = new double[n];
        double sumU = averageU;
        for(int i = 0; i < n; i++){
            double generated = 0.0;
            while(generated == 0.0){
                generated = random.nextDouble();
            }
            double nextSumU = sumU * Math.pow(generated, 1. / ((double) n - (double) i));
            utilizations[i] = sumU - nextSumU;
            sumU = nextSumU;
        }
        return utilizations;
    }


}
