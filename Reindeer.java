import java.util.Queue;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class Reindeer extends Creature {
	private final Queue<Reindeer> queue;
	private final Random random = new Random();

	public Reindeer(int id, Queue<Reindeer> queue,
			CyclicBarrier line, CyclicBarrier hold, Semaphore harness) {
		super(id, "Reindeer", "relax", line, hold, harness);
		this.queue = queue;
	}

	@Override public int time() {
		return 3650 + (random.nextInt(10) + 1);
	}

	@Override public void queueUp() {
		queue.add(this);
	}
}
