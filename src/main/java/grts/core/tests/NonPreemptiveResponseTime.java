package grts.core.tests;

import grts.core.schedulable.AbstractRecurrentTask;
import grts.core.schedulable.Schedulable;
import grts.core.taskset.TaskSet;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class NonPreemptiveResponseTime {


    public boolean isSchedulable(Schedulable schedulable, Schedulable otherSchedulable, TaskSet taskSet){
        if(schedulable.getOffset() != 0 || taskSet.stream().anyMatch(schedulable1 -> schedulable1.getOffset() != 0)){
            throw new UnsupportedOperationException("Can't compute the response time on a task with an offset");
        }
        List<Long> responseTimes = new LinkedList<>();
        List<AbstractRecurrentTask> schedulables = taskSet.stream().map(schedulable1 -> {
            if(!(schedulable1 instanceof AbstractRecurrentTask)){
                throw new IllegalArgumentException("Can't compute the response time if all the tasks are not recurrent");
            }
            return (AbstractRecurrentTask) schedulable1;
        }).collect(Collectors.toList());
        if(!(schedulable instanceof AbstractRecurrentTask) || (otherSchedulable != null && !(otherSchedulable instanceof AbstractRecurrentTask))){
            throw new IllegalArgumentException("The task to test and the task with the lowest priority must be a recurrent one");
        }
        AbstractRecurrentTask task = (AbstractRecurrentTask) schedulable;

        long cost = 0;
        if(otherSchedulable != null){
            cost = otherSchedulable.getWcet() - 1;
        }

        long maxQ = (long) Math.floor((double)computeLi(task, cost, schedulables) / task.getMinimumInterArrivalTime());

        long prevW = 0;
        long currW;
        System.out.println("maxQ = " + maxQ);
        /*for(int q = 0; q <= maxQ; q++){
            while(true){
                long prevWCp = prevW;
                currW = cost + q * task.getWcet() + schedulables.stream()
                        .mapToLong(value -> (1l + (long) (Math.floor((double) prevWCp / (double) value.getMinimumInterArrivalTime()))) * value.getWcet())
                        .sum();
                if(prevW == currW){
                    responseTimes.add(currW + task.getWcet() - q * task.getMinimumInterArrivalTime());
                    break;
                }
                prevW = currW;
            }
        }*/
        for(int q = 0; q <= maxQ; q++){
            long prevWCp = prevW;
            currW = cost + q * task.getWcet() + schedulables.stream()
                    .mapToLong(value -> (1l + (long) (Math.floor((double) prevWCp / (double) value.getMinimumInterArrivalTime()))) * value.getWcet())
                    .sum();
            //            if(prevW == currW){
            responseTimes.add(currW + task.getWcet() - q * task.getMinimumInterArrivalTime());
            //                break;
            //            }
            prevW = currW;
        }
        System.out.println("Response Time : " + responseTimes.stream().max(Long::compare).get());
        return responseTimes.stream().max(Long::compare).get() <= schedulable.getDeadline();
    }

    private long computeLi(AbstractRecurrentTask task, long cost, List<AbstractRecurrentTask> higherTasks){
        long currL;
        long prevL = 1;
        higherTasks.add(task);
        System.out.println("cost :" + cost);
        while(true){
            long prevLcp = prevL;
            currL = cost + higherTasks.stream()
                    .mapToLong(value -> ((long) Math.ceil((double) prevLcp / (double) value.getMinimumInterArrivalTime())) * value.getWcet())
                    .sum();
            if(currL == prevL){
                return currL;
            }
            prevL = currL;
        }
    }
}
