import java.util.ArrayList;
import java.util.List;

public class Xmas {
	public static final int ELVES = 10;
	public static final int MIN_ELVES = 3;
	public static final int REINDEER = 9;
	public static final int MIN_REINDEER = REINDEER;

	public static void main(String[] args) {
		Object token = new Object(); // the conch shell, Santa's mutex

		NotifyQueue<Elf> elfQueue = new NotifyQueue<Elf>(MIN_ELVES, token);
		NotifyQueue<Reindeer> reindeerQueue = new NotifyQueue<Reindeer>(MIN_REINDEER, token);

		Santa santa = new Santa(token, elfQueue, reindeerQueue);

		List<Elf> elves = new ArrayList<Elf>();
		for (int i = 1; i <= ELVES; ++i) elves.add(new Elf(i, elfQueue));

		List<Reindeer> reindeer = new ArrayList<Reindeer>();
		for (int i = 1; i <= REINDEER; ++i) reindeer.add(new Reindeer(i, reindeerQueue));

		Supervisor supervisor = new Supervisor(santa, elves, reindeer);

		new Thread(santa).start();
		for (Reindeer r : reindeer) new Thread(r).start();
		for (Elf elf : elves) new Thread(elf).start();
		new Thread(supervisor).start();
	}
}
