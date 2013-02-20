import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Santa implements Runnable {
	public static final String MEET = "meeting in the study";
	public static final String DELIVER = "delivering presents";

	private final Queue<Elf> elves;
	private final Queue<Reindeer> reindeer;
	
	public Santa(Queue<Elf> elves, Queue<Reindeer> reindeer) {
		this.elves = elves; this.reindeer = reindeer;
	}

	@Override public String toString() { return "Santa"; }

	private boolean elvesReady() {
		return elves.size() >= Xmas.MIN_ELVES;
	}

	private boolean reindeerReady() {
		return reindeer.size() >= Xmas.MIN_REINDEER;
	}

	private boolean ready() {
		return elvesReady() || reindeerReady();
	}

	@Override public void run() {
		try {
			synchronized (this) {
				while (true) {
					do {
						this.wait();
					} while (!ready());
					while (reindeerReady()) deliverPresents();
					while (elvesReady()) holdMeeting();
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private void deliverPresents() {
		process(reindeer, Xmas.MIN_REINDEER, DELIVER);
	}

	private void holdMeeting() {
		process(elves, Xmas.MIN_ELVES, MEET);
	}

	private void process(Queue<? extends Creature> queue, int count, String action) {
		final List<Creature> list = new ArrayList<Creature>();
		for (int i = 0; i < count; ++i) list.add(queue.remove());
		System.out.println(Util.join(list) + ", and " + this + " " + action);
	}
}
