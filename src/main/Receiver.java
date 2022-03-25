package project2.main;

import project2.network.Network;
import project2.network.Packet;
import project2.transport.Transport;

/**
 * Represents a receiver process.
 */
public class Receiver implements Runnable {

    // Whether the network layer should emulate packet loss.
    private final boolean emulateLoss;
    // The message received by this process.
    public String output;

    public Receiver(boolean emulateLoss) {
        this.emulateLoss = emulateLoss;
    }

    @Override
    public void run() {
        receiveMessage();
    }

    /**
     * Receives a message from the sender and saves it.
     */
    public void receiveMessage() {
        // Construct the network layer.
        Network n = new Network(Transport.RECEIVER_PORT, emulateLoss);
        // Construct the transport layer.
        Transport t = new Transport(false, n);
        // Receive the packets from the sender.
        Packet[] pks = t.receiveWithARQ();
        // Terminate the network layer.
        n.terminate();
        // Reconstruct the original message and save it.
        output = Packet.combine(pks);
    }
}
