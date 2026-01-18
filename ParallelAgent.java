package test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ParallelAgent implements Agent {

    private final Agent inner;
    private final BlockingQueue<Runnable> queue;

    private final Thread worker;
    private volatile boolean running;

    public ParallelAgent(Agent inner) {
        if (inner == null) throw new IllegalArgumentException("inner agent cannot be null");
        this.inner = inner;

        this.queue = new ArrayBlockingQueue<>(1024);
        this.running = true;

        this.worker = new Thread(() -> {
            while (running) {
                try {
                    Runnable task = queue.take();
                    task.run();
                } catch (InterruptedException e) {
                    // allow exit when closing
                } catch (Exception ignored) {
                    // avoid killing the thread if callback throws
                }
            }
        });

        this.worker.start();
    }

    @Override
    public String getName() {
        return inner.getName();
    }

    @Override
    public void reset() {
        inner.reset();
    }

    @Override
    public void callback(String topic, Message msg) {
        if (!running) return;

        // capture values for async execution
        Runnable task = () -> inner.callback(topic, msg);

        // don't block forever; if full, just drop (prevents deadlocks)
        queue.offer(task);
    }

    @Override
    public void close() {
        if (!running) return;

        running = false;
        worker.interrupt();
        try {
            worker.join(1000);
        } catch (InterruptedException ignored) {}

        // close underlying agent
        inner.close();
    }
}
