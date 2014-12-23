package os_Assignment_6;

/**
 * 
 * @author Omar
 *
 *This is the main class that initializes the 5 philosophers and gives them a reference to the table they will be sitting at.
 *After initialization the threads of the 5 philosophers are started
 *
 *
 */

public class PhilosopherThreads {

	final static int NUM_OF_PHILOSOPHERS = 5; // Total philosopher = 5

	/**
	 * In the main method we do the following:
	 * 
	 *(i) Create 5 threads for the 5 objects of Philosopher class => Thread[] philosophers 
	 *(ii) Create a reference to the table where the philosophers
	 * will be sitting => DiningPhilosophers diningTable
	 *(iii) Use the for loop and Initialize the thread with object
	 *of Philosopher class and assign each philosopher an id and then start that thread
	 */
	public static void main(String[] Args) {
		Thread[] philosophers = new Thread[5];
		DiningPhilosophers diningTable = new DiningPhilosophers();

		for (int philosopherNumber = 0; philosopherNumber < NUM_OF_PHILOSOPHERS; philosopherNumber++) {
			philosophers[philosopherNumber] = new Thread(new Philosopher(
					diningTable, philosopherNumber));
			philosophers[philosopherNumber].start();
		}

	}

}
