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
import grts.core.generator.GlobalNetworkGenerator;
import grts.core.json.parser.SimulatorJacksonParser;
import grts.core.priority.policies.ClassicOPA;
import grts.core.priority.policies.DeadlineMonotonic;
import grts.core.priority.policies.EarliestDeadlineFirst;
import grts.core.priority.policies.RateMonotonic;
import grts.core.processor.policies.IProcessorPolicy;
import grts.core.processor.policies.MonoProcessor;
import grts.core.processor.policies.offline.LPDPM;
import grts.core.schedulable.Schedulable;
import grts.core.simulator.Simulator;
import grts.core.simulator.events.*;
import grts.core.taskset.HyperPeriod;
import grts.core.taskset.TaskSet;
import grts.core.taskset.TaskSetFactory;
import grts.core.tests.ResponseTimeTest;
import grts.logger.EventLogger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
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
        try {
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
//            ResponseTimeTest responseTimeTest = new ResponseTimeTest();
//            List<Schedulable> schedulables = ts.stream().collect(Collectors.toList());
//            Schedulable taskToTest = schedulables.remove(1);
//            System.out.println("taskToTest : " + taskToTest);
//            responseTimeTest.isSchedulable(taskToTest, new TaskSet(schedulables));
//            ClassicOPA opa = new ClassicOPA(ts);
        } catch (NoSuchFileException e) {
            System.err.println("File not found: " + e.getLocalizedMessage());
            usage();
            System.exit(EXIT_FAILURE);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        for(int i = 0; i < 500; i++) {
//            GlobalNetworkGenerator globalNetworkGenerator = new GlobalNetworkGenerator(50, 10, 0.75, 0.75);
//            globalNetworkGenerator.generateGlobalNetwork(1000, 0.01);
//        }
//        PeriodicTasksGenerator uUnifast = new PeriodicTasksGenerator(50, 20, 1.);
//        TaskSet taskSet = uUnifast.generateImplicitPeriodicTaskSet(1000, 0.1);
//        taskSet.stream().forEach(System.out::println);

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
