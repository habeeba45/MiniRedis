
import command.CommandHandler;
import database.InMemoryDatabase;
import protocol.RespParser;
import protocol.RespWriter;
import protocol.RespResponse;
import persistence.DatabasePersistence;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        InMemoryDatabase database = new InMemoryDatabase();

        // Load previously saved data when server starts
        try {
            DatabasePersistence.load(database);
        } catch (Exception e) {
            System.out.println(
                    "Could not load database: " + e.getMessage()
            );
            return;
        }

        // Save database when server shuts down
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            System.out.println("\nShutting down MiniRedis...");

            try {
                DatabasePersistence.save(database);
            } catch (Exception e) {
                System.out.println(
                        "Could not save database: " + e.getMessage()
                );
            }

        }));

        try (ServerSocket serverSocket = new ServerSocket(6379)) {

            System.out.println("MiniRedis server started on port 6379");

            while (true) {

                // Accept a new client
                Socket clientSocket = serverSocket.accept();

                System.out.println("New client connected!");

                // Create a separate thread for this client
                Thread clientThread = new Thread(() -> {
                    handleClient(clientSocket, database);
                });

                // Start the thread
                clientThread.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method handles one client
    private static void handleClient(
            Socket clientSocket,
            InMemoryDatabase database) {

        try (Socket socket = clientSocket) {

            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            while (true) {

                List<String> parts = RespParser.readCommand(input);

                // Client disconnected
                if (parts == null) {
                    break;
                }

                System.out.println(
                        Thread.currentThread().getName()
                                + " received: " + parts
                );

                RespResponse response =
                        CommandHandler.handle(parts, database);

                RespWriter.writeResponse(output, response);
            }

        } catch (Exception e) {

            System.out.println(
                    "Client error: " + e.getMessage()
            );
        }

        System.out.println("Client connection closed.");
    }
}



