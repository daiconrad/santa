import java.util.Random;
import java.util.concurrent.TransferQueue;

public class Elf extends Creature {
	private final TransferQueue<Elf> queue;
	private final Random random = new Random();

	public Elf(int id, TransferQueue<Elf> queue) {
		super(id, "Elf", "work");
		this.queue = queue;
	}

	@Override public int time() {
		return (random.nextInt(14) + 1) * 10;
	}

	@Override public void queueUp() throws InterruptedException {
        System.out.format("%s needs Santa's help...%n", this);
		queue.transfer(this);
	}
}
