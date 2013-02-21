import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;

public class Santa implements Runnable {
	public static final String MEET = "meeting in the study";
	public static final String DELIVER = "delivering presents";

	private final Queue<Elf> elves;
	private final Queue<Reindeer> reindeer;
	private final CyclicBarrier meeting, delivery;
	
	public Santa(Queue<Elf> elves, Queue<Reindeer> reindeer,
			CyclicBarrier meeting, CyclicBarrier delivery) {
		this.elves = elves; this.reindeer = reindeer;
		this.meeting = meeting; this.delivery = delivery;
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
		} catch (InterruptedException|BrokenBarrierException e) {
			System.out.println(this + " " + e.getClass().getSimpleName());
			Thread.currentThread().interrupt();
		}
	}

	private void deliverPresents() throws InterruptedException, BrokenBarrierException {
		process(reindeer, Xmas.MIN_REINDEER, DELIVER, delivery);
	}

	private void holdMeeting() throws InterruptedException, BrokenBarrierException {
		process(elves, Xmas.MIN_ELVES, MEET, meeting);
	}

	private void process(Queue<? extends Creature> queue,
			int count, String action, CyclicBarrier done)
				throws InterruptedException, BrokenBarrierException {
		final List<Creature> list = new ArrayList<Creature>();
		for (int i = 0; i < count; ++i) list.add(queue.remove());
		System.out.println(Util.join(list) + ", and " + this + " " + action);
		Thread.sleep(5);
		int waiting = done.getNumberWaiting();
		if (waiting == count) {
			System.out.println("Ho! Ho! Ho! All finished!");
		} else {
			System.out.printf("And yet, only %d of %d are waiting...%n", waiting, count);
		}
		try {
			done.await(54, TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			System.out.println("Santa is sad :(");
		}
	}
}
