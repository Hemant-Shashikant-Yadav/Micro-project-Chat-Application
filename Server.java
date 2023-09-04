import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {

    // Declare a Socket variable to represent the connection with an individual
    // client.
    Socket socket;
    static int port = 1234;
    // Declare a List of BufferedWriter objects to store the output streams for all
    // connected clients.
    // Each BufferedWriter in the list is responsible for sending messages to a
    // specific client.
    private static final List<BufferedWriter> clients = new ArrayList<>();

    // Constructor to initialize the Server instance with a client Socket.
    // The Socket object represents the connection with an individual client.
    public Server(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            // Create a buffered reader and writer to communicate with the client.
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            synchronized (clients) {
                // Add the client's writer to the list of active clients.
                clients.add(writer);
            }

            while (true) {
                // Read a message from the client.
                String data = reader.readLine().trim();

                synchronized (clients) {
                    // Broadcast the message to all connected clients.
                    for (BufferedWriter client : clients) {
                        try {
                            client.write(data);
                            client.write("\r\n");
                            client.flush();
                        } catch (Exception e) {
                            // If an exception occurs while sending the message to a client,
                            // print the stack trace for debugging purposes.
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            // If an exception occurs while handling the client connection,
            // print the stack trace for debugging purposes.
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                // Accept incoming client connections.
                Socket socket = serverSocket.accept();
                // Create a new thread to handle the client's communication.
                Server server = new Server(socket);
                Thread thread = new Thread(server);
                thread.start();
            }
        } catch (Exception e) {
            // If an exception occurs while setting up the server or accepting connections,
            // print the stack trace for debugging purposes.
            e.printStackTrace();
        }
    }
}
