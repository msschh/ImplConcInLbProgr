package connection;

@FunctionalInterface
public interface ChatConnectionListener {
    void onConnection(FixedChatConnection connection);
}
