package application;

import connection.ChatServer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.stage.Stage;
import visual.ChatApplication;
import visual.LoginApplication;

public class Main {

    public static void main(String[] args) {
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
        
        LoginApplication.main(args);
    }
}
