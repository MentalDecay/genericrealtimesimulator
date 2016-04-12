package grts.core.simulator.events;

import grts.core.schedulable.Job;
import grts.core.simulator.Scheduler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class JobActivationEvent extends AbstractEventOnJob implements Event {

    /**
     * Creates a new Activate Job Event.
     * @param scheduler The scheduler which created the event.
     * @param time The time of the event.
     * @param priority the priority of the event.
     * @param job The job associated to the event.
     */
    public JobActivationEvent(Scheduler scheduler, long time, int priority, Job job) {
        super(scheduler, time, priority, job);
    }

    @Override
    public void handle() {
        getScheduler().addActiveJob(getJob());
        Constructor<?> constructorDeadlineCheck = null;
        Constructor<?> constructorChooseJob = null;
        Constructor<?> constructorJobActivation = null;
        try {
            constructorDeadlineCheck = EventMap.getEvent("DeadlineCheck").getConstructor(Scheduler.class, Long.class, Integer.class, Job.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            getScheduler().addEvent((Event) constructorDeadlineCheck.newInstance(getScheduler(), getJob().getDeadlineTime(), EventMap.getPriority("DeadlineCheck"), getJob()));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        try {
            constructorChooseJob = EventMap.getEvent("ChooseJob").getConstructor(Scheduler.class, Long.class, Integer.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            getScheduler().addEvent((Event) constructorChooseJob.newInstance(getScheduler(), getTime(), EventMap.getPriority("ChooseJob")));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        Job nextJob = getJob().getTask().getRealNextJob(getTime());
        try {
            constructorJobActivation = EventMap.getEvent("JobActivation").getConstructor(Scheduler.class, Long.class, Integer.class, Job.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            getScheduler().addEvent((Event) constructorJobActivation.newInstance(getScheduler(), nextJob.getActivationTime(), EventMap.getPriority("JobActivation"), nextJob));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "JobActivationEvent : " + getJob() + " time : " + getTime();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof JobActivationEvent)){
            return false;
        }
        JobActivationEvent event = (JobActivationEvent) obj;
        return getScheduler() == event.getScheduler() &&
                getTime() == event.getTime() &&
                getJob().equals(event.getJob());
    }

    @Override
    public String getName() {
        return "Activate Job Event";
    }
}
