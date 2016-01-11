import concurrent.ConcurrentStackLockFree;
import concurrent.ConcurrentStackWithLock;
import concurrent.StackEmptyException;
import concurrent.StackFullException;

class Pusher2 extends Thread {
	ConcurrentStackWithLock<Integer> cs;
	int base;

	Pusher2(ConcurrentStackWithLock<Integer> cs, int base) {
		this.cs = cs;
		this.base = base;
	}

	@Override
	public void run() {
		for (int i = 0; i < cs.MAXSIZE / 2; i++) {
			boolean good = false;
			while (good == false) {
				try {
					cs.push(base + i);
					good = true;
				} catch (StackFullException e) {
				}
//				System.out.println("[" + Thread.currentThread().getId() + "] pushed :" + (i + base));
			}
		}
	}
}

class Popper2 extends Thread {
	ConcurrentStackWithLock<Integer> cs;

	Popper2(ConcurrentStackWithLock<Integer> cs) {
		this.cs = cs;
	}

	@Override
	public void run() {
		int p = 0;
		for (int i = 0; i < cs.MAXSIZE / 2; i++) {
			boolean good = false;
			while (good == false) {
				try {
					p = cs.pop();
					good = true;
				} catch (StackEmptyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
//			System.out.println("[" + Thread.currentThread().getId() + "] popped :" + p);
		}
	}

}

public class CStackWithLockTester {
	public static void main0(String[] args) {
		int p;
		ConcurrentStackLockFree<Integer> cs = new ConcurrentStackLockFree<Integer>();
		for (int i = 0; i < 10; i++) {
			cs.push(i);
			System.out.println("pushed " + i);
		}
		for (int i = 0; i < 10; i++) {
			p = cs.pop();
			System.out.println("popped " + p);
			if (p != 10 - i - 1) {
				System.out.println("Not all good");
				System.exit(1);
			}
		}
		System.out.println("All good so far");
	}

	public static void main(String[] args) throws InterruptedException {
		Thread[] pu = new Pusher2[2];
		ConcurrentStackWithLock<Integer> cs = new ConcurrentStackWithLock<Integer>();
		pu[0] = new Pusher2(cs, 0);
		pu[1] = new Pusher2(cs, 100);

		Thread[] po = new Popper2[2];
		po[0] = new Popper2(cs);
		po[1] = new Popper2(cs);

		long startTime = System.currentTimeMillis();

		pu[0].start();
		pu[1].start();

		po[0].start();
		po[1].start();

		// to see output in sequence

		po[0].join();
		po[1].join();

		pu[0].join();
		pu[1].join();

		System.out.println("Done");

		long stopTime = System.currentTimeMillis();
		System.out.println("Run Time for Concurrent Stack with Locks : " + (stopTime - startTime));
		
	}
}


