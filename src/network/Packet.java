package project2.network;

import java.io.Serializable;

/**
 * Represents a packet (to be precise, a segment).
 */
public class Packet implements Serializable {

    // Denotes the number of characters each packet can contain.
    public static final int PACKET_SIZE = 3;

    // Denotes the port of the receiver of this packet.
    public int targetPort;
    // Denotes whether the packet is an acknowledgement packet or not.
    public boolean ack;
    // Denotes whether this packet is the last packet that is being sent.
    public boolean lastPacket;

    // Denotes the sequence number. Either 0 or 1.
    public int sequenceNumber;
    // Denotes the characters that are included in this packet. For an ack packet, this is null.
    public final String characters;

    // This is set to true by the network layer when it could not receive a packet within
    // the given time period.
    public boolean timedOut;

    public Packet(String characters) {
        this.characters = characters;
        this.sequenceNumber = -1;
        this.ack = false;
        this.timedOut = false;
        this.lastPacket = false;
    }

    /**
     * Splits the given string into multiple packets of PACKET_SIZE many characters.
     * @param message the message to split.
     * @return the packets.
     */
    public static Packet[] split(String message) {
        // Determine the number of packets that the given string
        int packetNum = (int)Math.ceil((double)message.length() / (double)PACKET_SIZE);
        Packet[] packets = new Packet[packetNum];
        int j = 0;
        for(int i = 0; i < message.length(); i += PACKET_SIZE) {
            int end = Math.min(i + PACKET_SIZE, message.length());
            String sub = message.substring(i, end);
            packets[j++] = new Packet(sub);
        }
        // Set the last packet.
        packets[packets.length-1].lastPacket = true;
        return packets;
    }

    /**
     * Reconstructs the given ordered packet array into a string.
     * @param packets ordered list of packets received from the sender.
     * @return the original message.
     */
    public static String combine(Packet[] packets) {
        StringBuilder sb = new StringBuilder();
        for(Packet p : packets) sb.append(p.characters);
        return sb.toString();
    }

    /**
     * Returns the string representation of the packet. Useful for debugging.
     * @return the string representation of the packet.
     */
    @Override
    public String toString() {
        return (ack ? "ACK[" : "MSG[") + sequenceNumber + (ack ? "]" : (", \"" + characters + "\"]"));
    }
}
