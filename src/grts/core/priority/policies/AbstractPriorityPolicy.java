package grts.core.priority.policies;

import grts.core.taskset.TaskSet;

import java.util.Objects;

public abstract class AbstractPriorityPolicy implements IPriorityPolicy {

    private final String policyName;
    private final TaskSet taskSet;

    AbstractPriorityPolicy(String policyName, TaskSet taskSet) {
        this.taskSet = taskSet;
        this.policyName = Objects.requireNonNull(policyName);
    }

    @Override
    public String getPolicyName() {
        return policyName;
    }
}
