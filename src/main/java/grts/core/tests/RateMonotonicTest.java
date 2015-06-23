package grts.core.tests;

import grts.core.priority.policies.RateMonotonic;
import grts.core.schedulable.AbstractRecurrentTask;
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
    public boolean isTaskSetSchedulable(TaskSet taskSet) {
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
    public Predicate<TaskSet> getPredicate() {
        return this::isTaskSetSchedulable;
    }
}
