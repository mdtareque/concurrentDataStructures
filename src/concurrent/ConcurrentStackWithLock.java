package concurrent;

public class ConcurrentStackWithLock<E> implements Stack<E> {
	private static int counter = 0;
	public static final int MAXSIZE = 1000;
	private Object[] stack;
	private SimpleLock lock;
	private int top, size;
	
	/**
	 * Creates a unbounded concurrent queue
	 */
	public ConcurrentStackWithLock() {
		lock = new SimpleLock();
		size = MAXSIZE;
		stack = new Object[MAXSIZE]; 
		top = -1;
	}

	/**
	 * This method will try to push item into stack until it succeeds
	 * @throws StackFullException 
	 */
	@Override
	public void push(E e) throws StackFullException {
		// Create a new item
		try {
			System.out.println("Time: [" + counter++ +"] Thread: [" + Thread.currentThread().getId() + "]: Acquired lock for pushing " + e );
			lock.lock();
			if(top == size - 1) throw new StackFullException("Stack full");
			stack[++top] = e;
		} catch (InterruptedException e1) {
			e1.printStackTrace();
//		} catch (StackFullException e1) {
//			e1.printStackTrace();
		} finally {
			lock.unlock();
			System.out.println("Time: [" + counter++ +"] Thread: [" + Thread.currentThread().getId() + "]: Released lock after pushing.");
			
		}
		
	}

	/**
	 * This method will try to pop item from stack until it succeeds
	 * @throws StackEmptyException 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public E pop() throws StackEmptyException {
		try {
			System.out.println("Time: [" + counter++ +"] Thread: [" + Thread.currentThread().getId() + "]: Acquired Lock for popping an element.");
			lock.lock();
			if(top == -1) throw new StackEmptyException("Stack is empty");
			return (E)stack[top--];
		} catch (InterruptedException e) {
			e.printStackTrace();
//		} catch (StackEmptyException e) {
//			e.printStackTrace();
		} 
		finally {
			lock.unlock();
			System.out.println("Time: [" + counter++ +"] Thread: [" + Thread.currentThread().getId() + "]: Released lock after popping.");
		}
		return null;
	}

}

class SimpleLock {

	private boolean isLocked = false;

	public synchronized void lock() throws InterruptedException {
		while (isLocked) {
			wait();
		}
		isLocked = true;
	}

	public synchronized void unlock() {
		isLocked = false;
		notify();
	}
}

