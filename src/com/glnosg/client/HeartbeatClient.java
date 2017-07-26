package com.glnosg.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class HeartbeatClient {

    private JTextField outgoing;
    private JTextArea incoming;
    private JButton sendButton;

    private PrintWriter out;
    private PrintWriter hbOut;

    public static void main(String[] args) {
        new HeartbeatClient().go();
    }

    public void go() {

        JFrame frame = new JFrame("Heartbeat test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel incomingPanel = new JPanel();
        JPanel outgoingPanel = new JPanel();

        incoming = new JTextArea(15,  40);
        incoming.setEditable(false);
        JScrollPane incomingScroller = new JScrollPane(incoming);
        incomingScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        incomingScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        outgoing = new JTextField(33);

        sendButton = new JButton("Send");
        sendButton.addActionListener(new SendButtonListener());

        incomingPanel.add(incomingScroller);
        outgoingPanel.add(outgoing);
        outgoingPanel.add(sendButton);

        frame.setSize(550, 280);
        frame.setVisible(true);
        frame.add(BorderLayout.CENTER, incomingPanel);
        frame.add(BorderLayout.SOUTH, outgoingPanel);

        setUpNetworking();
    }

    public void setUpNetworking() {
        try {
            Socket clientSocket = new Socket("127.0.0.1", 40123);

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            hbOut = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Connection with server established: " + clientSocket.getInetAddress().getHostName() );

            Thread serverListener = new Thread(new ServerListener(in));
            serverListener.start();

        } catch (IOException ioe) {
            incoming.append("Couldn't set up connection");
            ioe.printStackTrace();
        }
    }

    public class SendButtonListener implements ActionListener {
        String message;
        public void actionPerformed(ActionEvent ae) {
            message = outgoing.getText();
            out.println(message);
            System.out.println("Message sent to server: " + message);
            outgoing.setText("");
            outgoing.requestFocus();
        }
    }

    public class ServerListener implements Runnable {

        BufferedReader in;

        public ServerListener (BufferedReader  br) {
            in = br;
        }

        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    if (message.equals("h|b")) {
                        out.println(message);
                    } else {
                        incoming.append(message + "\n");
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
