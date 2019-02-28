package application;

import java.io.DataInputStream;
import java.io.EOFException;
import networking.CommandClient;
import networking.CommandServer;
import server.ServerCommands;

public class Application {

    public static void main(String[] args) {
        CommandServer commandServer = new CommandServer(2317);
        ServerCommands commands = new ServerCommands();
        commandServer.setCommands(commands);
        new Thread(commandServer).start();

        CommandClient cc = new CommandClient("localhost", 2317);
        Object[] params = new Object[]{"test", "test"};
        cc.call("loginUser", params, true, socket -> {
            try {
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                System.out.println("Logged user id: " + dis.readInt());
            } catch (EOFException e) {
                System.out.println("Login failed");
            }
        });
    }
}
