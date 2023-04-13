public class ElevatorSystemEvent {

    public enum EventType {ToggleDirectionLamp, ToggleFloorLamp, UpdateFloorNumber}

    private EventType type;
    private int floorNum;
    private boolean floorLampState;
    private int elevatorNum;
    private int currentFloor;
    private String log;

    public ElevatorSystemEvent(int floorNum, boolean floorLampState, EventType type){
        this.floorNum = floorNum;
        this.floorLampState = floorLampState;
        this.type = type;
    }

    public ElevatorSystemEvent(int elevatorNum, int currentFloor, EventType type){
        this.elevatorNum = elevatorNum;
        this.currentFloor = currentFloor;
        this.type = type;
    }

    public ElevatorSystemEvent(String log, EventType type){
        this.log = log;
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

    public int getFloorNum() {
        return floorNum;
    }

    public boolean isFloorLampState() {
        return floorLampState;
    }

    public int getElevatorNum() {
        return elevatorNum;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public String getLog() {
        return log;
    }
}
