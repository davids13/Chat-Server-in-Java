package org.academiadecodigo.bootcamp.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    private BufferedReader termianlIn;
    private PrintWriter termainlOut;
    private BufferedReader inputFromTheServer;
    private PrintWriter outputToServer;
    private final int PORT = 8888;
    private Socket clientSocket;

    public Client() {
        connections();
    }

    private void connections() {

        try{
            //client socket
            clientSocket = new Socket(InetAddress.getLocalHost(), PORT);
            System.out.println("Client Socket has been established");

            //server connection
            inputFromTheServer = new BufferedReader((new InputStreamReader(clientSocket.getInputStream())));
            outputToServer = new PrintWriter(clientSocket.getOutputStream(), true);

            //terminal connection
            termianlIn = new BufferedReader(new InputStreamReader(System.in));
            termainlOut = new PrintWriter(clientSocket.getOutputStream(), true);
        }
        catch(Exception ex) {
            System.out.println("Connection fails - more info: " + ex.getMessage());
        }
    }

    public void start() {

        String str;

        //create thread pool fixed
        ExecutorService fixedPool = Executors.newFixedThreadPool(1);

        while(true) {
            try{
                fixedPool.submit(new MyThread());
            }
            catch(Exception ex) {
                System.out.println("Failed to create a fixed thread pool: " + ex.getMessage());
            }

            try {
                //blocking method
                str = termianlIn.readLine();

                //send to terminal
                outputToServer.println(str);
            }
            catch (Exception ex) {
                System.out.println("Failed to read and/or write: " + ex.getMessage());
            }
        }

        //fixedPool.shutdown();
    }

    public class MyThread implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    //blocking method
                    String str = inputFromTheServer.readLine();

                    //write to terminal
                    System.out.println(str);
                }
                catch (IOException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }
        }
    }
}
