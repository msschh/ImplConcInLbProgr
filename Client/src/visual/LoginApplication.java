package visual;

import java.io.EOFException;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import model.User;

import networking.CommandClient;
import networking.Protocol;
import utilities.InputValidator;
import utilities.Utilities;

public class LoginApplication extends Application {

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private TextField createUsername;
    @FXML
    private PasswordField createPassword;
    @FXML
    private PasswordField createRePassword;

    private final CommandClient client = new CommandClient("localhost", Protocol.PORT);

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL resource = getClass().getResource("/visual/LoginFxml.fxml");
        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();
        primaryStage.setTitle("Chat App");
        primaryStage.setScene(new Scene(root, 800, 500));

        // cand este apasat butonul de inchidere, atunci este inchisa intreaga aplicatie, nu doar
        // fereastra de chat
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        

        primaryStage.show();
    }

    @FXML
    private void login(ActionEvent event) {
        String usernameInput = this.username.getText();
        String passwordInput = this.password.getText();
        String lastIp = Utilities.findIp();

        // valideaza username-ul
        if (!InputValidator.isUsernameValid(usernameInput)) {
            Alert usernameInvalidAlert = new Alert(AlertType.ERROR, "The username is invalid.", ButtonType.CLOSE);
            usernameInvalidAlert.show();
            return;
        }

        // valideaza parola
        if (!InputValidator.isPasswordValid(passwordInput)) {
            Alert passwordInvalidAlert = new Alert(AlertType.ERROR, "The password is invalid.", ButtonType.CLOSE);
            passwordInvalidAlert.show();
            return;
        }
        
        Object[] parameters = new Object[]{usernameInput, passwordInput, lastIp};
        this.client.call("loginUser", parameters, true, socket -> {
            try {
                // stored if we will need in the future
                Integer loggedUserId = Protocol.readResult(socket.getInputStream());
                try {
                    User user = new User(loggedUserId, usernameInput, lastIp);
                    Stage secondStage = new Stage();
                    UserListApplication userListApplication = new UserListApplication(user);
                    userListApplication.start(secondStage);

                    //close the primary scene
                    Stage stage = (Stage) username.getScene().getWindow();
                    stage.close();
                } catch (Exception ex) {
                    Logger.getLogger(LoginApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(LoginApplication.class.getName()).log(Level.SEVERE, null, ex);
            } catch (EOFException e) {
                JOptionPane.showMessageDialog(null, "Wrong username or password");
            }
        });
    }

    @FXML
    private void createAccount(ActionEvent event) {
        String usernameInput = this.createUsername.getText();
        String passwordInput = this.createPassword.getText();
        String retryPasswordInput = this.createRePassword.getText();

        // valideaza username-ul
        if (!InputValidator.isUsernameValid(usernameInput)) {
            Alert usernameInvalidAlert = new Alert(AlertType.ERROR, "The username is invalid.", ButtonType.CLOSE);
            usernameInvalidAlert.show();
            return;
        }

        // valideaza parola
        if (!InputValidator.isPasswordValid(passwordInput)) {
            Alert passwordInvalidAlert = new Alert(AlertType.ERROR, "The retry password is invalid.", ButtonType.CLOSE);
            passwordInvalidAlert.show();
            return;
        }

        // valideaza parola de retry
        if (!InputValidator.isPasswordValid(retryPasswordInput)) {
            Alert passwordInvalidAlert = new Alert(AlertType.ERROR, "The password is invalid.", ButtonType.CLOSE);
            passwordInvalidAlert.show();
            return;
        }

        if (!passwordInput.equals(retryPasswordInput)) {
            Alert alert = new Alert(AlertType.ERROR, "Passwords do not match", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        Object[] parameters = new Object[]{usernameInput, passwordInput};
        this.client.call("createUser", parameters, socket -> {
            try {
                int success = socket.getInputStream().read();
                if (success == 1) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(AlertType.INFORMATION, "User created!", ButtonType.OK);
                        alert.showAndWait();
                    });
                } else if (success == 2) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(AlertType.ERROR, "Existent username!", ButtonType.OK);
                        alert.showAndWait();
                    });
                }
                return;
            } catch (IOException ioe) {
            }
            Platform.runLater(() -> {
                Alert alert = new Alert(AlertType.ERROR, "Server related error!", ButtonType.OK);
                alert.showAndWait();
            });
        });
    }

    public static void main(String args[]) {
        Application.launch(args);
    }
}
