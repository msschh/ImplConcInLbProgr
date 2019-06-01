package application;

import connection.ChatServer;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.stage.Stage;
import visual.ChatApplication;
import visual.LoginApplication;

public class Main {

    public static void main(String[] args) {
        try {
            try (final DatagramSocket socket = new DatagramSocket()) {
                socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                System.out.println(socket.getLocalAddress().getHostAddress());
            }
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

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
