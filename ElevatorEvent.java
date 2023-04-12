import java.io.Serial;
import java.io.Serializable;
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
    private long time;
    private final int currentFloor;
    private final int carButton;
    private FloorSubSystem.FloorButton floorButton;
    private int elevatorNum;
    private FloorSubSystem.Fault fault;

    /**
     * @param time         the time it takes for the simulation to occur.
     * @param currentFloor the current floor of the elevator in the simulation.
     * @param carButton    the number of the next floor the elevator goes to.
     */
    public ElevatorEvent(long time, int currentFloor, FloorSubSystem.FloorButton floorButton, int carButton,
                         FloorSubSystem.Fault fault) {
        this.time = time;
        this.currentFloor = currentFloor;
        this.carButton = carButton;
        this.floorButton = floorButton;
        this.fault = fault;
    }

    public ElevatorEvent(int elevatorNum, int currentFloor, int requestFloor, FloorSubSystem.Fault fault) {
        this.carButton = requestFloor;
        this.elevatorNum = elevatorNum;
        this.currentFloor = currentFloor;
        this.fault = fault;
    }

    public long time() {
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

    public FloorSubSystem.Fault fault() {
        return fault;
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
}
