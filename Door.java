/**
 * The door class models the door of the elevator. It shows the status of the door.
 *
 * @author Oluwatomisin Ajayi
 * @version March 10th, 2022
 */
public class Door {
    public enum DoorState{OPEN, MOVING, CLOSED, STUCK}
    private final Logger logger;
    private DoorState status;

    public Door(){
        this.status = DoorState.CLOSED;
        this.logger = new Logger();
    }

    /**
     * This method gets the current status of the door.
     * @return status returns the status of the door
     */
    public DoorState getStatus(){
        return status;
    }

    /**
     * This method sets the status of the door to OPEN.
     */
    public void openDoor(){
        logger.info("Doors Open");
        status = DoorState.OPEN;
    }

    /**
     * This method sets the status of the door to CLOSED.
     */
    public void closeDoor(){
        logger.info("Doors Closed");
        status = DoorState.CLOSED;
    }

    /**
     * This method sets the status of the door to STUCK.
     */
    public void breakDoor(){
        this.status = DoorState.STUCK;
        logger.info("Doors Stuck");
    }

    /**
     * This method sets the status of the door to MOVING.
     */
    public void doorMoving(){
        status = DoorState.MOVING;
    }
}