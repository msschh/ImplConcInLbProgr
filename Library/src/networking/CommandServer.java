package networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.TreeMap;
import java.util.logging.Level;

public class CommandServer implements Runnable {

    private final int port;
    private Object commands;
    private final TreeMap<String, Method> commandMap = new TreeMap<>();
    private ServerSocket serverSocket;

    public CommandServer() {
        this(Protocol.PORT);
    }
    
    public CommandServer(int port) {
        this.port = port;
    }

    public Object getCommands() {
        return commands;
    }

    public void setCommands(Object commands) {
        this.commands = commands;
    }

    public void start() {
        new Thread(this, "Command server").start();
    }

    public void stop() {
        try {
            if (this.serverSocket != null) {
                this.serverSocket.close();
            }
        } catch (Exception e) {
        } finally {
            this.serverSocket = null;
        }
    }
    
    public boolean isRunning() {
        return this.serverSocket != null;
    }

    private void init() {
        Class c = this.commands.getClass();
        this.commandMap.clear();
        while (c != null && !c.equals(Object.class)) {
            Method[] methods = c.getDeclaredMethods();

            for (Method method : methods) {
                Command commandAnnotation = method.getDeclaredAnnotation(Command.class);

                if (commandAnnotation == null) {
                    continue;
                }

                Class<?>[] exceptionTypes = method.getExceptionTypes();
                if (exceptionTypes.length != 1 || !exceptionTypes[0].equals(IOException.class)) {
                    continue;
                }

                String commandName = commandAnnotation.value();
                if (commandName.isEmpty()) {
                    commandName = method.getName();
                }

                Class<?>[] types = method.getParameterTypes();
                if (types.length == 0 || !types[0].equals(Socket.class)) {
                    continue;
                }

                this.commandMap.put(commandName, method);
            }

            c = c.getSuperclass();
        }
    }

    @Override
    public void run() {
        this.init();

        try (ServerSocket ss = new ServerSocket(this.port)) {
            this.serverSocket = ss;

            while (true) {
                Socket s = ss.accept();

                this.startAccept(s);
            }
        } catch (IOException e) {
            Protocol.LOG.log(Level.WARNING, null, e);
        }
    }

    private void startAccept(final Socket s) {
        Runnable clientCommand = () -> {
            this.accept(s);
        };

        Thread clientCommandThraed = new Thread(clientCommand, "Client command: " + s.toString());
        clientCommandThraed.start();
    }

    private void accept(Socket socket) {
        try (Socket s = socket) {
            String command = Protocol.readString(s.getInputStream());

            Method m = this.commandMap.get(command);
            if (m == null) {
                Protocol.LOG.log(Level.SEVERE, "No command: {0}!", command);
                return;
            }
            m.setAccessible(true);

            if (m.getParameterCount() > 1) {
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

                Object[] params = (Object[]) ois.readUnshared();
                Object[] invokeParams = new Object[params.length + 1];
                invokeParams[0] = s;
                for (int i = 0; i < params.length; ++i) {
                    invokeParams[i + 1] = params[i];
                }
                m.invoke(this.commands, invokeParams);
            } else {
                m.invoke(this.commands, s);
            }
        } catch (IOException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            Protocol.LOG.log(Level.WARNING, null, e);
        }
    }
}
