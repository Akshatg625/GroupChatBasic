package sample;

import javafx.application.Platform;
import sample.Client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class TaskReadThread implements Runnable {
    //private variables
    Socket socket;
    Client client;
    DataInputStream input;

    //constructor
    public TaskReadThread(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;
    }

    @Override
    public void run() {
        //continuously loop it
        while (true) {
            try {
                //Create data input stream
                input = new DataInputStream(socket.getInputStream());

                //get input from the client
                String message = input.readUTF();

                //append message of the Text Area of UI (GUI Thread)
                Platform.runLater(() -> {
                    //display the message in the textarea
                    client.txtAreaDisplay.appendText(message + "\n");
                });
            } catch (IOException ex) {
                System.out.println("Error reading from server: " + ex.getMessage());
                ex.printStackTrace();
                break;
            }
        }
    }
}