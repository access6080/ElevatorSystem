import java.io.*;
import java.util.Scanner;

/**
 *
 *
 * @author Chibuzo Okpara
 * @version January 29th, 2023
 */
public class FloorSubSystem implements Runnable{

    private MessageQueue queue;

    public FloorSubSystem(){
        this.queue = new MessageQueue();
    }

    /**
     * this class 
     * @param filename
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
     *
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
