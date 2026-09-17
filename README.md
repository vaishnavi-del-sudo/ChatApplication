**Chatting with JV**

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

_```text
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
    
