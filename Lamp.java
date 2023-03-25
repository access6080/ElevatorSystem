/**
 * This class models a lamp object
 * @author Boma Iyaye
 * @version March 10th, 2023
 */
public class Lamp {

    boolean lampState;

    /**
     * Creates a lamp object that is turned off
     */
    public Lamp() {
        this.lampState = false;
    }
    /**
     * Turns the lamp on by setting the lamp state to true
     */
    public void turnOn(){
        lampState = true;
    }

    /**
     * Turns the lamp off by setting the lamp state to false
     */
    public void turnOff(){
        lampState = false;
    }

    /**
     * Returns the current state of the lamp.
     * @return  true if turned on ,return false if turned off
     */
    public boolean isLampState() {
        return lampState;
    }
}
