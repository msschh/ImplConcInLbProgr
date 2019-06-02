package connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import model.User;
import utilities.SimpleListener;

public class FixedChatConnection implements Runnable {

    protected Socket socket;
    protected final User you;
    protected final User other;
    protected boolean running;
    protected OutputStream outputStream;
    protected ChatMessageListener messageListener;
    protected SimpleListener<Void> connectionLostListener;

    public FixedChatConnection(User you, User other) {
        this.you = you;
        this.other = other;
    }

    public FixedChatConnection(Socket socket, User you, User other) {
        this(you, other);
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public User getYou() {
        return you;
    }

    public User getOther() {
        return other;
    }

    public ChatMessageListener getMessageListener() {
        return messageListener;
    }

    public void setMessageListener(ChatMessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public SimpleListener<Void> getConnectionLostListener() {
        return connectionLostListener;
    }

    public void setConnectionLostListener(SimpleListener<Void> connectionLostListener) {
        this.connectionLostListener = connectionLostListener;
    }
    
    public void stop() {
        this.running = false;
    }

    /**
     *
     * @return true if sent false otherwise. Call again when reconnected.
     */
    public boolean sendMessage(String message) {
        try {
            this.outputStream.write(message.getBytes());
            this.outputStream.write(0);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    @Override
    public void run() {
        this.running = true;
        try (Socket s = this.socket) {
            this.outputStream = s.getOutputStream();

            StringBuilder sb = new StringBuilder();
            InputStream is = s.getInputStream();
            int t;
            while (this.running) {
                t = is.read();

                if (t < 0) {
                    break;
                }

                if (t == 0) {
                    if (this.messageListener != null) {
                        this.messageListener.onMessage(sb.toString());
                    }
                    sb.delete(0, sb.length());
                } else {
                    sb.append((char) t);
                }
            }
        } catch (IOException ex) {
        }
        
        this.connectionLostListener.execute(null);
    }

}
