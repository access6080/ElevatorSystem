import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

/**
 * This record encapsulates an event occurring in the ElevatorSystem.
 *
 * @author Oluwatomisin Ajayi
 * @version January 30th, 2023
 */
public final class ElevatorEvent implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;
    private Date time;
    private int currentFloor;
    private final int carButton;
    private FloorSubSystem.FloorButton floorButton;
    private int elevatorNum;

    /**
     * @param time         the time it takes for the simulation to occur.
     * @param currentFloor the current floor of the elevator in the simulation.
     * @param carButton    the number of the next floor the elevator goes to.
     */
    public ElevatorEvent(Date time, int currentFloor, FloorSubSystem.FloorButton floorButton, int carButton) {
        this.time = time;
        this.currentFloor = currentFloor;
        this.carButton = carButton;
        this.floorButton = floorButton;
    }

    public ElevatorEvent(int elevatorNum,  int requestFloor) {
        this.carButton = requestFloor;
        this.elevatorNum = elevatorNum;

    }

    public Date time() {
        return time;
    }

    public int currentFloor() {
        return currentFloor;
    }

    public int carButton() {
        return carButton;
    }

    public FloorSubSystem.FloorButton floorButton() {
        return floorButton;
    }

    public int elevatorNum(){
        return elevatorNum;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ElevatorEvent) obj;
        return Objects.equals(this.time, that.time) &&
                this.currentFloor == that.currentFloor &&
                this.carButton == that.carButton;
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, currentFloor, carButton);
    }

    @Override
    public String toString() {
        return "ElevatorEvent[" +
                "time=" + time + ", " +
                "currentFloor=" + currentFloor + ", " +
                "button=" + carButton + ']';
    }
}
