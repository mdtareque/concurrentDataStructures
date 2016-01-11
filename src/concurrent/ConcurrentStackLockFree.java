package concurrent;
import java.util.concurrent.atomic.AtomicReference;

import queue.EmptyException;

public class ConcurrentStackLockFree<E> implements Stack<E> {

	private static int counter = 0;
	
	public static final int MAXSIZE = 1000;
	private AtomicReference<StackNode> head;
	/**
	 * Creates a unbounded concurrent stack
	 */
	public ConcurrentStackLockFree() {
		head = new AtomicReference();
	}

	/**
	 * This method will try to push item into stack until it succeeds
	 */
	@Override
	public void push(E e) {
		//Create a new item
		System.out.println("Time: [" + counter++ +"] Thread: [" + Thread.currentThread().getId() + "]: pushing " + e );
		StackNode<E> newHead = new StackNode<E>(e);
		StackNode<E> headNode = null;
		do
		{
			System.out.println("Time: [" + counter++ +"] Thread: [" + Thread.currentThread().getId() + "]: trying to push using compareAndSet" );
			headNode = head.get();
			newHead.next = headNode;
		}while(!head.compareAndSet(headNode, newHead));
	}

	/**
	 * This method will try to pop item from stack until it succeeds
	 * @throws EmptyException 
	 */
	@Override
	public E pop() {
		System.out.println("Time: [" + counter++ +"] Thread: [" + Thread.currentThread().getId() + "]: popping.");
		StackNode<E> headNode = head.get();
		do
		{
			headNode = head.get();
			System.out.println("Time: [" + counter++ +"] Thread: [" + Thread.currentThread().getId() + "]: trying to pop using compareAndSet" );
			if(headNode == null)
				return null;
		}while(!head.compareAndSet(headNode, headNode.next));
		return headNode.item;
	}

	/**
	 * Inner class to hold stack items 
	 * @param <E>
	 */
	private class StackNode<E>{
		private E item;
		StackNode<E> next;

		public StackNode(E item) {
			this.item = item;
		}
	}

}

