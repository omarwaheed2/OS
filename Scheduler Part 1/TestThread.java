package os_assignment_9;

/**
 * TestThread.java
 * 
 * This thread is a test class used to demonstrate how the scheduler operates.
 *  This thread runs forever, periodically displaying its name until its state
 *  is set to sleeping. When the turn for this thread comes again CPU notifies
 *  it and sets its sleeping state to false, thus it starts running again.
 *
 *@author Omar Waheed
 */

import java.util.*;



class TestThread extends Thread {
	private String name; // name of the thread
	private Random burstTime; // random burst time generator (int)
	public boolean sleeping = true; // is this thread sleeping/waiting state?
	Scheduler CPUScheduler;
	private boolean isDone;
	double currentBurstTime;
	private double endIdle =1;
	private int totalWait = 1;
	private int timesInWaitLoop = 1;
	private double remainingBurstTime;

	public TestThread(String id, Scheduler CPUScheduler) {
		name = id;
		burstTime = new Random();
		this.CPUScheduler = CPUScheduler;
		isDone = false;
		
	}

	/**
	 * Tells if this process is done, so CPUScheduler can put it back
	 * into the top queue
	 * 
	 * @return is this process done?
	 */
	public boolean isDone(){
		return isDone;
	}
	
	public double getBurstTime(){
		return currentBurstTime;
	}
	
	public double getRemainingBurst(){
		return remainingBurstTime;
	}
	
	/**
	 * this method returns the name of the thread
	 * 
	 * @return String: returns the name of the thread
	 */
	public String toString() {
		return name;
	}
	
	/**
	 * 
	 * @param startTime The starting time of thread i.e. when it entered the top queue for the first time
	 * @return how much time it has waited since it came
	 */
	public double getWaitTime(){
		return endIdle;
	}
	
	/**
	 * 
	 * @return average time the thread has been in the list waiting.
	 * 
	 */
	public double getAvgIdleTime(){
		return (totalWait/timesInWaitLoop);
	}

	/**
	 * Start the timing (wait time) again for this process. Used
	 * when a process's burst time has been completed.
	 */
	public void startTimeAgain(){
		endIdle = 0;
	}
	
	
	/**
	 * This run method handles all the operations for thread and its interaction
	 * with CPU scheduler. Basically in short the following happens:-
	 * 
	 * -> this method assigns a burst time to the thread when it enters for the
	 * first time or it completes its burst time and comes back for another
	 * round.
	 * 
	 * -> There are two if(sleeping) blocks , more specifically if(sleeping){
	 * wait() } blocks, and there are two for an important reason. The
	 * if(sleeping) block inside the for loop checks if the CPU wakes up before
	 * burst time of the this thread is completed. If this happens then this
	 * thread waits until the Scheduler picks it and wakes it up i.e by
	 * notifying.
	 * 
	 * The second if(sleeping) block, just before the for loop, is for the
	 * Occasion when burst time of this thread is less than the time slice of
	 * the scheduler. When the process completes its burst time:-
	 * 
	 * -> it gets out of the for loop
	 * 
	 * -> Wakes up the CPU by notifying it
	 * 
	 * -> the CPU puts this thread back to sleep and at the back of the queue
	 * Now if we didn't have this if(sleeping) block outside for loop, this
	 * thread would again enter the for loop and print "Thread x in loop 0"
	 * before it enters the while(sleeping) loop that is inside the 'for' loop,
	 * and this would cause anomalous behavior.
	 * 
	 * This thread will halt if the 'stop' is pressed in the GUI.
	 * 
	 * This method also keeps record of the wait time of this thread. While
	 * this thread is waiting in the queue, the variable endIdle keeps incrementing
	 * at 1 second interval. This time is updated in the GUI in the wait time text box.
	 * 
	 */
	public void run() {
		/*
		 * The thread does something
		 */
		while (true) {

			currentBurstTime = burstTime.nextInt(250); // number from 0 - 250
			remainingBurstTime = currentBurstTime;
			
			

			/*
			 * wait() below:- don't enter for loop, when this thread woke up i.e
			 * it completed its burst time so now it is at the back of the queue
			 * and should wait for its turn.
			 */
			endIdle = 0;
			totalWait = 0;
			if (sleeping) {
				try {
					synchronized (this) {
						if(sleeping)
							while(sleeping){

								wait(1000);
								endIdle++;
							}
						isDone = false;
						totalWait +=  endIdle;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			for (double i = currentBurstTime; i >= 0; i--) {
				try {
					remainingBurstTime = i;
					CPUScheduler.updateBurstTimes(name, remainingBurstTime);
				
					Thread.sleep(100);
					while(CPUScheduler.stop){Thread.sleep(2);}// wait
					if (sleeping) { // wait if preempted
						synchronized (this) {
							
							if(sleeping)
								timesInWaitLoop++;
							while(sleeping){
							
							wait(1000);// 1 sec wait
							endIdle++;
							}
							isDone = false;
							totalWait +=  endIdle;
						}
					}
				} catch (InterruptedException e) {
				}
			}
			isDone = true; // thread has completed burst time.
			synchronized (CPUScheduler) {
				
				if (CPUScheduler.isSleeping()) {
					sleeping = true;
					System.out.println(name + " is done and  WAKING CPU UP!!!");
					CPUScheduler.notify();

				}

			}

		}

	}

}
