import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * this class tests the method getData() in the FloorSubSystem class
 * @author chibuzo okpara
 * @version February 3rd, 2023
 */
public class FloorSubSystemTest {
    private FloorSubSystem floorSubSystem;
    MessageQueue queue;

    @Before
    public void setup(){
        this.queue = new MessageQueue();
        floorSubSystem = new FloorSubSystem(queue);
    }

    @After
    public void tearDown(){}

    @Test
    public void getDataTest() throws Exception{
        floorSubSystem.getData("data.txt");
        assertNotEquals(0, queue.size());

    }
}
