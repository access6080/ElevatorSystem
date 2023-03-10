import java.util.HashMap;
import java.util.Map;

/**
 * The Elevator Subsystem controls the elevator.
 *
 * @author Saad Eid
 * @author Geoffery Koranteng
 */
public class ElevatorSubsystem implements Runnable {
    private final Map<Integer, Elevator> elevatorList;
    private final int numElevators;
    private final  int numFloors;
    private final MessageQueue queue;
    private final Logger logger;
    public static final int PRIORITY = -2;
    public ElevatorSubsystemState status;
    private enum ElevatorSubsystemState {
        Start,
        Checking_Elevator_Status,
        Sending_Elevator_New_Job,
        StandBy
    }

    /**
     * Create a new elevator subsystem with numElevators and numFloors
     * @param numElevators the number of elevators in the system
     */
    public ElevatorSubsystem(int numFloors, int numElevators, MessageQueue queue) {
        this.elevatorList = new HashMap<>();
        this.status = ElevatorSubsystemState.Start;
        this.numElevators = numElevators;
        this.queue = queue;
        this.numFloors = numFloors;
        this.logger = new Logger();
    }

    private void subsystemOperation() throws InterruptedException {
        ElevatorEvent new_job = null;
        if(this.queue.hasAMessage(PRIORITY)){
            Message msg = this.queue.getMessage(PRIORITY);

            if(msg.type() == MessageType.Function_Request){
                this.status = ElevatorSubsystemState.Checking_Elevator_Status;
            }

            if(msg.type() == MessageType.Job){
                this.status = ElevatorSubsystemState.Sending_Elevator_New_Job;
                new_job = (ElevatorEvent) msg.data();
            }
        }

        switch (this.status){
            case Start -> startup();
            case Checking_Elevator_Status -> checkElevatorStatus();
            case Sending_Elevator_New_Job -> dispatchNewJob(new_job);
        }
    }



    @Override
    public void run() {
        while(Thread.currentThread().isAlive()){
            try {
                subsystemOperation();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void startup() {
        //Initialize the elevators
        for (int i = 1; i < numElevators + 1; i ++) {
            this.elevatorList.put(i, new Elevator(i, numFloors, this.queue));
        }

        this.status = ElevatorSubsystemState.StandBy;
    }

    private void dispatchNewJob(ElevatorEvent job) {
        Message msg = new Message(1, ElevatorSystemComponent.ElevatorSubSystem, job, MessageType.Job);
        logger.info("Sending a new Job to Elevator " + 1);
        this.queue.addMessage(msg);
    }

    private void checkElevatorStatus() {
        logger.info("Checking the status of the elevator");
        boolean isIdle = elevatorList.get(1).isIdle();

        Message msg = new Message(Scheduler.PRIORITY,
                ElevatorSystemComponent.ElevatorSubSystem, isIdle, MessageType.Function_Request);
        logger.info("Sending Elevator Status to the Scheduler");
        this.queue.addMessage(msg);
        this.status = ElevatorSubsystemState.StandBy;
    }
}

