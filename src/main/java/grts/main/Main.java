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
import grts.core.exceptions.UnschedulableException;
import grts.core.generator.GlobalNetworkGenerator;
import grts.core.generator.PeriodicTasksGenerator;
import grts.core.json.parser.SimulatorJacksonParser;
import grts.core.priority.policies.ClassicOPA;
import grts.core.processor.policies.IProcessorPolicy;
import grts.core.processor.policies.MonoProcessor;
import grts.core.schedulable.AbstractRecurrentTask;
import grts.core.schedulable.PeriodicTask;
import grts.core.schedulable.Schedulable;
import grts.core.simulator.Simulator;
import grts.core.simulator.events.*;
import grts.core.taskset.*;
import grts.core.tests.GlobalNetworkSchedulabilityTest;
import grts.core.tests.NonPreemptiveResponseTimeTest;
import grts.core.tests.PreemptiveResponseTimeTest;
import grts.logger.EventLogger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Tristan Fautrel
 */
public class Main {

    private final static int EXIT_FAILURE = 1;

    public static void main(String[] args) {
        if (args.length < 2) {
            usage();
            System.exit(EXIT_FAILURE);
        }

        InputStream inputStreamTasks;
        InputStream inputStreamArchitecture;
        SimulatorJacksonParser parser;
        TaskSet ts;
        Architecture architecture;
        EventLogger logger;
        /*try {
            inputStreamTasks = Files.newInputStream(Paths.get(args[0]), StandardOpenOption.READ);
            inputStreamArchitecture = Files.newInputStream(Paths.get(args[1]), StandardOpenOption.READ);
            parser = new SimulatorJacksonParser(inputStreamTasks, inputStreamArchitecture);
            ts = TaskSetFactory.createTaskSetFromParser(parser);
            architecture = parser.parseArchitecture();
            System.out.println("Hyper Period : " + HyperPeriod.compute(ts));
            IProcessorPolicy processorPolicy = new MonoProcessor(architecture, new ClassicOPA(ts));
            logger = new EventLogger("logs", JobActivationEvent.class, DeadlineCheckEvent.class,
                    DeadlineMissedEvent.class, PreemptionEvent.class, JobExecutionStartEvent.class,
                    JobExecutionStopEvent.class, SimulationStopEvent.class);
            Simulator simulator = new Simulator(ts, processorPolicy, logger);
            long timer = HyperPeriod.compute(ts);
            simulator.simulate(timer);
            logger.writeJson();
//            PreemptiveResponseTimeTest responseTimeTest = new PreemptiveResponseTimeTest();
//            List<Schedulable> schedulables = ts.stream().collect(Collectors.toList());
//            Schedulable taskToTest = schedulables.remove(2);
//            System.out.println("taskToTest : " + taskToTest);
//            System.out.println(responseTimeTest.isSchedulable(taskToTest, new TaskSet(schedulables)));
//            taskToTest = schedulables.remove(0);
//            System.out.println("taskToTest : " + taskToTest);
//            System.out.println(responseTimeTest.isSchedulable(taskToTest, new TaskSet(schedulables)));
//            ClassicOPA opa = new ClassicOPA(ts);
        } catch (NoSuchFileException e) {
            System.err.println("File not found: " + e.getLocalizedMessage());
            usage();
            System.exit(EXIT_FAILURE);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /*List<CANNetwork> networks = new LinkedList<>();
        AbstractRecurrentTask task1 = new PeriodicTask(6, 2, 6, 0, "t1");
        AbstractRecurrentTask task2 = new PeriodicTask(7, 3, 7, 0, "t2");
        AbstractRecurrentTask task3 = new PeriodicTask(14, 4, 13, 0, "t3");
        List<AbstractRecurrentTask> tasksR1 = new LinkedList<>();
        tasksR1.add(task1);
        tasksR1.add(task3);
        List<AbstractRecurrentTask> tasksR2 = new LinkedList<>();
        tasksR2.add(task2);
        networks.add(new CANNetwork(tasksR1));
        networks.add(new CANNetwork(tasksR2));
        networks.get(1).addExternalSchedulable(task3, networks.get(0));
        GlobalNetwork globalNetwork = new GlobalNetwork(networks);

//        for(int i = 0; i < 50000; i++) {
//        GlobalNetworkGenerator globalNetworkGenerator = new GlobalNetworkGenerator(5, 2, 0.5, 0.75);
//        GlobalNetwork globalNetwork = globalNetworkGenerator.generateGlobalNetwork(1000, 0.01);
//            System.out.println("validity : " + globalNetwork.checkValidity());
        GlobalNetworkSchedulabilityTest test = new GlobalNetworkSchedulabilityTest();
        System.out.println(test.isSchedulable(globalNetwork));*/

        AbstractRecurrentTask task1 = new PeriodicTask(12, 2, 12, 0, "t1");
        AbstractRecurrentTask task2 = new PeriodicTask(14, 3, 14, 0, "t2");
        AbstractRecurrentTask task3 = new PeriodicTask(14, 4, 13, 0, "t3");

//        NonPreemptiveResponseTimeTest test = new NonPreemptiveResponseTimeTest();
        LinkedList<Schedulable> tasks = new LinkedList<>();
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(task1);
//        System.out.println(test.computeResponseTime(task1, null, new TaskSet(tasks)));
        ClassicOPA opa;
        try {
            opa = new ClassicOPA(new TaskSet(tasks));
        } catch (UnschedulableException e) {
            System.out.println("no result");
            return;
        }
        System.out.println("result : ");
        System.out.println(opa.getPriorities());
        //        }


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
