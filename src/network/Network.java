package project2.network;

import project2.main.Main;

import java.io.*;
import java.net.*;
import java.util.Random;

/**
 * Represents the Network layer.
 */
public class Network {

    private DatagramSocket socket;
    private final boolean emulateLoss;

    // Used for emulating packet loss.
    private final Random rand = new Random(63946);

    public Network(int port, boolean emulateLoss) {
        this.emulateLoss = emulateLoss;
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.err.println("[IP] Could not create the socket.");
            e.printStackTrace();
            this.socket = null;
        }
    }

    // Sends the given packet.
    public void send(Packet packet) {
        // Emulate packet loss with 30% probability.
        if(emulateLoss && rand.nextInt(10) < 3) {
            // For simplicity, the last acknowledgement is never lost!
            if(!(packet.ack && packet.lastPacket)) {
                if(Main.DEBUG) System.out.println("[Network] The packet " + packet + " was lost!");
                return;
            }
        }
        // Convert the packet into bytes.
        // object => ObjectOutputStream => ByteArrayOutputStream => byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(packet);
            oos.close();
        } catch (IOException e) {
            System.err.println("[IP] Could not convert the packet into bytes.");
            e.printStackTrace();
            return;
        }
        // Get the target address, which is always the local host.
        InetAddress targetAddr = null;
        try {
            targetAddr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            System.err.println("[IP] Could not get the local host address.");
            e.printStackTrace();
            return;
        }
        // Send the datagram.
        byte[] bytes = baos.toByteArray();
        DatagramPacket p = new DatagramPacket(bytes, bytes.length, targetAddr, packet.targetPort);
        try {
            socket.send(p);
        } catch (IOException e) {
            System.err.println("[IP] Could not send the packet.");
            e.printStackTrace();
        }
    }

    // Receives a packet within the given timeout period.
    public Packet receive(int timeout) throws SocketTimeoutException {
        // Set a timeout if some number other than zero is provided.
        if(timeout > 0) {
            try {
                socket.setSoTimeout(timeout);
            } catch (SocketException e) {
                System.err.println("[IP] Could not set the timeout");
                e.printStackTrace();
                return null;
            }
        }
        // Receive the datagram.
        byte[] buff = new byte[512];
        DatagramPacket p = new DatagramPacket(buff, buff.length);
        try {
            socket.receive(p);
        } catch(SocketTimeoutException e) {
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        // Convert the bytes into packet.
        // bytes => ByteArrayInputStream => ObjectInputStream => object
        ByteArrayInputStream bais = new ByteArrayInputStream(p.getData(), 0, p.getLength());
        ObjectInputStream ois = null;
        Packet packet = null;
        try {
            ois = new ObjectInputStream(bais);
            packet = (Packet) ois.readObject();
        } catch (Exception e) {
            System.err.println("[IP] Could not read the object.");
            e.printStackTrace();
            return null;
        }
        return packet;
    }

    public void terminate() {
        socket.close();
    }
}
