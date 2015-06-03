package GRTS.core.priority.policies;

import GRTS.core.schedulable.Job;

import java.util.List;

public interface IPriorityPolicy {

    /**
     * Get the name of the policy.
     * @return name of the policy
     */
    String getPolicyName();

    /**
     * Select the job which should be executing according to the policy implemented.
     * @param activeJobs the list of current active jobs
     */
    Job choseJobToExecute(List<Job> activeJobs, long time);

}
