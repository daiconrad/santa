import java.util.List;

public class Supervisor implements Runnable {
	private final Santa santa;
	private final List<Elf> elves;
	private final List<Reindeer> reindeer;

	public Supervisor(Santa santa, List<Elf> elves,
			List<Reindeer> reindeer) {
		this.santa = santa; this.elves = elves;
		this.reindeer = reindeer;
	}

	@Override public void run() {
		try {
			while (true) {
				Thread.sleep(2000);
				if (paused(santa) > 1000) {
					checkOn(santa, 1000);
					for (Elf elf : elves) checkOn(elf, 1000);
					for (Reindeer deer : reindeer) checkOn(deer, 4000);
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private long paused(Stateful item) {
		return System.currentTimeMillis() - item.getLatest();
	}

	private void checkOn(Stateful item, int tooLong) {
		long time = paused(item);
		if (time > tooLong) {
			System.out.printf("%s has been %s for %dms%n",
					item, item.getState(), time);
		}
	}
}
