import java.util.ArrayList;
/**
 * The Elevator class models the elevator component.
 * 
 * @author Saad Eid
 * @version January 31th, 2023
 */
public class Elevator extends Thread implements Runnable{

	private static final int IDLE = 0;
	private static final int UP = 1;
	private static final int DOWN = 2;
	
	private int elevatorNum;
	private int currFloor;
	private int numFloors;
	private int currentDirection;		
	private boolean movingUp;
	private boolean movingDown;
	private boolean doorOpen;
	private boolean elevatorReady;	
	private MessageQueue queue;
	private ArrayList<Integer> reqFloors;
		
	/**
	 *  Constructor of the elevator class.
	 *  Instantiates the the data structure - arraylist and message queue
	 */
	public Elevator (int elevatorNum, int numFloors) {
		this.elevatorNum = elevatorNum;
		this.numFloors = numFloors;
		this.movingUp = false;
		this.movingDown = false;
		this.doorOpen = false;
		this.elevatorReady = true;
		this.currFloor = 1;
		this.reqFloors = new ArrayList<>();
		this.queue = new MessageQueue();
	}
	
	/**
	 * This method moves the elevator up.
	 */
	public void moveUp() {
		System.out.println("Now moving up.");
		movingUp = true;
		movingDown = false;
		currentDirection = UP;
	}
	
	/**
	 * This method returns whether if the elevator is moving up.
	 */
	public boolean isMovingUp() {
		return this.movingUp;
	}
	
	/**
	 * This method moves the elevator down.
	 */
	public void moveDown() {
		System.out.println("Now moving down.");
		movingUp = false;
		movingDown = true;
		currentDirection = DOWN;
	}
	
	/**
	 * This method returns whether if the elevator is moving up.
	 */
	public boolean isMovingDown() {
		return this.movingDown;
	}
	
	/**
	 * This method stops the elevator.
	 */
	public void stopElevator() {
		System.out.println("The elevator is stopping.");
		movingUp = false;
		movingDown = false;
		currentDirection = IDLE;
		System.out.println("The elevator is stopped.");
	}
	
	/**
	 * This method returns whether if the elevator is at idle.
	 */
	public boolean isIdle() {
		if (currentDirection == IDLE) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * This method adds the floor number into required floor list.
	 */
	public void chooseFloor(int floorNum) {
		this.reqFloors.add(floorNum);
	}
	
	/**
	 * This method removes the floor number from the required floor list.
	 */
	public void removeFloor(int floorNum) {
		this.reqFloors.remove(floorNum);
	}
	
	//Replaces moveToFloor
	/**
	 * This method moves the elevator up by one floor.
	 */
	public void moveOneFloor() {
		switch (currentDirection) {
			case UP:
				if (currFloor != reqFloors.get(0)) {
					currFloor++;
				}
				if (currFloor > numFloors) {
					currFloor = numFloors;
				}
				System.out.println("Currently on floor " + currFloor + ", moving up.");
				break;
			case DOWN:
				if (currFloor != reqFloors.get(0)) {
					currFloor--;
				}
				if (currFloor <= 0) {
					currFloor = 1;
				}
				System.out.println("Currently on floor " + currFloor + ", moving down.");
				break;
		}
	}
	
	/**
	 * This method opens the door of the elevator.
	 */
	public void openDoor() {
		System.out.println("The doors are opening.");
		try {
			wait(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("The doors are opened.");
		this.doorOpen = true;		
	}
	
	/**
	 * This method closes the door of the elevator.
	 */
	public void closeDoor(){
		System.out.println("The doors are closing.");
		try {
			wait(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("The doors are closed.");
		this.doorOpen = false;
	}
	
	/**
	 * This method returns whether the door of the elevator is open.
	 */
	public boolean isDoorOpen() {
		return this.doorOpen;
	}
	
	/**
	 * This method returns whether the elevator is ready to move.
	 */
	public boolean isElevatorReady() {
		return this.elevatorReady;
	}
	
	@Override
	public void run() {
		System.out.println("The elevator thread " + elevatorNum + " started.");
		while (true) {
			// If there are requested floors, and the elevator is ready to move
			// (Doors are closed/no pending requests)
			if (isIdle() && reqFloors.isEmpty()) {
				System.out.println("ON STANDBY");
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (!reqFloors.isEmpty() && elevatorReady) {
				moveOneFloor();
				
				// Update the scheduler about current status
				elevatorReady = false;
				//send message to scheduler

				// waiting for on the instruction from Scheduler
			}
		}
	}
}
