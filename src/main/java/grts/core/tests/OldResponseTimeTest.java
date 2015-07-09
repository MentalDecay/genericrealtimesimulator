package grts.core.tests;

import grts.core.architecture.Architecture;
import grts.core.schedulable.AbstractRecurrentTask;
import grts.core.schedulable.Schedulable;
import grts.core.taskset.TaskSet;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class OldResponseTimeTest extends AbstractSchedulabilityTest implements SchedulabilityTest{

    public OldResponseTimeTest() {
        super();
    }

    @Override
    public boolean isSchedulable(TaskSet taskSet) {
        throw new UnsupportedOperationException("The response time needs a schedulable to test");
    }

//    TODO Add offsets in the computation
    @Override
    public boolean isSchedulable(Schedulable schedulable, TaskSet taskSet) {
        if(schedulable.getOffset() != 0 || taskSet.stream().anyMatch(schedulable1 -> schedulable1.getOffset() != 0)){
            throw new UnsupportedOperationException("Can't compute the response time on a task with an offset");
        }
        List<Schedulable> schedulables = taskSet.stream().collect(Collectors.toList());
        schedulables.add(schedulable);
        TaskSet taskSetToTest = new TaskSet(schedulables);
        if(taskSetToTest.stream().mapToDouble(value -> {
            if(!(value instanceof AbstractRecurrentTask)){
                throw new IllegalArgumentException("Can't compute the response time of a non recurrent task");
            }
            AbstractRecurrentTask task = (AbstractRecurrentTask) value;
            return (double) task.getWcet() / (double) task.getMinimumInterArrivalTime();
        }).sum() > 1){
            throw new IllegalArgumentException("This TaskSet has an utilization greater than 1");
        }
        long busyPeriod = computeBusyPeriod(taskSetToTest);
        List<Long> responseTimes = new LinkedList<>();
        AbstractRecurrentTask task = (AbstractRecurrentTask) schedulable;
        long maxM = (long) Math.ceil((double) busyPeriod / (double) task.getMinimumInterArrivalTime());
        List<AbstractRecurrentTask> higherTasks = taskSet.stream().map(schedulable1 -> (AbstractRecurrentTask) schedulable1).collect(Collectors.toList());
        LongStream.range(1, maxM+1).forEach(m -> {
            long t = (m - 1) * task.getMinimumInterArrivalTime();
            if (t == 0) {
                t = 1;
            }
            boolean add = true;
            while (true) {
                long res = computeResponseTime(t, m, task, higherTasks);
                if (res == t) {
                    break;
                }
                t = res;
                if (t > (m - 1) * task.getMinimumInterArrivalTime() + task.getDeadline()) {
                    add = false;
                    break;
                }
            }
            if(add) {
                long responseTime = t - (m - 1) * task.getMinimumInterArrivalTime();
                responseTimes.add(responseTime);
            }
        });
        if(responseTimes.size() != maxM){
            return false;
        }
        System.out.println(responseTimes.stream().max(Long::compare).get());
        return responseTimes.stream().max(Long::compare).get() <= schedulable.getDeadline();
//        return false;
    }

    @Override
    public boolean isSchedulable(Schedulable schedulable, Schedulable otherSchedulable, TaskSet taskSet) {
        throw new UnsupportedOperationException("Can't test the schedulability in Rate Monotonic on two tasks");
    }

    private long computeResponseTime(long t, long m, AbstractRecurrentTask task, List<AbstractRecurrentTask> higherTasks){
        return m * task.getWcet() + higherTasks.stream()
                .mapToLong(value -> ((long) Math.ceil((double) t / (double) value.getMinimumInterArrivalTime())) * value.getWcet())
                .sum();
    }

    private long computeBusyPeriod(TaskSet taskSet){
        long currentLi = 0;
//      Begins to 1 because the task with the lowest priority is unknown.
        long nextLi = 1;
        while(currentLi != nextLi){
            currentLi = nextLi;
            long li = currentLi;
            nextLi = taskSet.stream().mapToLong(value -> {
                if(!(value instanceof AbstractRecurrentTask)){
                    throw new IllegalArgumentException("Can't compute the response time of a non recurrent task");
                }
                AbstractRecurrentTask task = (AbstractRecurrentTask) value;
                return ((long) Math.ceil((double) li / (double) task.getMinimumInterArrivalTime())) * task.getWcet();
            }).sum();
        }
        System.out.println("busy time : " + currentLi);
        return currentLi;
    }
}
