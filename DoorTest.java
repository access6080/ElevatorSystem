import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * this class tests the door class
 * @author chibuzo okpara
 * @version March 10, 2023
 */
public class DoorTest {
    private Door door;

    @Before
    public void setup(){

        door = new Door();
    }

    @After
    public void tearDown(){}

    @Test
    public void GetInitialStatusTest() {
        assertEquals(Door.DoorState.CLOSED, door.getStatus());
    }

    @Test
    public void openDoorTest(){
        door.openDoor();
        assertEquals(Door.DoorState.OPEN, door.getStatus());
    }

    @Test
    public void movingDoorTest(){
        door.doorMoving();
        assertEquals(Door.DoorState.MOVING, door.getStatus());
    }

    @Test
    public void closedWhileMovingDoorTest(){
        door.doorMoving();
        door.openDoor();
        assertNotEquals(Door.DoorState.OPEN, door.getStatus());
    }
    @Test
    public void closedDoorTest(){
        door.openDoor();
        door.closeDoor();
        assertEquals(Door.DoorState.OPEN, door.getStatus());
    }

}




