/**
 * This class models the Entire elevator system.
 *  It initializes all components as threads and calls the start method on all threads initialized.
 *
 * @author Geoffery Koranteng
 * @version February 2nd, 2022
 */
public class ElevatorSystem {
    public static void main(String[] args) throws InterruptedException {
        MessageQueue queue = new MessageQueue();
        Logger logger = new Logger();

        Thread schedulerThread = new Thread(new Scheduler(queue), "Scheduler");
        Thread floorSubSystemThread = new Thread(new FloorSubSystem(queue), "FloorSubsystem");
        Thread elevatorThread = new Thread(new Elevator(1, 20, queue), "Elevator");

        logger.info("Starting Elevator System");
        schedulerThread.start();
        floorSubSystemThread.start();
        elevatorThread.start();

        schedulerThread.join();
        floorSubSystemThread.join();
        elevatorThread.join();

        logger.info("Elevator System Shutting Down");
    }
}
