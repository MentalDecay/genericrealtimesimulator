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

package grts.core.simulator;

import grts.core.priority.policies.IPriorityPolicy;
import grts.core.processor.policies.IProcessorPolicy;
import grts.core.schedulable.Job;
import grts.core.simulator.events.ActivateJobEvent;
import grts.core.simulator.events.Event;
import grts.core.simulator.events.EventQueue;
import grts.core.taskset.ITaskSet;

import java.util.List;
import java.util.TreeSet;

/**
 * This class represents an event-triggered simulator.
 * @author Tristan Fautrel
 */
public class Simulator {

    private final Scheduler scheduler;
    private final ITaskSet taskSet;
    private final EventQueue eventQueue = new EventQueue();

    /**
     * Creates a new Simulator.
     * @param taskSet the tasks set of the simulator.
     * @param priorityPolicy the priority policy of the simulator.
     * @param processorPolicy the processor policy of the simulator.
     */
    public Simulator(ITaskSet taskSet, IPriorityPolicy priorityPolicy, IProcessorPolicy processorPolicy) {
        this.scheduler = new Scheduler(priorityPolicy, processorPolicy);
        this.taskSet = taskSet;
    }

    /**
     * Simulates the scheduling of the tasks set during the time or until an event stops it.
     * @param time the maximum time of the simulation.
     */
    public void simulate(long time){
        initEventQueue();
        System.out.println("init : ");
        eventQueue.forEach(System.out::println);
        System.out.println("\n\n\n");
        while(!scheduler.isOver()){
            Event event = eventQueue.poll();
            System.out.println("Process event : " + event);
            System.out.println("Other events : ");
            eventQueue.forEach(System.out::println);
            if(event.getTime() > time){
                //Simulation is over.
                System.out.println("End of simulation by timer");
                return;
            }
            List<Event> events = scheduler.performEvent(event);
            events.forEach(eventQueue::offer);
            System.out.println("\nCreated events : ");
            events.forEach(System.out::println);
            System.out.println("\n\n\n");
        }
        System.out.println("End of simulation by deadline missed");
    }

    /**
     * Creates the first events of the simulation. The events created are the first ActivateJobEvent of each task.
     */
    private void initEventQueue(){
        taskSet.getRecurrentTasks().forEach(task -> {
            Job firstJob = task.getFirstJob();
            System.out.println(firstJob);
            eventQueue.offer(new ActivateJobEvent(scheduler, firstJob.getActivationTime(), firstJob));
        });
    }

}