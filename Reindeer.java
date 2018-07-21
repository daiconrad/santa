import java.util.Random;
import java.util.concurrent.TransferQueue;

public class Reindeer extends Creature {
	private final TransferQueue<Reindeer> queue;
	private final Random random = new Random();

	public Reindeer(int id, TransferQueue<Reindeer> queue) {
		super(id, "Reindeer", "relax");
		this.queue = queue;
	}

	@Override public int time() {
		return 3650 + (random.nextInt(10) + 1);
	}

	@Override public void queueUp() throws InterruptedException {
        System.out.format("%s has returned from vacation...%n", this);
		queue.transfer(this);
	}
}
