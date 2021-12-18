package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public AnchorPane ap_main; //allows to override the method initialize
//setting up widgets
    @FXML
    private Button button_send;
    @FXML
    private TextField tf_message;
    @FXML
    private VBox vbox_messages;
    @FXML
    private ScrollPane sp_main;

    private Server server;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try{
            server = new Server(new ServerSocket(1234));
            /* listens to incoming connection and*/
            /* creates a socket to communicate when it receives one */
        } catch(IOException e) { //print a message if application catches an error
            e.printStackTrace();
            System.out.println("Error creating server!");
        }

        vbox_messages.heightProperty().addListener((observable, oldValue, newValue) -> {
            sp_main.setVvalue((Double) newValue);
        /* makes it so the application automatically scrolls to the bottom when a message comes */
        });

        server.receiveMessageFromClient(vbox_messages);
        /* we use another thread because waiting a method is a blocking operation */
        /* and we want to do other things in the meanwhile, like sending a message */

        button_send.setOnAction(event -> {
            String messageToSend = tf_message.getText();
            if (!messageToSend.isEmpty()){
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER_RIGHT);
                hBox.setPadding(new Insets(5,5,5,10));

                Text text = new Text(messageToSend);
                TextFlow textFlow = new TextFlow(text);

                textFlow.setStyle("-fx-color: rgb(255,255,255); " +
                        "-fx-background-color: rgb(0,97,242);" + //green looked horrible
                        "-fx-background-radius: 20px");

                textFlow.setPadding(new Insets(5,10,5,10));
                text.setFill(Color.color(1.0,1.0,1.0));
                //we have absolutely no idea why setFill doesn't work on server side yet it does on client side
                //edit: this now works and we still have no idea why

                hBox.getChildren().add(textFlow);
                vbox_messages.getChildren().add(hBox);

                server.sendMessageToClient(messageToSend);
                tf_message.clear();
            }
        });

    }

    public static void addLabel(String messageFromClient, VBox vbox) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5,5,5,10));

        Text text = new Text(messageFromClient);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: rgb(208,204,204)" +
                ";-fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5,5,5,10));
        hBox.getChildren().add(textFlow);

        Platform.runLater(() -> vbox.getChildren().add(hBox));
    }
}
