import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * this class tests the method getData() in the FloorSubSystem class.
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

    }
}
