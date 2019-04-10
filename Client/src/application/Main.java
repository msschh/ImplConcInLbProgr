package application;

import connection.ChatConnection;
import connection.ChatServer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
//        LoginApplication.main(args);

        ChatServer chatServer = new ChatServer(4321);
        chatServer.setConnectionListener(connextion -> { // in caz de conexiune. Aici poti sa deshizi un dialog in care retii connextion si cand apesi pe send apelezi sendMessage
            connextion.setMessageListener(message -> {
                connextion.sendMessage("You said: " + message);
            });
            
            connextion.sendMessage("Hello there");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            connextion.sendMessage("How are you?");
        });
        new Thread(chatServer).start();// Devii disponibil ca server.

        ChatConnection cc = new ChatConnection("localhost", 4321);
        cc.setMessageListener(message -> {
            System.out.println("Recieved: " + message);
        });
        
        new Thread(cc).start();// Te re/conectezi in acest thread.
        
        while (!cc.sendMessage("Hello?")) { // daca sendMessage e false ii zici ca nu a fost trimis mesajul
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
