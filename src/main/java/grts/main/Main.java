/* Copyright (c) 2015, Tristan Fautrel
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package grts.main;

import grts.core.architecture.Architecture;
import grts.core.json.parser.SimulatorJacksonParser;
import grts.core.priority.policies.EarliestDeadlineFirst;
import grts.core.priority.policies.IPriorityPolicy;
import grts.core.priority.policies.RateMonotonic;
import grts.core.processor.policies.IProcessorPolicy;
import grts.core.processor.policies.RestrictedProcessorPolicy;
import grts.core.simulator.Simulator;
import grts.core.taskset.HyperPeriod;
import grts.core.taskset.TaskSet;
import grts.core.taskset.TaskSetFactory;
import grts.logger.EventLogger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

/**
 * @author Tristan Fautrel
 */
public class Main {

    private final static int EXIT_FAILURE = 1;

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

        if (args.length < 2) {
            usage();
            System.exit(EXIT_FAILURE);
        }

        InputStream inputStreamTasks;
        InputStream inputStreamArchitecture;
        SimulatorJacksonParser parser;
        try {
            inputStreamTasks = Files.newInputStream(Paths.get(args[0]));
            inputStreamArchitecture = Files.newInputStream(Paths.get(args[1]));
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
        IProcessorPolicy processorPolicy = new RestrictedProcessorPolicy(architecture.getProcessors(),  policy);
//        IProcessorPolicy processorPolicy = new MonoProcessor(policy);
        Simulator simulator = new Simulator(ts, policy, processorPolicy, logger);
        long timer = HyperPeriod.compute(ts);
        simulator.simulate(timer);
        logger.writeJson();
    }

    private static void usage() {
        Path current = Paths.get(System.getProperty("user.dir"));
        Path classPath = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        Path target = current.relativize(classPath);
        StringBuilder sb = new StringBuilder();
        sb.append("java").append(' ');
        if (classPath.toString().endsWith(".jar")) {
            sb.append("-jar").append(' ').append(target);
        } else {
            sb.append("-cp").append(' ').append(target).append(' ').append(Main.class.getCanonicalName());
        }
        sb.append(' ').append("TASKSET.json").append(' ').append("ARCHITECTURE.json");
        System.out.println(sb.toString());
    }

}