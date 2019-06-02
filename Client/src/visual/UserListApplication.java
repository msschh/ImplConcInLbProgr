package visual;

import connection.ChatConnection;
import connection.ChatServer;
import connection.FixedChatConnection;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import networking.CommandClient;
import networking.Protocol;

import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

public class UserListApplication extends Application {

    @FXML
    private TextField usernameTextField;
    @FXML
    private ListView usersListView;
    @FXML
    private Button usernameSearchButton;

    private final CommandClient client = new CommandClient("localhost", Protocol.PORT);

    @Override
    public void init() throws Exception {
        super.init();

    }

    @Override
    public void start(Stage userListStage) throws Exception {
        URL resource = getClass().getResource("/visual/UserListFXML.fxml");
        FXMLLoader loader = new FXMLLoader(resource);
        loader.setController(this);
        Parent root = loader.load();
        userListStage.setTitle("List of users");
        userListStage.setScene(new Scene(root, 800, 500));

        usersListView.setOnMouseClicked(this::userClicked);

        userListStage.setOnShown(this::windowShown);

        userListStage.show();
    }

    private void windowShown(WindowEvent event) {
        populateUserListView("");

        ChatServer chatServer = new ChatServer(ChatServer.PORT);
        chatServer.setConnectionListener(connextion -> { // in caz de conexiune. Aici poti sa deshizi un dialog in care retii connextion si cand apesi pe send apelezi sendMessage
            Platform.runLater(() -> {
                try {
                    ChatApplication chatApplication = new ChatApplication(connextion);
                    chatApplication.start(new Stage());
                } catch (Exception ex) {
                    Logger.getLogger(LoginApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        });
        new Thread(chatServer).start();// Devii disponibil ca server.
    }

    private void userClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            User user = (User) usersListView.getSelectionModel().getSelectedItem();
            Stage chatStage = new Stage();
            try {
                ChatConnection chatConnection = new ChatConnection(user, user.getLastIp(), ChatServer.PORT);
                ChatApplication chatApplication = new ChatApplication(chatConnection);
                chatApplication.start(chatStage);
                new Thread(chatConnection).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void updateUsersList(ActionEvent event) {
        String username = this.usernameTextField.getText();
        populateUserListView(username);
    }

    private void populateUserListView(String username) {
        Object[] parameters = new Object[]{username};
        this.client.call("getUsers", parameters, socket -> {
            try {
                final List<User> usersList = Protocol.readResult(socket.getInputStream());
                Platform.runLater(() -> {
                    ObservableList<User> items = FXCollections.observableArrayList(usersList);
                    usersListView.setItems(items);
                });
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(LoginApplication.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

}
