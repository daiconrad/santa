import java.util.Queue;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;

public class Elf extends Creature implements Comparable<Elf> {
	private final Queue<Elf> queue;
	private final Random random = new Random();

	public Elf(int id, Queue<Elf> queue, CyclicBarrier line, CyclicBarrier hold) {
		super(id, "Elf", "work", line, hold);
		this.queue = queue;
	}

	@Override public int time() {
		return (random.nextInt(14) + 1) * 10;
	}

	@Override public void queueUp() {
		queue.add(this);
	}

	@Override public int compareTo(Elf that) {
		return this.id - that.id;
	}
}
