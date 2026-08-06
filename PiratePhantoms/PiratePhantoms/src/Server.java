import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Server {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("Server started on port 12345");
        while (true) {
            Socket client1 = serverSocket.accept();
            System.out.println("Client 1 connected: " + client1.getInetAddress());
            Socket client2 = serverSocket.accept();
            System.out.println("Client 2 connected: " + client2.getInetAddress());
            // Start a new game session for the two clients
            Thread gameSession = new Thread(() -> handleGameSession(client1, client2));
            gameSession.setName("GameSessionThread");
            gameSession.start();
        }
    }

    private static void handleGameSession(Socket client1, Socket client2) {
        try {
            // Setup communication streams for both clients
            DataInputStream in1 = new DataInputStream(client1.getInputStream());
            DataOutputStream out1 = new DataOutputStream(client1.getOutputStream());
            DataInputStream in2 = new DataInputStream(client2.getInputStream());
            DataOutputStream out2 = new DataOutputStream(client2.getOutputStream());

            // TODO: Initialize game state (map, players) and synchronize between clients
            // Placeholder for game loop to broadcast updates every 100ms as per proposal
            while (true) {
                // Read inputs from clients
                // Update game state (authoritative server logic)
                // Broadcast updated state to both clients
                Thread.sleep(100); // Simulate 100ms update interval
                // Example: out1.writeUTF("Game state update"); out2.writeUTF("Game state update");
            }
        } catch (Exception e) {
            System.err.println("Error in game session: " + e.getMessage());
            // TODO: Log error to errorlog.txt
        } finally {
            try {
                client1.close();
                client2.close();
            } catch (Exception e) {
                System.err.println("Error closing client connections: " + e.getMessage());
            }
        }
    }
}
