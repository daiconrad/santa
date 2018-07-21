import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Santa implements Runnable {
	public static final String MEET = "meeting in the study";
	public static final String DELIVER = "delivering presents";

	public static final String SNOW =
        "\n\t\t*\t\t\t*\t\t*\n\n\t*\t\t\t*\t\t*\t\t*\n\n\t\t*\t\t*\t\t*\t\t*\n\n\t*\t\t*\t\t*\t\t*\n\n\t\t*\t\t*\n";

	private final Semaphore door;
	private final Queue<List<? extends Creature>> queue;
	
	public Santa(Semaphore door, Queue<List<? extends Creature>> queue) {
		this.door = requireNonNull(door);
		this.queue = requireNonNull(queue);
	}

	@Override public String toString() { return "Santa"; }

	private String sleepMessage() {
		if (door.availablePermits() > 0) {
			return "Hmm. Someone's already at the door.";
		} else {
			return this + " is sleeping. ZZzzz...";
		}
	}

	@Override
    public void run() {
		try {
			while (true) {
				System.out.println(sleepMessage());
				door.acquire(); // sleep until someone knocks
                System.out.println("Santa: \"Who's that knocking at my door??\"");
                activityWithFriends(queue.remove());
			}
		} catch (InterruptedException e) {
            System.out.format("%s is done%n", this);
		}
	}

    private String joinIds(List<? extends Creature> list) {
        return list.stream().mapToInt(Creature::getId).mapToObj(Integer::toString).collect(joining(", "));
    }

	private void activityWithFriends(List<? extends Creature> list) throws InterruptedException {
        String act = list.get(0).getReason();
        String type = list.get(0).getName();

		// release them from waiting; Santa is ready for them
		for (Creature c : list) { c.welcomeIn(); }

		System.out.printf("%s %s, and %s %s%n", type, joinIds(list), this, act);
        if (list.size() == Xmas.MIN_REINDEER) System.out.println(SNOW);
		Thread.sleep(5);

		System.out.println("Santa: \"Good effort, everyone. Ho! Ho! Ho!\"");
		// tell them to go back to whatever they were doing
		for (Creature c : list) { c.grantLeave(); }
	}
}
