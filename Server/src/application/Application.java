package application;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import networking.CommandClient;
import networking.CommandServer;
import networking.Protocol;
import server.ServerCommands;

public class Application {

    public static void main(String[] args) {
        CommandServer commandServer = new CommandServer(Protocol.PORT);
        ServerCommands commands = new ServerCommands();
        commandServer.setCommands(commands);
        new Thread(commandServer).start();

        CommandClient cc = new CommandClient("localhost", Protocol.PORT);
        Object[] params = new Object[]{"test", "test"};
        cc.call("loginUser", params, true, socket -> {
            try {
                Integer result = Protocol.<Integer>readResult(socket.getInputStream());
                System.out.println("Logged user id: " + result);
            } catch (EOFException e) {
                System.out.println("Login failed");
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
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
