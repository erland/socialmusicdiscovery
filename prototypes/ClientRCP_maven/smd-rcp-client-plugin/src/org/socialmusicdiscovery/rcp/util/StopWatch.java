/*
 *  Copyright 2010-2011, Social Music Discovery project
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of Social Music Discovery project nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL SOCIAL MUSIC DISCOVERY PROJECT BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.socialmusicdiscovery.rcp.util;

import java.util.Stack;

import org.apache.commons.lang.time.DurationFormatUtils;

/**
 * A utility to time duration of tasks, based on a wrapped instance of the
 * {@link org.apache.commons.lang.time.StopWatch}. Use as a simple performance
 * analyzer in several ways:
 * <ul>
 * <li>As a single anonymous watch using static methods {@link #start(String)}
 * and {@link #stop()}. Each call to {@link #start(String)} prints elapsed time
 * since last start, {@link #stop()} prints duration of last task and total
 * time.</li>
 * <li>As any number of named watches by calling constructor and methods
 * {@link #task(String)} and {@link #done()}. Each call to {@link #task(String)}
 * prints elapsed time since last call, {@link #done()} prints duration of last
 * task and total time.</li>
 * <li>As a stack of named watches accessible via {@link #push(String, String)} (creates a new named watch),
 * {@link #start(String)} (starts a new task on most recent watch), {@link #pop()} (stops most recent watch) 
 * and {@link #stop()} (stops all watches).</li>
 * </ul>
 * 
 * @author Peer Törngren
 * 
 */
public class StopWatch {
	private final org.apache.commons.lang.time.StopWatch sw = new org.apache.commons.lang.time.StopWatch();
	private static final StopWatch INSTANCE = new StopWatch(null);
	private static Stack<StopWatch> STACK = new Stack<StopWatch>();
	
	private String task;
	private long taskStarted;
	private final String name;
	private int indent = 0; 

	public StopWatch(String name) {
		this.name = name;
	}

	/** Start a new task
	 * @param taskName
	 */
	public void task(String taskName) {
		if (task==null) {
			sw.start();
			taskStarted = 0;
		} else {
			split();
		}
		task = taskName;
		out("start ...");
	}

	public void done() {
		split();
		
		sw.stop();
		task = "TOTAL";
		out(sw.toString());
		task = null;
		sw.reset();
	}

	private void split() {
		sw.split();
		long splitTime = sw.getSplitTime();
		String duration = DurationFormatUtils.formatDurationHMS(splitTime-taskStarted);
		out(duration);
		taskStarted=splitTime;
	}

	/**
	 * Push a new, named watch on the stack. 
	 * @param name
	 * @return StopWatch
	 */
	public static StopWatch push(String name, String taskName) {
		StopWatch stopWatch = new StopWatch(name);
		stopWatch.indent = STACK.size();
		STACK.push(stopWatch);
		start(taskName);
		return stopWatch;
	}

	/**
	 * Stop all active watches.
	 */
	public static void stop() {
		if (STACK.isEmpty()) {
			INSTANCE.done();
		} else {
			while(!STACK.isEmpty()) {
				pop();
			}
		}
	}

	/**
	 * Stop most recent watch on stack.
	 */
	public static void pop() {
		STACK.pop().done();
	}
	
	public static void start(String taskName) {
		if (STACK.isEmpty()) {
			INSTANCE.task(taskName);
		} else {
			STACK.peek().task(taskName);
		}
	}
	
	private void out(String string) {
		String id = name==null ? task : name+"."+task;
		for (int i = 0; i < indent; i++) {
			System.out.print("  ");
		}
		System.out.println(id + ": " + string);
	}

	private static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException("Failed!", e);  //$NON-NLS-1$
		}
	}
	
	public static void main(String[] args) {
		System.out.println("==DEFAULT==");
		StopWatch.INSTANCE.task("First");
		sleep(101);
		StopWatch.INSTANCE.task("Second");
		sleep(102);
		StopWatch.INSTANCE.done();

		System.out.println("==NAMED==");
		StopWatch.push("A", "a1");
		sleep(101);
		StopWatch.start("a2");
		sleep(102);
		
		StopWatch.push("B", "b1");
		sleep(201);
		StopWatch.start("b2");
		sleep(202);
		StopWatch.pop();
		
		StopWatch.start("a3");
		sleep(102);
		
		StopWatch.stop();
	}
}
