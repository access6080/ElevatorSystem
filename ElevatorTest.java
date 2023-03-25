import static org.junit.Assert.*;
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
		this.elevator = new Elevator(elevatorNum, queue);
	}
	
	@Test
	public void moveDownTest() {
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

	}

	@Test
	public void closeDoorTest() {

	}

	@Test
	public void openDoorTest() {

	}
	
	@Test
	public void moveOneFloorTest() {

	}
	
	@Test
	public void elevatorDefaultStateTest() {

	}

	@Test
	public void elevatorMovingOneFloorTest() {

	}
}
