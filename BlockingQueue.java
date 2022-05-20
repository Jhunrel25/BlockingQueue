import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.LinkedList;
import java.util.Queue;

public class BlockingQueue<Type> {

	private Queue<Type>   queue;
	private int           maxSize;
	private Lock          lock;
	private Condition     condition;

	public BlockingQueue(int maxSize) {
		this.queue      = new LinkedList<Type>();
		this.maxSize    = maxSize;
		this.lock       = new ReentrantLock();
		this.condition  = lock.newCondition();
	}

	public BlockingQueue(int maxSize, boolean fairness) {
		this.queue      = new LinkedList<Type>();
		this.maxSize    = maxSize;
		this.lock       = new ReentrantLock(fairness);
		this.condition  = this.lock.newCondition();
	}

	public BlockingQueue() {
		this.queue      = new LinkedList<Type>();
		this.maxSize    = 10;
		this.lock       = new ReentrantLock();
		this.condition  = this.lock.newCondition();
	}

	public void put(Type e) {
		this.lock.lock();

		try {
			while (this.queue.size() == this.maxSize)
				this.condition.await();

			this.queue.add(e);
			this.condition.signalAll();

		} catch (InterruptedException err) {
			err.printStackTrace();

		} finally {
			this.lock.unlock();
		}
	}

	public Type take() throws InterruptedException {
		this.lock.lock();

		try {
			while (this.queue.size() == 0)
				this.condition.await();

			Type element = this.queue.remove();
			this.condition.signalAll();

			return element;

		} finally {
			this.lock.unlock();
		}
	}
}
