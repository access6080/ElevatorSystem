import java.io.Serializable;

public record ElevatorUpdate(int elevatorNum, long startTime, long finishTime) implements Serializable {}
