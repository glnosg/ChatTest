package com.glnosg.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectedClient {

    //private String userName;

    private Socket clientSocket;
    private BufferedReader inputStream;
    private PrintWriter outputStream;

    public ConnectedClient (Socket s) {
        clientSocket = s;
        setInputStream();
        setOutputStream();
    }

    private void setInputStream() {
        try {
            inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void setOutputStream() {
        try {
            outputStream = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public BufferedReader getInputStream () {
        return inputStream;
    }

    public PrintWriter getOutputStream() {
        return outputStream;
    }
}
