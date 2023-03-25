import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * The Elevator class models the elevator component.
 *
 * @author Saad Eid
 * @author Geoffery Koranteng
 * @version January 31st, 2023
 */
public class Elevator implements Runnable {
	private final int elevatorNum;
	private final Door doors;
	private final Motor motor;
	private final Lamp elevatorLamp;
	private int currFloor;
	private ElevatorEvent currentJob;
	private int finalDestination;
	private boolean movingUp;
	private boolean movingDown;
	private boolean servicing;
	private final MessageQueue queue;
	private final LinkedList<ElevatorEvent> jobQueue;
	private final ArrayList<Integer> reqFloors;
	private final Logger logger;
	private ElevatorState status;
	private boolean isIdle;
	private final ArrivalSensor sensor;
	private enum ElevatorState {
		Start,
		Servicing_Request,
		StandBy
	}

	//The time to move one floor in the elevator.
	private static final int TimeToMoveOneFloor = 6000;

	/**
	 *  Constructor of the elevator class.
	 *  Instantiates the data structure - arraylist and message queue
	 */
	public Elevator (int elevatorNum, MessageQueue queue) {
		this.elevatorNum = elevatorNum;
		this.movingUp = false;
		this.movingDown = false;
		this.currFloor = 1;
		this.jobQueue = new LinkedList<>();
		this.reqFloors = new ArrayList<>();
		this.queue = queue;
		this.logger = new Logger();
		this.isIdle = true;
		this.doors = new Door();
		this.status = ElevatorState.Start;
		this.sensor = new ArrivalSensor();
		this.servicing = false;
		this.motor = new Motor();
		this.elevatorLamp = new Lamp();
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
	 * This method returns whether if the elevator is moving up.
	 */
	public boolean isMovingUp() {
		return this.movingUp;
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
		logger.info("The elevator is stopped.");
	}

	/**
	 * This method returns whether if the elevator is at idle.
	 */
	public boolean isIdle() {
		return isIdle;
	}

	public int getElevatorNum() {
		return elevatorNum;
	}

	public void elevatorOperation() throws InterruptedException {
		switch (status){
			case Start, StandBy -> checkForJob();
			case Servicing_Request -> executeJob();
		}
	}

	/**
	 * Checks if the elevator has received a new job
	 */
	private void checkForJob() {
		if(this.queue.hasAMessage(elevatorNum)) {
			try {
				Message msg = this.queue.getMessage(elevatorNum);
				ElevatorEvent job = (ElevatorEvent) msg.data();
				this.jobQueue.add(job);
				this.reqFloors.add(job.carButton());

				this.status = ElevatorState.Servicing_Request;
				return;
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		this.status = ElevatorState.StandBy;
	}

	/**
	 * This method executes a job scheduled by the Scheduler component
	 */
	public void executeJob() throws InterruptedException {
			if(!servicing) {
				this.currentJob = jobQueue.poll();
				if(currentJob == null) return;

				this.isIdle = false;
				this.finalDestination = currentJob.carButton();
				this.logger.info("Updating ElevatorSubSystem with idle status");
				this.queue.addMessage(new Message(ElevatorSubsystem.PRIORITY, elevatorNum,
						false, MessageType.Status_Update));
				this.servicing = true;
			}

			if(!reqFloors.isEmpty()){
				Collections.sort(reqFloors);
			}

			int movingTo = 0;
			int distance = this.currFloor - movingTo;

			if(this.finalDestination == reqFloors.get(0)){
				movingTo = currentJob.carButton();
			}
			else if (currentJob.floorButton().equals(FloorSubSystem.FloorButton.UP)){
					movingTo = Math.min(this.finalDestination, reqFloors.get(0));
			} else {
				movingTo = Math.max(this.currentJob.carButton(), reqFloors.get(0));
				reqFloors.remove(0);
			}

			//Computing the time to move between floors
			long waitTime = (long) TimeToMoveOneFloor * Math.abs(distance);

			this.motor.startMotor();
			if (Math.max(this.currFloor, movingTo) == movingTo) {
				logger.info("Currently on floor " + currFloor + ", moving up.");
				this.currFloor += 1;
				Thread.sleep(waitTime); //waiting to move between floors
			} else {
				logger.info("Currently on floor " + currFloor + ", moving down.");
				this.currFloor -= 1;
				Thread.sleep(waitTime); //waiting to move between floors
			}

			this.motor.stopMotor();

			logger.info("Arrival Sensor Activated");
			ElevatorRequest update = new ElevatorRequest(this.elevatorNum,  currFloor);
			this.sensor.notifyScheduler(update, queue);
			if(this.currFloor == movingTo) {
				// Now one the desired floor
				logger.info("Now on floor " + currFloor);
				this.doors.openDoor();
				this.elevatorLamp.turnOn();
				try {
					Thread.sleep(7000);
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
					throw new RuntimeException(e);
				}
				this.doors.closeDoor();
				this.elevatorLamp.turnOff();

				if(this.currFloor == this.finalDestination) {
					logger.info("Updating ElevatorSubSystem with idle status");
					this.queue.addMessage(new Message(ElevatorSubsystem.PRIORITY, elevatorNum,
							true, MessageType.Status_Update));
					isIdle = true;
					this.servicing = false;
					this.currentJob = null;
					this.status = ElevatorState.StandBy;
				}
			}
	}

	/**
	 * This method is implemented from the runnable interface
	 * and allows this class to be run by a thread instance.
	 */
	@Override
	public void run() {
		logger.info("Elevator " + elevatorNum + " started.");
		while (true) {
			try {
				elevatorOperation();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}