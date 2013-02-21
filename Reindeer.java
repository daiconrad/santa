import java.util.Queue;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;

public class Reindeer extends Creature implements Comparable<Reindeer> {
	private final Queue<Reindeer> queue;
	private final Random random = new Random();

	public Reindeer(int id, Queue<Reindeer> queue, CyclicBarrier line, CyclicBarrier hold) {
		super(id, "Reindeer", "relax", line, hold);
		this.queue = queue;
	}

	@Override public int time() {
		return 3650 + (random.nextInt(10) + 1);
	}

	@Override public void queueUp() {
		queue.add(this);
	}

	@Override public int compareTo(Reindeer that) {
		return this.id - that.id;
	}
}
