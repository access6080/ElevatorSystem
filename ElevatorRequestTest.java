import static org.junit.Assert.*;

/**
 * This class tests that the ElevatorRequest class is working as expected
 *
 * @author Boma Iyaye
 * @version 30th January, 2023.
 */
public class ElevatorRequestTest {

    private ElevatorRequest request;

    @org.junit.Before
    public void setUp() throws Exception {
        this.request = new ElevatorRequest(1,12);
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    /**
     * This method tests the elevatorNum method
     */
    @org.junit.Test
    public void elevatorNumTest() {
        assertEquals(1,request.elevatorNum());
    }

    /**
     * This method tests the requestedFloor method
     */
    @org.junit.Test
    public void requestedFloorTest() {
        assertEquals(12,request.requestedFloor());
    }
}