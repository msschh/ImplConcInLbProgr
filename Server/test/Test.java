
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Test {

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 510; i++) {
            new Thread(Test::startConnection, "Connection " + (i + 1)).start();
        }
    }

    static int var;

    private static void startConnection() {
        try {
            System.out.println(++var);
            Socket s = new Socket("localhost", 80);
            OutputStream out = s.getOutputStream();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            ps.println("GET / localhost");
            ps.println("Host: localhost");
            ps.println();

            byte[] toByteArray = baos.toByteArray();
            for (byte b : toByteArray) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
                }
                out.write(b);
            }

            InputStream is = s.getInputStream();

            while (true) {
                int t = is.read();
                if (t < 0) {
                    break;
                }
            }

            s.close();
        } catch (Exception e) {
        }

        System.out.println(--var);
    }
}
