import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This class is a test case for the ElevatorEvent class.
 *
 * @author Oluwatomisin Ajayi
 * @version January 30th, 2023
 */
public class ElevatorEventTest {
    private ElevatorEvent event;

    /**
     * This method is to set up the case before every test.
     */
    @Before
    public void setUp(){
//        this.event = new ElevatorEvent("120",5,3);
    }

    /**
     * This method tests the getter method for time in the ElevatorEvent class.
     */
    @Test
    public void testGetTime() {
        assertEquals(120, event.time());
    }

    /**
     * This method tests the getter method for the current floor in the ElevatorEvent class.
     */
    @Test
    public void testGetCurrentFloor() {
        assertEquals(5, event.currentFloor());
    }


}
