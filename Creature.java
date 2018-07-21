import java.util.concurrent.Semaphore;

public abstract class Creature implements Runnable, Comparable<Creature> {
	private final int id;
	private final String name, act;
	private final Semaphore waiting;
	private final Semaphore leave;

	public Creature(int id, String name, String act) {
		this.id = id;
		this.name = name;
		this.act = act;
		leave = new Semaphore(0);
		waiting = new Semaphore(0);
	}

	public abstract void queueUp() throws InterruptedException;

	public abstract int time();

    public void welcomeIn() {
        waiting.release();
    }

	public void grantLeave() {
		leave.release();
	}

	@Override public int compareTo(Creature that) {
		return this.id - that.id;
	}

	@Override public String toString() { return name + " " + id; }

    public String getName() { return name; }

    public int getId() { return id; }

	private void actFor(int time) throws InterruptedException {
		System.out.printf("%s is %sing%n", this, act);
		Thread.sleep(time); // simulate activity
	}

	@Override public void run() {
		try {
			while (true) {
				actFor(time());
				queueUp(); // get in line for Santa
				waiting.acquire(); // wait to be brought in by Santa
				leave.acquire(); // wait for Santa to dismiss us
			}
		} catch (InterruptedException e) {
            System.out.format("%s is done%n", this);
		}
	}
}
