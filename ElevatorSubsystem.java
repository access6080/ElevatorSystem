import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.PortUnreachableException;

/**
 * The Elevator Subsystem controls the elevator.
 *
 * @author Saad Eid
 * @author Geoffery Koranteng
 */
public class ElevatorSubsystem implements Runnable {
    private final Map<Integer, Elevator> elevatorList;
    private final Elevator elevator;
    private final MessageQueue queue;
    private final Logger logger;
    public static final int PRIORITY = -2;
    private DatagramSocket receiveSocket;
    private DatagramPacket packet;
    private static final PORT = 3003; //Or 3002
    public ElevatorSubsystemState status;
    private enum ElevatorSubsystemState {
        Start,
        Checking_Elevator_Status,
        Sending_Elevator_New_Job,
        Awaiting_Elevator_Response,
        StandBy
    }

    /**
     * Create a new elevator subsystem with numElevators and numFloors
     * @param numElevators the number of elevators in the system
     */
    public ElevatorSubsystem(int numFloors, int numElevators, MessageQueue queue) {
        this.elevator = new Elevator(1, numFloors, queue);
        this.status = ElevatorSubsystemState.Start;
        this.queue = queue;
        this.logger = new Logger();
        this.receiveSocket = new DatagramSocket(PORT);
        //Creating the message service
    	MessageService msgService = new MessageService(receiveSocket, ElevatorSystemComponent.ElevatorSubSystem);
    }
    
    /**
     * Create a new elevator subsystem with numElevators and numFloors.
     * Each elevator has their own message queue.
     * @param numElevators the number of elevators in the system
     */
    public ElevatorSubsystem(int numFloors, int numElevators) {
    	//Creating the different elevators
        for(int i = 0, i <= numElevators, i++) {
        	this.elevatorlist.put(new Elevator(i, numFloors, new MessageQueue());
        }
        this.status = ElevatorSubsystemState.Start;
        this.queue = queue;
        this.logger = new Logger();
        this.receiveSocket = new DatagramSocket(PORT);
        //Creating the message service
    	MessageService msgService = new MessageService(receiveSocket, ElevatorSystemComponent.ElevatorSubSystem);
    }

    private void subsystemOperation() throws InterruptedException {
        ElevatorEvent new_job = null;
        boolean isIdle = false;
        boolean hasMessage = this.queue.hasAMessage(PRIORITY);
        if(hasMessage){
            Message msg = this.queue.getMessage(PRIORITY);

            if(msg.type() == MessageType.Function_Request){
                this.status = ElevatorSubsystemState.Checking_Elevator_Status;
            }

            if(msg.type() == MessageType.Job){
                this.status = ElevatorSubsystemState.Sending_Elevator_New_Job;
                new_job = (ElevatorEvent) msg.data();
            }

            if(msg.type() ==  MessageType.Function_Response){
                this.status = ElevatorSubsystemState.Awaiting_Elevator_Response;
                isIdle = (boolean) msg.data();
            }
        }

        switch (this.status){
            case Start -> startup();
            case Checking_Elevator_Status -> checkElevatorStatus();
            case Sending_Elevator_New_Job -> dispatchNewJob(new_job);
            case Awaiting_Elevator_Response -> RespondToFunctionRequest(isIdle);
        }
    }

    private void RespondToFunctionRequest(boolean isIdle) {
        Message msg = new Message(Scheduler.PRIORITY, ElevatorSystemComponent.ElevatorSubSystem,
                isIdle, MessageType.Function_Response);
		
        this.queue.addMessage(msg);
        logger.info("Responding to Scheduler Request");
        this.status = ElevatorSubsystemState.StandBy;
    }


    @Override
    public void run() {
        while(Thread.currentThread().isAlive()){
            try {
            	//Reciving the message and adding it to the queue
            	this.queue.addMessage(msgService.receive());
            	//Performing the opertaion
                subsystemOperation();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void startup() {
        //Start the elevators
        Thread elevatorThread = new Thread(this.elevator, "Elevator 1");
        elevatorThread.start();

        this.status = ElevatorSubsystemState.StandBy;
    }

    private void dispatchNewJob(ElevatorEvent job) {
        Message msg = new Message(1, ElevatorSystemComponent.ElevatorSubSystem, job, MessageType.Job);
        logger.info("Sending a new Job to Elevator " + 1);
        this.queue.addMessage(msg);
        this.status = ElevatorSubsystemState.StandBy;
    }

    private void checkElevatorStatus() {
        logger.info("Checking the status of the elevator");

        Message msg = new Message(this.elevator.getElevatorNum(),
                ElevatorSystemComponent.ElevatorSubSystem, null, MessageType.Function_Request);
        this.queue.addMessage(msg);
        this.status = ElevatorSubsystemState.StandBy;
    }
}

