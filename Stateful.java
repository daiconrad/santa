public interface Stateful {
	State getState();
	void setState(State state);
	long getLatest();
}
