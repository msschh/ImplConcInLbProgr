package networking;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;

public class CommandClient {

    private String ip;
    private int port;

    public CommandClient() {
    }

    public CommandClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
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

    public void call(String command) {
        this.call(command, false);
    }

    public void call(String command, boolean wait) {
        this.call(command, null, wait);
    }

    public void call(String command, Object[] parameters, boolean wait) {
        this.call(command, parameters, wait, (s) -> {
        });
    }

    public void call(String command, ExecuteListener listener) {
        this.call(command, false, listener);
    }

    public void call(String command, boolean wait, ExecuteListener listener) {
        this.call(command, null, wait, listener);
    }

    public void call(String command, Object[] parameters, ExecuteListener listener) {
        this.call(command, parameters, false, listener);
    }

    public void call(String command, Object[] parameters, boolean wait, ExecuteListener listener) {
        Runnable r = () -> {
            try (Socket s = new Socket(ip, port)) {
                OutputStream out = s.getOutputStream();
                out.write(command.getBytes());
                out.write(0);

                if (parameters != null && parameters.length > 0) {
                    ObjectOutputStream oos = new ObjectOutputStream(out);
                    oos.writeUnshared(parameters);
                }

                listener.execute(s);
            } catch (IOException ex) {
                Protocol.LOG.log(Level.SEVERE, null, ex);
            }
        };
        if (!wait) {
            new Thread(r).start();
        } else {
            r.run();
        }
    }
}
