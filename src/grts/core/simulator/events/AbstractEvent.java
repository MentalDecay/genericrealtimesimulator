package grts.core.simulator.events;

import com.sun.istack.internal.NotNull;
import grts.core.simulator.Scheduler;

import java.util.Objects;

public abstract class  AbstractEvent implements Comparable<AbstractEvent>, IEvent {

    private final Scheduler scheduler;
    private final long time;

    protected AbstractEvent(Scheduler scheduler, long time) {
        this.scheduler = scheduler;
        this.time = time;
    }

    @Override
    public int compareTo(AbstractEvent o) {
        if(getTime() != o.getTime()){
            return (int) (getTime() - o.getTime());
        }
        else {
            if (o.getPriority() == 7 && getPriority() == 7) {
                return 0;
            } else {
                if (o.getPriority() == getPriority()) {
                    if(toString().equals(o.toString())){
                        return 0;
                    }
                    return 1;
                } else {
                    return getPriority() - o.getPriority();
                }
            }
        }
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    @Override
    public long getTime() {
        return time;
    }

    protected abstract int getPriority();

}
