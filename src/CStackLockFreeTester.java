import concurrent.ConcurrentStackLockFree;

class Pusher extends Thread {
	ConcurrentStackLockFree<Integer> cs;
	int base;
	Pusher(ConcurrentStackLockFree<Integer> cs, int base) {
		this.cs = cs;
		this.base=base;
	}
	@Override
	public void run() {
		for (int i = 0; i < ConcurrentStackLockFree.MAXSIZE/2; i++) {
			cs.push(base+i);
			//System.out.println("[" + Thread.currentThread().getId() + "] pushed :" + (i+base));
		}
	}
}

class Popper extends Thread {
	ConcurrentStackLockFree<Integer> cs;
	Popper(ConcurrentStackLockFree<Integer> cs) {
		this.cs = cs;
	}

	@Override
	public void run() {
		int p;
		for (int i = 0; i < ConcurrentStackLockFree.MAXSIZE/2; i++) {
			p = cs.pop();
			//System.out.println("[" + Thread.currentThread().getId() + "] popped :" + p);
		}
	}
}

public class CStackLockFreeTester {
	public static void main0(String[] args) {
		int p;
		ConcurrentStackLockFree<Integer> cs = new ConcurrentStackLockFree<Integer>();
		for (int i = 0; i < 10; i++) {
			cs.push(i);
			System.out.println("pushed "+ i);
		}
		for (int i = 0; i < 10; i++) {
			p = cs.pop();
			System.out.println("popped " + p);
			if(p != 10-i-1) {
				System.out.println("Not all good");
				System.exit(1);
			}
		}
		System.out.println("All good so far");
	}

	public static void main(String[] args) throws InterruptedException {
		int threadSize = 5;
		Thread[] pu = new Pusher[threadSize];
		ConcurrentStackLockFree<Integer> cs = new ConcurrentStackLockFree<Integer>();
//		pu[0] = new Pusher(cs, 0);
//		pu[1] = new Pusher(cs, 100);
		
		Thread[] po = new Popper[threadSize];
//		po[0] = new Popper(cs);
//		po[1] = new Popper(cs);
		for (int i = 0; i < threadSize; i++) {
			pu[i] = new Pusher(cs, 100*i);
			po[i] = new Popper(cs);
		}
		
		long startTime = System.currentTimeMillis();

		for (int i = 0; i < threadSize; i++) {
			pu[i].start();
			po[i].start();
		}
		for (int i = 0; i < threadSize; i++) {
			pu[i].join();
			po[i].join();
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
		System.out.println("Run Time for Concurrent Stack without Locks : " + (stopTime - startTime));
	}
}



