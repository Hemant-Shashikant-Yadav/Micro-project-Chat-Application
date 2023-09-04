package packageclient;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;

public class ClientPackage implements ActionListener, Runnable {
    JTextField text; // Text field for user input
    JPanel p; // Panel to display messages
    JScrollPane scrollPane; // Scroll pane to handle message overflow
    static Box vertical = Box.createVerticalBox(); // Vertical box to stack messages
    static JFrame F = new JFrame(); // Main chat window
    private String chatHistoryFileName;

    BufferedReader reader; // Input stream to receive messages
    BufferedWriter writer; // Output stream to send messages
    String name; // Name of the client

    // Constructor
    public ClientPackage(String name, int position, String Image) {
        this.name = name;
        this.chatHistoryFileName = name + "_chat_history.txt";
        initializeGUI(position, Image); // Initialize the graphical user interface
        setupNetworkConnection(); // Set up the network connection to the server
        loadChatHistory();
        Thread T = new Thread(); // Create a new thread
        T.start(); // Start the thread (which will call the run method)
    }

    private void saveChatHistory(String senderName, String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(chatHistoryFileName, true))) {
            writer.write(senderName + ": " + message); // Include the sender's name in the chat history
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadChatHistory() {
        try (BufferedReader reader = new BufferedReader(new FileReader(chatHistoryFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int colonIndex = line.indexOf(":");
                if (colonIndex >= 0) {
                    String senderName = line.substring(colonIndex + 2, line.indexOf("<br>"));
                    String message = line.substring(colonIndex + 2); // Add 2 to skip ": " after the sender's name

                    // Format and display the loaded message in the GUI
                    JPanel panel = formatLable(message);

                    // Add the formatted message to the main panel
                    p.setLayout(new BorderLayout());
                    JPanel messagePanel = new JPanel(new BorderLayout());
                    // Place the message on the left or right side based on whether it belongs to
                    // the current user or others
                    if (senderName.equals(name)) {
                        messagePanel.add(panel, BorderLayout.LINE_END);
                    } else {
                        messagePanel.add(panel, BorderLayout.LINE_START);
                    }
                    vertical.add(messagePanel);
                    vertical.add(Box.createVerticalStrut(20));
                    p.add(vertical, BorderLayout.PAGE_START);

                    // Scroll to the bottom of the panel
                    JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                    verticalScrollBar.setValue(verticalScrollBar.getMaximum());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to initialize the graphical user interface
    private void initializeGUI(int position, String Image1) {
        F = new JFrame(name); // Window title
        F.setLayout(null);
        F.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // F.setSize(400, 800);
        F.setBounds(position, 0, 400, 800);
        F.setResizable(false);

        // Create the header panel
        JPanel p1 = new JPanel();
        p1.setBackground(new Color(78, 79, 235));
        p1.setBounds(0, 0, 400, 80);
        p1.setLayout(null);
        F.add(p1);

        // Create the attachment button
        // Add a new button to the GUI
        JButton attachButton = new JButton("...");
        attachButton.setBounds(325, 20, 40, 40);
        attachButton.setBackground(new Color(7, 94, 84));
        attachButton.setForeground(Color.WHITE);
        p1.add(attachButton);

        // Add an action listener to the attachment button
        attachButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Open a file chooser dialog to select a file
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(F);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    // Read the selected file and send it to the server
                    sendFile(selectedFile);
                }
            }
        });

        // Create a back arrow icon and add it to the header panel
        ImageIcon icon7 = new ImageIcon(ClassLoader.getSystemResource("left-arrow.png"));
        Image icon8 = icon7.getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT);
        ImageIcon icon9 = new ImageIcon(icon8);
        JLabel label3 = new JLabel(icon9);
        label3.setBounds(2, 10, 60, 60);
        p1.add(label3);

        // Add a mouse click listener to the back arrow icon to exit the application
        label3.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });

        // Create a user icon and add it to the header panel
        ImageIcon icon1 = new ImageIcon(ClassLoader.getSystemResource(Image1));
        Image icon2 = icon1.getImage().getScaledInstance(60, 60, Image.SCALE_DEFAULT);
        ImageIcon icon3 = new ImageIcon(icon2);
        JLabel label1 = new JLabel(icon3);
        label1.setBounds(55, 10, 60, 60);
        p1.add(label1);

        // Create a label to display the user's name and add it to the header panel
        JLabel label4 = new JLabel(name);
        label4.setBounds(120, 20, 80, 20);
        label4.setForeground(Color.WHITE);
        label4.setFont(new Font("Sans-Serif", Font.BOLD, 18));
        p1.add(label4);

        // Create a label to display "Online" and add it to the header panel
        JLabel label5 = new JLabel("Online");
        label5.setBounds(122, 40, 80, 20);
        label5.setForeground(Color.WHITE);
        label5.setFont(new Font("Sans-Serif", Font.BOLD, 12));
        p1.add(label5);

        // Create the main panel to display messages
        p = new JPanel();
        p.setBounds(5, 85, 375, 625);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        F.add(p);

        // Create a scroll pane and add the main panel to it
        scrollPane = new JScrollPane(p);
        scrollPane.setBounds(5, 85, 375, 625);
        F.add(scrollPane);

        // Add a component listener to the main frame to scroll to the bottom when the
        // frame is resized
        F.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                verticalScrollBar.setValue(verticalScrollBar.getMaximum());
            }
        });

        // Create a text field for user input and add it to the main frame
        text = new JTextField();
        text.setBounds(5, 715, 300, 38);
        text.setFont(new Font("Sans-Serif", Font.PLAIN, 20));
        F.add(text);

        // Create a send button and add it to the main frame
        JButton send = new JButton("Send");
        send.setBounds(310, 715, 70, 40);
        send.setBackground(new Color(7, 94, 84));
        send.setForeground(Color.white);
        F.add(send);

        // Add an action listener to the send button to send the user's message
        send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String out = name + "<br>" + text.getText();

                    // Format the user's message and add it to the panel
                    JPanel p2 = formatLable(out);

                    p.setLayout(new BorderLayout());
                    JPanel right = new JPanel(new BorderLayout());
                    right.add(p2, BorderLayout.LINE_END);
                    vertical.add(right);
                    vertical.add(Box.createVerticalStrut(20));
                    p.add(vertical, BorderLayout.PAGE_START);

                    // Send the message to the server
                    try {
                        writer.write(out);
                        writer.write("\r\n");
                        writer.flush();
                    } catch (Exception ea) {
                        ea.printStackTrace();
                    }

                    text.setText(""); // Clear the text field after sending the message

                    F.repaint();
                    F.invalidate();
                    F.validate();
                    JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                    verticalScrollBar.setValue(verticalScrollBar.getMaximum()); // Scroll to the bottom of the panel
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        F.setVisible(true); // Make the main frame visible
    }

    String fileName;

    private void sendFile(File file) {
        try {
            byte[] fileData = Files.readAllBytes(file.toPath());
            fileName = file.getName();
            String data = "Data File:" + Base64.getEncoder().encodeToString(fileData);
            String message = name + "<br>" + "FILE:" + fileName;

            // Display the filename in the GUI
            String out = name + "<br>Sent a file: " + fileName;
            JPanel p2 = formatLable(out);

            p.setLayout(new BorderLayout());
            JPanel right = new JPanel(new BorderLayout());
            right.add(p2, BorderLayout.LINE_END);
            vertical.add(right);
            vertical.add(Box.createVerticalStrut(20));
            p.add(vertical, BorderLayout.PAGE_START);


            F.repaint();
            F.invalidate();
            F.validate();
            JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
            verticalScrollBar.setValue(verticalScrollBar.getMaximum()); // Scroll to the bottom of the panel
            try {
                writer.write(message);
                writer.write("\r\n");
                writer.flush();
                writer.write(data);
                writer.flush();


            } catch (Exception ea) {
                ea.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to set up the network connection with the server
    private void setupNetworkConnection() {
        try {
            Socket socket = new Socket("localhost", 1234); // Connect to the server on localhost and port 1234
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // Initialize output stream
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Initialize input stream

            Thread receiveThread = new Thread(this); // Create a new thread to handle receiving messages
            receiveThread.start(); // Start the thread (which will call the run method)
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to format and display the message in a JPanel
    public static JPanel formatLable(String out) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Create a label to display the message using HTML to format the content
        JLabel label = new JLabel("<html><p style=\"width: 150px\">" + out + "</p></html>");
        label.setFont(new Font("Sans-Serif", Font.PLAIN, 18));
        label.setBackground(new Color(37, 211, 102));
        label.setOpaque(true);
        label.setBorder(new EmptyBorder(5, 5, 5, 30));
        panel.add(label);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        JLabel time = new JLabel();

        time.setText(sdf.format(cal.getTime()));

        // Create a label to display the timestamp of the message
        time.setOpaque(true);
        time.setBorder(new EmptyBorder(1, 2, 2, 200));
        panel.add(time);
        return panel;
    }
    FileOutputStream outputStream;
    // Runnable interface method that runs in the receiving messages thread
    public void run() {
        try {
            String msg;
            while (true) {
                msg = reader.readLine(); // Read a message from the input stream
                if (!msg.contains("Data File:")){
                    saveChatHistory(name, msg);
                }

                if (msg.contains(name)) {
                    continue; // Skip processing if the message contains the client's own name
                }
                // Add the vertical box to the main panel
                if (msg.contains("Data")) {
                    // Extract the filename and file data from the message
                    int DataIndex = msg.indexOf("Data");
                    System.out.println(DataIndex);
                    String fileData = msg.substring(DataIndex+10);

                    // Decode the Base64-encoded file data to byte array
                    byte[] decodedFileData = Base64.getDecoder().decode(fileData);

                    File destinationFile = new File(fileName);

                    try {

                        // Write the decoded file data to the destination file
                         outputStream = new FileOutputStream(destinationFile);
                        outputStream.write(decodedFileData);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    F.repaint();
                    F.invalidate();
                    F.validate();
                    JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                    verticalScrollBar.setValue(verticalScrollBar.getMaximum());

                } else {
                    // Handle other types of messages as before
                    JPanel panel = formatLable(msg); // Format the message into a JPanel

                    JPanel left = new JPanel(new BorderLayout()); // Create a panel to align the message to the left
                    left.add(panel, BorderLayout.LINE_START); // Add the formatted message to the left panel
                    vertical.add(left); // Add the left panel to the vertical box
                    vertical.add(Box.createVerticalStrut(20));
                    p.add(vertical, BorderLayout.PAGE_START);
                    F.repaint();
                    F.invalidate();
                    F.validate();
                    JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                    verticalScrollBar.setValue(verticalScrollBar.getMaximum()); // Scroll to the bottom of the panel
                    // ...
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ActionListener interface method for handling events (not implemented in this
    // code)
    public void actionPerformed(ActionEvent e) {
    }
}
