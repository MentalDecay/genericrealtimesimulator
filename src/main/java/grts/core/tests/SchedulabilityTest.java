package grts.core.tests;

import grts.core.architecture.Architecture;
import grts.core.taskset.TaskSet;

import java.util.Optional;
import java.util.function.Predicate;

public interface SchedulabilityTest {

    /**
     * Runs a test to know if the TaskSet is schedulable. This test may not be sure (it can only be a sufficient condition).
     * @param taskSet The TaskSet to test.
     * @return true if the TaskSet is schedulable.
     */
    boolean isTaskSetSchedulable(TaskSet taskSet);

    /**
     * Transforms the test of schedulability into a predicate.
     * @return The predicate which represents the test of schedulability.
     */
    Predicate<TaskSet> getPredicate();

    /**
     * Get the Architecture.
     * @return An optional of Architecture which is empty if the test doesn't need to know the architecture.
     */
    Optional<Architecture> getArchitecture();
}
