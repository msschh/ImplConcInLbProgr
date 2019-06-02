package dao;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DBOperations {

    ThreadPoolExecutor executor;

    public DBOperations() {
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
    }

    public void writeDB(Consumer consumer, Object object) {
        executor.execute(() -> consumer.accept(object));
    }

}
