import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This class serves as a test case for the MessageQueue class.
 *
 * @author Geoffery Koranteng
 * @author January 29th, 2023
 */
public class MessageQueueTest {
    private MessageQueue queue;

    /**
     * this method is run before every test in this test case.
     */
    @Before
    public void setUp() {
        this.queue = new MessageQueue();
    }

    /**
     * tests the addMessage method of the MessageQueue class.
     */
    @Test
    public void testAddMessage() {
        this.queue.addMessage(new Message(0, ElevatorSystemComponent.Scheduler, "Hello World", null));

        assertEquals(1, this.queue.size());
    }

    /**
     * tests the getMessage method of the MessageQueue class.
     */
    @Test
    public void testGetMessage() {
        this.queue.addMessage(new Message(0, ElevatorSystemComponent.Scheduler, "Hello World", null));

        assertEquals(1, this.queue.size());

        Message m;

        try {
            m = this.queue.getMessage(0);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertNotNull(m);
        assertEquals(0, m.priority());
        assertEquals(ElevatorSystemComponent.Scheduler, m.sender());
        assertEquals("Hello World", m.data());

        assertEquals(0, this.queue.size());
    }

    /**
     * tests the size method of the MessageQueue class.
     */
    @Test
    public void testSize(){
        this.queue.addMessage(new Message(0, ElevatorSystemComponent.Scheduler, "Hello World", null));

        assertEquals(1, this.queue.size());
    }
}