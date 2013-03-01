import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class Xmas implements Runnable {
	public static final int SANTA = 1;
	public static final int ELVES = 10;
	public static final int MIN_ELVES = 3;
	public static final int REINDEER = 9;
	public static final int MIN_REINDEER = REINDEER;

	private final Semaphore door;

	public Xmas(Semaphore door) { this.door = door; }

	@Override public void run() {
		door.release();
	}

	public static void main(String[] args) {
		Semaphore door = new Semaphore(0);
		Xmas wakeSanta = new Xmas(door);

		Semaphore invite = new Semaphore(0);
		Semaphore harness = new Semaphore(0);

		Queue<Elf> elfQueue = new LinkedBlockingQueue<Elf>();
		Queue<Reindeer> deerQueue = new LinkedBlockingQueue<Reindeer>();

		CyclicBarrier elfWait = new CyclicBarrier(MIN_ELVES, wakeSanta);
		CyclicBarrier deerWait = new CyclicBarrier(MIN_REINDEER, wakeSanta);

		Santa santa = new Santa(door, elfQueue, deerQueue, invite, harness);

		List<Elf> elves = new ArrayList<Elf>();
		for (int i = 1; i <= ELVES; ++i) {
			elves.add(new Elf(i, elfQueue, elfWait, invite));
		}
		List<Reindeer> reindeer = new ArrayList<Reindeer>();
		for (int i = 1; i <= REINDEER; ++i) {
			reindeer.add(new Reindeer(i, deerQueue, deerWait, harness));
		}

		new Thread(santa, santa.toString()).start();
		for (Reindeer deer : reindeer) new Thread(deer, deer.toString()).start();
		for (Elf elf : elves) new Thread(elf, elf.toString()).start();
	}
}
