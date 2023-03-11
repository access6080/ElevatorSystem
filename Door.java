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

    public DoorState getStatus(){
        return status;
    }

    public void openDoor(){
        status = DoorState.OPEN;
    }

    public void closeDoor(){
        status = DoorState.CLOSED;
    }
    public void doorMoving(){
        status = DoorState.MOVING;
    }
}
