package project2.main;

public class Main {

    public static boolean DEBUG = false;

    public static void main(String[] args) {
        System.out.println("Initiating test with packet loss...");
        test("Stop-and-Wait ARQ is a simple flow control protocol. " +
                "Simple, yet it wastes a lot of bandwidth.", true);
    }

    /**
     * Creates a sender and receives, and checks their connectivity by sending the given
     * message from the sender to the receiver.
     * @param testMessage the message to send from the sender to the receiver.
     * @param emulateLoss whether packet loss should be emulated at the network layer.
     */
    public static void test(String testMessage, boolean emulateLoss) {
        // Create the sender and receiver processes.
        Receiver receiver = new Receiver(emulateLoss);
        Sender sender = new Sender(testMessage, emulateLoss);
        // Create the threads.
        Thread receiverThread = new Thread(receiver);
        Thread senderThread = new Thread(sender);
        // Run the threads.
        receiverThread.start();
        senderThread.start();
        // Join the threads.
        try {
            senderThread.join();
            receiverThread.join();
        } catch (InterruptedException e) {
            System.err.println("[Main.java] Could not join the threads.");
            e.printStackTrace();
        }
        // Evaluate the results.
        boolean success = receiver.output.equals(testMessage);
        System.out.println(success ? "\tSuccess!" : "\tFailure! ");
        if(!success) {
            System.out.println("\t\tExpected: " + testMessage + "\n\t\tReceived: " + receiver.output);
        }
    }
}
