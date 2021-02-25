package sample;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.Connection;

public class Client extends Application {
    TextField txtName;
    TextField txtInput;
    ScrollPane scrollPane;
    public TextArea txtAreaDisplay;

    DataOutputStream output = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stg) throws Exception {
        VBox vBox = new VBox();

        scrollPane = new ScrollPane();   //pane to display text messages
        HBox hBox = new HBox(); //pane to hold input textfield and send button

        txtAreaDisplay = new TextArea();
        txtAreaDisplay.setEditable(false);
        scrollPane.setContent(txtAreaDisplay);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        //define textfield and button and add to hBox
        txtName = new TextField();
        txtName.setPromptText("Name");
        txtName.setPrefWidth(50);
        txtName.setTooltip(new Tooltip("Write your name. "));
        txtInput = new TextField();
        txtInput.setPromptText("New message");
        txtInput.setTooltip(new Tooltip("Write your message. "));
        Button btnSend = new Button("Send");
        btnSend.setOnAction(new ButtonListener());

        hBox.getChildren().addAll(txtName, txtInput, btnSend);
        hBox.setHgrow(txtInput, Priority.ALWAYS);  //set textfield to grow as window size grows

        //set center and bottom of the borderPane with scrollPane and hBox
        vBox.getChildren().addAll(scrollPane, hBox);
        vBox.setVgrow(scrollPane, Priority.ALWAYS);

        //create a scene and display
        Scene scene = new Scene(vBox, 330, 600);
        stg.setTitle("Client: JavaFx Text Chat App");
        stg.setScene(scene);
        stg.show();

        stg.setScene(scene);
        stg.show();

        try {
            // Create a socket to connect to the server
            Socket socket = new Socket(Connection.host, Connection.port);

            //Connection successful
            txtAreaDisplay.appendText("Connected. \n");

            // Create an output stream to send data to the server
            output = new DataOutputStream(socket.getOutputStream());

            //create a thread in order to read message from server continuously
            TaskReadThread task = new TaskReadThread(socket, this);
            Thread thread = new Thread(task);
            thread.start();
        } catch (IOException ex) {
            txtAreaDisplay.appendText(ex.toString() + '\n');
        }

    }


    private class ButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            try {
                //get username and message
                String username = txtName.getText().trim();
                String message = txtInput.getText().trim();

                //if username is empty set it to 'Unknown'
                if (username.length() == 0) {
                    username = "Unknown";
                }
                //if message is empty, just return : don't send the message
                if (message.length() == 0) {
                    return;
                }

                //send message to server
                output.writeUTF("[" + username + "]: " + message + "");
                output.flush();

                //clear the textfield
                txtInput.clear();
            } catch (IOException ex) {
                System.err.println(ex);
            }

        }
    }

}

