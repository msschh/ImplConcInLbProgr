package connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer implements Runnable {

    public static final int PORT = 5823;
    
    protected boolean running;
    protected ServerSocket serverSocket;
    protected final int port;
    protected ChatConnectionListener connectionListener;

    public ChatServer(int port) {
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
                FixedChatConnection fcc = new FixedChatConnection(s);
                new Thread(fcc).start();
                if (this.connectionListener != null) {
                    this.connectionListener.onConnection(fcc);
                }
            }
        } catch (IOException ex) {
        }
    }

}
