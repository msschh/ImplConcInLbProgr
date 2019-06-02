package utilities;

@FunctionalInterface
public interface SimpleListener<P> {

    void execute(P param);
}
