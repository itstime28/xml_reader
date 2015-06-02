package wegilant;

import java.util.concurrent.BlockingQueue;

public class Consumer implements Runnable{

    protected BlockingQueue<String> queue = null;

    public Consumer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    public void run() {
    	System.out.println("In Consumer");
        try {
        	while(true){
        		String str = queue.take();
        		 System.out.println("In Consumer " + new StringBuilder(str).reverse().toString());
        	}
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}