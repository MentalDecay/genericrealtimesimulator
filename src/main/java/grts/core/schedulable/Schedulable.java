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

package grts.core.schedulable;

/**
 * This interface represents a generic schedulable object.
 * @author Tristan Fautrel
 */
public interface Schedulable {

    /**
     * Get the Worst Case Execution Time of the task.
     * @return WCET of the task
     */
    long getWcet();

    /**
     * Get the deadline of the task.
     * @return deadline of the task
     */
    long getDeadline();

    /**
     * Get the name of the task
     * @return String name
     */
    String getName();

    /**
     * Get the offset of the task.
     * @return the offset of the task
     */
    long getOffset();

    /**
     * Get the time of the next activation of a job. Returns the current time if the next activation is at this time.
     * Also updates the next job if needed.
     * @param time when the task should get the time of the next activation of a job.
     * @return the time of the next activation of a job
     */
    long getNextActivationTime(long time);

    /**
     * Get the next job of the task. Should only be used after checking if time matches with the next activation time to avoid jobs redundancy.
     * @return the next job of the task
     */
    Job getNextJob(long time);

    /**
     * Get the next job according to the time. If the time is equal or superior to the next job returned, a new job is created.
     * @param time when the task should return a new job
     * @return the new Job created by the task or previously created if the time is inferior to the activation of the next job
     */
    Job getRealNextJob(long time);

    /**
     * Get the first job of the task. Should be used only one time.
     * @return the first Job of the task.
     */
    Job getFirstJob();

    /**
     * Creates a new Schedulable with the exact same parameters.
     * @return A new Schedulable with the exact same parameters.
     */
    Schedulable copy();

}