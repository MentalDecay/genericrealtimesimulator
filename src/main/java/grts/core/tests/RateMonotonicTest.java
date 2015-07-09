package grts.core.tests;

import grts.core.schedulable.AbstractRecurrentTask;
import grts.core.schedulable.Schedulable;
import grts.core.taskset.TaskSet;
import java.util.OptionalDouble;
import java.util.function.Predicate;

public class RateMonotonicTest extends AbstractSchedulabilityTest implements SchedulabilityTest {

    /**
     * Creates a new Rate Monotonic Test. None parameters are needed.
     */
    public RateMonotonicTest() {
    }

    @Override
    public boolean isSchedulable(TaskSet taskSet) {
        int n = taskSet.getSchedulablesNumber();
        OptionalDouble testSum = taskSet.stream().mapToDouble(value -> {
            if(!(value instanceof AbstractRecurrentTask)){
                throw new IllegalArgumentException("The taskSet is not a recurrent one");
            }
            AbstractRecurrentTask task = (AbstractRecurrentTask) value;
            return (double) task.getWcet() / (double) task.getMinimumInterArrivalTime();
        }).reduce(Double::sum);
        if(!testSum.isPresent()){
            throw new IllegalArgumentException("Empty taskSet");
        }
        return testSum.getAsDouble() <= n * (Math.pow(2, (double) 1 / (double) n) - 1);
    }

    @Override
    public boolean isSchedulable(Schedulable schedulable, TaskSet taskSet) {
        throw new UnsupportedOperationException("Can't test the schedulability in Rate Monotonic on a task");
    }

    @Override
    public boolean isSchedulable(Schedulable schedulable, Schedulable otherSchedulable, TaskSet taskSet) {
        throw new UnsupportedOperationException("Can't test the schedulability in Rate Monotonic on two tasks");
    }
}
