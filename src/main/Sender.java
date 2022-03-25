package project2.main;

import project2.network.Network;
import project2.network.Packet;
import project2.transport.Transport;

/**
 * Represents a sender process.
 */
public class Sender implements Runnable {

    // The message to send.
    private final String msg;
    // Whether the network layer should emulate packet loss.
    private final boolean emulateLoss;

    public Sender(String msg, boolean emulateLoss) {
        this.msg = msg;
        this.emulateLoss = emulateLoss;
    }

    @Override
    public void run() {
        sendMessage();
    }

    /**
     * Sends a message to the receiver.
     */
    public void sendMessage() {
        // Construct the network layer.
        Network n = new Network(Transport.SENDER_PORT, emulateLoss);
        // Construct the transport layer.
        Transport t = new Transport(true, n);
        // Split the given message into packets.
        Packet[] packets = Packet.split(msg);
        // Send the packets to the receiver.
        t.sendWithARQ(packets);
        // Terminate the network layer.
        n.terminate();
    }
}
