package visual;

import java.io.EOFException;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import networking.CommandClient;
import networking.Protocol;

public class LoginApplication extends Application {

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    private final CommandClient client = new CommandClient("localhost", Protocol.PORT);

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL resource = getClass().getResource("/visual/FXML.fxml");
        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();
        primaryStage.setTitle("Registration Form FXML Application");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
    }

    @FXML
    private void login(ActionEvent event) {
        String u = this.username.getText();
        String p = this.password.getText();

        Object[] parameters = new Object[]{u, p};
        this.client.call("loginUser", parameters, socket -> {
            try {
                JOptionPane.showMessageDialog(null, Protocol.readResult(socket.getInputStream()));
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(LoginApplication.class.getName()).log(Level.SEVERE, null, ex);
            } catch (EOFException e) {
                JOptionPane.showMessageDialog(null, "Wrong username or password");
            }
        });
    }

    public static void main(String args[]) {
        Application.launch(args);
    }
}
