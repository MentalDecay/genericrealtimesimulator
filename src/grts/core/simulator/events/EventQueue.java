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

package grts.core.simulator.events;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * This class represents a queue of scheduling events.
 * @author Tristan Fautrel
 */
public class EventQueue implements Iterable<Event> {

    private final TreeSet<Event> events = new TreeSet<>();

    /**
     * Inserts the specified event into this queue.
     * @param event The event to add
     * @return true if the element was added to this queue, else false
     */
    public boolean offer(Event event) {
        return events.add(event);
    }

    /**
     * Retrieves and removes the next event of this queue, or returns null if this queue is empty. The next event is chosen according to its time and the priority of the event.
     * @return the head of this queue, or null if this queue is empty
     */
    public Event poll() {
        return events.pollFirst();
    }

    /**
     * Returns an iterator over events.
     * @return an iterator over events
     */
    @Override
    public Iterator<Event> iterator() {
        return events.iterator();
    }

}
