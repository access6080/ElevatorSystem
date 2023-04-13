import java.io.*;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *this class reads the text file that contains all the data pertaining to the time, floor and button(floor to achieve)
 * the class stores all the data then creates and adds a message to the queue delivered to the scheduler
 * @author Chibuzo Okpara
 * @author Geoffery koranteng
 * @author Boma Iyaye
 * @version January 29th, 2023
 */
public class FloorSubSystem{
    private final Logger logger;
    public static int PRIORITY = -1;
    private static final DateFormat FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final Calendar CALENDAR = Calendar.getInstance();
    private final MessageService service;
    DatagramSocket sendAndReceive;

    private ArrayList<ElevatorSystemView> views;
    private ArrayList<Floor> floors;
    private String filename;

    public enum FloorButton {
        UP,
        DOWN
    }

    public enum Fault {
        TRANSIENT,
        NONE, HARD
    }

    public FloorSubSystem(String filename) {
        this.logger = new Logger();
        try {
            sendAndReceive = new DatagramSocket(3001);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        this.service = new MessageService(sendAndReceive, ElevatorSystemComponent.FloorSubSystem);
        views = new ArrayList<>();
        floors = populateFloors();
        this.filename = filename;
    }

    private ArrayList<Floor> populateFloors() {
        ArrayList<Floor> list = new ArrayList<>();

        for (int i = 1; i < UtilityInformation.NUMBER_OF_FLOORS + 1 ; i++) {
            list.add(new Floor(i));

        }
        return list;
    }

    /**
     *this method reads the csv file as well as create a message object that is added to the queue of messages
     * @param filename name of the text file
     * @throws FileNotFoundException This exception is thrown when the file being open is not found
     */
    public void getData(String filename) throws FileNotFoundException {
        File file = new File(filename);
        Scanner reader = new Scanner(file);
        reader.nextLine();

        while(reader.hasNextLine()){
            String line = reader.nextLine();

            String[] lineArray = line.split(" ");

            String time = lineArray[0];
            int floor = Integer.parseInt(lineArray[1]);
            FloorButton floorButton = getFloorButton(lineArray[2]);
            int carButton = Integer.parseInt(lineArray[3]);
            Fault fault = getFault(lineArray[4]);

            Date dateTime;
            try {
                dateTime = getDate(time);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            ElevatorEvent event = new ElevatorEvent(dateTime.getTime(), floor, floorButton, carButton, fault);
            Message msg = new Message(ElevatorSystemComponent.FloorSubSystem, ElevatorSystemComponent.Scheduler,
                    event, MessageType.Job);

            toggleDirectionLamp(floor);

            Timer  jobTImer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    service.send(msg);
                    logger.info("Data sent to Scheduler\n");
                }
            };
            jobTImer.schedule(task, dateTime);

            logger.info("Received event request with time " + dateTime + "( " + dateTime.getTime() + " millis )"
                    + " on floor " + floor + " going " + floorButton  + " to requested floor " + carButton);
        }
    }

    public FloorButton getFloorButton(String s) {
        switch(s) {
            case "UP" -> {
                return FloorButton.UP;
            }
            case "DOWN" -> {
                return FloorButton.DOWN;
            }
            default -> throw new IllegalArgumentException("Illegal Argument");
        }
    }

    private Fault getFault(String fault){
        switch (fault){
            case "TRANSIENT" -> {
                return Fault.TRANSIENT;
            }
            case "HARD" -> {
                return Fault.HARD;
            }
            default -> {
                return Fault.NONE;
            }
        }
    }


    /**
     * This method is implemented from the runnable interface
     * and allows this class to be run by a thread instance.
     */

    public void run() {
        logger.info("Floor Subsystem Started");
        try {
            getData(this.filename);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        while(Thread.currentThread().isAlive()) {
            Message msg = this.service.receive();
            processMessages(msg);
        }
    }

    private Date getDate(String time) throws ParseException {
        Date date = FORMAT.parse(time);
        CALENDAR.setTime(date);
        Calendar today = Calendar.getInstance();
        CALENDAR.set(Calendar.YEAR, today.get(Calendar.YEAR));
        CALENDAR.set(Calendar.MONTH, today.get(Calendar.MONTH));
        CALENDAR.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH));
        return CALENDAR.getTime();
    }

    public void processMessages(Message msg) {
        if(msg.type().equals(MessageType.ArrivalSensorActivated)){
            ElevatorRequest req = (ElevatorRequest) msg.data();
            updateViews(new ElevatorSystemEvent(req.elevatorNum(),req.floor(),
                    ElevatorSystemEvent.EventType.UpdateFloorNumber));
            logger.info("Elevator " + req.elevatorNum() + " reached floor " + req.floor());
        } else if (msg.type().equals(MessageType.ElevatorJobComplete)) {
            ElevatorUpdate data = (ElevatorUpdate) msg.data();
            toggleFloorLamp(data.elevatorNum());
            logger.info("Elevator " + data.elevatorNum() + " completed Job");
        }
    }


    private void toggleFloorLamp(int floorNum) {
        for (Floor f: floors) {
            if (f.getFloorNum()==floorNum){
                f.toggleFloorLamp();
                updateViews(new ElevatorSystemEvent(f.getFloorNum(),f.getFloorLampState(),ElevatorSystemEvent.EventType.ToggleFloorLamp));
                Timer floorLampTimer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        f.toggleFloorLamp();
                        updateViews(new ElevatorSystemEvent(f.getFloorNum(),f.getFloorLampState(),ElevatorSystemEvent.EventType.ToggleFloorLamp));
                    }
                };
                floorLampTimer.schedule(task,2000);
            }
        }

    }

    private void toggleDirectionLamp(int floorNum) {
        for (Floor f: floors) {
            if (f.getFloorNum()==floorNum){
                f.toggleDirectionLamp();
                updateViews(new ElevatorSystemEvent(f.getFloorNum(),f.getDirectionLampState(),ElevatorSystemEvent.EventType.ToggleDirectionLamp));
                Timer floorDirectionTimer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        f.toggleDirectionLamp();
                        updateViews(new ElevatorSystemEvent(f.getFloorNum(),f.getDirectionLampState(),ElevatorSystemEvent.EventType.ToggleDirectionLamp));
                    }
                };
                floorDirectionTimer.schedule(task,2000);
            }
        }

    }
    public void updateViews(ElevatorSystemEvent event ){
        for (ElevatorSystemView v: views) {
            v.update(event);
        }
    }

    public void addView(ElevatorSystemView view){
        this.views.add(view);
    }

    public static void main(String[] args) {
        FloorSubSystem floorSubSystem = new FloorSubSystem("normal.txt");
        floorSubSystem.run();

    }
}
