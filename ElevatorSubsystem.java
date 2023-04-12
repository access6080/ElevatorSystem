import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * The Elevator Subsystem controls the elevator.
 *
 * @author Saad Eid
 * @author Geoffery Koranteng
 */
public class ElevatorSubsystem implements Runnable {
    private final HashMap<Integer, Elevator> elevatorList;
    private final HashMap<Integer, Boolean> statuses;
    private final Logger logger;
    private final LinkedList<ElevatorEvent> jobQueue;
    private final MessageQueue messageQueue;
    public static final int PRIORITY = -2;
    private static final int PORT = 3003;
    public ElevatorSubsystemState status;
    public MessageService service;
    public  static final int ElevatorNumber = 4;
    private enum ElevatorSubsystemState {
        Start,
        Checking_Elevator_Status,
        Sending_Elevator_New_Job,
        Awaiting_Elevator_Response,
        StandBy
    }


    /**
     * Create a new elevator subsystem with numElevators and numFloors.
     * Each elevator has their own message queue.
     */
    public ElevatorSubsystem(int numFloors) {
        this.status = ElevatorSubsystemState.Start;
        this.logger = new Logger();
        this.jobQueue = new LinkedList<>();

        DatagramSocket receiveSocket;
        try {
            receiveSocket = new DatagramSocket(PORT);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        //Creating the message service
        this.service = new MessageService(receiveSocket, ElevatorSystemComponent.ElevatorSubSystem);
        this.messageQueue = new MessageQueue();
        this.statuses = new HashMap<>();
        this.elevatorList = createElevators(numFloors);
    }

    private HashMap<Integer, Elevator> createElevators(int numFloors) {
        HashMap<Integer, Elevator> elevators = new HashMap<>();

        for(int i = 1; i < ElevatorSubsystem.ElevatorNumber + 1; i++ ){
            elevators.put(i, new Elevator(i, this.messageQueue));
        }

        return elevators;
    }
    private void subsystemOperation()  {
        switch (this.status){
            case Start -> startup();
            case StandBy -> standby();
            case Checking_Elevator_Status -> RespondToFunctionRequest();
            case Sending_Elevator_New_Job -> dispatchNewJob();
        }
    }
    private void standby() {
        while(messageQueue.hasAMessage(PRIORITY)) {

            try {
                Message msg = messageQueue.getMessage(PRIORITY);

                if (msg.type() == MessageType.Function_Request) {
                    this.status = ElevatorSubsystemState.Checking_Elevator_Status;
                }

                if (msg.type() == MessageType.Job) {
                    ElevatorEvent job = (ElevatorEvent) msg.data();
                    logger.info("Received new job from Scheduler with data " + job);
                    this.status = ElevatorSubsystemState.Sending_Elevator_New_Job;
                    jobQueue.add(job);
                }

                if(msg.type() == MessageType.Status_Update){
                    this.statuses.put(msg.getElevator(), (boolean) msg.data());
                }

                if(msg.type() == MessageType.ArrivalSensorActivated){
                    ElevatorRequest req = (ElevatorRequest) msg.data();
                    logger.info("Elevator " + req.elevatorNum() + " activated floor sensor on floor "  +  req.floor());
                    this.service.send(new Message(ElevatorSystemComponent.ElevatorSubSystem,
                            ElevatorSystemComponent.Scheduler, req, MessageType.ArrivalSensorActivated));
                }

                if(msg.type() == MessageType.ElevatorJobComplete){
                    // Todo: Elevator Update object not null
                    logger.info("Elevator " + msg.getElevator() + ": Job Complete");
                    this.statuses.put(msg.getElevator(), (boolean) msg.data());
                    this.service.send(new Message(ElevatorSystemComponent.ElevatorSubSystem,
                            ElevatorSystemComponent.Scheduler,
                            new ElevatorUpdate(msg.getElevator(), 0, 0),
                            MessageType.ElevatorJobComplete));
                }
                if(msg.type() == MessageType.Hard_Fault){
                    logger.info("Elevator " + msg.getElevator() + ": Hard Fault detected");
                    this.statuses.remove(msg.getElevator());
                    this.service.send(new Message(ElevatorSystemComponent.ElevatorSubSystem,
                            ElevatorSystemComponent.Scheduler, msg.getElevator(), MessageType.Hard_Fault));
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


        }
      }

    private void RespondToFunctionRequest() {
        Message msg = new Message(ElevatorSystemComponent.ElevatorSubSystem, ElevatorSystemComponent.Scheduler,
                new ElevatorStatus(this.statuses), MessageType.Function_Response);

        this.service.send(msg);
        logger.info("Responding to Scheduler Request");
        this.status = ElevatorSubsystemState.StandBy;
    }

    private void startup() {
        //Start the elevators
        for(Elevator e: elevatorList.values()){
            new Thread(e, "Elevator " + e.getElevatorNum()).start();
            this.statuses.put(e.getElevatorNum(), true);
        }
        this.status = ElevatorSubsystemState.StandBy;
    }

    private void dispatchNewJob() {
        while(!jobQueue.isEmpty()) {
            ElevatorEvent job = jobQueue.poll();
            if (job == null) return;
            Message msg = new Message(job.elevatorNum(), ElevatorSystemComponent.ElevatorSubSystem, job,
                    MessageType.Job);
            this.statuses.put(job.elevatorNum(), false);
            logger.info("Sending a new Job to Elevator " + job.elevatorNum());
            this.messageQueue.addMessage(msg);
        }
        this.status = ElevatorSubsystemState.StandBy;
    }

    public void run() {
        Thread messageReceiverThread = new Thread(() -> {
            while(true){
                Message msg = this.service.receive();
                this.messageQueue.addMessage(new Message(PRIORITY, msg.sender(), msg.data(), msg.type()));
            }
        });
        messageReceiverThread.start();


        while(Thread.currentThread().isAlive()) {
            subsystemOperation();
        }
    }

    public static void main(String[] args) {
        ElevatorSubsystem elevatorSubsystem  = new ElevatorSubsystem(20);
        elevatorSubsystem.run();
    }
}
