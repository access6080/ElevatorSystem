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
}
