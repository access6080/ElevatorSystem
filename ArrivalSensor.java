/**
 * The ArrivalSensor class notifies the ElevatorSubsystem on its current floor.
 *
 * @author Geoffrey Koranteng
 * @version March 10th, 2023
 */
public class ArrivalSensor {

    /**
     * This method notifies the scheduler when the arrival sensor is active.
     */
    public void notifyScheduler(ElevatorRequest req, MessageQueue queue)  {
        queue.addMessage(new Message(ElevatorSubsystem.PRIORITY, ElevatorSystemComponent.Elevator, req,
                MessageType.ArrivalSensorActivated));
    }
}