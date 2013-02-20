import java.util.List;

public class Santa implements Runnable, Stateful {
	public static final String MEET = "meeting in the study";
	public static final String DELIVER = "delivering presents";

	private final Object token;
	private final NotifyQueue<Elf> elves;
	private final NotifyQueue<Reindeer> reindeer;
	
	private volatile State state;
	private volatile long latest;

	public Santa(Object token, NotifyQueue<Elf> elves,
			NotifyQueue<Reindeer> reindeer) {
		this.token = token; this.elves = elves;
		this.reindeer = reindeer;
		setState(State.UNKNOWN);
	}

	@Override public State getState() { return state; }
	@Override public void setState(State state) {
		this.state = state;
		latest = System.currentTimeMillis();
	}
	@Override public long getLatest() { return latest; }

	@Override public String toString() { return "Santa"; }

	private boolean ready() {
		boolean ready;
		synchronized (elves) {
			synchronized (reindeer) {
				ready = elves.ready() || reindeer.ready();
			}
		}
		return ready;
	}

	@Override public void run() {
		try {
			synchronized (token) {
				while (true) {
					setState(State.WAITING);
					do {
						token.wait();
					} while (!ready());
					setState(State.WAKING);
					boolean reindeerReady;
					synchronized (reindeer) {
						reindeerReady = reindeer.ready();
					}
					if (reindeerReady) doItems(reindeer, DELIVER);
					boolean elvesReady;
					synchronized (elves) {
						elvesReady = elves.ready();
					}
					if (elvesReady) doItems(elves, MEET);
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private void doItems(NotifyQueue<? extends Creature> queue, String action) {
		setState(State.GATHERING);
		final List<? extends Creature> list;
		synchronized (queue) {
			list = queue.getItems();
		}
		System.out.println(list);
		for (Creature c : list) {
			synchronized (c) {
				c.setState(State.MEETING);
			}
			System.out.printf("Got %s%n", c);
		}
		setState(State.MEETING);
		System.out.println(Util.join(list) + ", and " + this + " " + action);
		setState(State.FINISHING);
		for (Creature c : list) {
			c.setState(State.FINISHING);
			synchronized (c) {
				c.notify();
				c.fulfill();
			}
			System.out.println("released " + c);
		}
	}
}
