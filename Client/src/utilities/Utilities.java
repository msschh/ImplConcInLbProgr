package utilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;

public class Utilities {

    private static final TreeMap<String, Application> retrivers = new TreeMap<>();

    public static void beRetrieved(Application application) {
        List<String> params = application.getParameters().getRaw();
        synchronized (Utilities.retrivers) {
            Utilities.retrivers.put(params.get(0), application);
        }
    }

    public static <A extends Application> A appRetriver(Class<A> cls) {
        try {
            Method main = cls.getDeclaredMethod("main", String[].class);
            main.setAccessible(true);
            String token = cls.getName() + System.nanoTime();
            main.invoke(null, (Object) new String[]{token});

            while (true) {
                Thread.sleep(100);

                synchronized (Utilities.retrivers) {
                    Application app = Utilities.retrivers.get(token);
                    if (app != null) {
                        return (A) app;
                    }
                }
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InterruptedException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
