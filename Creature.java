import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public abstract class Creature implements Runnable, Comparable<Creature> {
	private final int id;
	private final String name, action;
	private final CyclicBarrier line;
	private final Semaphore waiting;
	private final Semaphore leave;

	public Creature(int id, String name, String action,
			CyclicBarrier line, Semaphore waiting) {
		this.id = id;
		this.name = name;
		this.action = action;
		this.line = line;
		this.waiting = waiting;
		leave = new Semaphore(0);
	}

	public abstract void queueUp();

	public abstract int time();

	public void grantLeave() {
		leave.release();
	}

	@Override public int compareTo(Creature that) {
		return this.id - that.id;
	}

	@Override public String toString() { return name + " " + id; }

	private void actFor(int time) throws InterruptedException {
		System.out.printf("%s is %sing%n", this, action);
		Thread.sleep(time); // simulate activity
	}

	@Override public void run() {
		try {
			while (true) {
				actFor(time());
				queueUp();
				line.await(); // get in line for Santa
				waiting.acquire(); // wait to be brought in by Santa
				leave.acquire(); // wait for Santa to dismiss us
			}
		} catch (InterruptedException|BrokenBarrierException e) {
			System.out.println(this + " " + e.getClass().getSimpleName());
			Thread.currentThread().interrupt();
		}
	}
}
