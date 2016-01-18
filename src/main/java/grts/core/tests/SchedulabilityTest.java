package grts.core.tests;

import grts.core.schedulable.Schedulable;
import grts.core.taskset.TaskSet;

public interface SchedulabilityTest {

    /**
     * Runs a test to know if the TaskSet is schedulable. This test may not be sure (it can only be a sufficient condition).
     * @param taskSet The TaskSet to test.
     * @return true if the TaskSet is schedulable.
     */
    boolean isSchedulable(TaskSet taskSet);

    /**
     * Tests if the Schedulable can be scheduled on this TaskSet.
     * @param schedulable The schedulable to test.
     * @param taskSet The TaskSet where the schedulable should be added.
     * @return true if the task is schedulable in this TaskSet.
     */
    boolean isSchedulable(Schedulable schedulable, TaskSet taskSet);

    boolean isSchedulable(Schedulable schedulable, Schedulable otherSchedulable, TaskSet taskSet);
}
