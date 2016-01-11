import java.util.Random;

import concurrent.CStack;

class Runner extends Thread {
	public void run() {
		System.out.println(Thread.currentThread().getId());
		
		
	}
}



public class Test {
	static int[] toPushData;
	static int pushPtr;
	static int poppedData[];
	static Random rand;
	static {
		toPushData = new int[100];
		poppedData = new int[100];
		rand = new Random();
		for (int i = 0; i < toPushData.length; i++) {
			toPushData[i] = rand.nextInt(100-0 + 1) + 0;
			System.out.println(toPushData[i]);
		}
		
	}
	public static void mainNormal(String[] args) throws Exception {
		int p;
		CStack c = new CStack();
		
		for (int i = 0; i < 10; i++) {
			c.push(i);
		}
		
		for (int i = 9; i >= 0; i--) {
			p = c.pop();
			if(p != i) {
				System.err.println("Pop Failed, not expected element.");
				System.exit(1);
			}
		}
		
		System.out.println("All good");
	}
	
	public static void main(String[] args) {
		Runner[] runners = new Runner[5];
		for (int i = 0; i < runners.length; i++) {
			runners[i] = new Runner();
			runners[i].start();
		}
	}
}
