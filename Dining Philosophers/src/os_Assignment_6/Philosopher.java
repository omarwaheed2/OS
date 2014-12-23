package os_Assignment_6;

/**
 * 
 * @author Omar Waheed
 * 
 *         This class basically creates a philosopher, assigns it a number/id
 *         and a table. When the object of Philosopher class is started as a
 *         thread, it keeps on repeating a pattern of sleeping (waiting), taking
 *         chopstics, sleeping (waiting) and finally stop eating and returning
 *         chopsticks.
 *
 */

public class Philosopher extends Thread {

	private int PhilosopherNumber;
	DiningPhilosophers diningTable; // reference to the philosopeher's table

	/**
	 * Assigns philosopher an id and a reference to table where all the other
	 * philosophers from this same class will be sitting
	 * 
	 * @param referenceToTable
	 *            reference to table where the philosopher will be sitting
	 * @param num
	 *            philosopher's number or his id.
	 */
	public Philosopher(DiningPhilosophers referenceToTable, int num) {
		diningTable = referenceToTable;
		PhilosopherNumber = num;

	}
	
	/**
	 * Run method does the basic algorhitm that is
	 * (1)takeChopstics()
	 * (2)eat
	 * (3)returnChopsticks()
	 * 
	 * nap gives the overall dining philosophers program a random behaviour
	 */

	public void run() {
		while (true) {

			SleepUtilities.nap();
			diningTable.takeChopsticks(PhilosopherNumber);
			SleepUtilities.nap();
			
			diningTable.returnChopsticks(PhilosopherNumber);

		}
	}

}
