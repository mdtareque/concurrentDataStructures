package concurrent;

import queue.EmptyException;

public interface Stack <E>{

	public void push(E e) throws StackFullException;
	public E pop() throws StackEmptyException, EmptyException;

}