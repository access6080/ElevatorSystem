import java.util.LinkedList;
import java.util.Queue;

/**
 * The Scheduler class models the Scheduler component of the  Elevator system.
 * It receives messages from the Floor sub-system,
 * which it saves in a job queue for when the elevator requests for a job.
 *
 * @author Geoffery Koranteng
 * @version January 29th, 2023
 */
public class Scheduler implements Runnable {
    /** The message queue through which the scheduler receives and sends messages */
    private final MessageQueue messageQueue;

    /** The priority of the scheduler */
    public static final int PRIORITY = 0;

    /** FIFO Queue for elevator jobs */
    private final Queue<ElevatorEvent> jobQueue;

    /**
     * The constructor of the scheduler class.
     *
     * @param queue the message queue for sending and receiving messages.
     */
    public Scheduler(MessageQueue queue) {
        this.messageQueue = queue;
        this.jobQueue = new LinkedList<>();
    }

    /**
     * This method checks the message queue for messages sent to the scheduler.
     * Based on the sender of the message, the scheduler either adds a job (ElevatorEvent object)
     * to the job queue or removes and sends a Job to the elevator component of the system.
     *
     * @throws InterruptedException thrown when the thread is interrupted
     */
    public void retrieveAndSchedule() throws InterruptedException {
        Message newMessage = messageQueue.getMessage(PRIORITY);

        if(newMessage == null) return;

        if(newMessage.sender().equals(ElevatorSystemComponent.FloorSubSystem)){
            ElevatorEvent event = (ElevatorEvent) newMessage.data();

            //Log Event

            jobQueue.add(event);
        }

        if(newMessage.sender().equals(ElevatorSystemComponent.Elevator)){
            ElevatorRequest req = (ElevatorRequest) newMessage.data();

            //Log Event

            Message res = new Message(req.elevatorNum(), ElevatorSystemComponent.Scheduler, jobQueue.poll());

            messageQueue.addMessage(res);

            //Log Event
        }
    }

    /**
     * This method is implemented from the runnable interface
     * and allows this class to be run by a thread instance.
     */
    @Override
    public void run() {
        while(Thread.currentThread().isAlive()){
            try {
                retrieveAndSchedule();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
