/**
 * This record encapsulates an event occurring in the ElevatorSystem.
 *
 * @author Oluwatomisin Ajayi
 * @version January 30th, 2023
 *
 * @param time the time it takes for the simulation to occur.
 * @param currentFloor the current floor of the elevator in the simulation.
 * @param button the number of the next floor the elevator goes to.
 */
public record ElevatorEvent(int time, int currentFloor, int button) {}
