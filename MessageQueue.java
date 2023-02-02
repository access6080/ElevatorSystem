import java.util.LinkedList;

/**
 * The MessageQueue class is a custom implementation of a messaging queue
 * that uses an arraylist as the  backing data structure.
 * It is used to pass messages between the different components of the Elevator System.
 * Due to the multithreaded nature of the elevator system and the use of this class as a
 * shared resource, all methods are implemented to be thread-safe.
 *
 * @author Geoffery Koranteng
 * @version January 29th, 2023
 */
public class MessageQueue {
    /** The backing data structure of the Message queue class*/
    private final LinkedList<Message> elevatorlist;
    private final LinkedList<Message> schedulerList;

    /**
     * Constructor of the MessageQueue class.
     * It instantiates the backing data structure - the arraylist.
     */
    public MessageQueue() {
        this.elevatorlist = new LinkedList<>();
        this.schedulerList = new LinkedList<>();
    }

    /**
     * This method returns the first message with a specific priority from the queue.
     *
     * @param priority the priority of the message being returned
     * @return returns the first message of a given priority.
     * @throws InterruptedException this method is thrown when the thread is interrupted
     */
    public synchronized Message getMessage(int priority) throws InterruptedException {
        if(!isEmpty()) {
            if (priority == Scheduler.PRIORITY) {
                return this.schedulerList.pollFirst();
            }

            if (priority > Scheduler.PRIORITY) {
                return this.elevatorlist.pollFirst();
            }
        }

        return null;
    }

    /**
     * This method adds a message to the end of the queue.
     * @param m the message being added to the queue.
     */
    public synchronized void addMessage(Message m){
        if(m.priority() == Scheduler.PRIORITY) this.schedulerList.addLast(m);

        if(m.priority() > Scheduler.PRIORITY) this.elevatorlist.addLast(m);
    }

    /**
     * this method returns true  if the queue is empty or false it is not.
     * @return true if queue is empty and false otherwise.
     */
    private boolean isEmpty(){
        return (elevatorlist.size() + schedulerList.size()) == 0;
    }

    /**
     * Returns the size of the queue.
     *
     * @return the size of the queue.
     */
    public synchronized int size(){
        return (elevatorlist.size() + schedulerList.size());
    }

    /**
     * This method checks if a message exists for a specified priority.
     *
     * @param priority the priority of the message
     * @return true if a message exist for the specified priority and false otherwise.
     */
    public boolean hasAMessage(int priority) {
        if(priority == Scheduler.PRIORITY) return schedulerList.size() != 0;

        if(priority > Scheduler.PRIORITY) return elevatorlist.size() != 0;

        return false;
    }
}
