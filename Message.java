/**
 * This record encapsulates a message that is sent from one component of the elevator system to another.
 *
 * @author Geoffery Koranteng
 * @version January 29th, 2023
 *
 * @param priority the priority of the message. This determines the recipient of the message.
 * @param sender the component responsible for creating the message and sending it through the message queue
 * @param data the payload of the message. This can be any object or data type.
 */
public record Message(int priority, ElevatorSystemComponent sender, Object data) { }