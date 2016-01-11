package stackElimination;

import java.util.Random;

public class Backoff {
	final int minDelay, maxDelay;
	final Random random;
	int limit;

	Backoff(int min, int max) {
		minDelay = min;
		maxDelay = max;
		limit = minDelay;
		random = new Random();
	}

	public void backoff() throws InterruptedException {
		int delay = random.nextInt(limit);
		limit = Math.min(maxDelay, 2 * limit);
		Thread.sleep(delay);
	}
}
