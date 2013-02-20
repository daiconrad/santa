import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;

public class Xmas implements Runnable {
	public static final int ELVES = 10;
	public static final int MIN_ELVES = 3;
	public static final int REINDEER = 9;
	public static final int MIN_REINDEER = REINDEER;

	private final Santa santa;

	public Xmas(Santa santa) { this.santa = santa; }

	public static void main(String[] args) {
		Queue<Elf> elfQueue = new LinkedBlockingQueue<Elf>();
		Queue<Reindeer> reindeerQueue = new LinkedBlockingQueue<Reindeer>();

		Santa santa = new Santa(elfQueue, reindeerQueue);
		Xmas wakeSanta = new Xmas(santa);

		CyclicBarrier elfBarrier = new CyclicBarrier(MIN_ELVES, wakeSanta);
		CyclicBarrier reindeerBarrier = new CyclicBarrier(MIN_REINDEER, wakeSanta);

		List<Elf> elves = new ArrayList<Elf>();
		for (int i = 1; i <= ELVES; ++i) {
			elves.add(new Elf(i, elfQueue, elfBarrier));
		}
		List<Reindeer> reindeer = new ArrayList<Reindeer>();
		for (int i = 1; i <= REINDEER; ++i) {
			reindeer.add(new Reindeer(i, reindeerQueue, reindeerBarrier));
		}

		new Thread(santa).start();
		for (Reindeer deer : reindeer) new Thread(deer).start();
		for (Elf elf : elves) new Thread(elf).start();
	}

	@Override public void run() {
		synchronized (santa) { santa.notify(); }
	}
}
