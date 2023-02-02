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

    /** Logger for logging events **/
    private final Logger logger;

    /** A flag that tells the scheduler to shutdown **/
    private boolean shutdown;

    /** A constant tells the system a shut-down event being requested **/
    public static int SHUTDOWN = -1;

    /**
     * The constructor of the scheduler class.
     *
     * @param queue the message queue for sending and receiving messages.
     */
    public Scheduler(MessageQueue queue) {
        this.messageQueue = queue;
        this.shutdown = false;
        this.jobQueue = new LinkedList<>();
        this.logger = new Logger();
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
            logger.info("Received a message from Floor Subsystem");

            setShutDownFlag(event.time());


            jobQueue.add(event);
        }

        if(newMessage.sender().equals(ElevatorSystemComponent.Elevator)){
            ElevatorRequest req = (ElevatorRequest) newMessage.data();

            //Log Event
            logger.info("Received a request from Elevator");

            ElevatorEvent job = jobQueue.poll();
            if(job == null) return;

            Message res = new Message(req.elevatorNum(), ElevatorSystemComponent.Scheduler, job);
            messageQueue.addMessage(res);

            //Log Event
            logger.info("Sent a Job to the Elevator");
        }
    }

    /**
     * This method tells the system to shuts down or keep operating.
     * If it is shutting down it sends out a shut-down request to the elevator.
     */
    private void continueOrShutDown() {
        if(this.shutdown && jobQueue.isEmpty() && !messageQueue.hasAMessage(Scheduler.PRIORITY)) {
            logger.info("Shutting Down");

            this.messageQueue.addMessage(new Message(SHUTDOWN, ElevatorSystemComponent.Scheduler, null));
            Thread.currentThread().interrupt();
        }
    }

    /**
     * This method sets the flag that tells the elevator to shut down
     */
    public void setShutDownFlag(int time){
        if(time == SHUTDOWN) this.shutdown = true;
    }

    /**
     * This method is implemented from the runnable interface
     * and allows this class to be run by a thread instance.
     */
    @Override
    public void run() {
        logger.info("Scheduler Service Started");
        while(Thread.currentThread().isAlive()){
            if(Thread.currentThread().isInterrupted()) break;
            try {
                retrieveAndSchedule();
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }
            continueOrShutDown();
        }
    }
}
