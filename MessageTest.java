import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This class serves as a test case for the Message class.
 *
 * @author Geoffery Koranteng
 * @author January 29th, 2023
 */
public class MessageTest {
    private Message m;

    /**
     * this method is run before every test in this test case.
     */
    @Before
    public void setUp() {
        this.m = new Message(0, ElevatorSystemComponent.FloorSubSystem, "Hello World", null);
    }

    /**
     * tests the priority getter method of the Message class.
     */
    @Test
    public void testPriorityGetterMethod() {
        assertEquals(0, m.priority());
    }

    /**
     * tests the sender getter method of the Message class.
     */
    @Test
    public void testSenderGetterMethod() {
        assertEquals(ElevatorSystemComponent.FloorSubSystem, m.sender());
    }

    /**
     * tests the data getter method of the Message class.
     */
    @Test
    public void testDataGetterMethod() {
        assertEquals("Hello World", m.data());
    }
}