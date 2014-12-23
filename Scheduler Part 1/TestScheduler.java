package os_assignment_9;

import os_assignment_9.Scheduler;
import os_assignment_9.TestThread;

/**
 * TestScheduler.java
 * 
 * This class tests the Scheduler. It creates three threads, adds them to the
 * queue that is present in scheduler class and each thread gets a reference to
 * the Scheduler that is used in TestThread class for reasons described in
 * TestThread itself. Finally the Scheduler is started ,which eventually starts
 * all the threads.
 *
 * @author Omar Waheed
 */

public class TestScheduler {

	private final static int DEFAULT_NUM_OF_THREADS = 6; // set a number of threads to be in queue

	public static void main(String args[]) {
	
		// uncomment below to use the default quantum, 1 second
		Scheduler CPUScheduler = new Scheduler(); 
		//Scheduler CPUScheduler = new Scheduler(3000); // quantum of 3 seconds

		for (int i = 1; i <= DEFAULT_NUM_OF_THREADS; i++) {
			TestThread thread = new TestThread("Thread " + i, CPUScheduler);
			thread.start();
			CPUScheduler.addThread(thread);
		}
		
		CPUScheduler.run();
		
	

	}
}
