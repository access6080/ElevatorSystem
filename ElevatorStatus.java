import java.io.Serializable;
import java.util.HashMap;

public record ElevatorStatus(HashMap<Integer, Boolean> status) implements Serializable { }
