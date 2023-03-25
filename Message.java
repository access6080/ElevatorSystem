import java.util.Objects;

/**
 * This record encapsulates a message that is sent from one component of the elevator system to another.
 *
 * @author Geoffery Koranteng
 * @version January 29th, 2023
 */
public final class Message {
    private  int priority;
    private  ElevatorSystemComponent sender;
    private ElevatorSystemComponent recipient;
    private int elevator;
    private final Object data;
    private final MessageType type;

    /**
     * @param priority the priority of the message. This determines the recipient of the message.
     * @param sender   the component responsible for creating the message and sending it through the message queue
     * @param data     the payload of the message. This can be any object or data type.
     */
    public Message(int priority, ElevatorSystemComponent sender, Object data, MessageType type) {
        this.priority = priority;
        this.sender = sender;
        this.data = data;
        this.type = type;
    }

    public Message(int priority, int sender, Object data, MessageType type) {
        this.priority = priority;
        this.elevator = sender;
        this.data = data;
        this.type = type;
    }

    public Message(ElevatorSystemComponent sender, ElevatorSystemComponent recipient, Object data, MessageType type) {
        this.type = type;
        this.sender = sender;
        this.data = data;
        this.recipient = recipient;

    }

    public int priority() {
        return priority;
    }

    public ElevatorSystemComponent sender() {
        return sender;
    }

    public Object data() {
        return data;
    }

    public MessageType type() {
        return type;
    }

    public int getElevator() {
        return elevator;
    }

    public ElevatorSystemComponent recipient() {
        return recipient;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Message) obj;
        return this.priority == that.priority &&
                Objects.equals(this.sender, that.sender) &&
                Objects.equals(this.data, that.data) &&
                Objects.equals(this.type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(priority, sender, data, type);
    }

    @Override
    public String toString() {
        return "Message[" +
                "priority=" + priority + ", " +
                "sender=" + sender + ", " +
                "data=" + data + ", " +
                "type=" + type + ']';
    }
}

