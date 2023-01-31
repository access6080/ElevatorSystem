import java.io.*;
import java.util.Scanner;

/**
 *this class reads the csv file that contains all the data pertaining to the time, floor and button(floor to achieve)
 * the class stores all the data then creates and adds a message to the queue delivered to the scheduler
 * @author Chibuzo Okpara
 * @version January 29th, 2023
 */
public class FloorSubSystem implements Runnable{

    private MessageQueue queue;

    public FloorSubSystem(){
        this.queue = new MessageQueue();
    }

    /**
     *this method reads the csv file as well as create a message object that is added to the queue of messages
     * @param filename name of the csv file
     * @throws FileNotFoundException
     */
    private void getData(String filename) throws FileNotFoundException {
        File file = new File(filename);
        Scanner reader = new Scanner(file);

        while(reader.hasNextLine()){
            String line = reader.nextLine();
            String[] lineArray = line.split(",");

            int time = Integer.parseInt(lineArray[0]);
            int floor = Integer.parseInt(lineArray[1]);
            int button = Integer.parseInt(lineArray[2]);

            ElevatorEvent event = new ElevatorEvent(time, floor, button);
            Message msg = new Message(Scheduler.PRIORITY, ElevatorSystemComponent.FloorSubSystem, event);
            queue.addMessage(msg);

        }
    }

    /**
     *this method will executes getData() method
     */
    @Override
    public void run() {
        while(Thread.currentThread().isAlive()){
            try {
                getData("file.csv");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
