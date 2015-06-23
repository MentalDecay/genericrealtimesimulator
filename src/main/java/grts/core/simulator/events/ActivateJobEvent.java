package grts.core.simulator.events;

import grts.core.schedulable.Job;
import grts.core.simulator.Scheduler;

public class ActivateJobEvent extends AbstractEventOnJob implements Event {

    /**
     * Creates a new Activate Job Event.
     * @param scheduler The scheduler which created the event.
     * @param time The time of the event.
     * @param job The job associated to the event.
     */
    public ActivateJobEvent(Scheduler scheduler, long time, Job job) {
        super(scheduler, time, job);
    }

    @Override
    public void handle() {
        getScheduler().addActiveJob(getJob());

        getScheduler().addEvent(new CheckDeadlineEvent(getScheduler(), getJob().getDeadlineTime(), getJob()));
        getScheduler().addEvent(new ChooseJobEvent(getScheduler(), getTime()));
        Job nextJob = getJob().getTask().getRealNextJob(getTime());
        getScheduler().addEvent(new ActivateJobEvent(getScheduler(), nextJob.getActivationTime(), nextJob));
    }

    @Override
    public String toString() {
        return "ActivateJobEvent : " + getJob() + " time : " + getTime();
    }

    @Override
    public int getPriority() {
        return 6;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ActivateJobEvent)){
            return false;
        }
        ActivateJobEvent event = (ActivateJobEvent) obj;
        return getScheduler() == event.getScheduler() &&
                getTime() == event.getTime() &&
                getJob().equals(event.getJob());
    }

    @Override
    public String getName() {
        return "Activate Job Event";
    }
}
