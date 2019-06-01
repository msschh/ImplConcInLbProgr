package connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class FixedChatConnection implements Runnable {

    private final Socket socket;
    protected boolean running;
    protected OutputStream outputStream;
    protected ChatMessageListener messageListener;

    public FixedChatConnection() {
        this.socket = null;
    }

    public FixedChatConnection(Socket socket) {
        this.socket = socket;
    }

    public ChatMessageListener getMessageListener() {
        return messageListener;
    }

    public void setMessageListener(ChatMessageListener messageListener) {
        this.messageListener = messageListener;
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
    }

}
