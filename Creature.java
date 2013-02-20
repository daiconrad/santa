public abstract class Creature implements Runnable {
	protected final int id;
	protected final String name;

	public Creature(int id, String name) {
		this.id = id; this.name = name;
	}

	public abstract void queueUp();

	public abstract int getTime();

	@Override public String toString() { return name + " " + id; }

	@Override public void run() {
		try {
			synchronized (this) {
				while (true) {
					Thread.sleep(getTime());
					queueUp();
					this.wait();
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
