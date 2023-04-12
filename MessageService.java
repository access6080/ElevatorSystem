import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * A class that encapsulates message sending using UDP in the elevator system.
 * It exposes methods to send and receive messages and transforms the message
 * to the appropriate data type based on the sender and the recipient.
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
        byte[] message = encryptMessage(msg);
        DatagramPacket sendPacket;
        try {
            sendPacket = new DatagramPacket(message, message.length,
                    InetAddress.getLocalHost(), getPort(msg.recipient()));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

//        printMessageInformation(sendPacket, true);

        try {
            clientSocket.send(sendPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method receives a message using the socket of the class that calls it.
     *
     * @return Message received
     */
    public Message receive() {
        byte[] data = new byte[1000];
        DatagramPacket receivePacket = new DatagramPacket(data, data.length);

        try {
            clientSocket.receive(receivePacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        printMessageInformation(receivePacket, false);

        Message msg;
        try {
            msg = decryptByteArray(receivePacket.getData());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return msg;
    }

    /**
     * Prints to the standard output, the information of a packet being sent or received
     * @param sendPacket the packet whose information is printed.
     * @param sending a boolean indicating if the packet is being or receive.
     */
    private void printMessageInformation(DatagramPacket sendPacket, boolean sending) {
        System.out.println();
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
//        System.out.println(new String(sendPacket.getData()));
        System.out.println(Arrays.toString(sendPacket.getData()));
        System.out.println();
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
     * @return an array of bytes
     */
    private byte[] encryptMessage(Message msg){
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

        //Add Message Type
        message.add((byte) msg.type().ordinal());

        // Add Separator
        message.add(sep);

        // add Data
        switch (msg.type()){
            case Job -> {
                ElevatorEvent event = (ElevatorEvent) msg.data();
                try {
                    byte[] data = serializeData(event);
                    addToMessage(message, sep, data);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            case Function_Response -> {
                ElevatorStatus status = (ElevatorStatus) msg.data();
                try {
                    byte[] data =  serializeData(status);
                    addToMessage(message, sep, data);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            case ElevatorJobComplete -> {
                ElevatorUpdate update = (ElevatorUpdate) msg.data();
                try {
                    byte[] data = serializeData(update);
                    addToMessage(message, sep, data);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            case ArrivalSensorActivated -> {
                ElevatorRequest req = (ElevatorRequest) msg.data();
                try {
                    byte[] data = serializeData(req);
                    addToMessage(message, sep, data);
                }  catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return getByteArray(message);
    }


    /**
     * @param message The arraylist of bytes of the message being sent
     * @param sep The token separating the message
     * @param data the data being added to the message
     */
    private static void addToMessage(ArrayList<Byte> message, byte sep, byte[] data) {
        // add length of  data
        // convert length to string
        String len = String.valueOf(data.length);
        byte[] lenArr = len.getBytes();
        message.add((byte) lenArr.length);

        //Add  Separator
        message.add(sep);
        for(byte b: lenArr) message.add(b);

        // Add Separator
        message.add(sep);

        for (byte  b : data) message.add(b);
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
     * @param arr the byte array being decrypted
     * @return a message Object
     * @throws IOException thrown when serialization fails
     */
    private Message decryptByteArray(byte[] arr) throws IOException {
        byte sender = arr[0];
        byte recipient = arr[2];
        int type = arr[4] ;
        int len = arr[6];

        ElevatorSystemComponent msgSender =  ElevatorSystemComponent.values()[sender];
        ElevatorSystemComponent msgRecipient = ElevatorSystemComponent.values()[recipient];
        MessageType msgType =  MessageType.values()[type];

        if(MessageType.values()[type].equals(MessageType.Job) ||
                MessageType.values()[type].equals(MessageType.Function_Response) ||
                MessageType.values()[type].equals(MessageType.ArrivalSensorActivated) ||
                MessageType.values()[type].equals(MessageType.ElevatorJobComplete)){
            int messageStart = 8 + len;
            byte[] lengthOfData = Arrays.copyOfRange(arr, 8, messageStart);
            int dataLength = Integer.parseInt(new String(lengthOfData));
            int messageEnd = (messageStart + dataLength + 1);
            byte[] message = Arrays.copyOfRange(arr, messageStart + 1,  messageEnd);
            ByteArrayInputStream bios =  new ByteArrayInputStream(message);
            ObjectInputStream ois = new ObjectInputStream(bios);
            Object event;

            try {
                event = ois.readObject();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            return new Message(msgSender, msgRecipient, event, msgType);
        }

       return new Message(msgSender, msgRecipient, null, msgType);
    }

    /**
     * Converts an arraylist of bytes into an array  of  bytes
     * @param arr the arraylist of bytes being converted
     * @return an array of bytes.
     */
    private byte[] getByteArray(ArrayList<Byte> arr) {
        byte[] result = new byte[arr.size()];

        for(int i = 0; i < arr.size(); i++) {
            result[i] = arr.get(i);
        }

        return result;
    }
}
