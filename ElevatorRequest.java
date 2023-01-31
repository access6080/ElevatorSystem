/**
 * This record Encapsulates the requests made by the Elevator component to
 * the scheduler component of the elevator system
 *
 * @author Boma Iyaye 101197410
 * @version January 29th ,2023
 *
 * @param elevatorNum id number of the elevator making the request
 * @param requestedFloor floor on which the elevator made the request
 */
public record ElevatorRequest(int elevatorNum, int requestedFloor) {
}
