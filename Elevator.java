import java.util.ArrayList;

/**
 * The Elevator class models the elevator component.
 * 
 * @author Saad Eid
 * @author Geoffery Koranteng
 * @version January 31st, 2023
 */
public class Elevator extends Thread{
	private static final int IDLE = 0;
	private static final int UP = 1;
	private static final int DOWN = 2;
	private final int elevatorNum;
	private int currFloor;
	private final int numFloors;
	private int currentDirection;		
	private boolean movingUp;
	private boolean movingDown;
	private boolean doorOpen;
	private final MessageQueue queue;
	private final ArrayList<Integer> reqFloors;
	private final Logger logger;
	private boolean shutdown;
	private boolean isIdle;
		
	/**
	 *  Constructor of the elevator class.
	 *  Instantiates the data structure - arraylist and message queue
	 */
	public Elevator (int elevatorNum, int numFloors, MessageQueue queue) {
		this.elevatorNum = elevatorNum;
		this.numFloors = numFloors;
		this.movingUp = false;
		this.movingDown = false;
		this.doorOpen = false;
		this.currFloor = 1;
		this.reqFloors = new ArrayList<>();
		this.queue = queue;
		this.logger = new Logger();
		this.shutdown = false;
		this.isIdle = true;

	}
	
	/**
	 * This method returns the current floor
	 * 
	 * @return the current floor of the elevator
	 */
	public int getCurrFloor() {
		return this.currFloor;
	}
	
	/**
	 * This method returns the current floor
	 * 
	 * @return the reqFloor arraylist
	 */
	public ArrayList<Integer> getReqFloors() {
		return this.reqFloors;
	}
	
	/**
	 * This method moves the elevator up.
	 */
	public void moveUp() {
		logger.info("Now moving up.");
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
		logger.info("Now moving down.");
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
		logger.info("The elevator is stopping.");
		movingUp = false;
		movingDown = false;
		currentDirection = IDLE;
		logger.info("The elevator is stopped.");
	}
	
	/**
	 * This method returns whether if the elevator is at idle.
	 */
	public boolean isIdle() {
		return isIdle;
	}
	
	/**
	 * This method adds the floor number into required floor list.
	 */
	public void chooseFloor(int floorNum) {
		if (!reqFloors.contains(floorNum)){
			this.reqFloors.add(floorNum);
		}		
	}

	/**
	 * This method moves the elevator up by one floor.
	 */
	public void moveOneFloor() {
		switch (currentDirection) {
			case UP -> {
				if (currFloor != reqFloors.get(0)) {
					currFloor++;
				}
				if (currFloor > numFloors) {
					currFloor = numFloors;
				}
				logger.info("Currently on floor " + currFloor + ", moving up.");
			}
			case DOWN -> {
				if (currFloor != reqFloors.get(0)) {
					currFloor--;
				}
				if (currFloor <= 0) {
					currFloor = 1;
				}
				logger.info("Currently on floor " + currFloor + ", moving down.");
			}
		}
	}

	public int getElevatorNum() {
		return elevatorNum;
	}

	/**
	 * This method executes a job scheduled by the Scheduler component
	 */
	public void executeJob() throws InterruptedException {
		if(this.queue.hasAMessage(elevatorNum)) {
			Message msg = this.queue.getMessage(elevatorNum);

			if(msg.type().equals(MessageType.Function_Request)){
				Message response = new Message(ElevatorSubsystem.PRIORITY, ElevatorSystemComponent.Elevator,
						isIdle(), MessageType.Function_Response);
				this.queue.addMessage(response);
				logger.info("Responding to function request -> isIdle()");
				return;
			}

			ElevatorEvent job = (ElevatorEvent) msg.data();

			int movingTo = job.button();

			if (Math.max(this.currFloor, movingTo) == currFloor) {
				this.currentDirection = DOWN;
			} else {
				this.currentDirection = UP;
			}

			this.isIdle = false;
			if (Math.max(this.currFloor, movingTo) == movingTo) {
				logger.info("Currently on floor " + currFloor + ", moving up.");
			} else {
				logger.info("Currently on floor " + currFloor + ", moving down.");
			}
			this.currFloor = movingTo;

			// Now one the desired floor
			logger.info("Now on floor " + currFloor);
			this.openDoor();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
				throw new RuntimeException(e);
			}
			this.closeDoor();
			isIdle = true;
		}
	}
	
	/**
	 * This method opens the door of the elevator.
	 */
	public void openDoor() {
		logger.info("The doors are opening.");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
		logger.info("The doors are opened.");
		this.doorOpen = true;		
	}
	
	/**
	 * This method closes the door of the elevator.
	 */
	public void closeDoor(){
		logger.info("The doors are closing.");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
		logger.info("The doors are closed.");
		this.doorOpen = false;
	}

	/**
	 * This method returns whether the door of the elevator is open.
	 */
	public boolean isDoorOpen() {
		return this.doorOpen;
	}


	/**
	 * This method sets the flag that tells the elevator to shut down
	 */
	private void setShutdownFlag() {
		this.shutdown = true;
	}

	/**
	 * This method initiates the shut-down process
	 */
	private void continueOrShutdown() {
		if(this.shutdown) {
			logger.info("Shutting Down.");
			Thread.currentThread().interrupt();
		}
	}


	
	/**
	 * This implementation of the ElevatorSubsystem Class
	 * Receives packets from the Scheduler and forwards them to the corresponding elevator
	 * RECEIVES ONLY, sending is handled by each respective elevator
	 * 
	 * @author Saad Eid
	 *
	
	/**
	 * This method is implemented from the runnable interface
	 * and allows this class to be run by a thread instance.
	 */
	@Override
	public void run() {

		logger.info("Elevator " + elevatorNum + " started.");
		while (Thread.currentThread().isAlive()) {
			if(Thread.currentThread().isInterrupted()) break;

			try {
				executeJob();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

			if(reqFloors.isEmpty() && !queue.hasAMessage(elevatorNum)) continueOrShutdown();
		}
	}
}
