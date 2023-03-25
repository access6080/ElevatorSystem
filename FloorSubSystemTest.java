import static org.junit.Assert.*;
import java.io.FileNotFoundException;
import org.junit.Before;
import org.junit.Test;

public class FloorSubSystemTest {

    private FloorSubSystem floorSubSystem;

    @Before
    public void setUp() {
        floorSubSystem = new FloorSubSystem();
    }

    @Test
    public void testGetData() {
        try {
            floorSubSystem.getData("data.txt");
        } catch (FileNotFoundException e) {
            fail("File not found exception thrown");
        }
        // Assert that the message service contains at least one message
    }

    @Test
    public void testGetFloorButton() {
        assertEquals(FloorSubSystem.FloorButton.UP, floorSubSystem.getFloorButton("UP"));
        assertEquals(FloorSubSystem.FloorButton.DOWN, floorSubSystem.getFloorButton("Down"));
        try {
            floorSubSystem.getFloorButton("invalid");
            fail("Expected IllegalArgumentException not thrown");
        } catch (IllegalArgumentException e) {
            // Expected exception thrown, test passes
        }
    }

}
