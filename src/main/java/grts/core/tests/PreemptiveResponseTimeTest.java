package grts.core.tests;

import grts.core.schedulable.AbstractRecurrentTask;
import grts.core.schedulable.Schedulable;
import grts.core.taskset.TaskSet;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class PreemptiveResponseTimeTest extends AbstractSchedulabilityTest implements SchedulabilityTest {

    public PreemptiveResponseTimeTest(){
        super(null);
    }

    @Override
    public boolean isSchedulable(TaskSet taskSet) {
        throw new UnsupportedOperationException("The response time needs a schedulable to test");
    }

    @Override
    public boolean isSchedulable(Schedulable schedulable, Schedulable otherSchedulable, TaskSet taskSet) {
        throw new UnsupportedOperationException("The response time needs a schedulable to test");
    }

    /**
     * Tests the response time of the task.
     * @param schedulable The schedulable to test.
     * @param taskSet The set of tasks with higher priorities than schedulable.
     * @return true if the task is schedulable.
     */
    @Override
    public boolean isSchedulable(Schedulable schedulable, TaskSet taskSet) {
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
        if(!(schedulable instanceof AbstractRecurrentTask)){
            throw new IllegalArgumentException("The task to test must be a recurrent one");
        }
        AbstractRecurrentTask task = (AbstractRecurrentTask) schedulable;
        int q = 0;
        while(true){
            long w = computeW(task, schedulables, q);
            if(w == -1){
                return false;
            }
            responseTimes.add(w - q * task.getMinimumInterArrivalTime());
            if(w <= (q+1) * task.getMinimumInterArrivalTime()){
                break;
            }
            q++;
        }
        System.out.println(responseTimes.stream().max(Long::compare).get());
        return responseTimes.stream().max(Long::compare).get() <= schedulable.getDeadline();
    }

    private long computeW(AbstractRecurrentTask task, List<AbstractRecurrentTask> higherPriorityTasks, long q){
        long t = q * task.getMinimumInterArrivalTime();
        if(t == 0){
            t = 1;
        }
        while(true){
            long previousW = t;
            long nextW = (q + 1) * task.getWcet() +
                    higherPriorityTasks.stream()
                            .mapToLong(higherPriorityTask -> ((long) Math.ceil(
                                    (double) previousW / (double) higherPriorityTask.getMinimumInterArrivalTime()))
                                    * higherPriorityTask.getWcet())
                            .sum();
            if(nextW == previousW){
                return nextW - q * task.getMinimumInterArrivalTime();
            }
            if (nextW > q * task.getMinimumInterArrivalTime() + task.getDeadline()) {
                return -1;
            }
            t = nextW;
        }
    }
}
