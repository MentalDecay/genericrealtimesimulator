package grts.core.tests;

import grts.core.architecture.Architecture;
import grts.core.taskset.TaskSet;

import java.util.HashMap;
import java.util.function.Predicate;

public class SchedulabilityTestFactory {
    private final HashMap<String, Predicate<TaskSet>> map = new HashMap<>();

    /**
     * Creates a new Schedulability Test Factory.
     * Tests available :
     * Rate Monotonic (with the sufficient condition).
     * @param architecture The architecture needed to some tests.
     */
    public SchedulabilityTestFactory(Architecture architecture){
        RateMonotonicTest rateMonotonicTest = new RateMonotonicTest();
        map.put("Rate Monotonic", rateMonotonicTest::isSchedulable);
    }

    /**
     * Get the test from the name if it exists or throw an IllegalArgumentException.
     * @param name The name of the required test.
     * @return The predicate of TaskSet which is the test of schedulability.
     */
    public Predicate<TaskSet> getTaskSetTest(String name){
        return map.getOrDefault(name, taskSet -> {
            throw new UnsupportedOperationException("The " + name + " is not implemented");
        });
    }
}
