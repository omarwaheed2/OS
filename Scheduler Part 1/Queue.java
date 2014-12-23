package os_assignment_9;

/**
*  SingleQueue.java
*
* This class implements a real queue instead of a circular queue that consisted of a index based vector.
* 
* @author Omar Waheed
*

*/

import java.util.*;

public class Queue<TestThread> {
	private LinkedList<TestThread> queue;

	public Queue() {
		queue = new LinkedList<TestThread>();

	}

	/**
	 * this method returns the next thread in the queue.
	 * 
	 * @return Thread, the next thread in the queue
	 */
	public TestThread getNext() {
		TestThread nextElement = null;

		if (!queue.isEmpty()) {
			nextElement = queue.remove();
		}

		return nextElement;
	}

	/**
	 * Returns the boolean telling if queue is empty or not.
	 * 
	 * @return true if queue is empty, else false
	 */
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	/**
	 * this method adds a thread to the queue
	 * 
	 * @return void
	 */
	public void addItem(TestThread t) {
		queue.add(t);

	}

	/**
	 * Tell how many threads are in the queue
	 * 
	 * @return The number of threads in the queue
	 */
	public int getSize() {
		return queue.size();
	}

}