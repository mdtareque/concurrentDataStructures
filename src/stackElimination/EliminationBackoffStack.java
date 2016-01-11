package stackElimination;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicStampedReference;

public class EliminationBackoffStack<T> extends LockFreeStack<T> implements MyStack<T> {
	private EliminationArray<T> eliminationArray;
	private static ThreadLocal<RangePolicy> policy;
	private int exchangerCapacity, exchangerWaitDuration;

	EliminationBackoffStack(final int exchangerCapacity, int exchangerWaitDuration) {
		super(0, 0);
		this.exchangerCapacity = exchangerCapacity;
		this.exchangerWaitDuration = exchangerWaitDuration;
		eliminationArray = new EliminationArray<T>(exchangerCapacity, exchangerWaitDuration);
		policy = new ThreadLocal<RangePolicy>() {
			protected synchronized RangePolicy initialValue() {
				return new RangePolicy(exchangerCapacity);
			}
		};
	}

	public String getStackInfo() {
		return exchangerCapacity + "," + exchangerWaitDuration;
	}

	public void push(T value) {
		RangePolicy rangePolicy = policy.get();
		Node<T> node = new Node<T>(value);

		while (true) {
			if (this.tryPush(node))
				return;
			else {
				try {
					T otherValue = eliminationArray.visit(value, rangePolicy.getRange());
					if (otherValue == null) {
						rangePolicy.recordEliminationSuccess();
						return;
					}
				} catch (TimeoutException e) {
					//System.out.println("Elimination Timed-out . Trying normal push");
					rangePolicy.recordEliminationTimeout();
				}
			}
		}
	}

	public T pop() throws Exception {
		RangePolicy rangePolicy = policy.get();

		while (true) {
			Node<T> returnNode = tryPop();
			if (returnNode != null)
				return returnNode.value;
			else {
				try {
					T otherValue = eliminationArray.visit(null, rangePolicy.getRange());
					if (otherValue != null) {
						rangePolicy.recordEliminationSuccess();
						return otherValue;
					}
				} catch (TimeoutException e) {
					//System.out.println("Elimination Timed-out . Trying normal pop");
					rangePolicy.recordEliminationTimeout();
				}
			}
		}
	}
}

class EliminationArray<T> {
	private int duration = 10;
	private Random random;
	private LockFreeExchanger<T>[] exchanger;

	@SuppressWarnings("unchecked")
	EliminationArray(int capacity, int duration) {
		this.duration = duration;
		exchanger = (LockFreeExchanger<T>[]) new LockFreeExchanger[capacity];
		for (int i = 0; i < capacity; i++) {
			exchanger[i] = new LockFreeExchanger<T>();
		}
		random = new Random();
	}

	public T visit(T value, int range) throws TimeoutException {
		int slot = random.nextInt(range);
		return (exchanger[slot].exchange(value, duration, TimeUnit.MILLISECONDS));
	}
}

class LockFreeExchanger<T> {
	static final int EMPTY = 0, WAITING = 1, BUSY = 2;
	AtomicStampedReference<T> slot = new AtomicStampedReference<T>(null, 0);

	public T exchange(T myItem, long timeout, TimeUnit unit) throws TimeoutException {
		long nanos = unit.toNanos(timeout);
		long timeBound = System.nanoTime() + nanos;
		int[] stampHolder = { EMPTY };

		while (true) {
			if (System.nanoTime() > timeBound)
				throw new TimeoutException();
			T yrItem = slot.get(stampHolder);
			int stamp = stampHolder[0];
			switch (stamp) {
			case EMPTY:
				if (slot.compareAndSet(yrItem, myItem, EMPTY, WAITING)) {
					while (System.nanoTime() > timeBound) {
						yrItem = slot.get(stampHolder);
						if (stampHolder[0] == BUSY) {
							slot.set(null, EMPTY);
							return yrItem;
						}
					}
					if (slot.compareAndSet(myItem, null, WAITING, EMPTY))
						throw new TimeoutException();
					else {
						yrItem = slot.get(stampHolder);
						slot.set(null, EMPTY);
						return yrItem;
					}
				}
				break;

			case WAITING:
				if (slot.compareAndSet(yrItem, myItem, WAITING, BUSY))
					return yrItem;
				break;

			case BUSY:
				break;

			default: // impossible;
						// ...
			}
		}
	}
}
