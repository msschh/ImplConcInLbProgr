/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visual;

import connection.FixedChatConnection;
import java.net.URL;
import java.util.List;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import networking.CommandClient;
import networking.Protocol;

public class ChatApplication extends Application {

    private final FixedChatConnection chatConnection;

    @FXML
    private TextArea messages;
    @FXML
    private TextArea message;
    @FXML
    private Button send;

    private final CommandClient client;

    public ChatApplication(CommandClient client, FixedChatConnection chatConnection) {
        this.client = client;
        this.chatConnection = chatConnection;
    }

    public CommandClient getClient() {
        return client;
    }

    public FixedChatConnection getChatConnection() {
        return chatConnection;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL resource = getClass().getResource("/visual/Chat.fxml");
        FXMLLoader loader = new FXMLLoader(resource);
        loader.setController(this);
        Parent root = loader.load();
        primaryStage.setTitle("Chat: " + this.chatConnection.getYou());
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
        primaryStage.setOnShown(this::wondowShown);

        this.chatConnection.setMessageListener(message -> {
            this.messages.appendText(message);
            this.messages.appendText("\n");
        });
    }

    private void wondowShown(WindowEvent event) {
        Object[] params = new Object[]{};
        this.client.call("getMessages", params, s -> {
            try {
                List<String> msgs = Protocol.readResult(s.getInputStream());
                for (String msg : msgs) {
                    this.chatConnection.getMessageListener().onMessage(msg);
                }
            } catch (ClassNotFoundException e) {
            }
        });
    }

    @Override
    public void stop() throws Exception {
        super.stop(); //To change body of generated methods, choose Tools | Templates.
        this.chatConnection.stop();
    }

    @FXML
    private void onSend(ActionEvent event) {
        String message = this.message.getText();
        this.message.setText("");
        this.chatConnection.sendMessage(message);
        this.chatConnection.getMessageListener().onMessage(message);
    }
}
