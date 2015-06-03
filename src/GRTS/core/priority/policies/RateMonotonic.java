package GRTS.core.priority.policies;

import GRTS.core.schedulable.Job;
import GRTS.core.taskset.ITaskSet;

import java.util.Comparator;
import java.util.List;

public class RateMonotonic extends AbstractPriorityPolicy implements IPriorityPolicy {

    /**
     * Uses a rate monotonic priority policy. Uses fifo policy in case of equals periods.
     */


    public RateMonotonic(ITaskSet taskSet) {
        super("Rate Monotonic", taskSet);
    }


    @Override
    public Job choseJobToExecute(List<Job> activeJobs, long time){
        // Highest priority scheduling
        if(activeJobs.isEmpty()){
            return null;
        }
        Job jobToExecute = activeJobs.stream().min(Comparator.comparing(Job::getTaskInterArrivalTime)).get();
        return jobToExecute;
    }
}
