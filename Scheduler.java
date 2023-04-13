import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

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
    /** The current State of the Scheduler **/
    private SchedulerState status;
    /** THe Scheduling Algorithm being used **/
    private SchedulerAlgorithm algorithm;
    /** The service used for UDP messaging **/
    private MessageService service;
    /** The various states of the Scheduler **/
    public enum SchedulerState{
        START, STANDBY, RECEIVING_FROM_FLOOR_SUBSYSTEM
    }
    /**The location os each elevator at any given point **/
    private HashMap<Integer, Integer> elevatorLocations;

    private HashMap<Integer, Boolean> elevatorStatuses;
    public ArrayList<ElevatorSystemView> views;

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
    }

    /**
     * The constructor of the scheduler class.
     */
    public Scheduler() {
        this.messageQueue = new MessageQueue();
        this.jobQueue = new LinkedList<>();
        this.logger = new Logger();
        this.status = SchedulerState.START;
        this.elevatorLocations = initializeElevatorLocations();
        this.elevatorStatuses  = initializeElevatorStatus();
        this.algorithm = new SchedulerAlgorithm();
        this.views =  new ArrayList<>();

        DatagramSocket sendReceiveSocket;
        try {
            sendReceiveSocket = new DatagramSocket(3002);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        this.service =  new MessageService(sendReceiveSocket,  ElevatorSystemComponent.Scheduler);
    }
    public void addViews(ElevatorSystemView view){
        this.views.add(view);
    }

    public void updateViews(ElevatorSystemEvent event){
        for (ElevatorSystemView v: views) {
            v.update(event);
        }
    }

    /**
     * This method checks the message queue for messages sent to the scheduler.
     * Based on the sender of the message, the scheduler either adds a job (ElevatorEvent object)
     * to the job queue or removes and sends a Job to the elevator component of the system.
     *
     */
    public void checkIfEventOccurred() {
        if(messageQueue.hasAMessage(PRIORITY)){
            logger.info("Event Occurred\n");

            Message msg;
            try {
                msg = messageQueue.getMessage(PRIORITY);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (msg.sender().equals(ElevatorSystemComponent.FloorSubSystem))
                this.status = SchedulerState.RECEIVING_FROM_FLOOR_SUBSYSTEM;


            switch (status) {
                case START -> this.status = SchedulerState.STANDBY;

                case STANDBY -> {
                    switch (msg.type()) {
                        case Job -> this.status = SchedulerState.RECEIVING_FROM_FLOOR_SUBSYSTEM;
                        case ElevatorJobComplete, ArrivalSensorActivated, Hard_Fault -> handleMessageUpdate(msg);
                    }
                }

                case RECEIVING_FROM_FLOOR_SUBSYSTEM -> AcceptJob((ElevatorEvent) msg.data());
            }
        }
    }

    private void handleMessageUpdate(Message msg) {
        switch (msg.type()) {
            case ElevatorJobComplete -> {
                ElevatorUpdate data = (ElevatorUpdate) msg.data();
                logger.info("Elevator " + data.elevatorNum() + " completed Job");
                updateViews(new ElevatorSystemEvent("Elevator " + data.elevatorNum() + " completed Job",
                        ElevatorSystemEvent.EventType.Log));

                this.elevatorStatuses.put(data.elevatorNum(), true);
                Message newMsg = new Message(ElevatorSystemComponent.Scheduler, ElevatorSystemComponent.FloorSubSystem,
                        data, MessageType.ElevatorJobComplete);

                this.service.send(newMsg);
                logger.info("Message Sent to FloorSubsystem");
                updateViews(new ElevatorSystemEvent("Message Sent to FloorSubsystem", ElevatorSystemEvent.EventType.Log));
            }
            case ArrivalSensorActivated -> {
                ElevatorRequest req = (ElevatorRequest) msg.data();
                logger.info("Elevator " + req.elevatorNum() + "  arrived to floor "  + req.floor());
                updateViews(new ElevatorSystemEvent("Elevator " + req.elevatorNum() + "  arrived to floor "  + req.floor(),
                        ElevatorSystemEvent.EventType.Log));
                this.elevatorLocations.put(req.elevatorNum(), req.floor());


                this.service.send(new Message(ElevatorSystemComponent.Scheduler, ElevatorSystemComponent.FloorSubSystem,
                        req, MessageType.ArrivalSensorActivated));
                logger.info("Message Sent to FloorSubsystem");
                updateViews(new ElevatorSystemEvent("Message Sent to FloorSubsystem",
                        ElevatorSystemEvent.EventType.Log));
            }
            case Hard_Fault -> {
                int elevatorNum = (int) msg.data();
                logger.info("Elevator " + elevatorNum  + " is Out of Service");
                updateViews(new ElevatorSystemEvent("Elevator " + elevatorNum  + " is Out of Service", ElevatorSystemEvent.EventType.Log));
                this.elevatorStatuses.remove(elevatorNum);
                this.elevatorLocations.remove(elevatorNum);
            }
        }
    }

    /**
     * This method initializes the locations of each elevator.
     *
     * @return A HashMap containing the location  of each elevator.
     */
    private HashMap<Integer, Integer> initializeElevatorLocations() {
        HashMap<Integer, Integer> map = new HashMap<>();

        for (int i = 1; i < UtilityInformation.NUMBER_OF_ELEVATORS + 1; i++) {
            map.put(i, 1);
        }

        return map;
    }

    private HashMap<Integer, Boolean> initializeElevatorStatus() {
        HashMap<Integer, Boolean> status = new HashMap<>();

        for(int i = 1; i < UtilityInformation.NUMBER_OF_ELEVATORS + 1; i++ ){
            status.put(i, true);
        }

        return status;
    }

    private void scheduleJob() {
        while(!jobQueue.isEmpty()) {
            ElevatorEvent job = jobQueue.poll();
            int bestElevator = algorithm.getBestCarToServiceJob(elevatorStatuses, elevatorLocations, job);
            elevatorStatuses.put(bestElevator, false);
            ElevatorEvent newJob = new ElevatorEvent(bestElevator, job.currentFloor(), job.carButton(), job.fault());

            logger.info("Sending new job to elevator " + bestElevator);
            updateViews(new ElevatorSystemEvent("Sending new job to elevator " + bestElevator, ElevatorSystemEvent.EventType.Log));
            Message message = new Message(ElevatorSystemComponent.Scheduler,
                    ElevatorSystemComponent.ElevatorSubSystem, newJob, MessageType.Job);
            this.service.send(message);
        }

        this.status = SchedulerState.STANDBY;
    }

    /**
     * This method receives a job from the floor subsystem and schedules it.
     * @param data The job received from the floor subsystem
     */
    private void AcceptJob(ElevatorEvent data) {
        logger.info("Received a message from floor sub system with data: " + data);
        updateViews(new ElevatorSystemEvent("Received a message from floor sub system with data: " + data, ElevatorSystemEvent.EventType.Log));
        jobQueue.add(data);

        scheduleJob();
        this.status = SchedulerState.STANDBY;
    }

    /**
     * This method is implemented from the runnable interface
     * and allows this class to be run by a thread instance.
     */
    public void run() {
        logger.info("Scheduler Service Started");
        updateViews(new ElevatorSystemEvent("Scheduler Service Started", ElevatorSystemEvent.EventType.Log));
        Thread messageReceiver = new Thread(() -> {
            while(true){
                logger.info("Awaiting message...");
                Message msg = service.receive();
                this.messageQueue.addMessage(new Message(PRIORITY, msg.sender(),  msg.data(), msg.type()));
            }
        },  "Message Receiver");
        messageReceiver.start();

        while(Thread.currentThread().isAlive()) {
            checkIfEventOccurred();
        }
    }

    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        scheduler.run();
    }
}