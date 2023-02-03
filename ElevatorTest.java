import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class ElevatorTest {

	private Elevator elevator;
	private MessageQueue queue;

	@Before
	public void setUp() throws Exception {
		int elevatorNum = 1;
		int numFloors = 10;

		this.queue = new MessageQueue();
		this.elevator = new Elevator(elevatorNum, numFloors, queue);
	}
	
	@Test
	public void moveDownTest() {
		elevator.moveDown();
		assertFalse(elevator.isMovingUp());
		assertTrue(elevator.isMovingDown());
	}

	@Test
	public void stopElevatorTest() {
		elevator.stopElevator();
		assertFalse(elevator.isMovingUp());
		assertFalse(elevator.isMovingDown());
		assertTrue(elevator.isIdle());
	}

	@Test
	public void chooseFloorTest() {
		elevator.chooseFloor(5);
		ArrayList<Integer> reqFloors = elevator.getReqFloors();
		assertEquals(1, reqFloors.size());
		assertEquals(5, reqFloors.get(0).intValue());
	}

	@Test
	public void closeDoorTest() {
		// Open the door before closing
		elevator.openDoor();
		assertEquals(true, elevator.isDoorOpen());

		// Close the door and check if it's closed
		elevator.closeDoor();
		assertEquals(false, elevator.isDoorOpen());
	}

	@Test
	public void openDoorTest() {
		// Close the door before opening
		elevator.closeDoor();
		assertEquals(false, elevator.isDoorOpen());

		// Open the door and check if it's open
		elevator.openDoor();
		assertEquals(true, elevator.isDoorOpen());
	}
	
	@Test
	public void moveOneFloorTest() {
		elevator.moveUp();
		elevator.chooseFloor(5);
		elevator.moveOneFloor();
		assertEquals(2, elevator.getCurrFloor());

		elevator.moveDown();
		elevator.chooseFloor(1);
		elevator.moveOneFloor();
		assertEquals(1, elevator.getCurrFloor());
	}
	
	@Test
	public void elevatorDefaultStateTest() {
		assertTrue(elevator.isElevatorReady());
		assertFalse(elevator.isMovingDown());
		assertFalse(elevator.isMovingUp());
		assertFalse(elevator.isDoorOpen());
	}

	@Test
	public void elevatorMovingOneFloorTest() {
		elevator.chooseFloor(2);
		elevator.chooseFloor(1);
		elevator.moveUp();
		assertTrue(elevator.isMovingUp());
		elevator.moveOneFloor();
		assertEquals(2, elevator.getCurrFloor());
		elevator.stopElevator();
		assertTrue(elevator.isIdle());
		elevator.openDoor();
		assertTrue(elevator.isDoorOpen());
		elevator.closeDoor();
		assertFalse(elevator.isDoorOpen());
	}
}
