package grts.main;

import grts.core.priority.policies.EarliestDeadlineFirst;
import grts.core.priority.policies.IPriorityPolicy;
import grts.core.priority.policies.RateMonotonic;
import grts.core.processor.policies.IProcessorPolicy;
import grts.core.processor.policies.InnocentGlobalPolicy;
import grts.core.processor.policies.MonoProcessor;
import grts.core.processor.policies.RestrictedProcessorPolicy;
import grts.core.schedulable.PeriodicTask;
import grts.core.schedulable.Schedulable;
import grts.core.simulator.Processor;
import grts.core.simulator.Simulator;
import grts.core.taskset.HyperPeriod;
import grts.core.taskset.TaskSet;
import grts.core.taskset.TaskSetFactory;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
//        AbstractRecurrentTask t1 = new PeriodicTask(7, 2, 7, 0, "t1");
//        AbstractRecurrentTask t2 = new PeriodicTask(11, 3, 11, 0, "t2");
//        AbstractRecurrentTask t3 = new PeriodicTask(13, 5, 13, 0, "t3");
//        AbstractRecurrentTask t1 = new PeriodicTask(4, 1, 4, 0, "t1");
//        AbstractRecurrentTask t2 = new PeriodicTask(5, 2, 5, 0, "t2");
//        AbstractRecurrentTask t3 = new PeriodicTask(10, 3, 10, 0, "t3");
//        AbstractRecurrentTask t1 = new SporadicTask(7, 2, 7, 0, "t1");
//        AbstractRecurrentTask t2 = new SporadicTask(11, 3, 11, 0, "t2");
//        AbstractRecurrentTask t3 = new SporadicTask(13, 5, 13, 0, "t3");


//        List<AbstractRecurrentTask> tasks = new LinkedList<>();
//        tasks.add(t1);
//        tasks.add(t2);
//        tasks.add(t3);
//        TaskSet ts = new TaskSet(tasks);
//        IPriorityPolicy policy = new EarliestDeadlineFirst(ts);
//        Logger logger = null;
//        try {
//            logger = new Logger("logs");
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.err.println("Fail logger creation");
//            return;
//        }
//        SchedulerTimeTriggedLegacy scheduler = new SchedulerTimeTriggedLegacy(ts, policy, logger);
//        scheduler.schedule(ts.getHyperPeriod());
//        logger.silentlyClose();


//        Processor processor1 = new Processor(0);
//        Processor []processorArray = new Processor[1];
//        processorArray[0] = processor1;
//        IProcessorPolicy processorPolicy = new RestrictedProcessorPolicy(processorArray, policy);
////        IProcessorPolicy processorPolicy = new MonoProcessor(policy);
//        Simulator simulator = new Simulator(ts, policy, processorPolicy);
//        long timer = ts.getHyperPeriod();
//        simulator.simulate(timer);

//        AbstractRecurrentTask t1 = new PeriodicTask(4, 2, 4, 0, "t1");
//        AbstractRecurrentTask t2 = new PeriodicTask(8, 4, 8, 0, "t2");
//        AbstractRecurrentTask t3 = new PeriodicTask(16, 8, 16, 0, "t3");
//        AbstractRecurrentTask t1 = new SporadicTask(4, 2, 4, 0, "t1");
//        AbstractRecurrentTask t2 = new SporadicTask(8, 4, 8, 0, "t2");
//        AbstractRecurrentTask t3 = new SporadicTask(16, 8, 16, 0, "t3");
//        List<ITask> tasks = new LinkedList<>();
//        tasks.add(t1);
//        tasks.add(t2);
//        tasks.add(t3);
//        ITaskSet ts = new TaskSet(tasks);
//        IPriorityPolicy policy = new EarliestDeadlineFirst(ts);
//
//        Processor processor1 = new Processor(0);
//        Processor processor2 = new Processor(1);
//        Processor []processorArray = new Processor[2];
//        processorArray[0] = processor1;
//        processorArray[1] = processor2;
//        IProcessorPolicy processorPolicy = new RestrictedProcessorPolicy(processorArray, policy);
//        Simulator simulator = new Simulator(ts, policy, processorPolicy);
//        long timer = ts.getHyperPeriod();
//        simulator.simulate(timer);

        TaskSet ts = null;
        try {
            ts = TaskSetFactory.createTaskSetFromFile("PeriodicTaskSet1.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        IPriorityPolicy policy = new EarliestDeadlineFirst(ts);

        Processor processor1 = new Processor(0);
        Processor processor2 = new Processor(1);
        Processor []processorArray = new Processor[2];
        processorArray[0] = processor1;
        processorArray[1] = processor2;
//        IProcessorPolicy processorPolicy = new RestrictedProcessorPolicy(processorArray, policy);
        IProcessorPolicy processorPolicy = new MonoProcessor(policy);
        Simulator simulator = new Simulator(ts, policy, processorPolicy);
        assert ts != null;
        long timer = HyperPeriod.compute(ts);
        simulator.simulate(timer);

    }
}
