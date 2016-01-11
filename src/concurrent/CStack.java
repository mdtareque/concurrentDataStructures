package concurrent;

public class CStack {

	int[] stack;
	int top;
	int size;
	static final int MAX=100;
	public CStack() {
		top=-1;
		size=MAX;
		stack = new int[MAX];
	}
	CStack(int n) {
		this();
		size=n;
		stack = new int[n];
	}
	public void push(int i) throws StackFullException {
		if(top < size)
			stack[++top] = i;
		else throw new StackFullException("Stack is full");
	}
	public int pop() throws StackEmptyException {
		if(top != -1)
			return stack[top--];
		else 
			throw new StackEmptyException("Stack is empty");
	}
	
}
