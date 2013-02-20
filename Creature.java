import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public abstract class Creature implements Runnable {
	protected final int id;
	protected final String name;
	private final CyclicBarrier barrier;

	public Creature(int id, String name, CyclicBarrier barrier) {
		this.id = id; this.name = name;
		this.barrier = barrier;
	}

	public abstract void queueUp();

	public abstract int getTime();

	@Override public String toString() { return name + " " + id; }

	@Override public void run() {
		try {
			while (true) {
				Thread.sleep(getTime());
				queueUp();
				barrier.await();
			}
		} catch (InterruptedException|BrokenBarrierException e) {
			Thread.currentThread().interrupt();
		}
	}
}
