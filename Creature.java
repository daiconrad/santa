import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public abstract class Creature implements Runnable {
	protected final int id;
	protected final String name, action;
	private final CyclicBarrier line, hold;

	public Creature(int id, String name, String action,
			CyclicBarrier line, CyclicBarrier hold) {
		this.id = id; this.name = name; this.action = action;
		this.line = line; this.hold = hold;
	}

	public abstract void queueUp();

	public abstract int time();

	@Override public String toString() { return name + " " + id; }

	private void actFor(int time) throws InterruptedException {
		System.out.printf("%s is %sing%n", this, action);
		Thread.sleep(time); // simulate action
	}

	@Override public void run() {
		try {
			while (true) {
				actFor(time());
				queueUp();
				line.await(); // get in line for Santa
				hold.await(); // do stuff with Santa
			}
		} catch (InterruptedException|BrokenBarrierException e) {
			System.out.println(this + " " + e.getClass().getSimpleName());
			Thread.currentThread().interrupt();
		}
	}
}
