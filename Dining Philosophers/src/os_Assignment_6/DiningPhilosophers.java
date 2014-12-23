package os_Assignment_6;

/**
 *
 * @author Omar Waheed
 * 
 *         This class consist of methods that enable the philosophers to pick
 *         chopsticks and eat (when conditions are appropriate), eat food and
 *         the return chopsticks, so that other neighboring philosophers can
 *         eat.
 *         
 *         Note: I have used two approaches to solve this problem. To use the 
 *         second approach you will need to comment out the first approach and
 *         then uncomment the second approach. Sorry for using inline commenting
 *         of methods (to separate the two approaches), but the block comment was not working.
 *         If you use eclipse then just select the whole block of code that is 
 *         commented and use ctrl + /  to uncomment or comment the whole block of inline comments.
 */

class DiningPhilosophers {
	enum State {
		THINKING, HUNGRY, EATING
	};

	public static State[] state = new State[5]; // states of philosophers

	/**
	 * Constructor: Initializes the state of all the philosophers to thinking.
	 * Furthermore we also start a thread of class EatingTest that checks if two
	 * neighbors are not eating simultaneously which is an obvious flaw.
	 */
	public DiningPhilosophers() {
		for (int i = 0; i < 5; i++) {
			state[i] = State.THINKING;
			System.out.println("philosopher " + i + " is thinking");
		}

		EatingTest isNeigbourEating = new EatingTest(state);
		Thread testThread = new Thread(isNeigbourEating);
		testThread.start();
	}

	/**
	 * This method a helper method that returns the current state of our philosopher's stored in State[]; this information is used
	 * in the class EatingTest to check the condition if any two neighboring
	 * philosophers are not eating at the same time.
	 * 
	 * @return array that contains the current state of our philosophers.
	 */
	public static State[] getState() {
		return state;
	}

	/*************************FIRST APPROACH STARTS HERE*************************/


//Start uncommenting/commenting from below here to enable or disable this approach
	
	/**
	 * This method basically changes the state of a thinking philosopher to
	 * 'hungry' and then checks if he has chopsticks on his left and right. If
	 * he doesn't have chopsticks on left or right or on both sides, then he
	 * waits until there are chopsticks on his both sides; when chopsticks are
	 * available, i.e. some other philosopher notifies him that
	 * "check if there are chopsticks available on both of your sides" he picks
	 * them up ( by setting his state to 'eating').
	 * 
	 * @param i
	 *            philosopher number
	 */
	public synchronized void takeChopsticks(int i) {

		state[i] = State.HUNGRY;
		System.out.println("philosopher " + i + " is HUNGRY ");

		test(i);
		while (state[i] != State.EATING) {
			try {
				System.out.println("philosopher " + i + " is WAITING");
				wait(); /* thread should wait as its condition to eat are not satisfied*/
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * This method sets the state of philosopher from eating to thinking i.e
	 * returns the chopsticks
	 * 
	 * @param i
	 *            philosopher number
	 */
	public synchronized void returnChopsticks(int i) {
		state[i] = State.THINKING;
		System.out.println("philosopher " + i + "  FINISHED FOOD");
		// test left and right neighbors
		test((i + 4) % 5);
		test((i + 1) % 5);
	}
	
	//Stop uncommenting/commenting from below here to enable or disable this approach

	/*************************FIRST APPRAOCH ENDS HERE********************************/

	
	/*****************************SECOND APPROACH STARTS HERE***************************/
	
	
	
	/*THIS IS MY SECOND APPROACH FOR TAKING CHOPSTICKS. To make it work do the following:
	 * (1) comment the return chopsticks and take chopsticks methods above.
	 * (2) uncomment this whole block of code which include "takeChopstics()" and "returnChopsticks()" methods.
	 * (3) in 'test' method, instead of notifyAll(), use notify().
	 * 
	 * Note : If we test both neighbors ( in returnChopsticks() ) in this approach it causes a dead lock, so make sure not to use it.
	 */
	


	//Start uncommenting/commenting from below here to enable or disable this approach
	
//	/**
//	 * 
//	 * 
//	 * This method basically changes the state of a thinking philosopher to
//	 * 'hungry' and then checks if he has chopsticks on his left and right. If
//	 * he doesn't have chopsticks on left or right or on both sides, then he
//	 * waits until there are chopsticks on his both sides; when chopsticks are
//	 * available, i.e. some other philosopher notifies him that
//	 * "check if there are chopsticks available on both of your sides" he picks
//	 * them up ( by setting his state to 'eating').
//	 * 
//	 * 
//	 * 
//	 * @param i
//	 *            philosopher number
//	 */
//	public synchronized void takeChopsticks(int i) {
//
//		state[i] = State.HUNGRY;
//		System.out.println("philosopher " + i + " is HUNGRY ");
//		while (state[i] != State.EATING) {
//			test(i);
//			if (state[i] != State.EATING) {
//				try {
//					System.out.println("philosopher " + i + " is WAITING");
//					wait(); /* thread should wait as its condition to eat are not satisfied*/
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//
//	public synchronized void returnChopsticks(int i) {
//		state[i] = State.THINKING;
//		System.out.println("philosopher " + i + "  FINISHED FOOD and returned the chopsticks");
//		/* We dont need to test left and right neighbors in this approach => causes deadlock
//		// test left and right neighbors
//		 test((i + 4) % 5);
//		 test((i + 1) % 5);
//		 * 
//		 */
//	}
//	

	
	//Stop uncommenting/commenting from below here to enable or disable this approach
	
	/************************************************Second APPROACH ENDS***********************************/


	/**
	 * This method tests if the condition to start eating are satisfied.
	 * Following are the conditions to start eating: (i) The philosopher on left
	 * and right of the hungry philosopher should not be eating (ii) and the
	 * hungry philosopher should indeed be hungry.
	 * 
	 * if the above conditions are satisfied then the philosopher's state is
	 * changed to 'eating' and he notifies everone on the table to see if anyone
	 * else in the waiting list can eat now
	 * 
	 * @param i
	 *            philosopher number
	 */

	private synchronized void test(int i) {
		if ((state[(i + 4) % 5] != State.EATING) && (state[i] == State.HUNGRY)
				&& (state[(i + 1) % 5] != State.EATING)) {
			state[i] = State.EATING;
			System.out.println("philosopher " + i + " EATING");
			/* If using second approach, change notifyAll() to notify()*/
			this.notifyAll(); // notify all other thread to get off from waiting queue

		}
	}
}
