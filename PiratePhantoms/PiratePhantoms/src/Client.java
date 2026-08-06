import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Client {
    public static void main(String[] args) throws Exception {
        String serverIP = "localhost"; // Placeholder IP, to be set by user input
        int port = 12345;
        Socket socket = new Socket(serverIP, port);
        System.out.println("Connected to server at " + serverIP + ":" + port);

        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        // TODO: Implement client-side prediction and movement input sending
        // TODO: Receive game state updates from server and render locally
        while (true) {
            // Placeholder for reading server updates
            // String update = in.readUTF();
            // System.out.println("Server update: " + update);
            Thread.sleep(100); // Simulate update interval
        }
    }
}
