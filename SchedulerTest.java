import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This class serves as a test case for the Scheduler class
 * 
 * @author Geoffery Koranteng
 * @version January 30th, 2023
 */
public class SchedulerTest {

    private MessageQueue queue;
    private Scheduler scheduler;

    /**
     * this method is run before every test in this test case.
     */
    @Before
    public void setUp() {
        this.queue = new MessageQueue();
        this.scheduler = new Scheduler(queue);
    }

    /**
     * tests the retrieveAndSchedule method in scheduler
     */
    @Test
    public void testRetrieveAndSchedule() {
//        queue.addMessage(new Message(Scheduler.PRIORITY, ElevatorSystemComponent.FloorSubSystem,
//                new ElevatorEvent("210", 1, 4), null));
//
//        try {
//            scheduler.eventOccured();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//        assertEquals(0, queue.size());
    }
}