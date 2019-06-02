package connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;
import networking.Protocol;

public class ChatServer implements Runnable {

    public static final int PORT = 5823;

    protected boolean running;
    protected ServerSocket serverSocket;
    protected final int port;
    protected ChatConnectionListener connectionListener;
    protected final User user;

    public ChatServer(User user, int port) {
        this.user = user;
        this.port = port;
    }

    public ChatConnectionListener getConnectionListener() {
        return connectionListener;
    }

    public void setConnectionListener(ChatConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    public void stop() {
        this.running = false;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
        } finally {
            this.serverSocket = null;
        }
    }

    private void acceptConnection(Socket socket) {
        try {
            User other = Protocol.readResult(socket.getInputStream());
            FixedChatConnection fcc = new FixedChatConnection(socket, this.user, other);
            new Thread(fcc).start();
            if (this.connectionListener != null) {
                this.connectionListener.onConnection(fcc);
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        this.running = true;
        try {
            this.serverSocket = new ServerSocket(this.port);
            try (ServerSocket ss = this.serverSocket) {
                while (true) {
                    Socket s = ss.accept();
                    new Thread(() -> acceptConnection(s)).start();
                }
            }
        } catch (IOException ex) {
        }
    }

}
