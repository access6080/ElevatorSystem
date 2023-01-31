import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ElevatorTest {

	private Elevator elevator;
	private int elevatorNum = 1;
	private int numFloors = 10;

	@BeforeEach
	void setUp() throws Exception {
		this.elevator = new Elevator(elevatorNum, numFloors);
	}

	@Test
	void test() {
		fail("Not yet implemented");
	}

}
