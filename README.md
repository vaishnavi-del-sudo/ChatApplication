=================================
       CHATTING WITH JV
=================================

Chatting with JV is a simple two-person real-time chat application developed in Java.

The application follows a client-server architecture. A central server manages the connections between two clients and forwards chat messages and read receipts between them.


**Features**

- Two-person real-time chat
- Java Swing graphical user interface
- TCP socket communication
- Client-server architecture
- Message timestamps
- Online/offline status
- Join and leave notifications
- Delivered and seen read receipts
- Emoji picker
- Light mode and dark mode
- Message bubbles
- Automatic message scrolling
- Long-message wrapping
- Third-client rejection when the chat is full

**Technologies Used**

- Java
- Java Swing
- TCP/IP sockets
- Java threads
- Java Collections
- Java I/O

No external Java libraries are required.

**Project Structure**


chatting-with-jv/

│

├── README.md

├── .gitignore

│

├── src/

│   ├── ChatClient.java

│   ├── ChatServer.java

│   └── ClientHandler.java

│

└── report/

    └── PROJECT_REPORT.md_


**Requirements**

1. Java Development Kit (JDK) 8 or newer
2. Terminal or Command Prompt
3. Git

(JDK 17 or newer is recommended.)

Check whether Java is installed:

**Bash**__
java -version
javac -version

**Setup**

1. Clone the repository

2. Compile the project

From the project root directory, run:

javac src/ChatServer.java src/ClientHandler.java src/ChatClient.java

If compilation is successful, the required .class files will be generated inside the src directory.

Running the Application

The application requires one server and two clients.

Open three terminal windows.

_Terminal 1 — Start the Server_

From the project root:

java -cp src ChatServer

The server will start listening on TCP port 5000.

You should see output similar to:

=================================
       CHATTING WITH JV
=================================
Chat Server Started on port 5000
Maximum clients: 2
Waiting for clients...

_Terminal 2 — Start Client 1_

From the project root:

java -cp src ChatClient

Enter the first user's name when prompted.

_Terminal 3 — Start Client 2_

From the project root:

java -cp src ChatClient

Enter the second user's name.

The two clients can now exchange messages.

**ChatServer**

ChatServer creates a ServerSocket on port 5000 and waits for incoming connections.

The server allows a maximum of two clients at the same time.

Each accepted client is assigned a separate ClientHandler thread.

**ClientHandler**

ClientHandler handles communication for one connected client.

It processes:

*Username information
*Chat messages
*Read receipts
*Client disconnections
*Join notifications
*Leave notifications

**ChatClient**

ChatClient provides the graphical interface using Java Swing.

The interface includes:

*Chat message area
*Message input field
*Send button
*Emoji picker
*Dark/light mode
*Online/offline status
*Message timestamps
*Read receipts
*Message Protocol

The application uses simple text messages over TCP.

Important message types include:

**JOIN**

Used when a client joins.

**LEAVE**

Used when a client disconnects.

**MSG**

Used to send a chat message.

**READ**

Used when a message has been read.

**FULL**

Used when a third client attempts to connect while two clients are already connected.

**Read Receipts**

When a client receives a message, it sends a READ
messageId notification to the server.

The server uses the message ID to identify the original sender and sends the read receipt back to that client.

The sender's message then changes from the delivered state to the seen state.

**Configuration**

The default configuration is:

Server address: localhost
Server port: 5000
Maximum clients: 2

The client currently uses:

private static final String SERVER_ADDRESS = "localhost";
private static final int SERVER_PORT = 5000;

The server uses port:

private static final int PORT = 5000;

If you change the port, make sure the port number is changed consistently in both the server and client.

**Author**

_Vaishnavi Yadav_
