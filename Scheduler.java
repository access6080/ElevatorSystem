import java.util.LinkedList;
import java.util.Queue;

/**
 * The Scheduler class models the Scheduler component of the  Elevator system.
 * It receives messages from the Floor sub-system,
 * which it saves in a job queue for when the elevator requests for a job.
 *
 * @author Geoffery Koranteng
 * @author Oluwatomisin Ajayi
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

    /** A flag that tells the scheduler to shut down **/
    private boolean shutdown;

    /** A constant tells the system a shut-down event being requested **/
    public static int SHUTDOWN = -1;

    private SchedulerState status;

    public enum SchedulerState{
        START, AWAITING_ELEVATOR_STATE, AWAITING_JOB_REQUEST, RECEIVING_FROM_FLOOR_SUBSYSTEM
    }

    public enum SchedulerEvent{
        RequestingElevatorState
    }

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
        this.status = SchedulerState.START;
    }


    /**
     * This method checks the message queue for messages sent to the scheduler.
     * Based on the sender of the message, the scheduler either adds a job (ElevatorEvent object)
     * to the job queue or removes and sends a Job to the elevator component of the system.
     *
     * @throws InterruptedException thrown when the thread is interrupted
     */
    public void retrieveAndSchedule() throws InterruptedException {
        if (messageQueue.hasAMessage(PRIORITY)) {
            Message newMessage = messageQueue.getMessage(PRIORITY);
            ElevatorSystemComponent sender = newMessage.sender();
            Object data = newMessage.data();

            if(sender.equals(ElevatorSystemComponent.FloorSubSystem) && !this.status.equals(SchedulerState.START))
                this.status = SchedulerState.RECEIVING_FROM_FLOOR_SUBSYSTEM;

            switch(status){
                case START -> {
                    if(sender.equals(ElevatorSystemComponent.FloorSubSystem)){
                        scheduleJob((ElevatorEvent) data);
                    } else if (sender.equals(ElevatorSystemComponent.ElevatorSubSystem)) {
                        handleJobRequest(data);
                    }
                }

                case AWAITING_ELEVATOR_STATE -> {
                    if(sender.equals(ElevatorSystemComponent.ElevatorSubSystem)){
                        boolean elevatorState = (boolean) data;
                        if(elevatorState){
                            ElevatorEvent job = jobQueue.poll();
                            if(job == null) return;
                            Message newJob = new Message(ElevatorSubsystem.PRIORITY,
                                    ElevatorSystemComponent.Scheduler, job, MessageType.Job);
                            messageQueue.addMessage(newJob);
                        }
                        this.status = SchedulerState.AWAITING_JOB_REQUEST;
                    }
                }

                case AWAITING_JOB_REQUEST -> {
                    if (sender.equals(ElevatorSystemComponent.ElevatorSubSystem)){
                        handleJobRequest(data);
                    }
                }

                case RECEIVING_FROM_FLOOR_SUBSYSTEM -> scheduleJob((ElevatorEvent) data);
            }
        }


    }

    private void handleJobRequest(Object data) {
        ElevatorRequest req = (ElevatorRequest) data;

        //Log Event
        logger.info("Received a request from Elevator");

        ElevatorEvent job = jobQueue.poll();
        if(job == null) return;

        Message res = new Message(req.elevatorNum(), ElevatorSystemComponent.Scheduler, job, MessageType.Job);
        messageQueue.addMessage(res);

        //Log Event
        logger.info("Sent a Job to the Elevator");
        this.status = SchedulerState.AWAITING_JOB_REQUEST;
    }

    private void scheduleJob(ElevatorEvent data) {
        logger.info("Received a message from Floor Subsystem");

        jobQueue.add(data);

        setShutDownFlag(data.time());

        logger.info("Checking if Elevator is idle");
        Message msg = new Message(ElevatorSubsystem.PRIORITY,
                ElevatorSystemComponent.Scheduler,
                SchedulerEvent.RequestingElevatorState, MessageType.Function_Request);
        messageQueue.addMessage(msg);

        this.status = SchedulerState.AWAITING_ELEVATOR_STATE;
    }

    /**
     * This method tells the system to shuts down or keep operating.
     * If it is shutting down it sends out a shut-down request to the elevator.
     */
    private void continueOrShutDown() {
        if(this.shutdown && jobQueue.isEmpty() && !messageQueue.hasAMessage(Scheduler.PRIORITY)) {
            logger.info("Shutting Down");

            this.messageQueue.addMessage(new Message(SHUTDOWN,
                    ElevatorSystemComponent.Scheduler, null, MessageType.Shutdown));
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
