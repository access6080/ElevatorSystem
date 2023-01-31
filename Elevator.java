import java.util.ArrayList;


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
	
	public synchronized void moveUp() {
		System.out.println("Now moving up.");
		movingUp = true;
		movingDown = false;
		currentDirection = UP;
	}
	
	public boolean isMovingUp() {
		return this.movingUp;
	}
	
	public synchronized void moveDown() {
		System.out.println("Now moving down.");
		movingUp = false;
		movingDown = true;
		currentDirection = DOWN;
	}
	
	public boolean isMovingDown() {
		return this.movingDown;
	}
	
	public synchronized void stopElevator() {
		System.out.println("The elevator is stopping.");
		movingUp = false;
		movingDown = false;
		System.out.println("The elevator is stopped.");
	}
	
	public boolean isIdle() {
		if (currentDirection == IDLE) {
			return true;
		}else {
			return false;
		}
	}
	
	
	public void chooseFloor(int floorNum) {
		this.reqFloors.add(floorNum);
	}
	
	public void removeFloor(int floorNum) {
		this.reqFloors.remove(floorNum);
	}
	
	//Replaces moveToFloor
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
	
	public boolean isDoorOpen() {
		return this.doorOpen;
	}
	
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
