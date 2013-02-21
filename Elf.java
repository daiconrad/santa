import java.util.Queue;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class Elf extends Creature {
	private final Queue<Elf> queue;
	private final Random random = new Random();

	public Elf(int id, Queue<Elf> queue,
			CyclicBarrier line, CyclicBarrier hold, Semaphore invite) {
		super(id, "Elf", "work", line, hold, invite);
		this.queue = queue;
	}

	@Override public int time() {
		return (random.nextInt(14) + 1) * 10;
	}

	@Override public void queueUp() {
		queue.add(this);
	}
}
