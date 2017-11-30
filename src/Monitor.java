/**
 * Class Monitor To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor {
	/*
	 * ------------ Data members ------------
	 */
	// Task 2:
	// any philosopher can have only 4 states
	enum States {
		THINKING, EATING, TALKING, HUNGRY, WANTS_TO_TALK
	}

	// the number of states that exist
	private States[] sts;

	// the number of philosophers
	private int numOfPhils;
	// flag that tells if there is someone talking
	private static boolean someoneIsTalking;

	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers) {
		// the number of philosophers who have those states
		sts = new States[piNumberOfPhilosophers];
		// initializes all philosophers with state of THINKING
		for (int i = 0; i < piNumberOfPhilosophers; i++)
			sts[i] = States.THINKING;

		numOfPhils = piNumberOfPhilosophers;
	}

	/*
	 * ------------------------------- User-defined monitor procedures
	 * -------------------------------
	 */

	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public synchronized void pickUp(final int piTID) {
		// assumes that the philosopher who wants to pick up chopsticks must be
		// hungry
		sts[piTID - 1] = States.HUNGRY;
		// determines if the philosopher is allowed to eat
		testEat(piTID - 1);

		while (sts[piTID - 1] != States.EATING) {
			try {
				// the phil has to wait until neighbors finish eating
				this.wait();
			} catch (InterruptedException e) {
				e.getMessage();
				e.printStackTrace();
			}
		}
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public synchronized void putDown(final int piTID) {
		// assumes that the philosopher who wants to put down chopsticks is to
		// think again
		sts[piTID - 1] = States.THINKING;

		// Task 4: prevents starvation from happening we test the left and right
		// neighbors of the phil who has just done eating: Set them to EATING
		// once 2 chopsticks are available

		// determines if the phil to the left is allowed to eat
		testEat((piTID + numOfPhils - 2) % numOfPhils);
		// determines if the phil to the right is allowed to eat
		testEat((piTID) % numOfPhils);
	}

	/**
	 * Only one philosopher at a time is allowed to philosophy (while she is not
	 * eating).
	 */
	public synchronized void requestTalk(final int piTID) {
		// assumes that the philosopher wants to talk
		sts[piTID - 1] = States.WANTS_TO_TALK;
		// tests if the philosopher is eating
		testTalk(piTID - 1);

		while (sts[piTID - 1] != States.TALKING) {
			try {
				// waits until the phil who is talking to finish
				this.wait();
			} catch (InterruptedException e) {
				e.getMessage();
				e.printStackTrace();
			}
		}
	}

	/**
	 * When one philosopher is done talking stuff, others can feel free to start
	 * talking.
	 */
	public synchronized void endTalk(final int piTID) {
		// resets the phil back to think
		sts[piTID - 1] = States.THINKING;
		// reset the flag to default value
		someoneIsTalking = false;
		// notifies other phils that one of them may start to talk
		this.notify();
	}

	/**
	 * Tests the states of the phils to the left and right of the philosopher
	 * with position id i
	 * 
	 * @param i
	 *            represents the position id of the passed in philosopher
	 */
	private synchronized void testEat(int i) {
		int left, right;
		// determines the phil to the left of the phil passed in with position
		// id i
		left = (i + numOfPhils - 1) % numOfPhils;
		// determines the phil to the right of the phil passed in with position
		// id i
		right = (i + 1) % numOfPhils;

		// a hungry phil is allowed to eat only if both neighbors are not eating
		if ((sts[left] != States.EATING) && (sts[i] == States.HUNGRY) && (sts[right] != States.EATING)) {
			sts[i] = States.EATING;
			this.notify();
		}
	}

	/**
	 * Tests if there is any phil is talking now
	 * 
	 * @param i
	 *            represents the position id of the passed in philosopher
	 */
	private synchronized void testTalk(int i) {
		// a phil who wants to talk cannot be eating
		if (sts[i] != States.EATING && sts[i] == States.WANTS_TO_TALK) {
			// loops thru to see if there already a philosopher is talking
			for (int j = 0; j < numOfPhils; j++) {
				if (sts[j] == States.TALKING) {
					someoneIsTalking = true;
					break;
				}
			}
			// the phil is allowed to talk since no one is talking now
			if (!someoneIsTalking) {
				sts[i] = States.TALKING;
				this.notify();
			}
		}
	}
}

// EOF
