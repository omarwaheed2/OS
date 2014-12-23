package os_assignment_9;

import java.awt.*;

import javax.swing.JFrame;

import java.awt.event.*;

import os_assignment_9.Queue;
import os_assignment_9.TestThread;

/**
 * Scheduler.java
 *
 * This class is represents a multifeedback queue. It consist of three
 * queues : topQueue, middleQueue and fcfsQueue. It receives a quantum
 * either from user or it uses a default quantum i.e. DEFAULT_QUANTUM.
 * The first queue runs on a  round robin scheduling algorithm with
 * quantum of user or default. the middle queue run on R.R aswell but
 * with a double the specified quantum. The bottom or fcfsQueue runs 
 * like a usual FCFS scheduling.
 * 
 * I have made a GUI for this which shows the working of the queue. It
 * also has information related to the ongoing threads such as their
 * response ratio, wait time in queue.
 * 
 * The green colored thread in GUI is the one that is currently being processed.
 * The red colored thread is the one that has been waiting for the longest time
 * in the thread.
 * 
 * If the response ratio of a certain thread exceeds the RATIO_Threshold it is promoted
 * to the upper queue. the RATIO_THRESHOLD to go to middle queue is lower than the one
 * to be promoted to the highest queue (topQueue)
 * 
 * @author Omar Waheed
 */

public class Scheduler extends Frame implements Runnable {
	private Queue<TestThread> topQueue; // topQueue for scheduler
	private Queue<TestThread> middleQueue;
	private Queue<TestThread> fcfsQueue;
	public boolean stop = true;
	private int timeSlice;
	private static final int DEFAULT_QUANTUM = 2000; // 3 second
	private static final double RATIO_THRESHOLD = 1.4;
	private boolean sleeping;

	/*
	 * JFRAME variables below
	 */

	/*
	 * The boxes that represent the queue itself in the GUI Q1 is first queue Q2
	 * is second queue Q3 is third queue
	 * 
	 * the notation Q1_1 ... QA_B represents the Bth thread in Ath queue
	 */
	private TextField Q1_1;
	private TextField Q1_2;
	private TextField Q1_3;
	private TextField Q1_4;
	private TextField Q1_5;
	private TextField Q1_6;

	private TextField Q2_1;
	private TextField Q2_2;
	private TextField Q2_3;
	private TextField Q2_4;
	private TextField Q2_5;
	private TextField Q2_6;

	private TextField Q3_1;
	private TextField Q3_2;
	private TextField Q3_3;
	private TextField Q3_4;
	private TextField Q3_5;
	private TextField Q3_6;

	/* waiting time for each thread */
	private TextField t1WaitTime;
	private TextField t2WaitTime;
	private TextField t3WaitTime;
	private TextField t4WaitTime;
	private TextField t5WaitTime;
	private TextField t6WaitTime;
	/*----------------------------*/

	/* Remaining BurstTimes textboxes */
	private TextField t1Remaining;
	private TextField t2Remaining;
	private TextField t3Remaining;
	private TextField t4Remaining;
	private TextField t5Remaining;
	private TextField t6Remaining;
	/*-------------------------------*/

	/* Response Ration textBoxes */
	private TextField responseHeading;
	private TextField respT1;
	private TextField respT2;
	private TextField respT3;
	private TextField respT4;
	private TextField respT5;
	private TextField respT6;
	/*-------------------------------*/

	/* Gui for the 3 queues */
	private TextField queueNum1;
	private TextField queueNum2;
	private TextField queueNum3;
	/*---------------------------------*/
	private TextField currentThread;

	private Button startButton;
	private static final int HEIGHT = 800;
	private static final int WIDTH = 1200;

	/**
	 * This is the constructor for Scheduler. It takes the default quantum,
	 * initializes the GUI, the queues (top,middle and fcfs) and sets its
	 * (multifeed back queue's) sleeping state to false.
	 * 
	 * 
	 */
	public Scheduler() {
		createGUI();
		timeSlice = DEFAULT_QUANTUM;
		topQueue = new Queue<TestThread>();
		middleQueue = new Queue<TestThread>();
		fcfsQueue = new Queue<TestThread>();
		sleeping = false;
	}

	/**
	 * This is the second constructor for Scheduler. It takes a quantum that the
	 * user can specify by himself. This quantum is the quantum for top queue.
	 * For second queue the quantum is this quantum * 2. It also sets the
	 * multifeed-back queues status to sleeping, so it wont start right away
	 * after program is started
	 * 
	 * @param quantum
	 *            : quantum for Round Robin scheduling i.e how much time should
	 *            be given to each thread before it is pre-empted in top queue,
	 *            then in second queue the time given is quantum * 2.
	 */
	public Scheduler(int quantum) {
		createGUI();
		timeSlice = quantum;
		topQueue = new Queue<TestThread>();
		middleQueue = new Queue<TestThread>();
		fcfsQueue = new Queue<TestThread>();
		sleeping = false;
	}

	/**
	 * Creates the GUI for multifeedback queue.
	 */
	private void createGUI() {
		setSize(WIDTH, HEIGHT);
		setTitle("Multi-feedback Queue");
		setBackground(new Color(50, 100, 200));
		setLayout(new FlowLayout());
		addWindowListener(new WindowDestroyer());
		queueNum1 = new TextField(2);
		add(queueNum1);
		queueNum1.setText("Q1");

		// MultiFeedbackQueue GUI init
		CreateMultiQueueGUI();

		// seperator to make GUI easier to read
		responseHeading = new TextField(160);
		responseHeading.setBackground(new Color(50, 100, 200));
		add(responseHeading);

		// remaining times GUI init
		createRemainingTimeBoxes();

		// wait time Gui init

		createWaitTimeGUI();

		// Response ratio GUI init

		respT1 = new TextField(22);
		add(respT1);
		respT1.setText("Response-Ratio T1: 0");

		respT2 = new TextField(22);
		add(respT2);
		respT2.setText("Response-Ratio T2: 0");

		respT3 = new TextField(22);
		add(respT3);
		respT3.setText("Response-Ratio T3: 0");

		respT4 = new TextField(22);
		add(respT4);
		respT4.setText("Response-Ratio T4: 0");

		respT5 = new TextField(22);
		add(respT5);
		respT5.setText("Response-Ratio T5: 0");

		respT6 = new TextField(22);
		add(respT6);
		respT6.setText("Response-Ratio T6: 0");

		currentThread = new TextField(100);
		add(currentThread);
		currentThread.setText("Press Start...");

		startButton = new Button("Start");
		startButton.addActionListener(new ToggleButtonListener());
		startButton.setPreferredSize(new Dimension(200, 25));
		add(startButton);
		setVisible(true);

	}

	/**
	 * The listener class for button and the associated method for what happens
	 * if the button is clicked
	 * 
	 * @author Omar
	 *
	 */
	private class ToggleButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Start")) {
				startButton.setLabel("Stop");
				stop = false;

			}

			if (e.getActionCommand().equals("Stop")) {
				startButton.setLabel("Start");
				stop = true;
			}
		}
	}

	/**
	 * This method creates the text boxes on the frame which show the total
	 * average time the process has been idle during its processing. This is a
	 * helper method for the main "createGUI" method.
	 */
	private void createWaitTimeGUI() {

		t1WaitTime = new TextField(22);
		add(t1WaitTime);
		t1WaitTime.setText("wait-time T1: 0");

		t2WaitTime = new TextField(22);
		add(t2WaitTime);
		t2WaitTime.setText("wait-time T2: 0");

		t3WaitTime = new TextField(22);
		add(t3WaitTime);
		t3WaitTime.setText("wait-time T3: 0");

		t4WaitTime = new TextField(22);
		add(t4WaitTime);
		t4WaitTime.setText("wait-time T4: 0");

		t5WaitTime = new TextField(22);
		add(t5WaitTime);
		t5WaitTime.setText("wait-time T5: 0");

		t6WaitTime = new TextField(22);
		add(t6WaitTime);
		t6WaitTime.setText("wait-time T6: 0");

	}

	/**
	 * This method creates text boxes that show the remaining time of each
	 * process on the queue
	 */
	private void createRemainingTimeBoxes() {
		t1Remaining = new TextField(22);
		add(t1Remaining);
		t1Remaining.setText("Remaining Burst Thread 1 : 0");

		t2Remaining = new TextField(22);
		add(t2Remaining);
		t2Remaining.setText("Remaining Burst Thread 2 : 0");

		t3Remaining = new TextField(22);
		add(t3Remaining);
		t3Remaining.setText("Remaining Burst Thread 3 : 0");

		t4Remaining = new TextField(22);
		add(t4Remaining);
		t4Remaining.setText("Remaining Burst Thread 4 : 0");

		t5Remaining = new TextField(22);
		add(t5Remaining);
		t5Remaining.setText("Remaining Burst Thread 5 : 0");

		t6Remaining = new TextField(22);
		add(t6Remaining);
		t6Remaining.setText("Remaining Burst Thread 6 : 0");
	}

	/**
	 * This method creates text boxes that show the multilevel queue. Each row
	 * depicts a single queue and each column in the queue is a specific thread
	 * residing in that queue
	 */
	private void CreateMultiQueueGUI() {
		// first queue
		Q1_1 = new TextField(22);
		add(Q1_1);
		Q1_1.setText("Thread 1");

		Q1_2 = new TextField(22);
		add(Q1_2);
		Q1_2.setText("Thread 2");

		Q1_3 = new TextField(22);
		add(Q1_3);
		Q1_3.setText("Thread 3");

		Q1_4 = new TextField(22);
		add(Q1_4);
		Q1_4.setText("Thread 4");

		Q1_5 = new TextField(22);
		add(Q1_5);
		Q1_5.setText("Thread 5");

		Q1_6 = new TextField(22);
		add(Q1_6);
		Q1_6.setText("Thread 6");

		// second queue
		queueNum2 = new TextField(2);
		add(queueNum2);
		queueNum2.setText("Q2");
		Q2_1 = new TextField(22);
		add(Q2_1);
		Q2_1.setText("");

		Q2_2 = new TextField(22);
		add(Q2_2);
		Q2_2.setText("");

		Q2_3 = new TextField(22);
		add(Q2_3);
		Q2_3.setText("");

		Q2_4 = new TextField(22);
		add(Q2_4);
		Q2_4.setText("");

		Q2_5 = new TextField(22);
		add(Q2_5);
		Q2_5.setText("");

		Q2_6 = new TextField(22);
		add(Q2_6);
		Q2_6.setText("");

		// third queue---------
		queueNum3 = new TextField(2);
		add(queueNum3);
		queueNum3.setText("Q3");
		Q3_1 = new TextField(22);
		Q3_1.setEditable(true);
		Q3_1.setLocation(700, 700);
		add(Q3_1);
		Q3_1.setText("");

		Q3_2 = new TextField(22);
		add(Q3_2);
		Q3_2.setText("");

		Q3_3 = new TextField(22);
		add(Q3_3);
		Q3_3.setText("");

		Q3_4 = new TextField(22);
		add(Q3_4);
		Q3_4.setText("");

		Q3_5 = new TextField(22);
		add(Q3_5);
		Q3_5.setText("");

		Q3_6 = new TextField(22);
		add(Q3_6);
		Q3_6.setText("");
	}

	/**
	 * This method tells whether the scheduler is sleeping or is it scheduling a
	 * task.
	 * 
	 * @return the sleeping state of Scheduler, a boolean
	 */
	public boolean isSleeping() {
		return sleeping;
	}

	/**
	 * adds a thread to the topQueue
	 * 
	 * @return void
	 */
	public void addThread(TestThread t) {
		topQueue.addItem(t);
	}

	/**
	 * this method puts the scheduler to sleep for a time quantum Important
	 * note: I added update queues in fcfs sleeping block because, there might
	 * be a situation where all threads are in fcfs queue, then all of the other
	 * will starve except the one that is being processed because, it will be
	 * finished, took off the queue and then inserted again. As queues are FIFO,
	 * it will always process this thread take it off when done and then put it
	 * back in and process this thread again and this will go on for infinite
	 * amount of time.
	 * 
	 * @return void
	 */
	private void schedulerSleep(int level, TestThread current) {
		synchronized (this) {
			try {
				int time = 0;
				if (level == 0) {
					sleeping = true;
					while (time < timeSlice) {
						Thread.sleep(1);// Do not overuse CPU
						if (!stop) {
							time++;
						}
						if (current.isDone()) {
							wait();
							break;
						}
					}

					sleeping = false;
				} else if (level == 1) {
					sleeping = true;
					while (time < timeSlice * 2) {
						Thread.sleep(1);
						if (!stop) {
							time++;
						}
						if (current.isDone()) {
							wait();
							break;
						}
					}
					sleeping = false;
				} else if (level == 2) {
					sleeping = true;
					wait(); // FCFS
					sleeping = false;
				}
				updateQueues();
			} catch (InterruptedException e) {
			}
			;
		}
	}

	/**
	 * 
	 * @param name
	 *            the name of the thread
	 * @param remainingBurstTime
	 *            the burst time of thread
	 */
	public void updateBurstTimes(String name, double remainingBurstTime) {
		switch (name) {

		case "Thread 1":
			t1Remaining.setText("Rem Burst Thread 1 : " + remainingBurstTime);
			break;

		case "Thread 2":
			t2Remaining.setText("Rem Burst Thread 2 : " + remainingBurstTime);
			break;
		case "Thread 3":
			t3Remaining.setText("Rem Burst Thread 3 : " + remainingBurstTime);
			break;
		case "Thread 4":
			t4Remaining.setText("Rem Burst Thread 4 : " + remainingBurstTime);
			break;
		case "Thread 5":
			t5Remaining.setText("Rem Burst Thread 5 : " + remainingBurstTime);
			break;
		case "Thread 6":
			t6Remaining.setText("Rem Burst Thread 6 : " + remainingBurstTime);
			break;
		default:
			break;
		}

	}

	/**
	 * In the run method following happens:
	 * 
	 * 
	 * It checks if the stop button has been clicked in the GUI. If it is, then
	 * the processing is brought to a halt, until the user clicks start.
	 * 
	 * When start is clicked:-
	 * 
	 * (1)Pick a process from the 3 queues based on queue priority and the
	 * highest response ratio
	 * 
	 * 
	 * (2) dispatches the thread and set its sleeping state to false, after
	 * which it starts running or is handed to CPU for processing
	 * 
	 * (3) Scheduler sleeps for a specified amount of time slice
	 * 
	 * (4) If time slice finishes before the burst time of thread, then that
	 * thread is pre-empted i.e threads sleeping state is set to false and it is
	 * put in the lower queue. If it is already in the lowest queue (FCFS) it is
	 * put at the back of the queue; After preemption the next one in queue is
	 * loaded according to priority criteria and the above (1) to (4) steps are
	 * repeated.
	 * 
	 * However if the burst time is less than time slice then the process
	 * finishes AND then wakes up the scheduler (by notifying scheduler - > from
	 * TestThread class). This process is put in the same queue again, but it
	 * begins with a new burst time.
	 * 
	 * It also has some methods that update the GUI such as update threads
	 * inside the queues in GUI, set the color of currently processed thread to
	 * green, set the color of the thread with greatest wait time to red, update
	 * their response ratios in the GUI etc
	 * 
	 * 
	 * 
	 */
	public void run() {
		int updateCount = 0;
		if (topQueue.getSize() > 0) {

			System.out.println("* * * Top Queue Quantum  = " + timeSlice
					+ " * * *");
			System.out.println("* * * Middle Queue Quantum  = " + timeSlice * 2
					+ " * * *");
			System.out.println("* * *  Number of threads for scheduling = "
					+ topQueue.getSize() + " * * *");

			TestThread current; // current thread to be processed
			while (true) {
				if (!stop) {// if start is clicked in GUI
					resetGUI();// reset queues boxes to white color
					updateMostWaited();// update color of most waited thread

					try {
						if (!topQueue.isEmpty()) { // if top queue is not empty
							current = (TestThread) topQueue.getNext();
							if ((current != null) && (current.isAlive())) {

								processThreadInTopQueue(current);
							}
						} else if (!middleQueue.isEmpty()) {// if mid queue is not empty
							current = (TestThread) middleQueue.getNext();

							if ((current != null) && (current.isAlive())) {
								processThreadInMiddleQueue(current);
							}

						} else if (!fcfsQueue.isEmpty()) {
							current = (TestThread) fcfsQueue.getNext();

							if ((current != null) && (current.isAlive())) {
								processThreadInFCFSQueue(current);
							}

						}
					} catch (NullPointerException e3) {
					}
				} else {// if stop button clicked
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		} else {
			System.out
					.println("There is no job to perform, no threads in queue!");
		}
	}

	/**
	 * This method resets the background color of each text box in the GUI to
	 * white (for the one's representing the queue). It uses helper method to
	 * set each queues boxes to white
	 */
	private void resetGUI() {
		firstRowToWhite();
		secondRowToWhite();
		thirdRowToWhite();
	}

	/**
	 * Sets the boxes of topQueue GUI to white
	 */
	private void firstRowToWhite() {
		Q1_1.setBackground(Color.white);
		Q1_2.setBackground(Color.white);
		Q1_3.setBackground(Color.white);
		Q1_4.setBackground(Color.white);
		Q1_5.setBackground(Color.white);
		Q1_6.setBackground(Color.white);
	}

	/**
	 * Sets the boxes of middleQueue GUI to white
	 */

	private void secondRowToWhite() {
		Q2_1.setBackground(Color.white);
		Q2_2.setBackground(Color.white);
		Q2_3.setBackground(Color.white);
		Q2_4.setBackground(Color.white);
		Q2_5.setBackground(Color.white);
		Q2_6.setBackground(Color.white);
	}

	/**
	 * Sets the boxes of fcfsQueue GUI to white
	 */
	private void thirdRowToWhite() {
		Q3_1.setBackground(Color.WHITE);
		Q3_2.setBackground(Color.white);
		Q3_3.setBackground(Color.white);
		Q3_4.setBackground(Color.WHITE);
		Q3_5.setBackground(Color.white);
		Q3_6.setBackground(Color.white);

	}

	/**
	 * Update the wait time of all the threads in the queues and set the thread
	 * with greatest wait time to red.
	 */
	private void updateMostWaited() {
		double greatestWait = 0;
		TestThread current; // current Thread
		String nameOfMostWaited = ""; // name of the most waited thread
		int queueNum = 0; // which queue the current thread is in

		for (int i = 0; i < topQueue.getSize(); i++) {
			current = topQueue.getNext();
			if (current.getWaitTime() > greatestWait) {
				greatestWait = current.getWaitTime();
				nameOfMostWaited = current.toString();
				queueNum = 1;
				updateWaitTime(nameOfMostWaited, current);
			}
			topQueue.addItem(current);
		}

		for (int i = 0; i < middleQueue.getSize(); i++) {
			current = middleQueue.getNext();
			if (current.getWaitTime() > greatestWait) {
				greatestWait = current.getWaitTime();
				nameOfMostWaited = current.toString();
				queueNum = 2;
				updateWaitTime(nameOfMostWaited, current);
			}
			middleQueue.addItem(current);
		}

		for (int i = 0; i < fcfsQueue.getSize(); i++) {
			current = fcfsQueue.getNext();
			if (current.getWaitTime() > greatestWait) {
				greatestWait = current.getWaitTime();
				nameOfMostWaited = current.toString();
				queueNum = 3;
				updateWaitTime(nameOfMostWaited, current);
			}
			fcfsQueue.addItem(current);
		}

		updateGreatestWaitInGUI(nameOfMostWaited, queueNum); // set the greatest
																// wait thread
																// to red color

	}

	/**
	 * Helper method for updateMostWaited() method. This thread sets the color of the thread that has waited in the queue the
	 * most. The color for this thread will be red.
	 * 
	 * @param name
	 *            The name of current thread received from updateMostWaited()
	 * @param queueNum
	 *            which queue is that thead in, recieved from updateMostWaited()
	 */
	
	public void updateGreatestWaitInGUI(String name, int queueNum) {
		switch (name) {
		case "Thread 1":
			if (queueNum == 1) {
				Q1_1.setBackground(Color.red);
			}
			if (queueNum == 2) {
				Q2_1.setBackground(Color.red);
			}
			if (queueNum == 3) {
				Q3_1.setBackground(Color.red);
			}
			break;
		case "Thread 2":
			if (queueNum == 1) {
				Q1_2.setBackground(Color.red);
			}
			if (queueNum == 2) {
				Q2_2.setBackground(Color.red);
			}
			if (queueNum == 3) {
				Q3_2.setBackground(Color.red);
			}
			break;
		case "Thread 3":
			if (queueNum == 1) {
				Q1_3.setBackground(Color.red);
			}
			if (queueNum == 2) {
				Q2_3.setBackground(Color.red);
			}
			if (queueNum == 3) {
				Q3_3.setBackground(Color.red);
			}
			break;
		case "Thread 4":
			if (queueNum == 1) {
				Q1_4.setBackground(Color.red);
			}
			if (queueNum == 2) {
				Q2_4.setBackground(Color.red);
			}
			if (queueNum == 3) {
				Q3_4.setBackground(Color.red);
			}
			break;
		case "Thread 5":
			if (queueNum == 1) {
				Q1_5.setBackground(Color.red);
			}
			if (queueNum == 2) {
				Q2_5.setBackground(Color.red);
			}
			if (queueNum == 3) {
				Q3_5.setBackground(Color.red);
			}
			break;
		case "Thread 6":
			if (queueNum == 1) {
				Q1_6.setBackground(Color.red);
			}
			if (queueNum == 2) {
				Q2_6.setBackground(Color.red);
			}
			if (queueNum == 3) {
				Q3_6.setBackground(Color.red);
			}
			break;
		}

	}

	/**
	 * This method sets the color of currently running thread to green and also
	 * sets any previously green boxes to white, so that no two boxes have green
	 * color.
	 * 
	 * @param current
	 *            current thread being processed
	 * @param queue
	 *            which queue to set the color of
	 * @param color
	 *            green if true else white
	 */
	private void colorDeColorCurrentlyRunning(String current, String queue,
			boolean color) {
		Color currColor;
		if (color) {
			currColor = Color.green;
		} else {
			currColor = Color.WHITE;
		}
		switch (current) {

		case "Thread 1":
			if (queue.equals("top"))
				Q1_1.setBackground(currColor);
			else if (queue.equals("middle"))
				Q2_1.setBackground(currColor);
			else
				Q3_1.setBackground(currColor);
			break;
		case "Thread 2":
			if (queue.equals("top"))
				Q1_2.setBackground(currColor);
			else if (queue.equals("middle"))
				Q2_2.setBackground(currColor);
			else
				Q3_2.setBackground(currColor);
			break;
		case "Thread 3":
			if (queue.equals("top"))
				Q1_3.setBackground(currColor);
			else if (queue.equals("middle"))
				Q2_3.setBackground(currColor);
			else
				Q3_3.setBackground(currColor);
			break;
		case "Thread 4":
			if (queue.equals("top"))
				Q1_4.setBackground(currColor);
			else if (queue.equals("middle"))
				Q2_4.setBackground(currColor);
			else
				Q3_4.setBackground(currColor);
			break;
		case "Thread 5":
			if (queue.equals("top"))
				Q1_5.setBackground(currColor);
			else if (queue.equals("middle"))
				Q2_5.setBackground(currColor);
			else
				Q3_5.setBackground(currColor);
			break;
		case "Thread 6":
			if (queue.equals("top"))
				Q1_6.setBackground(currColor);
			else if (queue.equals("middle"))
				Q2_6.setBackground(currColor);
			else
				Q3_6.setBackground(currColor);
			break;
		}
	}

	/**
	 * This method processes the thread in front of the top queue, if there is
	 * one present.
	 * 
	 * @param current
	 *            Thread to be processed in top queue
	 */

	private void processThreadInTopQueue(TestThread current) {
		System.out.println("\t\t* * * dispatching " + current + " * * *\n");
		current.sleeping = false;
		colorDeColorCurrentlyRunning(current.toString(), "top", true);
		synchronized (current) {
			currentThread.setText("Processing : " + current.toString());
			current.notify();
		}

		schedulerSleep(0, current);
		current.sleeping = true;

		colorDeColorCurrentlyRunning(current.toString(), "top", false);
		if (current.isDone()) {
			topQueue.addItem(current);
			updateGUI(current.toString(), "top", false);
		} else {// demote if not done
			middleQueue.addItem(current);
			updateGUI(current.toString(), "middle", true);
		}

		System.out.println("\n\t\t* * * Context Switch * * * ");
		System.out.println("\t\t* * * preempting " + current + " * * *");

		updateQueues();
	}

	/**
	 * This method processes the thread in front of the middle queue, if there
	 * is one present.
	 * 
	 * @param current
	 *            Thread to be processed in middle queue
	 */
	private void processThreadInMiddleQueue(TestThread current) {
		System.out.println("\t\t* * * dispatching " + current + " * * *\n");
		current.sleeping = false;
		colorDeColorCurrentlyRunning(current.toString(), "middle", true);
		synchronized (current) {
			currentThread.setText("Processing : " + current.toString());
			current.notify();
		}
		schedulerSleep(1, current);
		current.sleeping = true;
		colorDeColorCurrentlyRunning(current.toString(), "middle", false);
		if (current.isDone()) { // demote if not done
			middleQueue.addItem(current);
			updateGUI(current.toString(), "middle", false);
		} else {
			fcfsQueue.addItem(current);
			updateGUI(current.toString(), "fcfs", true);
		}

		System.out.println("\n\t\t* * * Context Switch * * * ");
		System.out.println("\t\t* * * preempting " + current + " * * *");
		updateQueues();
	}

	/**
	 * This method processes the thread in front of the top queue, if there is
	 * one present.
	 * 
	 * @param current
	 *            Thread to be processed in lowest queue following FCFS
	 *            scheduling
	 */
	private void processThreadInFCFSQueue(TestThread current) {

		if ((current != null) && (current.isAlive())) {
			System.out.println("\t\t* * * dispatching " + current + " * * *\n");
			// while(stop){current.sleeping = true;}
			current.sleeping = false;
			colorDeColorCurrentlyRunning(current.toString(), "fcfs", true);
			synchronized (current) {
				currentThread.setText("Processing : " + current.toString());
				current.notify();
			}

			schedulerSleep(2, current);
			current.sleeping = true;
			colorDeColorCurrentlyRunning(current.toString(), "fcfs", false);
			fcfsQueue.addItem(current);
			// //done at this point
			updateGUI(current.toString(), "fcfs", false);

			System.out.println("\n\t\t* * * Context Switch * * * ");
			System.out.println("\t\t* * * preempting " + current + " * * *");
			updateQueues();
		}
	}

	/**
	 * This method updates the wait time of each thread on the GUI.
	 * 
	 * @param name
	 *            The name of the thread
	 * @param current
	 *            reference to the current thread
	 */
	private void updateWaitTime(String name, TestThread current) {
		switch (name) {

		case "Thread 1":
			t1WaitTime.setText("wait-time T1: " + current.getWaitTime());
			break;
		case "Thread 2":
			t2WaitTime.setText("wait-time T2: " + current.getWaitTime());
			break;
		case "Thread 3":
			t3WaitTime.setText("wait-time T3: " + current.getWaitTime());
			break;
		case "Thread 4":
			t4WaitTime.setText("wait-time T4: " + current.getWaitTime());
			break;

		case "Thread 5":
			t5WaitTime.setText("wait-time T5: " + current.getWaitTime());
			break;
		case "Thread 6":
			t6WaitTime.setText("wait-time T6: " + current.getWaitTime());
			break;
		}

	}

	/**
	 * Updates the queues in actual queue and their positions in GUI aswell. It
	 * also calculates the response ratio of eah thread in the queue. It uses
	 * this response ratio to decide (by comparing to RATIO_THRESHOLD) whether,
	 * the thread should be promoted, demoted or remain in the same queue.
	 */
	public void updateQueues() {
		double responseRatio = 0;

		if (!topQueue.isEmpty()) {
			for (int i = 0; i < topQueue.getSize(); i++) {
				TestThread current = (TestThread) topQueue.getNext();
				responseRatio = calculateRatio(current.getWaitTime(),
						current.currentBurstTime);
				updateResponseRatios(current.toString(), responseRatio);
				topQueue.addItem(current);

			}
		}

		if (!middleQueue.isEmpty()) {
			for (int i = 0; i < middleQueue.getSize(); i++) {
				TestThread current = (TestThread) middleQueue.getNext();
				responseRatio = calculateRatio(current.getWaitTime(),
						current.currentBurstTime);
				updateResponseRatios(current.toString(), responseRatio);
				if (responseRatio > RATIO_THRESHOLD + (RATIO_THRESHOLD / 10)) {
					System.out.println("promoting " + current.toString()
							+ " to top queue!");
					updateGUI(current.toString(), "top", false);

					topQueue.addItem(current);
				} else {
					middleQueue.addItem(current);
				}
			}
		}

		if (!fcfsQueue.isEmpty()) {
			for (int i = 0; i < fcfsQueue.getSize(); i++) {
				TestThread current = (TestThread) fcfsQueue.getNext();
				responseRatio = calculateRatio(current.getWaitTime(),
						current.currentBurstTime);
				updateResponseRatios(current.toString(), responseRatio);
				if (responseRatio > RATIO_THRESHOLD) {
					System.out.println("promoting " + current.toString()
							+ " to middle queue!");
					updateGUI(current.toString(), "middle", false);
					middleQueue.addItem(current);
				} else {
					fcfsQueue.addItem(current);
				}
			}
		}
	}

	/**
	 * updates the response ratio of the current thread in the GUI
	 * 
	 * @param name
	 *            The name of the thread
	 * @param responseRatio
	 *            response ratio of the current thread
	 */
	private void updateResponseRatios(String name, double responseRatio) {
		switch (name) {
		case "Thread 1":
			respT1.setText("Response-Ratio T1: " + responseRatio);
			break;
		case "Thread 2":
			respT2.setText("Response-Ratio T2: " + responseRatio);
			break;
		case "Thread 3":
			respT3.setText("Response-Ratio T3: " + responseRatio);
			break;
		case "Thread 4":
			respT4.setText("Response-Ratio T4: " + responseRatio);
			break;
		case "Thread 5":
			respT5.setText("Response-Ratio T5: " + responseRatio);
			break;
		case "Thread 6":
			respT6.setText("Response-Ratio T6: " + responseRatio);
			break;
		default:
			break;

		}
	}

	/**
	 * 
	 * This method updates the GUI by rearranging the threads according to the
	 * information sent to it via parameters. It either promotes or demotes the
	 * thread.
	 * 
	 * @param nameOfThread
	 *            The name of the thread
	 * @param sendTo
	 *            Name of the queue which the thread has to be sent to.
	 * @param demote
	 *            demote the thread i.e. send to a lower queue? or promote it?
	 */
	private void updateGUI(String nameOfThread, String sendTo, boolean demote) {
		switch (nameOfThread) {

		case "Thread 1":
			if (demote) {
				if (sendTo.equals("middle")) {
					Q1_1.setText("");

					Q2_1.setText(nameOfThread);
				} else if (sendTo.equals("fcfs")) {
					Q2_1.setText("");

					Q3_1.setText(nameOfThread);
				}
			} else { // promote
				if (sendTo.equals("middle")) {
					Q3_1.setText("");

					Q2_1.setText(nameOfThread);
				} else if (sendTo.equals("top")) {
					Q2_1.setText("");

					Q1_1.setText(nameOfThread);
				}
			}
			break;
		case "Thread 2":
			if (demote) {
				if (sendTo.equals("middle")) {
					Q1_2.setText("");

					Q2_2.setText(nameOfThread);
				} else if (sendTo.equals("fcfs")) {
					Q2_2.setText("");

					Q3_2.setText(nameOfThread);
				}
			} else { // promote
				if (sendTo.equals("middle")) {
					Q3_2.setText("");

					Q2_2.setText(nameOfThread);
				} else if (sendTo.equals("top")) {
					Q2_2.setText("");

					Q1_2.setText(nameOfThread);
				}
			}
			break;
		case "Thread 3":
			if (demote) {
				if (sendTo.equals("middle")) {
					Q1_3.setText("");

					Q2_3.setText(nameOfThread);
				} else if (sendTo.equals("fcfs")) {
					Q2_3.setText("");

					Q3_3.setText(nameOfThread);
				}
			} else { // promote
				if (sendTo.equals("middle")) {
					Q3_3.setText("");

					Q2_3.setText(nameOfThread);
				} else if (sendTo.equals("top")) {
					Q2_3.setText("");

					Q1_3.setText(nameOfThread);
				}
			}
			break;
		case "Thread 4":
			if (demote) {
				if (sendTo.equals("middle")) {
					Q1_4.setText("");

					Q2_4.setText(nameOfThread);
				} else if (sendTo.equals("fcfs")) {
					Q2_4.setText("");

					Q3_4.setText(nameOfThread);
				}
			} else { // promote
				if (sendTo.equals("middle")) {
					Q3_4.setText("");

					Q2_4.setText(nameOfThread);
				} else if (sendTo.equals("top")) {
					Q2_4.setText("");

					Q1_4.setText(nameOfThread);
				}
			}
			break;
		case "Thread 5":
			if (demote) {
				if (sendTo.equals("middle")) {
					Q1_5.setText("");

					Q2_5.setText(nameOfThread);
				} else if (sendTo.equals("fcfs")) {
					Q2_5.setText("");

					Q3_5.setText(nameOfThread);
				}
			} else { // promote
				if (sendTo.equals("middle")) {
					Q3_5.setText("");

					Q2_5.setText(nameOfThread);
				} else if (sendTo.equals("top")) {
					Q2_5.setText("");

					Q1_5.setText(nameOfThread);
				}
			}
			break;
		case "Thread 6":
			if (demote) {
				if (sendTo.equals("middle")) {
					Q1_6.setText("");

					Q2_6.setText(nameOfThread);
				} else if (sendTo.equals("fcfs")) {
					Q2_6.setText("");

					Q3_6.setText(nameOfThread);
				}
			} else { // promote
				if (sendTo.equals("middle")) {
					Q3_6.setText("");

					Q2_6.setText(nameOfThread);
				} else if (sendTo.equals("top")) {
					Q2_6.setText("");

					Q1_6.setText(nameOfThread);
				}
			}
			break;

		default:
			break;

		}
	}

	/**
	 * This method calculates the response ratio of the thread by using its
	 * waiting time and burst time.
	 * 
	 * 
	 * @param waitTime
	 *            The time the thread has been in the queue for,since its
	 *            arrival.
	 * @param burstTime
	 *            the burst time of the thread
	 * @return the response ratio of the thread.
	 */
	public double calculateRatio(double waitTime, double burstTime) {
		return ((waitTime + (burstTime)) / (burstTime));
	}

}
