package com.glnosg.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class HeartbeatServer {

    ArrayList<ConnectedClient> connectedClientsList = new ArrayList<>();

    public static void main(String[] args) {
        new HeartbeatServer().setUp();
    }

    public void setUp() {
        try {
            ServerSocket serverSocket = new ServerSocket(40123);

            while(true) {
                Socket clientSocket = serverSocket.accept();

                ConnectedClient connectedClient = new ConnectedClient(clientSocket);
                connectedClientsList.add(connectedClient);

                Thread clientHandler = new Thread(new ClientHandler(connectedClient));
                clientHandler.start();
            }
        } catch (IOException ioe) {
            System.out.println("IO error when opening a socket");
            ioe.printStackTrace();
        }
    }

    public void sendToAll(String message) {
        for (ConnectedClient currentClient : connectedClientsList) {
            PrintWriter out = currentClient.getOutputStream();
            out.println(message);
        }
    }

    public class ClientHandler implements Runnable {

        ConnectedClient currentClient;

        public ClientHandler(ConnectedClient client) {
            currentClient = client;
        }

        public void run() {

            BufferedReader in = currentClient.getInputStream();
            String message;

            try {
                while ((message = in.readLine()) != null) {
                    System.out.println("Message received: " + message);
                    sendToAll(message);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
