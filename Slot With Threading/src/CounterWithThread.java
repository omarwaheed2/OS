import java.awt.*;
import java.util.Random;

/**
 * A demonstration of a threaded application with a responsive interface.
 * Clicking on the "Start" button starts the counter. The button is then
 * labelled "Stop", pressing it will stop the count, and pressing it again will
 * resume it (etc., etc.). The counting is done by an object in the inner class
 * CounterRunner.
 */

public class CounterWithThread extends Frame {
	private double startTime;
	private double limitTime1;

	private Random randIntGen;
	private TextField textWindow1;

	private int count;
	private boolean isStopped;
	private boolean stop;
	public static final int HEIGHT = 400;
	public static final int WIDTH = 400;
	
	public CounterWithThread() {
		stop = true;
		count = 0;
		isStopped = false;
		randIntGen = new Random();

		addWindowListener(new WindowDestroyer());

		setLayout(new FlowLayout());

		textWindow1 = new TextField(5);
		add(textWindow1);
		textWindow1.setText("Starting....");

		// Declare, create, and start the counter thread.
		CounterRunner counterThread = new CounterRunner();
		counterThread.start();

	}// CounterWithThread

	
	/**
	 * 
	 * @param input new value of stop variable which is either true or false
	 */
	public void setStop(boolean input) {
		stop = input;
	}

	/**
	 * 
	 * @return checks and tells if the game is stopped or not
	 */
	public boolean isStopped() {
		return isStopped;
	}

	
	/**
	 * 
	 * @return the current number on slot ranging from 0 - 3
	 */
	public int getCount() {
		return count % 4;
	}

	/*
	 * This inner class does the counting. When an object in this class is
	 * started (using the Thread method start()), the run() method is
	 * automatically invoked.
	 * 
	 * limit time basically find a random time between 1 to 6 seconds and keeps the slot/count running until elapsed time since
	 * stop was pressed i.e (System.currentTimeMillis() - startTime) , is greater than our random limit time.
	 * This gives the stopping of slots a random manner and more realistic feel.
	 */
	private class CounterRunner extends Thread {

		public void run() {
			while (true) {
				try {
					
					sleep(40);

				} catch (InterruptedException e) {
					System.err.println("Interrupted.");
				}
				if (!stop) {

					startTime = System.currentTimeMillis();
					count++;
					limitTime1 = Math.abs((randIntGen.nextInt(6) * 1000));
					isStopped = true;

				} else {

					if (System.currentTimeMillis() - startTime > (limitTime1)) {
							isStopped = false;
					} else {

						count++;
					}

				}
			}

		}// run
	}// CounterRunner

}// CounterWithThread

