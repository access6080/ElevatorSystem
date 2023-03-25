import java.io.Serializable;
import java.util.Objects;

/**
 * This record Encapsulates the requests made by the Elevator component to
 * the scheduler component of the elevator system
 *
 * @author Boma Iyaye 101197410
 * @version January 29th ,2023
 */
public final class ElevatorRequest implements Serializable {
    private final int elevatorNum;
    private final int floor;

    /**
     * @param elevatorNum    id number of the elevator making the request
     * @param requestedFloor floor on which the elevator made the request or the current floor the elevator is on.
     */
    public ElevatorRequest(int elevatorNum, int requestedFloor) {
        this.elevatorNum = elevatorNum;
        this.floor = requestedFloor;
    }


    public int elevatorNum() {
        return elevatorNum;
    }

    public int floor() {
        return floor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ElevatorRequest) obj;
        return this.elevatorNum == that.elevatorNum &&
                this.floor == that.floor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(elevatorNum, floor);
    }

    @Override
    public String toString() {
        return "ElevatorRequest[" +
                "elevatorNum=" + elevatorNum + ", " +
                "requestedFloor=" + floor + ']';
    }

}
