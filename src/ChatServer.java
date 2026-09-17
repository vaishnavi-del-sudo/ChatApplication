import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    private static final int PORT = 5000;

    private static final int MAX_CLIENTS = 2;

    private static final List<ClientHandler> clients =
            Collections.synchronizedList(new ArrayList<>());


    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("       CHATTING WITH JV");
        System.out.println("=================================");
        System.out.println("Chat Server Started!");
        System.out.println("Only 2 people are allowed.");
        System.out.println("Waiting for clients...");
        System.out.println();


        try (ServerSocket serverSocket =
                     new ServerSocket(PORT)) {


            while (true) {

                Socket socket =
                        serverSocket.accept();


                synchronized (clients) {

                    if (clients.size() >= MAX_CLIENTS) {

                        PrintWriter tempOutput =
                                new PrintWriter(
                                        socket.getOutputStream(),
                                        true
                                );

                        tempOutput.println(
                                "FULL|Only two people can chat at a time."
                        );

                        socket.close();

                        System.out.println(
                                "Connection rejected: chat is full."
                        );

                        continue;
                    }


                    System.out.println(
                            "A client has connected!"
                    );


                    ClientHandler client =
                            new ClientHandler(socket);


                    clients.add(client);


                    Thread thread =
                            new Thread(client);


                    thread.start();
                }
            }


        } catch (IOException e) {

            System.out.println(
                    "Server error: " +
                    e.getMessage()
            );
        }
    }


    // ==========================================
    // SEND MESSAGE TO EVERY CONNECTED CLIENT
    // ==========================================

    public static void broadcast(
            String message) {

        synchronized (clients) {

            for (ClientHandler client : clients) {

                client.sendMessage(message);
            }
        }


        System.out.println(
                "Broadcast: " + message
        );
    }


    // ==========================================
    // REMOVE CLIENT
    // ==========================================

    public static void removeClient(
            ClientHandler client) {

        clients.remove(client);


        System.out.println(
                "A client disconnected."
        );
    }
}