import java.awt.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ChatClient extends JFrame {

    // =========================================================
    // SERVER SETTINGS
    // =========================================================

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5000;


    // =========================================================
    // GUI COMPONENTS
    // =========================================================

    private JPanel chatPanel;
    private JTextField messageField;

    private JButton sendButton;
    private JButton emojiButton;
    private JButton darkModeButton;

    private JScrollPane scrollPane;

    private JLabel statusLabel;


    // =========================================================
    // NETWORK
    // =========================================================

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;


    // =========================================================
    // USER
    // =========================================================

    private String username;


    // =========================================================
    // DARK MODE
    // =========================================================

    private boolean darkMode = false;


    // =========================================================
    // MESSAGE STORAGE
    // =========================================================

    private final Map<String, MessageBubble> ownMessages =
            new HashMap<>();


    // =========================================================
    // LIGHT MODE COLORS
    // =========================================================

    private static final Color LIGHT_HEADER =
            new Color(67, 139, 82);

    private static final Color LIGHT_BACKGROUND =
            new Color(236, 229, 221);

    private static final Color LIGHT_MY_BUBBLE =
            new Color(218, 242, 203);

    private static final Color LIGHT_OTHER_BUBBLE =
            Color.WHITE;


    // =========================================================
    // DARK MODE COLORS
    // =========================================================

    private static final Color DARK_HEADER =
            new Color(32, 55, 40);

    private static final Color DARK_BACKGROUND =
            new Color(25, 29, 27);

    private static final Color DARK_MY_BUBBLE =
            new Color(45, 72, 52);

    private static final Color DARK_OTHER_BUBBLE =
            new Color(48, 52, 50);


    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            ChatClient client =
                    new ChatClient();

            client.start();
        });
    }


    // =========================================================
    // START APPLICATION
    // =========================================================

    public void start() {

        username =
                JOptionPane.showInputDialog(
                        this,
                        "Enter your name:",
                        "Chatting with JV",
                        JOptionPane.PLAIN_MESSAGE
                );


        if (username == null ||
                username.trim().isEmpty()) {

            username = "Guest";
        }


        username = username.trim();


        createGUI();

        connectToServer();
    }


    // =========================================================
    // CREATE GUI
    // =========================================================

    private void createGUI() {

        setTitle("Chatting with JV");


        setSize(
                700,
                800
        );


        setMinimumSize(
                new Dimension(
                        550,
                        650
                )
        );


        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );


        setLocationRelativeTo(null);


        // =====================================================
        // MAIN PANEL
        // =====================================================

        JPanel mainPanel =
                new JPanel(
                        new BorderLayout()
                );


        mainPanel.setBackground(
                LIGHT_BACKGROUND
        );


        setContentPane(mainPanel);


        // =====================================================
        // HEADER
        // =====================================================

        JPanel header =
                new JPanel(
                        new BorderLayout()
                );


        header.setBackground(
                LIGHT_HEADER
        );


        header.setBorder(
                new EmptyBorder(
                        16,
                        30,
                        16,
                        20
                )
        );


        // =====================================================
        // HEADER TEXT
        // =====================================================

        JPanel headerText =
                new JPanel();


        headerText.setLayout(
                new BoxLayout(
                        headerText,
                        BoxLayout.Y_AXIS
                )
        );


        headerText.setOpaque(false);


        // TITLE

        JLabel title =
                new JLabel(
                        "(^_^ ) Chatting with JV"
                );


        title.setForeground(
                Color.WHITE
        );


        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        30
                )
        );


        // USERNAME

        JLabel userLabel =
                new JLabel(
                        username
                );


        userLabel.setForeground(
                Color.WHITE
        );


        userLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        17
                )
        );


        // ONLINE STATUS

        statusLabel =
                new JLabel(
                        "● Online"
                );


        statusLabel.setForeground(
                new Color(
                        220,
                        255,
                        220
                )
        );


        statusLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );


        headerText.add(title);

        headerText.add(
                Box.createVerticalStrut(2)
        );

        headerText.add(userLabel);

        headerText.add(
                Box.createVerticalStrut(2)
        );

        headerText.add(statusLabel);


        header.add(
                headerText,
                BorderLayout.CENTER
        );


        // =====================================================
        // DARK MODE BUTTON
        // =====================================================

        darkModeButton =
                new JButton(
                        "\u263E Dark"
                );


        darkModeButton.setFont(
                new Font(
                        "Segoe UI Symbol",
                        Font.BOLD,
                        13
                )
        );


        darkModeButton.setFocusPainted(false);


        darkModeButton.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        220,
                                        220,
                                        220
                                )
                        ),
                        new EmptyBorder(
                                8,
                                12,
                                8,
                                12
                        )
                )
        );


        darkModeButton.setBackground(
                Color.WHITE
        );


        darkModeButton.setOpaque(true);


        darkModeButton.addActionListener(
                e -> toggleDarkMode()
        );


        header.add(
                darkModeButton,
                BorderLayout.EAST
        );


        mainPanel.add(
                header,
                BorderLayout.NORTH
        );


        // =====================================================
        // CHAT PANEL
        // =====================================================

        chatPanel =
                new JPanel();


        chatPanel.setLayout(
                new BoxLayout(
                        chatPanel,
                        BoxLayout.Y_AXIS
                )
        );


        chatPanel.setBackground(
                LIGHT_BACKGROUND
        );


        chatPanel.setBorder(
                new EmptyBorder(
                        12,
                        8,
                        12,
                        8
                )
        );


        // =====================================================
        // SCROLL PANE
        // =====================================================

        scrollPane =
                new JScrollPane(
                        chatPanel
                );


        scrollPane.setBorder(null);


        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );


        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );


        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(16);


        scrollPane.getViewport()
                .setBackground(
                        LIGHT_BACKGROUND
                );


        mainPanel.add(
                scrollPane,
                BorderLayout.CENTER
        );


        // =====================================================
        // INPUT PANEL
        // =====================================================

        JPanel inputPanel =
                new JPanel(
                        new BorderLayout(
                                8,
                                0
                        )
                );


        inputPanel.setBackground(
                new Color(
                        245,
                        245,
                        245
                )
        );


        inputPanel.setBorder(
                new EmptyBorder(
                        10,
                        10,
                        10,
                        10
                )
        );


        // =====================================================
        // EMOJI BUTTON
        // =====================================================

        emojiButton =
                new JButton(
                        "😊"
                );


        emojiButton.setFont(
                new Font(
                        "Segoe UI Emoji",
                        Font.PLAIN,
                        20
                )
        );


        emojiButton.setPreferredSize(
                new Dimension(
                        48,
                        48
                )
        );


        emojiButton.setFocusPainted(false);


        emojiButton.setToolTipText(
                "Emoji"
        );


        emojiButton.addActionListener(
                e -> showEmojiMenu()
        );


        inputPanel.add(
                emojiButton,
                BorderLayout.WEST
        );


        // =====================================================
        // MESSAGE FIELD
        // =====================================================

        messageField =
                new JTextField();


        messageField.setFont(
                new Font(
                        "Segoe UI Emoji",
                        Font.PLAIN,
                        16
                )
        );


        messageField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        205,
                                        205,
                                        205
                                )
                        ),
                        new EmptyBorder(
                                8,
                                12,
                                8,
                                12
                        )
                )
        );


        inputPanel.add(
                messageField,
                BorderLayout.CENTER
        );


        // =====================================================
        // SEND BUTTON
        // =====================================================

        sendButton =
                new JButton(
                        "SEND"
                );


        sendButton.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        16
                )
        );


        sendButton.setPreferredSize(
                new Dimension(
                        105,
                        48
                )
        );


        sendButton.setFocusPainted(false);


        inputPanel.add(
                sendButton,
                BorderLayout.EAST
        );


        mainPanel.add(
                inputPanel,
                BorderLayout.SOUTH
        );


        // =====================================================
        // ACTIONS
        // =====================================================

        sendButton.addActionListener(
                e -> sendMessage()
        );


        messageField.addActionListener(
                e -> sendMessage()
        );


        setVisible(true);
    }


    // =========================================================
    // CONNECT TO SERVER
    // =========================================================

    private void connectToServer() {

        try {

            socket =
                    new Socket(
                            SERVER_ADDRESS,
                            SERVER_PORT
                    );


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


            output.println(username);


            Thread listener =
                    new Thread(
                            this::listenForMessages
                    );


            listener.setDaemon(true);

            listener.start();


        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not connect to server.\n\n"
                            +
                            "Make sure ChatServer is running first.",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =========================================================
    // LISTEN FOR SERVER
    // =========================================================

    private void listenForMessages() {

        try {

            String message;


            while ((message =
                    input.readLine()) != null) {

                final String received =
                        message;


                SwingUtilities.invokeLater(
                        () -> processServerMessage(
                                received
                        )
                );
            }


        } catch (IOException e) {

            SwingUtilities.invokeLater(
                    () -> {

                        statusLabel.setText(
                                "● Offline"
                        );


                        statusLabel.setForeground(
                                new Color(
                                        255,
                                        180,
                                        180
                                )
                        );


                        addSystemMessage(
                                "Disconnected from server."
                        );
                    }
            );
        }
    }


    // =========================================================
    // PROCESS SERVER MESSAGE
    // =========================================================

    private void processServerMessage(
            String message) {


        // =====================================================
        // CHAT FULL
        // =====================================================

        if (message.startsWith("FULL|")) {

            JOptionPane.showMessageDialog(
                    this,
                    "This chat already has two people.",
                    "Chat Full",
                    JOptionPane.WARNING_MESSAGE
            );


            closeConnection();


            return;
        }


        // =====================================================
        // JOIN
        // =====================================================

        if (message.startsWith("JOIN|")) {

            String joinedUser =
                    message.substring(5);


            addSystemMessage(
                    joinedUser +
                    " has joined the chat!"
            );


            if (!joinedUser.equals(username)) {

                statusLabel.setText(
                        "● Online"
                );


                statusLabel.setForeground(
                        new Color(
                                220,
                                255,
                                220
                        )
                );
            }


            return;
        }


        // =====================================================
        // LEAVE
        // =====================================================

        if (message.startsWith("LEAVE|")) {

            String leftUser =
                    message.substring(6);


            addSystemMessage(
                    leftUser +
                    " has left the chat!"
            );


            if (!leftUser.equals(username)) {

                statusLabel.setText(
                        "● Offline"
                );


                statusLabel.setForeground(
                        new Color(
                                255,
                                180,
                                180
                        )
                );
            }


            return;
        }


        // =====================================================
        // MESSAGE
        // =====================================================

        if (message.startsWith("MSG|")) {

            String[] parts =
                    message.split(
                            "\\|",
                            5
                    );


            if (parts.length < 5) {
                return;
            }


            String messageId =
                    parts[1];


            String sender =
                    parts[2];


            String time =
                    parts[3];


            String messageText =
                    parts[4];


            boolean ownMessage =
                    sender.equals(username);


            addMessage(
                    messageId,
                    messageText,
                    sender,
                    time,
                    ownMessage
            );


            if (!ownMessage) {

                if (output != null) {

                    output.println(
                            "READ|" +
                            messageId
                    );
                }
            }


            return;
        }


        // =====================================================
        // READ RECEIPT
        // =====================================================

        if (message.startsWith("READ|")) {

            String messageId =
                    message.substring(5);


            MessageBubble bubble =
                    ownMessages.get(
                            messageId
                    );


            if (bubble != null) {

                bubble.setSeen(true);

                bubble.repaint();
            }
        }
    }


    // =========================================================
    // SEND MESSAGE
    // =========================================================

    private void sendMessage() {

        String message =
                messageField
                        .getText()
                        .trim();


        if (message.isEmpty()) {
            return;
        }


        if (output != null) {

            String messageId =
                    UUID.randomUUID()
                            .toString();


            String time =
                    new SimpleDateFormat(
                            "h:mm a"
                    ).format(
                            new Date()
                    );


            output.println(
                    "MSG|" +
                    messageId +
                    "|" +
                    username +
                    "|" +
                    time +
                    "|" +
                    message
            );


            messageField.setText("");


            messageField.requestFocus();
        }
    }


    // =========================================================
    // ADD MESSAGE
    // =========================================================

    private void addMessage(
            String messageId,
            String message,
            String sender,
            String time,
            boolean ownMessage) {


        MessageBubble bubble =
                new MessageBubble(
                        message,
                        sender,
                        time,
                        ownMessage
                );


        if (ownMessage) {

            ownMessages.put(
                    messageId,
                    bubble
            );
        }


        // =====================================================
        // ROW
        // =====================================================

        JPanel row =
                new JPanel(
                        new BorderLayout()
                );


        row.setOpaque(false);


        row.setBorder(
                new EmptyBorder(
                        3,
                        5,
                        3,
                        5
                )
        );


        if (ownMessage) {

            row.add(
                    bubble,
                    BorderLayout.EAST
            );

        } else {

            row.add(
                    bubble,
                    BorderLayout.WEST
            );
        }


        // =====================================================
        // FIXED NATURAL HEIGHT
        // =====================================================

        Dimension bubbleSize =
                bubble.getPreferredSize();


        row.setPreferredSize(
                new Dimension(
                        100,
                        bubbleSize.height + 6
                )
        );


        row.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        bubbleSize.height + 6
                )
        );


        chatPanel.add(row);


        chatPanel.revalidate();

        chatPanel.repaint();


        scrollToBottom();
    }


    // =========================================================
    // SYSTEM MESSAGE
    // =========================================================

    private void addSystemMessage(
            String message) {


        JLabel systemLabel =
                new JLabel(
                        message
                );


        systemLabel.setFont(
                new Font(
                        "Arial",
                        Font.ITALIC,
                        14
                )
        );


        systemLabel.setForeground(
                darkMode
                        ? new Color(
                                205,
                                205,
                                205
                        )
                        : new Color(
                                70,
                                70,
                                70
                        )
        );


        systemLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );


        systemLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );


        systemLabel.setBorder(
                new EmptyBorder(
                        12,
                        5,
                        12,
                        5
                )
        );


        chatPanel.add(
                systemLabel
        );


        chatPanel.revalidate();

        chatPanel.repaint();


        scrollToBottom();
    }


    // =========================================================
    // SCROLL TO BOTTOM
    // =========================================================

    private void scrollToBottom() {

        SwingUtilities.invokeLater(() -> {

            JScrollBar bar =
                    scrollPane
                            .getVerticalScrollBar();


            bar.setValue(
                    bar.getMaximum()
            );
        });
    }


    // =========================================================
    // EMOJI MENU
    // =========================================================

    private void showEmojiMenu() {

        JPopupMenu menu =
                new JPopupMenu();


        String[] emojis = {

                "😊",
                "😂",
                "❤️",
                "😍",
                "😭",
                "😅",

                "😎",
                "😘",
                "🤗",
                "😌",
                "🥰",
                "😔",

                "😡",
                "👍",
                "👎",
                "👏",
                "🙏",
                "🎉",

                "✨",
                "💕",
                "💗",
                "💖",
                "🔥",
                "🌸",

                "🤍",
                "🤣",
                "☺",
                "♥",
                "★",
                "☀"
        };


        JPanel emojiPanel =
                new JPanel(
                        new GridLayout(
                                5,
                                6,
                                4,
                                4
                        )
                );


        emojiPanel.setBorder(
                new EmptyBorder(
                        7,
                        7,
                        7,
                        7
                )
        );


        for (String emoji : emojis) {

            JButton button =
                    new JButton(
                            emoji
                    );


            button.setFont(
                    new Font(
                            "Segoe UI Emoji",
                            Font.PLAIN,
                            20
                    )
            );


            button.setFocusPainted(false);


            button.setBorder(
                    BorderFactory.createEmptyBorder(
                            5,
                            5,
                            5,
                            5
                    )
            );


            button.addActionListener(
                    e -> {

                        insertEmoji(
                                emoji
                        );

                        menu.setVisible(false);
                    }
            );


            emojiPanel.add(button);
        }


        menu.add(
                emojiPanel
        );


        menu.show(
                emojiButton,
                0,
                -235
        );
    }


    // =========================================================
    // INSERT EMOJI
    // =========================================================

    private void insertEmoji(
            String emoji) {

        int position =
                messageField
                        .getCaretPosition();


        String currentText =
                messageField.getText();


        messageField.setText(
                currentText.substring(
                        0,
                        position
                )
                +
                emoji
                +
                currentText.substring(
                        position
                )
        );


        messageField.setCaretPosition(
                position +
                emoji.length()
        );


        messageField.requestFocus();
    }


    // =========================================================
    // DARK MODE BUTTON
    // =========================================================

    private void toggleDarkMode() {

        darkMode =
                !darkMode;


        if (darkMode) {

            darkModeButton.setText(
                    "\u2600 Light"
            );

        } else {

            darkModeButton.setText(
                    "\u263E Dark"
            );
        }


        updateTheme();
    }


    // =========================================================
    // UPDATE THEME
    // =========================================================

    private void updateTheme() {

        Color background =
                darkMode
                        ? DARK_BACKGROUND
                        : LIGHT_BACKGROUND;


        Color headerColor =
                darkMode
                        ? DARK_HEADER
                        : LIGHT_HEADER;


        Color inputBackground =
                darkMode
                        ? new Color(
                                35,
                                40,
                                37
                        )
                        : new Color(
                                245,
                                245,
                                245
                        );


        getContentPane()
                .setBackground(
                        background
                );


        chatPanel.setBackground(
                background
        );


        scrollPane.getViewport()
                .setBackground(
                        background
                );


        updateHeaderColor(
                getContentPane(),
                headerColor
        );


        updateInputColors(
                getContentPane(),
                inputBackground
        );


        chatPanel.repaint();


        repaint();
    }


    // =========================================================
    // UPDATE HEADER COLOR
    // =========================================================

    private void updateHeaderColor(
            Container container,
            Color headerColor) {

        for (Component component :
                container.getComponents()) {


            if (component instanceof JPanel) {

                JPanel panel =
                        (JPanel) component;


                if (panel.getLayout()
                        instanceof BorderLayout) {

                    Color current =
                            panel.getBackground();


                    if (current.equals(
                            LIGHT_HEADER
                    )
                    ||
                    current.equals(
                            DARK_HEADER
                    )) {

                        panel.setBackground(
                                headerColor
                        );
                    }
                }


                updateHeaderColor(
                        panel,
                        headerColor
                );
            }
        }
    }


    // =========================================================
    // UPDATE INPUT COLORS
    // =========================================================

    private void updateInputColors(
            Container container,
            Color inputBackground) {

        for (Component component :
                container.getComponents()) {


            if (component instanceof JPanel) {

                JPanel panel =
                        (JPanel) component;


                Color current =
                        panel.getBackground();


                if (current.equals(
                        new Color(
                                245,
                                245,
                                245
                        )
                )
                ||
                current.equals(
                        new Color(
                                35,
                                40,
                                37
                        )
                )) {

                    panel.setBackground(
                            inputBackground
                    );
                }


                updateInputColors(
                        panel,
                        inputBackground
                );
            }
        }
    }


    // =========================================================
    // CLOSE CONNECTION
    // =========================================================

    private void closeConnection() {

        try {

            if (socket != null) {

                socket.close();
            }

        } catch (IOException ignored) {
        }
    }


    // =========================================================
    // MESSAGE BUBBLE
    // =========================================================

    private class MessageBubble extends JPanel {

        private final boolean ownMessage;
        private final JTextArea messageArea;
        private final JLabel timeLabel;
        private final JLabel tickLabel;
        private boolean seen = false;

        // =====================================================
        // CONSTRUCTOR
        // =====================================================

        public MessageBubble(
                String message,
                String sender,
                String time,
                boolean ownMessage) {

            this.ownMessage = ownMessage;

            setOpaque(false);
            setLayout(new BorderLayout());

            // =================================================
            // CONTENT
            // =================================================

            JPanel content = new JPanel();
            content.setOpaque(false);
            content.setLayout(
                    new BoxLayout(
                            content,
                            BoxLayout.Y_AXIS
                    )
            );

            // =================================================
            // SENDER
            // =================================================

            if (!ownMessage) {

                JLabel senderLabel = new JLabel(sender);

                senderLabel.setFont(
                        new Font(
                                "Arial",
                                Font.BOLD,
                                13
                        )
                );

                senderLabel.setForeground(
                        darkMode
                                ? new Color(125, 205, 145)
                                : new Color(45, 120, 65)
                );

                senderLabel.setAlignmentX(
                        Component.LEFT_ALIGNMENT
                );

                content.add(senderLabel);

                content.add(
                        Box.createVerticalStrut(3)
                );
            }

            // =================================================
            // MESSAGE
            // =================================================

            messageArea = new JTextArea(message);

            messageArea.setFont(
                    new Font(
                            "Segoe UI Emoji",
                            Font.PLAIN,
                            16
                    )
            );

            messageArea.setForeground(
                    darkMode
                            ? new Color(245, 245, 245)
                            : Color.BLACK
            );

            messageArea.setEditable(false);
            messageArea.setFocusable(false);
            messageArea.setOpaque(false);
            messageArea.setBorder(null);
            messageArea.setLineWrap(true);
            messageArea.setWrapStyleWord(true);
            messageArea.setAlignmentX(
                    Component.LEFT_ALIGNMENT
            );

            // =================================================
            // TEXT WIDTH
            // =================================================

            final int MAX_TEXT_WIDTH = 340;
            final int MIN_TEXT_WIDTH = 45;

            FontMetrics metrics =
                    messageArea.getFontMetrics(
                            messageArea.getFont()
                    );

            int longestWidth = MIN_TEXT_WIDTH;

            String[] lines =
                    message.split("\\R", -1);

            for (String line : lines) {

                int lineWidth =
                        metrics.stringWidth(line);

                longestWidth =
                        Math.max(
                                longestWidth,
                                lineWidth
                        );
            }

            int textWidth =
                    Math.min(
                            MAX_TEXT_WIDTH,
                            longestWidth
                    );

            textWidth =
                    Math.max(
                            MIN_TEXT_WIDTH,
                            textWidth
                    );

            // =================================================
            // IMPORTANT:
            // Set the width first, then let Swing calculate
            // the real wrapped height. This prevents text and
            // emojis from being clipped inside the bubble.
            // =================================================

            messageArea.setSize(
                    new Dimension(
                            textWidth,
                            Short.MAX_VALUE
                    )
            );

            Dimension textSize =
                    messageArea.getPreferredSize();

            int textHeight =
                    Math.max(
                            textSize.height,
                            metrics.getHeight()
                    );

            messageArea.setPreferredSize(
                    new Dimension(
                            textWidth,
                            textHeight
                    )
            );

            messageArea.setMinimumSize(
                    new Dimension(
                            textWidth,
                            textHeight
                    )
            );

            messageArea.setMaximumSize(
                    new Dimension(
                            textWidth,
                            textHeight
                    )
            );

            content.add(messageArea);

            // =================================================
            // BOTTOM ROW
            // =================================================

            JPanel bottom =
                    new JPanel(
                            new FlowLayout(
                                    FlowLayout.RIGHT,
                                    3,
                                    0
                            )
                    );

            bottom.setOpaque(false);
            bottom.setAlignmentX(
                    Component.LEFT_ALIGNMENT
            );

            // =================================================
            // TIMESTAMP
            // =================================================

            timeLabel = new JLabel(time);

            timeLabel.setFont(
                    new Font(
                            "Arial",
                            Font.PLAIN,
                            10
                    )
            );

            timeLabel.setForeground(
                    darkMode
                            ? new Color(190, 195, 192)
                            : new Color(95, 100, 98)
            );

            bottom.add(timeLabel);

            // =================================================
            // TICKS
            // =================================================

            if (ownMessage) {

                // Use the Unicode escape so the check marks
                // compile reliably and use a symbol font.

                tickLabel =
                        new JLabel("\u2713\u2713");

                tickLabel.setFont(
                        new Font(
                                "Segoe UI Symbol",
                                Font.BOLD,
                                12
                        )
                );

                tickLabel.setForeground(
                        new Color(
                                100,
                                100,
                                100
                        )
                );

                tickLabel.setToolTipText(
                        "Delivered"
                );

                bottom.add(tickLabel);

            } else {

                tickLabel = null;
            }

            content.add(
                    Box.createVerticalStrut(3)
            );

            content.add(bottom);

            // =================================================
            // BUBBLE PADDING
            // =================================================

            setBorder(
                    new EmptyBorder(
                            8,
                            12,
                            7,
                            12
                    )
            );

            add(
                    content,
                    BorderLayout.CENTER
            );

            // =================================================
            // FINAL BUBBLE SIZE
            // =================================================

            Dimension contentSize =
                    content.getPreferredSize();

            int bubbleWidth =
                    textWidth + 24;

            if (!ownMessage) {

                bubbleWidth =
                        Math.max(
                                bubbleWidth,
                                115
                        );
            }

            bubbleWidth =
                    Math.min(
                            bubbleWidth,
                            MAX_TEXT_WIDTH + 24
                    );

            int bubbleHeight =
                    contentSize.height
                    + getInsets().top
                    + getInsets().bottom
                    + 2;

            setPreferredSize(
                    new Dimension(
                            bubbleWidth,
                            bubbleHeight
                    )
            );

            setMinimumSize(
                    new Dimension(
                            bubbleWidth,
                            bubbleHeight
                    )
            );

            setMaximumSize(
                    new Dimension(
                            bubbleWidth,
                            bubbleHeight
                    )
            );
        }

        // =====================================================
        // MARK AS SEEN
        // =====================================================

        public void setSeen(boolean seen) {

            this.seen = seen;

            if (tickLabel != null) {

                if (seen) {

                    tickLabel.setForeground(
                            new Color(
                                    0,
                                    150,
                                    136
                            )
                    );

                    tickLabel.setToolTipText("Seen");

                } else {

                    tickLabel.setForeground(
                            new Color(
                                    100,
                                    100,
                                    100
                            )
                    );

                    tickLabel.setToolTipText("Delivered");
                }
            }

            repaint();
        }

        // =====================================================
        // DRAW BUBBLE
        // =====================================================

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            if (ownMessage) {

                g2.setColor(
                        darkMode
                                ? DARK_MY_BUBBLE
                                : LIGHT_MY_BUBBLE
                );

            } else {

                g2.setColor(
                        darkMode
                                ? DARK_OTHER_BUBBLE
                                : LIGHT_OTHER_BUBBLE
                );
            }

            int width =
                    Math.max(
                            1,
                            getWidth() - 1
                    );

            int height =
                    Math.max(
                            1,
                            getHeight() - 1
                    );

            g2.fillRoundRect(
                    0,
                    0,
                    width,
                    height,
                    18,
                    18
            );

            g2.setColor(
                    darkMode
                            ? new Color(70, 75, 72)
                            : new Color(205, 205, 205)
            );

            g2.setStroke(
                    new BasicStroke(1f)
            );

            g2.drawRoundRect(
                    0,
                    0,
                    width,
                    height,
                    18,
                    18
            );

            g2.dispose();

            super.paintComponent(g);
        }
    }
}
