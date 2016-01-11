package stackElimination;

import java.util.concurrent.atomic.AtomicReference;

public class LockFreeStack<T> implements MyStack<T>
{
        AtomicReference<Node<T>> top = new AtomicReference<Node<T>>(null);
        static int MIN_DELAY = 1, MAX_DELAY = 100;
        Backoff backoff = new Backoff(MIN_DELAY, MAX_DELAY);
        
        LockFreeStack(int min, int max)
        {
                MIN_DELAY = min;
                MAX_DELAY = max;
        }
        protected boolean tryPush(Node<T> node)
        {
                Node<T> oldTop = top.get();
                node.next = oldTop;
                return (top.compareAndSet(oldTop,  node));
        }
        
        @Override
        public void push(T value) throws InterruptedException 
        {
                Node<T> node = new Node<T>((T)value);
                
                while(true)
                {
                        if(tryPush(node))
                        {
                                return;
                        }
                        else
                        {
                                backoff.backoff();
                        }
                }
        }

        protected Node<T> tryPop() throws Exception
        {
                Node<T> oldTop = top.get();
                if(oldTop == null)
                        throw new Exception("Its empty!");
                
                
                Node<T> newTop = oldTop.next;
                if(top.compareAndSet(oldTop,  newTop))
                        return oldTop;
                else
                        return null;
        }
        
        @Override
        public T pop() throws Exception, InterruptedException
        {
                while(true)
                {
                        Node<T> returnNode = tryPop();
                        if(returnNode != null)
                                return returnNode.value;
                        else
                                backoff.backoff();
                }
        }

        @Override
        public String getStackInfo() 
        {
                return MIN_DELAY + "," + MAX_DELAY;
        }
}
