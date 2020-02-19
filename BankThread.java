import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class BankThread implements Runnable {
   private Thread t;
   private String threadName;
   private int balance;
   
   private final BlockingQueue<String> reqQueue;
   private final BlockingQueue<String> resQueue;
   private final BlockingQueue<String> resultQueue;
   private final BlockingQueue<String> finalQueue;
   
   BankThread( String name, int value, BlockingQueue<String> reqQueue, BlockingQueue<String> resQueue, BlockingQueue<String> resultQueue, BlockingQueue<String> finalQueue) {
	  threadName = name;
	  balance = value;
	   this.reqQueue = reqQueue;
	   this.resQueue = resQueue;
	   this.resultQueue = resultQueue;
	   this.finalQueue = finalQueue;
   }
  
   public void run() {
	  
      try {
		  int counter = 0;
		  Thread.sleep(100);
		  String take = "";
		  String str = "";
          while (balance > 0 && take != null) {
			  //Thread.sleep(100);
                take = reqQueue.poll(500, TimeUnit.MILLISECONDS); 
				//take = reqQueue.take();
				
				if(take != null)
				{
					String request[] = take.split(";");
					int loan = Integer.parseInt(request[1]);
					resultQueue.put(request[0] +" requests a loan of " + loan + " dollar(s) from " + threadName);
					if(balance - loan >= 0){
						balance = balance - loan;
						str = threadName+";approved;"+loan;
						resQueue.put(str);
						//resultQueue.put(threadName + " approves a loan of " + loan +" dollar(s) from "+ request[0]);
					}
					else{
						str = threadName+";rejected;"+loan;
						resQueue.put(str);
						//resultQueue.put(threadName + " denies a loan of " + loan +" dollar(s) from "+ request[0]);
					}

				}	
			}
			finalQueue.put(threadName + " has "+ balance +" dollar(s) remaining.");
        }
       catch (InterruptedException e) {
         System.out.println("Thread " +  threadName + " interrupted.");
      }
   }
  
   public void start () {
      if (t == null) {
         t = new Thread (this, threadName);
         t.start ();
      }
   }
}