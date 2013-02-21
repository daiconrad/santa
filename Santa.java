import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class Santa implements Runnable {
	public static final String MEET = "meeting in the study";
	public static final String DELIVER = "delivering presents";

	private final Semaphore door, invite, harness;
	private final Queue<Elf> elves;
	private final Queue<Reindeer> reindeer;
	private final CyclicBarrier meeting, delivery;
	
	public Santa(Semaphore door, Queue<Elf> elves, Queue<Reindeer> reindeer,
			CyclicBarrier meeting, CyclicBarrier delivery,
			Semaphore invite, Semaphore harness) {
		this.door = door;
		this.elves = elves; this.reindeer = reindeer;
		this.meeting = meeting; this.delivery = delivery;
		this.invite = invite; this.harness = harness;
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
			while (true) {
				if (door.availablePermits() > 0) {
					System.out.println("Hmm. Someone's already at the door.");
				} else {
					System.out.println(this + " is sleeping. ZZzzz...");
				}
				door.acquire(); // sleep until someone knocks
				if (reindeerReady()) deliverPresents();
				else if (elvesReady()) holdMeeting();
			}
		} catch (InterruptedException|BrokenBarrierException e) {
			System.out.println(this + " " + e.getClass().getSimpleName());
			Thread.currentThread().interrupt();
		}
	}

	private void deliverPresents() throws InterruptedException, BrokenBarrierException {
		process(reindeer, Xmas.MIN_REINDEER, DELIVER, delivery, harness);
	}

	private void holdMeeting() throws InterruptedException, BrokenBarrierException {
		process(elves, Xmas.MIN_ELVES, MEET, meeting, invite);
	}

	private void process(Queue<? extends Creature> queue, int count,
			String action, CyclicBarrier finished, Semaphore wait)
				throws InterruptedException, BrokenBarrierException {
		final List<Creature> list = new ArrayList<Creature>();
		for (int i = 0; i < count; ++i) {
			list.add(queue.remove());
			wait.release(); // release them from waiting; Santa is ready for them
		}
		Collections.sort(list);

		System.out.println(Util.join(list) + ", and " + this + " " + action);
		Thread.sleep(5);

		int waiting = finished.getNumberWaiting();
		if (waiting == count) {
			System.out.println("Ho! Ho! Ho! All finished!");
		} else {
			System.out.printf("And yet, only %d of %d are waiting...%n", waiting, count);
		}
		finished.await(); // tell them to go back to whatever they were doing
	}
}
