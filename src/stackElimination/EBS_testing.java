package stackElimination;

import java.util.ArrayList;

public class EBS_testing {
	static double totalTimeToLock = 0;
	static double totalTimeToUnLock = 0;

	public static void main(String[] args) {
		System.out.println("starting");
		//960400
		int iterations = 900400, minBackoff = 1, maxBackoff = 20, exchangerCapacity = 2, exchangerWaitDuration = 10;
		int maxThreads = 16;
		System.out.println("EliminationBackoffStack");
		for (int numThreads = 2; numThreads < maxThreads; numThreads++) {
			MyStack<Integer> theLock = new EliminationBackoffStack<Integer>(exchangerCapacity, exchangerWaitDuration);
			testStack(theLock, numThreads, iterations);
		}
	}

	public static void testStack(final MyStack<Integer> theStack, int numThreads, final int numOperations) {
		totalTimeToLock = 0;
		totalTimeToUnLock = 0;
		ArrayList<Thread> theThreads = new ArrayList<Thread>();
		for (int i = 0; i < numThreads; i++) {
			final Thread ta1 = new Thread(new Runnable() {
				double timeToLock = 0;
				double timeToUnLock = 0;

				@Override
				public void run() {
					for (int b = 0; b < numOperations; b++) {
						long startTimeLock = System.nanoTime();
						try {
							theStack.push(b);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						timeToLock += System.nanoTime() - startTimeLock;
						long startTimeUnLock = System.nanoTime();
						try {
							theStack.pop();
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
						timeToUnLock += System.nanoTime() - startTimeUnLock;
					}
					double avrTimeToLock = ((double) timeToLock) / numOperations * 10e-6;
					double avrTimeToUnLock = ((double) timeToUnLock) / numOperations * 10e-6;

					totalTimeToLock += avrTimeToLock;
					totalTimeToUnLock += avrTimeToUnLock;
				}
			});
			theThreads.add(ta1);
		}

		for (int thread = 0; thread < theThreads.size(); thread++) {
			theThreads.get(thread).start();
		}
		for (int thread = 0; thread < theThreads.size(); thread++) {
			try {
				theThreads.get(thread).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(theStack.getStackInfo() + "," + numThreads + "," + totalTimeToLock / numThreads + ","
				+ totalTimeToUnLock / numThreads);
	}
}
