package stackElimination;

public interface MyStack<T>{
        public T pop() throws Exception, InterruptedException;
        public String getStackInfo();
        public void push(T value) throws InterruptedException;
}
