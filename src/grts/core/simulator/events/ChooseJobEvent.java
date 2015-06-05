package grts.core.simulator.events;


import grts.core.schedulable.Job;
import grts.core.simulator.Scheduler;

public class ChooseJobEvent extends AbstractEvent implements IEvent {

    public ChooseJobEvent(Scheduler scheduler, long time) {
        super(scheduler, time);
    }

    @Override
    public void doEvent() {
        if(!getScheduler().getActivatedJobs().isEmpty()){
            Job jobToExecute = getScheduler().getPolicy().choseJobToExecute(getScheduler().getActivatedJobs(), getTime());
            if(jobToExecute != null) System.out.println("job to execute : " + jobToExecute);
            if(getScheduler().getExecutingJob() == null){
                System.out.println("executing job : " + getScheduler().getExecutingJob());
                getScheduler().addEvent(new StartJobExecutionEvent(getScheduler(), getTime(), jobToExecute));
            }
            else if (getScheduler().getExecutingJob() != jobToExecute){
                getScheduler().addEvent(new PreemptionEvent(getScheduler(), getTime(), jobToExecute));
            }
            else{
                //Nothing to do, the executing job has the highest priority
            }
        }
    }

    @Override
    public String toString() {
        return "ChooseJobEvent : time : " + getTime();
    }

    @Override
    protected int getPriority() {
        return 7;
    }
}
