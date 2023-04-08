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
    private GUI elevatorGUI;
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
        this.elevatorGUI = new GUI();
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
            //elevators.put(i, new Elevator(i, this.messageQueue, elevatorGUI));
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
        Message msg = this.service.receive();

        if(msg.type() == MessageType.Function_Request){
            this.status = ElevatorSubsystemState.Checking_Elevator_Status;
        }

        if(msg.type() == MessageType.Job){
            this.status = ElevatorSubsystemState.Sending_Elevator_New_Job;
            jobQueue.add((ElevatorEvent) msg.data());
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
            new Thread(e, "Thread " + e.getElevatorNum()).start();
            this.statuses.put(e.getElevatorNum(), true);
        }
        this.status = ElevatorSubsystemState.StandBy;
    }
    
    private void dispatchNewJob() {
        ElevatorEvent job  =  jobQueue.poll();
        if(job == null) return;
        Message msg = new Message(job.elevatorNum(), ElevatorSystemComponent.ElevatorSubSystem, job, MessageType.Job);
        logger.info("Sending a new Job to Elevator " + job.elevatorNum());
        this.messageQueue.addMessage(msg);
        this.status = ElevatorSubsystemState.StandBy;
    }

    private void checkElevatorStatus() {
        logger.info("Checking the status of the elevator");
        while (this.messageQueue.hasAMessage(PRIORITY)){
            try {
                Message msg = this.messageQueue.getMessage(PRIORITY);
                if(msg.type() == MessageType.Status_Update){
                    this.statuses.put(msg.getElevator(), (boolean) msg.data());
                }
                if(msg.type() == MessageType.ArrivalSensorActivated){
                    this.service.send(new Message(ElevatorSystemComponent.ElevatorSubSystem,
                            ElevatorSystemComponent.Scheduler, msg.data(), MessageType.ArrivalSensorActivated));
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if(!this.status.equals(ElevatorSubsystemState.Checking_Elevator_Status )&&
                !this.status.equals(ElevatorSubsystemState.Sending_Elevator_New_Job))
            this.status = ElevatorSubsystemState.StandBy;
    }

    public void run() {
        while(true) {
            subsystemOperation();
            checkElevatorStatus();
        }
    }

    public static void main(String[] args) {
        ElevatorSubsystem elevatorSubsystem  = new ElevatorSubsystem(20);
        elevatorSubsystem.run();
    }
}
