import java.util.List;

public class Santa implements Runnable {
	public static final String MEET = "meeting in the study";
	public static final String DELIVER = "delivering presents";

	private final Object token;
	private final NotifyQueue<Elf> elves;
	private final NotifyQueue<Reindeer> reindeer;
	
	public Santa(Object token, NotifyQueue<Elf> elves,
			NotifyQueue<Reindeer> reindeer) {
		this.token = token; this.elves = elves;
		this.reindeer = reindeer;
	}

	@Override public String toString() { return "Santa"; }

	private boolean ready() {
		return elves.ready() || reindeer.ready();
	}

	@Override public void run() {
		try {
			synchronized (token) {
				while (true) {
					do {
						token.wait();
					} while (!ready());
					if (reindeer.ready()) doItems(reindeer, DELIVER);
					if (elves.ready()) doItems(elves, MEET);
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private void doItems(NotifyQueue<? extends Creature> queue, String action) {
		final List<? extends Creature> list = queue.getItems();
		System.out.println(Util.join(list) + ", and " + this + " " + action);
		for (Creature c : list) { synchronized (c) { c.notify(); } }
	}
}
