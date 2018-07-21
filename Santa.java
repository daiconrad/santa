import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class Santa implements Runnable {
	public static final String MEET = "meeting in the study";
	public static final String DELIVER = "delivering presents";

	public static final String SNOW =
        "\n\t\t*\t\t\t*\t\t*\n\n\t*\t\t\t*\t\t*\t\t*\n\n\t\t*\t\t*\t\t*\t\t*\n\n\t*\t\t*\t\t*\t\t*\n\n\t\t*\t\t*\n";

	private final Semaphore door;
	private final Queue<Elf> elves;
	private final Queue<Reindeer> reindeer;
	
	public Santa(Semaphore door, Queue<Elf> elves, Queue<Reindeer> reindeer) {
		this.door = door;
		this.elves = elves;
        this.reindeer = reindeer;
	}

	@Override public String toString() { return "Santa"; }

	private boolean elvesReady() {
		return elves.size() >= Xmas.MIN_ELVES;
	}

	private boolean reindeerReady() {
		return reindeer.size() >= Xmas.MIN_REINDEER;
	}

	private String sleepMessage() {
		if (door.availablePermits() > 0) {
			return "Hmm. Someone's already at the door.";
		} else {
			return this + " is sleeping. ZZzzz...";
		}
	}

	@Override public void run() {
		try {
			while (true) {
				System.out.println(sleepMessage());
				door.acquire(); // sleep until someone knocks
                System.out.println("Santa: \"Who's that knocking at my door??\"");
				if (reindeerReady()) deliverPresents();
				else if (elvesReady()) holdMeeting();
			}
		} catch (InterruptedException e) {
            System.out.format("%s is done%n", this);
		}
	}

	private void deliverPresents() throws InterruptedException {
		activityWithFriends(reindeer, Xmas.MIN_REINDEER, DELIVER);
	}

	private void holdMeeting() throws InterruptedException {
		activityWithFriends(elves, Xmas.MIN_ELVES, MEET);
	}

    private String joinIds(List<Creature> coll, String separator) {
        return coll.stream().mapToInt(Creature::getId).mapToObj(Integer::toString).collect(joining(separator));
    }

	private List<Creature> drainToSortedList(List<Creature> list,
			Queue<? extends Creature> queue, int count) {
		for (int i = 0; i < count; ++i) {
			list.add(queue.remove());
		}
		Collections.sort(list);
		return list;
	}

	private void activityWithFriends(Queue<? extends Creature> queue, int count, String act)
            throws InterruptedException {
		final List<Creature> list =
			drainToSortedList(new ArrayList<Creature>(), queue, count);

		// release them from waiting; Santa is ready for them
		for (Creature c : list) { c.welcomeIn(); }

        String type = list.get(0).getName();
		String ids = joinIds(list, ", ");
		System.out.printf("%s %s, and %s %s%n", type, ids, this, act);
        if (list.size() == 9) System.out.println(SNOW);
		Thread.sleep(5);

		System.out.println("Santa: \"Good effort, everyone. Ho! Ho! Ho!\"");
		// tell them to go back to whatever they were doing
		for (Creature c : list) { c.grantLeave(); }
	}
}
