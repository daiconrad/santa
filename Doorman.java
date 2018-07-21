import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TransferQueue;

public class Doorman<C extends Creature> implements Runnable {
	private final TransferQueue<C> sourceQueue;
	private final Queue<List<? extends Creature>> sinkQueue;
    private final Runnable action;
    private final String name;
    private final int count;

	public Doorman(int count, String name, TransferQueue<C> sourceQueue,
            Queue<List<? extends Creature>> sinkQueue, Runnable action) {
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
        List<C> list;
        try {
            list = new ArrayList<>();
            while (true) {
                C creature = sourceQueue.take();
                System.out.format("%s welcomes %s to Santa's study%n", name, creature);
                list.add(creature);
                if (list.size() == count) {
                    Collections.sort(list);
                    sinkQueue.add(list);
                    list = new ArrayList<>();
                    System.out.format("%s wakes up Santa%n", name);
                    action.run();
                }
            }
        } catch (InterruptedException e) {
            System.out.format("%s retiring for the evening%n", name);
        }
	}

    @Override
    public String toString() { return name; }
}
