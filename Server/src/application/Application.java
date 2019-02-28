package application;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        
        params = new Object[]{null};
        cc.call("searchUser", params, true, socket -> {
            try {
                ObjectInputStream dis = new ObjectInputStream(socket.getInputStream());
                while (true) {
                    Object object = dis.readUnshared();
                    System.out.println(object);
                }
            } catch (EOFException e) {
                System.out.println("Done listing all users");
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        });
    }
}
