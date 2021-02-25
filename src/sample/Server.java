package sample;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import sample.Connection;

public class Server extends Application {

    public TextArea txtAreaDisplay;
    List<TaskClientConnection> connectionList = new ArrayList<TaskClientConnection>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stg) throws Exception {

        txtAreaDisplay = new TextArea();
        txtAreaDisplay.setEditable(false);

        ScrollPane scrollPane = new ScrollPane();   //pane to display text messages
        scrollPane.setContent(txtAreaDisplay);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        // Create a scene and place it in the stage
        Scene scene = new Scene(scrollPane, 330, 600);
        stg.setTitle("Server: JavaFx Text Chat App"); // Set the stage title
        stg.setScene(scene); // Place the scene in the stage
        stg.show(); // Display the stage
        stg.setTitle("Basic Group Chat Application"); // Set the stage title
        stg.setScene(scene); // Place the scene in the stage
        stg.show(); // Display the stage

        // create a new thread
        {
            new Thread(() -> {
                try {
                    // Create a server socket
                    ServerSocket serverSocket = new ServerSocket(Connection.port);

                    //append message of the Text Area of UI (GUI Thread)
                    Platform.runLater(()
                            -> txtAreaDisplay.appendText("New server started at " + new Date() + '\n'));

                    //continous loop
                    while (true) {
                        // Listen for a connection request, add new connection to the list
                        Socket socket = serverSocket.accept();
                        TaskClientConnection connection = new TaskClientConnection(socket, this);
                        connectionList.add(connection);

                        //create a new thread
                        Thread thread = new Thread(connection);
                        thread.start();

                    }
                } catch (IOException ex) {
                    Platform.runLater(()
                            -> txtAreaDisplay.appendText(ex.toString() +'\n'));
                }
            }).start();
        }
    }

    public void broadcast(String message) {
        for (TaskClientConnection clientConnection : this.connectionList) {
            clientConnection.sendMessage(message);
        }
    }

}