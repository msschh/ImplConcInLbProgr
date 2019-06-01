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
        LoginApplication.main(args);
    }
}
