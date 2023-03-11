import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * A class that encapsulates message sending using UDP in the elevator system.
 * It exposes methods to send and receive messages and transforms the message
 * to the appropriate data type based on the sender and the recipient.
 *
 *
 * @author Geoffery Koranteng.
 * @version March 9th, 2023.
 */
public class MessageService extends MessageQueue {
    private final DatagramSocket clientSocket;
    private final ElevatorSystemComponent client;


    /**
     * Constructor of the Message Service
     * @param clientSocket  the datagram socket used to  send and receive messages
     * @param client the component of the elevator system instantiating the service
     */
    public MessageService(DatagramSocket clientSocket, ElevatorSystemComponent client) {
        this.clientSocket = clientSocket;
        this.client = client;
    }


    /**
     * This method sends a message to the recipient in the given message object.
     *
     * @param msg An object encapsulating the message and meta-data of the message being sent
     */
    public void send(Message msg) {
        byte[] message = getByteArray(encryptMessage(msg));
        DatagramPacket sendPacket;
        try {
            sendPacket = new DatagramPacket(message, message.length,
                    InetAddress.getLocalHost(), getPort(msg.recipient()));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        printMessageInformation(sendPacket, true);

        try {
            clientSocket.send(sendPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method recieves a message using the socket of the class that calls it.
     *
     * @return Message received
     */
    public Message receive() {
        byte[] data = new byte[1000];
        DatagramPacket receivePacket = new DatagramPacket(data, data.length);

        printMessageInformation(receivePacket, false);

        try {
            clientSocket.receive(receivePacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Message msg;
        try {
            msg = decryptByteArray(receivePacket.getData());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return msg;
    }

    /**
     * Prints to the standard output, the information of a packet being sent or recieved
     * @param sendPacket the packet whose information is printed.
     * @param sending a boolean indicating if the packet is being or receive.
     */
    private void printMessageInformation(DatagramPacket sendPacket, boolean sending) {
        if (sending) {
            System.out.println(client + " : Sending packet:");
            System.out.println("To host: " + sendPacket.getAddress());
            System.out.println("Destination host port: " + sendPacket.getPort());

        }
        else {
            System.out.println(client + " : Receiving packet:");
            System.out.println("From host: " + sendPacket.getAddress());
            System.out.println("Host port: " + sendPacket.getPort());
        }

        System.out.println("Length: " + sendPacket.getLength());
        System.out.print("Containing: ");
        System.out.println(new String(sendPacket.getData()));
        System.out.println(Arrays.toString(sendPacket.getData()));
    }

    /**
     * A  helper method that returns an appropriate port based on the recipient of the message
     * @param recipient The component receiving the message.
     * @return the port of the  recipient.
     */
    private int getPort(ElevatorSystemComponent recipient) {
        int port;
        switch (recipient) {
            case Scheduler -> port = 3002;
            case FloorSubSystem -> port  = 3001;
            case ElevatorSubSystem -> port  = 3003;
            default -> throw new IllegalArgumentException();
        }
        return port;
    }

    /**
     * Encrypts a message object into an array of bytes.
     * @param msg THe message object being converted.
     * @return an Arraylist of bytes
     */
    private ArrayList<Byte> encryptMessage(Message msg){
        ArrayList<Byte> message = new ArrayList<>();
        byte sep = (byte) '|';

        //  Add Sender
        message.add((byte) msg.sender().ordinal());

        // Add Separator
        message.add(sep);

        // Add Recipient
        message.add((byte) msg.recipient().ordinal());

        // Add Separator
        message.add(sep);


        switch (msg.type()){
            case Job -> {
                ElevatorEvent event = (ElevatorEvent) msg.data();
                try {
                    byte[] data = serializeData(event);
                    for (byte  b : data) message.add(b);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            case Function_Request -> {

            }

        }

        // Add Separator
        message.add(sep);

        //Add Message Type
        message.add((byte) msg.type().ordinal());

        return message;
    }

    /**
     * Converts an object to an array of bytes
     * @param event the  object being converted
     * @return an array of bytes
     * @throws IOException thrown when serialization fails
     */
    private byte[] serializeData(Object event) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(event);

        return baos.toByteArray();
    }

    /**
     * @param arr
     * @return
     * @throws IOException
     */
    private Message decryptByteArray(byte[] arr) throws IOException {
        byte sender = arr[0];
        byte recipient = arr[2];
        byte[] message = Arrays.copyOfRange(arr, 4,arr.length - 1);
        byte type = arr[arr.length - 1];

        ByteArrayInputStream bios =  new ByteArrayInputStream(message);
        ObjectInputStream ois = new ObjectInputStream(bios);
        ElevatorEvent event;
        try {
            event = (ElevatorEvent) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


        ElevatorSystemComponent msgSender =  ElevatorSystemComponent.values()[sender];
        ElevatorSystemComponent msgRecipient = ElevatorSystemComponent.values()[recipient];
        Object data = event;
        MessageType msgType =  MessageType.values()[type];

        return new Message(msgSender, msgRecipient, data, msgType);
    }

    private byte[] getByteArray(ArrayList<Byte> arr) {
        byte[] result = new byte[arr.size()];

        for(int i = 0; i < arr.size(); i++) {
            result[i] = arr.get(i);
        }

        return result;
    }
}
