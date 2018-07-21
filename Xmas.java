import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TransferQueue;

public class Xmas {
	public static final int ELVES = 12;
	public static final int MIN_ELVES = 3;
	public static final int REINDEER = 9;
	public static final int MIN_REINDEER = REINDEER;

    public static List<Thread> threads = new ArrayList<>();

    public static Thread remember(Thread thread) {
        threads.add(thread);
        return thread;
    }

	public static void main(String[] args) throws InterruptedException {
        int time = args.length == 0? 20_000 : Integer.parseInt(args[0].replace(",", "").replace("_", ""));
		Semaphore door = new Semaphore(0);

		Queue<List<? extends Creature>> santaQueue = new ConcurrentLinkedQueue<>();

		TransferQueue<Elf> elfQueue = new LinkedTransferQueue<Elf>();
		TransferQueue<Reindeer> deerQueue = new LinkedTransferQueue<Reindeer>();

        System.out.println("It's the most wonderul time of the year...");
		Santa santa = new Santa(door, santaQueue);
		remember(new Thread(santa, santa.toString())).start();

        var reginald = new Doorman<Reindeer>(MIN_REINDEER,
                "Reginald", deerQueue, santaQueue, () -> door.release());
        remember(new Thread(reginald, reginald.toString())).start();

        var alfred = new Doorman<Elf>(MIN_ELVES,
                "Alfred", elfQueue, santaQueue, () -> door.release());
        remember(new Thread(alfred, alfred.toString())).start();

		for (int i = 1; i <= REINDEER; ++i) {
			Reindeer deer = new Reindeer(i, deerQueue);
			remember(new Thread(deer, deer.toString())).start();
		}
		for (int i = 1; i <= ELVES; ++i) {
			Elf elf = new Elf(i, elfQueue);
			remember(new Thread(elf, elf.toString())).start();
		}

        Thread.sleep(time);
        System.out.println("Shutting down...");
        for (Thread t : threads) t.interrupt();
        System.out.println("Waiting for threads to finish...");
        for (Thread t : threads) t.join();

        System.out.println("All threads have terminated.");
        System.out.println("Be seeing you.");
	}
}
