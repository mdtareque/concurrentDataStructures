import concurrent.ConcurrentStackLockFree;
import queue.BoundedQueue;
import queue.EmptyException;
import queue.LockFreeQueue;

class Enquer2 extends Thread {
	// concurrent queue
	LockFreeQueue<Integer> cq;
	int base;
	Enquer2(LockFreeQueue<Integer> cq, int base) {
		this.cq = cq;
		this.base=base;
	}
	@Override
	public void run() {
		for (int i = 0; i < LockFreeQueue.maxSize; i++) {
			cq.enq(base+i);
			//System.out.println("[" + Thread.currentThread().getId() + "] pushed :" + (i+base));
		}
	}
}

class Dequer2 extends Thread {
	LockFreeQueue<Integer> cq;
	Dequer2(LockFreeQueue<Integer> cq) {
		this.cq = cq;
	}

	@Override
	public void run() {
		int p;
		for (int i = 0; i < LockFreeQueue.maxSize; i++) {
			try {
				p = cq.deq();
			} catch (EmptyException e) {
			}
			//System.out.println("[" + Thread.currentThread().getId() + "] popped :" + p);
		}
	}
}

public class LockFreeQueueTester {

	public static void main(String[] args) throws InterruptedException {
		int threadSize = 5;
		Thread[] en = new Enquer2[threadSize];
		LockFreeQueue<Integer> cq = new LockFreeQueue<Integer>();
		
		Thread[] de = new Dequer2[threadSize];
		for (int i = 0; i < threadSize; i++) {
			en[i] = new Enquer2(cq, 100*i);
			de[i] = new Dequer2(cq);
		}
		
		long startTime = System.currentTimeMillis();

		for (int i = 0; i < threadSize; i++) {
			en[i].start();
			de[i].start();
		}
		for (int i = 0; i < threadSize; i++) {
			en[i].join();
			de[i].join();
		}
		
		// to see output in sequence
		
		long stopTime = System.currentTimeMillis();
		System.out.println("Run Time for Concurrent Queue without Locks : " + (stopTime - startTime));
	}
}



