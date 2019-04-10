package connection;

@FunctionalInterface
public interface ChatMessageListener {
    void onMessage(String message);
}
