import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * this class tests the lamp class
 * @author chibuzo okpara
 * @version March 10, 2023
 */
public class LampTest {
    private Lamp lamp;

    @Before
    public void setup(){

        lamp = new Lamp();
    }

    @After
    public void tearDown(){}

    /**
     * class tests the initial status of the lamp
     */
    @Test
    public void initialLampState(){ assertFalse(lamp.isLampState());}

    /**
     * class tests if the lamp is on
     */
    @Test
    public void turnOnTest(){
        lamp.turnOn();
        assertTrue(lamp.isLampState());
    }

    /**
     * class tests if the lamp is off
     */
    @Test
    public void turnOffTest(){
        lamp.turnOn();
        lamp.turnOff();
        assertFalse(lamp.isLampState());
    }

}