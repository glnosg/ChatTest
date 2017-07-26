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
            ServerSocket serverSocket = new ServerSocket(40123, 50);

            Thread heartbeat = new Thread (new Heartbeat());
            heartbeat.start();

            while(true) {
                Socket clientSocket = serverSocket.accept();

                ConnectedClient connectedClient = new ConnectedClient(clientSocket);
                connectedClientsList.add(connectedClient);

                Thread clientHandler = new Thread(new ClientHandler(connectedClient));
                clientHandler.start();
            }
        } catch (IOException ioException) {
            System.out.println("Server ended the connection!");
            ioException.printStackTrace();
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
                message = in.readLine();
                while ((message != null) && !message.equals("h|b")) {
                    System.out.println("Message received: " + message);
                    sendToAll(message);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public class Heartbeat implements Runnable {

        private BufferedReader in;
        private PrintWriter out;

        public void run() {
            System.out.println("HB thread on");
            while (true) {
                oneBeat();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }

        private void oneBeat () {
            sendHb();
            checkHb();
        }

        private void sendHb() {
            for (ConnectedClient currentClient : connectedClientsList) {
                out = currentClient.getOutputStream();
                out.println("h|b");
                System.out.println("msg sent");
            }
        }

        private void checkHb(){

            String hbMsg = null;

            for (int i = 0; i < connectedClientsList.size(); i++) {

                in = connectedClientsList.get(i).getInputStream();

                try {
                    hbMsg = in.readLine();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                if (hbMsg != null) {
                    System.out.println("Client " + i + " received hb message: " + hbMsg);
                } else {
                    connectedClientsList.get(i).closeClientSocket();
                    connectedClientsList.remove(i);
                    System.out.println("Client " + i + " has been disconnected");
                }
            }
        }
    }
}
