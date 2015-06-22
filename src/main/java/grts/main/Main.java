package grts.main;

import grts.core.architecture.Architecture;
import grts.core.json.parser.SimulatorJacksonParser;
import grts.core.priority.policies.IPriorityPolicy;
import grts.core.priority.policies.RateMonotonic;
import grts.core.processor.policies.FBBFirstFitDecreasing;
import grts.core.processor.policies.FirstFitDecreasingUtilizationPolicy;
import grts.core.processor.policies.IProcessorPolicy;
import grts.core.simulator.Simulator;
import grts.core.taskset.HyperPeriod;
import grts.core.taskset.TaskSet;
import grts.core.taskset.TaskSetFactory;
import grts.logger.EventLogger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

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

        InputStream inputStreamTasks;
        SimulatorJacksonParser parser;
        try {
            inputStreamTasks = Files.newInputStream(Paths.get("PeriodicTaskSet1.json"));
            InputStream inputStreamArchitecture = Files.newInputStream(Paths.get("ArchitectureTwoProcessors.json"));
//            InputStream inputStreamArchitecture = Files.newInputStream(Paths.get("ArchitectureMonoProcessor.json"));
            parser = new SimulatorJacksonParser(inputStreamTasks, inputStreamArchitecture);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        TaskSet ts;
        Architecture architecture;
        ts = TaskSetFactory.createTaskSetFromParser(parser);
        architecture = parser.parseArchitecture();
        System.out.println(architecture);
        EventLogger logger;
        LinkedList<String> eventsToLog = new LinkedList<>();
        eventsToLog.add("Activate Job Event");
        eventsToLog.add("Check Deadline Event");
//        eventsToLog.add("Choose Job Event");
//        eventsToLog.add("Continue Or Stop Execution Event");
        eventsToLog.add("Deadline Missed Event");
        eventsToLog.add("Preemption Event");
        eventsToLog.add("Start Job Execution Event");
        eventsToLog.add("Stop Job Execution Event");
        eventsToLog.add("Stop Simulation Event");
        try {
            logger = new EventLogger("logs", eventsToLog);
        } catch (IOException e) {
            System.err.println("Can't create a logger");
            return;
        }

        IPriorityPolicy policy = new RateMonotonic(ts);
//
//        Processor processor1 = new Processor(0);
//        Processor processor2 = new Processor(1);
//        Processor []processorArray = new Processor[2];
//        processorArray[0] = processor1;
//        processorArray[1] = processor2;
//        IProcessorPolicy processorPolicy = new RestrictedProcessorPolicy(architecture,  policy);
        IProcessorPolicy processorPolicy = new FBBFirstFitDecreasing(architecture, ts);
        Simulator simulator = new Simulator(ts, policy, processorPolicy, logger);
        long timer = HyperPeriod.compute(ts);
        simulator.simulate(timer);
        logger.writeJson();

    }
}
