import java.util.Random;

public class Reindeer extends Creature implements Comparable<Reindeer> {
	private final NotifyQueue<Reindeer> queue;
	private final Random random = new Random();

	public Reindeer(int id, NotifyQueue<Reindeer> queue) {
		super(id, "Reindeer");
		this.queue = queue;
	}

	@Override public int getTime() {
		return 3650 + (random.nextInt(10) + 1);
	}

	@Override public void queueUp() {
		synchronized (queue) {
			queue.add(this);
		}
	}

	@Override public int compareTo(Reindeer that) {
		return this.id - that.id;
	}
}
