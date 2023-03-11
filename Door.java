/**
 * The door class models the door of the elevator. It shows the status of the door.
 *
 * @author Oluwatomisin Ajayi
 * @version March 10th, 2022
 */
public class Door {
    public enum DoorState{OPEN, MOVING, CLOSED}

    private DoorState status;

    public Door(){
        this.status = DoorState.CLOSED;
    }

    /**
     * This method gets the current status of the door.
     * @return status
     */
    public DoorState getStatus(){
        return status;
    }

    /**
     * This method sets the status of the door to OPEN.
     */
    public void openDoor(){
        status = DoorState.OPEN;
    }

    /**
     * This method sets the status of the door to CLOSED.
     */
    public void closeDoor(){
        status = DoorState.CLOSED;
    }

    /**
     * This method sets the status of the door to MOVING.
     */
    public void doorMoving(){
        status = DoorState.MOVING;
    }
}
