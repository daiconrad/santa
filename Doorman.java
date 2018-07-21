import static java.util.Objects.requireNonNull;

import java.util.Queue;
import java.util.concurrent.TransferQueue;

public class Doorman<T> implements Runnable {
	private final TransferQueue<T> sourceQueue;
	private final Queue<T> sinkQueue;
    private final Runnable action;
    private final String name;
    private final int count;

	public Doorman(int count, String name, TransferQueue<T> sourceQueue, Queue<T> sinkQueue, Runnable action) {
        this.count = requirePositive(count);
        this.name = requireNonNull(name);
		this.sourceQueue = requireNonNull(sourceQueue);
		this.sinkQueue = requireNonNull(sinkQueue);
        this.action = requireNonNull(action);
	}

    private final int requirePositive(int x) {
        if (x <= 0) throw new ArithmeticException(x + " must be greater than zero");
        return x;
    }

	@Override
    public void run() {
        try {
            while (true) {
                T individual = sourceQueue.take();
                System.out.format("%s welcomes %s to Santa's study%n", this, individual);
                sinkQueue.add(individual);
                if (sinkQueue.size() == count) {
                    System.out.format("%s wakes up Santa%n", this, individual);
                    action.run();
                    System.out.format("%s waits for the queue to empty%n", this, individual);
                    while (!sinkQueue.isEmpty()) {
                        if (Thread.interrupted()) break;
                        Thread.onSpinWait();
                    }
                    System.out.format("%s is ready to accept%n", this, individual);
                }
            }
        } catch (InterruptedException e) {
            System.out.format("%s retiring for the evening%n", name);
        }
	}

    @Override
    public String toString() { return name; }
}
