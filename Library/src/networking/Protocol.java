package networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.TreeMap;
import java.util.logging.Logger;

public final class Protocol {

    public static final Logger LOG = Logger.getLogger(Protocol.class.getName());

    public static final int PORT = 5738;
    public static final int NOTIFICATION_PORT = Protocol.PORT + 1;

    public static final int OK = 0;
    public static final int ERROR = 1;

    public static final int BUFFER_SIZE = 524288;
    public static final String WILDCARD = "?";
    public static final String SPLIT_PATTERN = "[/]|[\\\\]";

    private Protocol() {
    }

    public static String readString(InputStream in) throws IOException {
        StringBuilder res = new StringBuilder();

        int t;
        while (true) {
            t = in.read();

            if (t <= 0) {
                break;
            }
            
            if (Character.isSpaceChar(t)) {
                break;
            }

            res.append((char) t);
        }

        return res.toString();
    }
    
    public static void sendResult(OutputStream out, Object result) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeUnshared(result);
    }

    public static TreeMap<String, String> wildcardMatch(String path, String wild) {
        String[] p = path.split(Protocol.SPLIT_PATTERN);
        String[] w = wild.split(Protocol.SPLIT_PATTERN);

        if (p.length < w.length) {
            return null;
        }

        TreeMap<String, String> res = new TreeMap<>();
        for (int i = 0; i < w.length; ++i) {
            if (w[i].startsWith(Protocol.WILDCARD)) {
                String wildcard = w[i];
                wildcard = wildcard.substring(Protocol.WILDCARD.length());
                res.put(wildcard, p[i]);
                continue;
            }

            if (w[i].equals(p[i])) {
                continue;
            }

            return null;
        }

        return res;
    }
}
