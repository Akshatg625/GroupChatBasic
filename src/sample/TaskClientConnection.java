package sample;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javafx.application.Platform;
import sample.Server;

public class TaskClientConnection implements Runnable{

    private Socket socket;
    private Server server;
    DataOutputStream output;
    DataInputStream input;

    public TaskClientConnection(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public void sendMessage(String message) {
        try {

            output.writeUTF(message);
            output.flush();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Create data input and output streams
            input = new DataInputStream(
                    socket.getInputStream());
            output = new DataOutputStream(
                    socket.getOutputStream());

            while (true) {
                // Get message from the client
                String message = input.readUTF();

                //send message via server broadcast
                server.broadcast(message);

                //append message of the Text Area of UI (GUI Thread)
                Platform.runLater(() -> {
                    server.txtAreaDisplay.appendText(message + "\n");
                });
            }



        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

    }

}
