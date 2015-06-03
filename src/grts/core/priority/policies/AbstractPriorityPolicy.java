package grts.core.priority.policies;

import grts.core.taskset.ITaskSet;

import java.util.Objects;

public abstract class AbstractPriorityPolicy implements IPriorityPolicy {

    private final String policyName;
    private final ITaskSet taskSet;

    AbstractPriorityPolicy(String policyName, ITaskSet taskSet) {
        this.taskSet = taskSet;
        this.policyName = Objects.requireNonNull(policyName);
    }

    @Override
    public String getPolicyName() {
        return policyName;
    }
}
