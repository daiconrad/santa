public abstract class Creature implements Runnable, Stateful {
	protected final int id;
	protected final String name;

	private volatile State state;
	private volatile long latest;

	private volatile boolean need;

	public Creature(int id, String name) {
		this.id = id; this.name = name;
		setState(State.UNKNOWN);
		need = false;
	}

	public abstract void queueUp();

	public abstract int getTime();

	public boolean hasNeed() { return need; }
	public void fulfill() { need = false; }

	@Override public State getState() { return state; }
	@Override public void setState(State state) {
		this.state = state;
		latest = System.currentTimeMillis();
	}
	@Override public long getLatest() { return latest; }

	@Override public String toString() { return name + " " + id; }

	@Override public void run() {
		try {
			synchronized (this) {
				while (true) {
					setState(State.SLEEPING);
					Thread.sleep(getTime());
					setState(State.QUEUING);
					queueUp();
					setState(State.WAITING);
					need = true; // need Saint Nick
					do {
						this.wait();
					} while (hasNeed());
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
