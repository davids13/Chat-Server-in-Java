package org.academiadecodigo.bootcamp.server;

import org.academiadecodigo.bootcamp.commands.Commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerChat {

    //TODO prompt ser name
    //TODO who´s online and how many
    //TODO quit command
    //TODO welcome menu
    //TODO synchronised the for each to be atomic

    private final int PORT = 8888;
    private ArrayList<ClientConnection> listOfClientsConnections;
    private ServerSocket server;
    private ExecutorService fixedPool;

    public ServerChat() {

        try {
            //create a new server
            server = new ServerSocket(PORT);
            System.out.println("Server connection established...");

            //create an interface collection to receive a list of socket client connections
            listOfClientsConnections = new ArrayList<>();

            //create a fixed thread poll
            fixedPool = Executors.newFixedThreadPool(50);
        }
        catch (IOException ex) {
            System.out.println("Server connection failed - more info: " + ex.getMessage());
        }
    }

    public void start() {

        Commands.welcomeMenu();

        while (true) {
            ClientConnection clientConnection = null;
            try {
                Socket socket = server.accept();
                clientConnection = new ClientConnection(socket);

                Thread thread = new Thread(clientConnection);
                thread.start();
            }
            catch (IOException ex) {
                System.out.println("Start connection failed: " + ex.getMessage());
            }

            listOfClientsConnections.add(clientConnection);
            fixedPool.submit(clientConnection);
        }
    }

    public void sendAll(String msg) {

        //to be atomic
        synchronized (this) {
            for (ClientConnection cc : listOfClientsConnections) {
                cc.send(msg);
            }
        }
    }

    //my runnable class
    public class ClientConnection implements Runnable {

        private Socket clientsSockets;
        private BufferedReader in;
        private PrintWriter out;

        public ClientConnection(Socket socket) {
            this.clientsSockets = socket;

            try {
                in = new BufferedReader(new InputStreamReader(clientsSockets.getInputStream()));
            }
            catch (IOException ex) {
                System.out.println("Failed to read: " + ex.getMessage());
            }

            try {
                out = new PrintWriter(clientsSockets.getOutputStream());
            }
            catch (IOException ex) {
                System.out.println("Failed to write: " + ex.getMessage());
            }
        }

        @Override
        public void run() {

            while (true) {
                try {
                    //bocking method
                    String msg = in.readLine();
                    sendAll(msg);
                }
                catch (IOException ex) {
                    System.out.println("Cannot read the msg: " + ex.getMessage());
                }
            }
        }

        private void send(String msg) {

            try {
                //output stream of the socket
                out.println("User:" + msg + " : " + Commands.showDate());
            }
            catch (Exception ex) {
                System.out.println("Cannot sending the msg: " + ex.getMessage());
            }
        }
    }
}