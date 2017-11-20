import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Class DiningPhilosophers The main starter.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class DiningPhilosophers {
	/*
	 * ------------ Data members ------------
	 */

	/**
	 * This default may be overridden from the command line
	 */
	public static final int DEFAULT_NUMBER_OF_PHILOSOPHERS = 4;

	/**
	 * Dining "iterations" per philosopher thread while they are socializing
	 * there
	 */
	public static final int DINING_STEPS = 10;

	/**
	 * Our shared monitor for the philosophers to consult
	 */
	public static Monitor soMonitor = null;

	/*
	 * ------- Methods -------
	 */

	/**
	 * Main system starts up right here
	 */
	public static void main(String[] argv) {
		try {
			/*
			 * TASK 2: Should be able to set the number of philosophers from the
			 * command line or take the default number if no numbers are entered.
			 */
			// creates a scanner to read the command-line input
			Scanner scanner = new Scanner(System.in);

			int num = 0;
			boolean invalidInput = true;

			while (invalidInput) {
				try {
					// prompts for the # of philosophers
					System.out.print("Enter the number of philosophrs: ");
					// get the input as an int
					num = Integer.parseInt(scanner.nextLine());
					if (num < 2) {
						System.out.println("The number must be greater than 2! ");
					} else {
						invalidInput = false;
						System.out.println(String.format("The number of philosophers is %d.", num));
					}
				} catch (NumberFormatException nfe) {
					// chooses the default number of 4 if user input is not a
					// number
					System.out.println("Invalid input! Default number of 4 has been chosen.");
					invalidInput = false;
				}
			}

			int iPhilosophers = DEFAULT_NUMBER_OF_PHILOSOPHERS;

			// Make the monitor aware of how many philosophers there are
			if (num > 0)
				iPhilosophers = num;
			soMonitor = new Monitor(iPhilosophers);

			// Space for all the philosophers
			Philosopher aoPhilosophers[] = new Philosopher[iPhilosophers];
			
			System.out.println(iPhilosophers + " philosopher(s) came in for a dinner.");
			
			// Let 'em sit down
			for (int j = 0; j < iPhilosophers; j++) {
				aoPhilosophers[j] = new Philosopher();
				aoPhilosophers[j].start();
			}

//			System.out.println(iPhilosophers + " philosopher(s) came in for a dinner.");

			// Main waits for all its children to die...
			// I mean, philosophers to finish their dinner.
			for (int j = 0; j < iPhilosophers; j++)
				aoPhilosophers[j].join();

			System.out.println("All philosophers have left. System terminates normally.");
		} catch (InterruptedException e) {
			System.err.println("main():");
			reportException(e);
			System.exit(1);
		}
	} // main()

	/**
	 * Outputs exception information to STDERR
	 * 
	 * @param poException
	 *            Exception object to dump to STDERR
	 */
	public static void reportException(Exception poException) {
		System.err.println("Caught exception : " + poException.getClass().getName());
		System.err.println("Message          : " + poException.getMessage());
		System.err.println("Stack Trace      : ");
		poException.printStackTrace(System.err);
	}
}

// EOF
