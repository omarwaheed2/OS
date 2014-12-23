package os_Assignment_6;

public class EatingTest extends Thread {

	os_Assignment_6.DiningPhilosophers.State State;

	os_Assignment_6.DiningPhilosophers.State[] state; // store the state of the five philosophers

	/**
	 * 
	 * @param inState
	 *            current state of the philosophers on the table
	 */
	public EatingTest(os_Assignment_6.DiningPhilosophers.State[] inState) {
		state = inState;
	}

	/**
	 * The run method checks if any two neighboring philosophers are eating at
	 * the same time, which should not be happening. If they are eating at the same time then
	 * It prints out the appropriate statement
	 */
	public void run() {
		while (true) {
			state = DiningPhilosophers.getState();

			for (int i = 0; i < state.length; i++) {
				if ((state[(i + 4) % 5] == State.EATING	&& state[i] == State.EATING)){ // one of the neighbor eating with me
					System.out.println("NOT POSSIBLE! YOU CAN'T EAT IF IM EATING!!!!!!!!!!!!!!!");
					System.exit(0);
				}
				if ((state[(i + 1) % 5] == State.EATING	&& state[i] == State.EATING)){ // one of the neighbor eating with me
					System.out.println("NOT POSSIBLE! YOU CAN'T EAT IF IM EATING!!!!!!!!!!!!!!!");
					System.exit(0);
				}
				if ((state[(i + 4) % 5] == State.EATING	&& state[(i + 1) % 5] == State.EATING && state[i] == State.EATING)) {// both of the neighbor eating with me
					System.out.println("NOT POSSIBLE! YOU BOTH CAN'T EAT IF IM EATING!!!!!!!!!!!!!!!");
					System.exit(0);
				}
			}
		}

	}

}
