package connection;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ChatConnection extends FixedChatConnection {

    private String ip;
    private int port;

    public ChatConnection() {
    }

    public ChatConnection(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public ChatConnection(String ip, int port, ChatMessageListener messageListener) {
        this.ip = ip;
        this.port = port;
        this.messageListener = messageListener;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        this.running = true;
        while (this.running) {
            try (Socket socket = new Socket(this.ip, this.port)) {
                this.outputStream = socket.getOutputStream();

                StringBuilder sb = new StringBuilder();
                InputStream is = socket.getInputStream();
                int t;
                while (this.running) {
                    t = is.read();
                    
                    if (t < 0) {
                        break;
                    }
                    
                    if (t == 0) {
                        this.messageListener.onMessage(sb.toString());
                        sb.delete(0, sb.length());
                    } else {
                        sb.append((char) t);
                    }
                }
            } catch (IOException ex) {
            } finally {
                this.outputStream = null;
            }
        }
    }
}
