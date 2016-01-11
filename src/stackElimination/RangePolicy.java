package stackElimination;

public class RangePolicy {
	int maxRange;
	int currentRange = 1;

	RangePolicy(int maxRange) {
		this.maxRange = maxRange;
	}

	public void recordEliminationSuccess() {
		System.out.println("Eliminiation success.");
		if (currentRange < maxRange)
			currentRange++;
	}

	public void recordEliminationTimeout() {
		if (currentRange > 1)
			currentRange--;
	}

	public int getRange() {
		return currentRange;
	}
}
