package connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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

    @Override
    public void run() {
        this.running = true;
        try {
            this.serverSocket = new ServerSocket(this.port);
            try (ServerSocket ss = this.serverSocket) {
                Socket s = ss.accept();
                User other = Protocol.readResult(s.getInputStream());
                FixedChatConnection fcc = new FixedChatConnection(s, this.user, other);
                new Thread(fcc).start();
                if (this.connectionListener != null) {
                    this.connectionListener.onConnection(fcc);
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
        }
    }

}
