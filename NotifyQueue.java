import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.LinkedBlockingQueue;

public class NotifyQueue<E extends Comparable<E>> {
	private final LinkedBlockingQueue<E> queue;
	private final int threshold;
	private final Object recipient;

	public NotifyQueue(int threshold, Object recipient) {
		this.threshold = threshold;
		this.recipient = recipient;
		queue = new LinkedBlockingQueue<E>();
	}

	public boolean ready() {
		return size() >= threshold;
	}

	public void add(E e) {
		queue.add(e);
		if (ready()) {
			synchronized (recipient) {
				recipient.notify();
			}
		}
	}

	public E remove() {
		return queue.remove();
	}

	public int size() {
		return queue.size();
	}

	public List<E> getItems() {
		if (!ready()) throw new NoSuchElementException("too few");
		List<E> list = new ArrayList<E>();
		queue.drainTo(list, threshold);
		Collections.sort(list);
		return list;
	}
}
