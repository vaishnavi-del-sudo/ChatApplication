import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientHandler implements Runnable {

    private Socket socket;

    private BufferedReader input;

    private PrintWriter output;

    private String username;


    public ClientHandler(Socket socket) {

        this.socket = socket;


        try {

            input =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()
                            )
                    );


            output =
                    new PrintWriter(
                            socket.getOutputStream(),
                            true
                    );


        } catch (IOException e) {

            System.out.println(
                    "Error creating client handler."
            );
        }
    }


    @Override
    public void run() {

        try {

            // ==================================
            // GET USERNAME
            // ==================================

            username =
                    input.readLine();


            if (username == null ||
                    username.trim().isEmpty()) {

                username = "Anonymous";
            }


            username =
                    username.trim();


            System.out.println(
                    username +
                    " connected."
            );


            // ==================================
            // USER JOINED
            // ==================================

            ChatServer.broadcast(
                    "JOIN|" +
                    username
            );


            // ==================================
            // RECEIVE DATA
            // ==================================

            String data;


            while ((data =
                    input.readLine()) != null) {


                // ==================================
                // NORMAL MESSAGE
                // ==================================

                if (data.startsWith("MSG|")) {

                    String[] parts =
                            data.split(
                                    "\\|",
                                    5
                            );


                    if (parts.length >= 5) {

                        String messageId =
                                parts[1];

                        String message =
                                parts[4];


                        String time =
                                new SimpleDateFormat(
                                        "h:mm a"
                                ).format(
                                        new Date()
                                );


                        System.out.println(
                                username +
                                ": " +
                                message
                        );


                        // ==================================
                        // SEND MESSAGE TO BOTH CLIENTS
                        // ==================================

                        ChatServer.broadcast(
                                "MSG|" +
                                messageId +
                                "|" +
                                username +
                                "|" +
                                time +
                                "|" +
                                message
                        );
                    }
                }


                // ==================================
                // READ RECEIPT
                // ==================================

                else if (data.startsWith("READ|")) {

                    String[] parts =
                            data.split(
                                    "\\|",
                                    2
                            );


                    if (parts.length == 2) {

                        String messageId =
                                parts[1];


                        System.out.println(
                                "Message " +
                                messageId +
                                " was seen."
                        );


                        // Tell both clients
                        ChatServer.broadcast(
                                "READ|" +
                                messageId
                        );
                    }
                }
            }


        } catch (IOException e) {

            System.out.println(
                    username +
                    " disconnected."
            );


        } finally {

            // ==================================
            // USER LEFT
            // ==================================

            if (username != null) {

                ChatServer.broadcast(
                        "LEAVE|" +
                        username
                );
            }


            ChatServer.removeClient(this);


            try {

                socket.close();

            } catch (IOException ignored) {
            }
        }
    }


    // ==========================================
    // SEND DATA TO CLIENT
    // ==========================================

    public void sendMessage(
            String message) {

        if (output != null) {

            output.println(message);
        }
    }
}