
/**
 * This implementation of the ElevatorSubsystem Class
 * Receives packets from the Scheduler and forwards/routes them to the corresponding elevator
 * RECEIVES ONLY, sending is handled by each respective elevator
 * 
 * @author Saad Eid
 */


public class ElevatorSubsystem extends Thread {
	
	//List of elevators
	private Elevator elevatorList[];
	
	
	/**
	 * Create a new elevator subsystem with numElevators and numFloors
	 * @param numElevators the number of elevators in the system
	 */
	public ElevatorSubsystem(int numFloors, int numElevators) {
		elevatorList = new Elevator[numElevators];
		
		//Initialize the elevators
		for (int i = 0; i < numElevators; i ++) {
			elevatorList[i] = (new Elevator(i, numFloors, new MessageQueue()));
		}
		
		for (Elevator e: elevatorList) {
			e.start();
		}
	}
	

	/**
	 * Returns the elevator with the corresponding elevator number
	 * @param elevatorNum the elevator number
	 * @return corresponding elevator
	 */
	public Elevator getElevator(int elevatorNum) {
		return elevatorList[elevatorNum];
	}
	
	
	/**
	 * Print a status message in the console
	 * @param message the message to be printed
	 */
	public void print(String message) {
		System.out.println("ELEVATOR SUBSYSTEM: " + message);
	}
	
	/**
	 * Wait for the specified amount of time
	 * @param ms
	 */
	public void wait(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		int numFloors = 22, numElevators = 1;				
		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(numFloors, numElevators);
	}
}