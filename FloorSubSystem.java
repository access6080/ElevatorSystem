import java.io.*;
import java.util.Scanner;

/**
 *this class reads the csv file that contains all the data pertaining to the time, floor and button(floor to achieve)
 * the class stores all the data then creates and adds a message to the queue delivered to the scheduler
 * @author Chibuzo Okpara
 * @version January 29th, 2023
 */
public class FloorSubSystem implements Runnable{

    private final MessageQueue queue;
    private final Logger logger;

    public FloorSubSystem(MessageQueue queue){
        this.queue = queue;
        this.logger = new Logger();
    }

    /**
     *this method reads the csv file as well as create a message object that is added to the queue of messages
     * @param filename name of the csv file
     * @throws FileNotFoundException This exception is thrown when the file being open is not found
     */
    private void getData(String filename) throws FileNotFoundException {
        File file = new File(filename);
        Scanner reader = new Scanner(file);
        reader.nextLine();
        while(reader.hasNextLine()){
            String line = reader.nextLine();

            String[] lineArray = line.split(" ");

            int time = Integer.parseInt(lineArray[0]);
            int floor = Integer.parseInt(lineArray[1]);
            int button = Integer.parseInt(lineArray[2]);
            logger.info("Received event request with time " + time + " requesting floor "
                    + floor + " requested floor " + button);

            ElevatorEvent event = new ElevatorEvent(time, floor, button);
            Message msg = new Message(Scheduler.PRIORITY, ElevatorSystemComponent.FloorSubSystem, event);
            queue.addMessage(msg);
            logger.info("Data sent to Scheduler");

            continueOrShutDown(time);
        }
    }

    /**
     * This method tells  the system to shuts down or keep operating.
     * @param token a token that tells the system to shut down
     */
    private void continueOrShutDown(int token) {
        if(token == Scheduler.SHUTDOWN) {
            logger.info("Shutting Down");
            Thread.currentThread().interrupt();
        }
    }

    /**
     * This method is implemented from the runnable interface
     * and allows this class to be run by a thread instance.
     */
    @Override
    public void run() {
        logger.info("Floor Subsystem Started");
        while(Thread.currentThread().isAlive()){
            if(Thread.currentThread().isInterrupted()) break;
            try {
                getData("data.txt");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
