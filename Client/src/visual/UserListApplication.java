package visual;

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
        Parent root = loader.load();
        userListStage.setTitle("List of users");
        userListStage.setScene(new Scene(root, 800, 500));
        userListStage.show();
        usersListView = new ListView<String>();
        populateUserListView("");
    }

    @FXML
    private void updateUsersList(ActionEvent event) {
        String username = this.usernameTextField.getText();
        populateUserListView(username);
    }

    private void populateUserListView(String username) {
        Object[] parameters = new Object[]{username};
        this.client.call("getUsers", parameters, true, socket -> {
            try {
                List<User> usersList = Protocol.readResult(socket.getInputStream());
                ObservableList<String> items = FXCollections.observableArrayList(usersList.stream().map(User::getUsername).collect(Collectors.toList()));
                usersListView.setItems(items);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(LoginApplication.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

}