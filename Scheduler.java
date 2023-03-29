import java.net.DatagramSocket;
import java.net.SocketException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
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
public class Scheduler {
    /** The message queue through which the scheduler receives and sends messages */
    private final MessageQueue messageQueue;

    /** The priority of the scheduler */
    public static final int PRIORITY = 0;

    /** FIFO Queue for elevator jobs */
    private final LinkedList<ElevatorEvent> jobQueue;

    /** Logger for logging events **/
    private final Logger logger;

    private SchedulerState status;

    private SchedulerAlgorithm algorithm;

    private MessageService service;

    public enum SchedulerState{
        START, AWAITING_ELEVATOR_STATE, STANDBY, RECEIVING_FROM_FLOOR_SUBSYSTEM
    }

    private HashMap<Integer, Integer> elevatorLocations;

    private final long startTime;

    /**
     * The constructor of the scheduler class.
     *
     * @param queue the message queue for sending and receiving messages.
     * @deprecated Use new constructor.
     */
    @Deprecated
    public Scheduler(MessageQueue queue) {
        this.messageQueue = queue;
        this.jobQueue = new LinkedList<>();
        this.logger = new Logger();
        this.status = SchedulerState.START;
        this.startTime = System.currentTimeMillis();
    }

    public Scheduler() {
        this.messageQueue = new MessageQueue();

        this.jobQueue = new LinkedList<>();
        this.logger = new Logger();
        this.status = SchedulerState.START;
        this.elevatorLocations = getElevatorLocations();
        this.algorithm = new SchedulerAlgorithm();
        this.startTime = System.currentTimeMillis();

        DatagramSocket sendReceiveSocket;
        try {
            sendReceiveSocket = new DatagramSocket(3002);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        this.service =  new MessageService(sendReceiveSocket,  ElevatorSystemComponent.Scheduler);
    }



    /**
     * This method checks the message queue for messages sent to the scheduler.
     * Based on the sender of the message, the scheduler either adds a job (ElevatorEvent object)
     * to the job queue or removes and sends a Job to the elevator component of the system.
     *
     * @param msg The message received by the scheduler.
     */
    public void eventOccurred(Message msg) {
            if(msg.sender().equals(ElevatorSystemComponent.FloorSubSystem) && !this.status.equals(SchedulerState.START))
                this.status = SchedulerState.RECEIVING_FROM_FLOOR_SUBSYSTEM;

            if(msg.sender().equals(ElevatorSystemComponent.ElevatorSubSystem) && msg.type() == MessageType.Function_Response)
               this.status = SchedulerState.AWAITING_ELEVATOR_STATE;

            switch(status){
                case START -> {
                    if(msg.sender().equals(ElevatorSystemComponent.FloorSubSystem)){
                        scheduleJob((ElevatorEvent) msg.data());
                    }
                    this.status  = SchedulerState.STANDBY;
                }

                case STANDBY -> {
                    switch (msg.type()) {
                        case Job -> this.status = SchedulerState.RECEIVING_FROM_FLOOR_SUBSYSTEM;
                        case Function_Response -> this.status = SchedulerState.AWAITING_ELEVATOR_STATE;
                        case ElevatorJobComplete, ArrivalSensorActivated -> handleMessageUpdate(msg);
                    }
                }

                case AWAITING_ELEVATOR_STATE -> {
                    if(msg.sender().equals(ElevatorSystemComponent.ElevatorSubSystem)){
                        handleFunctionResponse((ElevatorStatus) msg.data());
                    }
                }

                case RECEIVING_FROM_FLOOR_SUBSYSTEM -> scheduleJob((ElevatorEvent) msg.data());
            }
    }

    private void handleMessageUpdate(Message msg) {
        switch (msg.type()) {
            case ElevatorJobComplete -> {
                ElevatorUpdate data = (ElevatorUpdate) msg.data();
                logger.info("Elevator " + data.elevatorNum() + " completed Job");
                Message newMsg = new Message(ElevatorSystemComponent.Scheduler, ElevatorSystemComponent.FloorSubSystem,
                        data, MessageType.ElevatorJobComplete);

                this.service.send(newMsg);
                logger.info("Message Sent to FloorSubsystem");
            }
            case ArrivalSensorActivated -> {
                ElevatorRequest req = (ElevatorRequest) msg.data();
                this.elevatorLocations.put(req.elevatorNum(), req.floor());

                this.service.send(new Message(ElevatorSystemComponent.Scheduler, ElevatorSystemComponent.FloorSubSystem,
                        req, MessageType.ArrivalSensorActivated));
                logger.info("Message Sent to FloorSubsystem");
            }
            case Status_Update -> {

            }
        }
    }

    private HashMap<Integer, Integer> getElevatorLocations() {
        HashMap<Integer, Integer> map = new HashMap<>();

        for (int i = 1; i < 5; i++) {
            map.put(i, 1);
        }

        return map;
    }

    private void handleFunctionResponse(ElevatorStatus data) {
        logger.info("Received Elevator Status");

        HashMap<Integer, Boolean> elevatorStatuses = data.status();
        ElevatorEvent job = jobQueue.poll();
        if(job == null) return;

        int bestElevator = algorithm.getBestCarToServiceJob(elevatorStatuses, elevatorLocations, job);
        ElevatorEvent newJob = new ElevatorEvent(bestElevator, job.carButton());

        logger.info("Sending new job to elevator " + bestElevator);
        Message message = new Message(ElevatorSystemComponent.Scheduler,
                ElevatorSystemComponent.ElevatorSubSystem, newJob, MessageType.Job);
        this.service.send(message);

        this.status = SchedulerState.STANDBY;
    }

    private void scheduleJob(ElevatorEvent data) {
        logger.info("Received a message from Floor Subsystem");

        jobQueue.addLast(data);

        logger.info("Checking if Elevator is idle");

        Message msg = new Message(ElevatorSystemComponent.Scheduler, ElevatorSystemComponent.ElevatorSubSystem,
                null, MessageType.Function_Request);
        this.service.send(msg);

        this.status = SchedulerState.AWAITING_ELEVATOR_STATE;
    }

    public long getTime(long time){
        return time - startTime;
    }

    /**
     * This method is implemented from the runnable interface
     * and allows this class to be run by a thread instance.
     */
    public void run() {
        logger.info("Scheduler Service Started");
        while(true) {
            logger.info("Awaiting message...");
            Message msg = service.receive();
            eventOccurred(msg);
        }
    }

    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        scheduler.run();
    }
}
