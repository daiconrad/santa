import java.util.Random;

public class Elf extends Creature implements Comparable<Elf> {
	private final NotifyQueue<Elf> queue;
	private final Random random = new Random();

	public Elf(int id, NotifyQueue<Elf> queue) {
		super(id, "Elf");
		this.queue = queue;
	}

	@Override public int getTime() {
		return (random.nextInt(14) + 1) * 10;
	}

	@Override public void queueUp() {
		queue.add(this);
	}

	@Override public int compareTo(Elf that) {
		return this.id - that.id;
	}
}
