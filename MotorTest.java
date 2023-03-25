import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * this a test class for motor class
 * @author chibuzo okpara
 * @version March 15, 2023
 */
public class MotorTest {
    private Motor motor;

    @Before
    public void setup(){

        motor = new Motor();
    }

    @After
    public void tearDown(){}

    /**
     * method tests the initialization of the motor
     */
    @Test
    public void initialStatus(){ assertFalse(motor.getStatus());}

    /**
     * method tests if motor is in motion
     */
    @Test
    public void startMotorTest(){
        motor.startMotor();
        assertTrue(motor.getStatus());
    }

    /**
     * method tests if motor is stopped
     */
    @Test
    public void stopMotorTest(){motor.startMotor();
        motor.stopMotor();
        assertFalse(motor.getStatus());}


}