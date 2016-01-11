import concurrent.ConcurrentStackLockFree;
import queue.BoundedQueue;

class Enquer extends Thread {
	// concurrent queue
	BoundedQueue<Integer> cq;
	int base;
	Enquer(BoundedQueue<Integer> cq, int base) {
		this.cq = cq;
		this.base=base;
	}
	@Override
	public void run() {
		for (int i = 0; i < cq.getCapacity(); i++) {
			cq.enq(base+i);
			//System.out.println("[" + Thread.currentThread().getId() + "] pushed :" + (i+base));
		}
	}
}

class Dequer extends Thread {
	BoundedQueue<Integer> cq;
	Dequer(BoundedQueue<Integer> cq) {
		this.cq = cq;
	}

	@Override
	public void run() {
		int p;
		for (int i = 0; i < cq.getCapacity(); i++) {
			p = cq.deq();
			//System.out.println("[" + Thread.currentThread().getId() + "] popped :" + p);
		}
	}
}

public class BoundedQueueTester {

	public static void main(String[] args) throws InterruptedException {
		int threadSize = 5;
		Thread[] en = new Enquer[threadSize];
		BoundedQueue<Integer> cq = new BoundedQueue<Integer>(1000);
//		pu[0] = new Pusher(cs, 0);
//		pu[1] = new Pusher(cs, 100);
		
		Thread[] de = new Dequer[threadSize];
//		po[0] = new Popper(cs);
//		po[1] = new Popper(cs);
		for (int i = 0; i < threadSize; i++) {
			en[i] = new Enquer(cq, 100*i);
			de[i] = new Dequer(cq);
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
//		pu[0].start();
//		pu[1].start();
		
//		po[0].start();
//		po[1].start();
		
		// to see output in sequence

//		po[0].join();
//		po[1].join();
		
//		pu[0].join();
//		pu[1].join();
		
//		System.out.println("Done");
		
		long stopTime = System.currentTimeMillis();
		System.out.println("Run Time for Concurrent Queue without Locks : " + (stopTime - startTime));
	}
}



