/**
* The ArrivalSensor class notifies the ElevatorSubsystem on its current floor.
* 
* @author Geoffrey Koranteng
* @version March 10th, 2023
*/
public class ArrivalSensor {

    private final int floorNum;
    private boolean isActive;

    public ArrivalSensor(int floorNum) {
        this.floorNum = floorNum;
        this.isActive =  false;
    }

    /**
    * This method returns the floor number.
    */
    public int getFloorNum() {
        return floorNum;
    }

    /**
    * This method returns true if the sensor is active and false otherwise.
    */
    public boolean isActive() {
        return isActive;
    }

    /**
    * This method sets the sensor to active.
    * @param active booelan that indicates if the sensor is active or not.
    */
    public void setActive(boolean active) {
        isActive = active;
    }
    
    /**
    * This method notifies the scheduler when the arrival sensor is active.
    */
    private void notifyScheduler()  {

    }
}
