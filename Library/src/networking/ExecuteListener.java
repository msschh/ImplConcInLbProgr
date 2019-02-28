package networking;

import java.io.IOException;
import java.net.Socket;

@FunctionalInterface
public interface ExecuteListener {

    void execute(Socket s) throws IOException;
}
