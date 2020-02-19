import java.util.concurrent.BlockingQueue;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Semaphore;

public class CustomerThread implements Runnable {
   private Thread t;
   private String threadName;
   private int balance;
   private int oldBalance;
   Semaphore sem; 
   
   private final BlockingQueue<String> reqQueue;
   private final BlockingQueue<String> resQueue;
   private final BlockingQueue<String> resultQueue;
   private final BlockingQueue<String> finalQueue;
   
   
   CustomerThread( String name, int value, BlockingQueue<String> reqQueue, BlockingQueue<String> resQueue, BlockingQueue<String> resultQueue, BlockingQueue<String> finalQueue, Semaphore sem) {
      threadName = name;
	  balance = value;
	  oldBalance = value;
	  this.reqQueue = reqQueue;
	  this.resQueue = resQueue;
	  this.resultQueue = resultQueue;
	  this.finalQueue = finalQueue;
	  this.sem = sem;
   }
  
   public void run() {
      try {
		  Thread.sleep(100);
          process();
         
      } catch (InterruptedException e) {
         System.out.println("Thread " +  threadName + " interrupted.");
      }
   }
   
    private  void process() throws InterruptedException {
		Random rand = new Random();
		String str = "";
        String take = "";
		
        while(balance > 0 && take != null) 
		{
			sem.acquire(); 
			int loanAmount = rand.nextInt(50) + 1;
			if(balance >= loanAmount){
				str = threadName +";"+Integer.toString(loanAmount);
				reqQueue.put(str);
			}else{
				 loanAmount = rand.nextInt(balance) + 1;
				str = threadName +";"+Integer.toString(loanAmount);
				reqQueue.put(str);
			}
			
			take = resQueue.poll(500, TimeUnit.MILLISECONDS);
			
			if(take != null ){
				String response[] = take.split(";");
				
				if(response[1].equals("approved")){
					resultQueue.put(response[0] + " approves a loan of " + loanAmount +" dollar(s) from "+ threadName);
					balance = balance - loanAmount;
				}
				else
					resultQueue.put(response[0] + " denies a loan of " + loanAmount +" dollar(s) from "+ threadName);
					//resultQueue.put(response[0] + " denies a loan of " + loanAmount +" := "+response[2] +" dollar(s) from "+ threadName);
			}
			 sem.release(); 
			
        }
		
		if(balance == 0)
			finalQueue.put(threadName + " has reached the objective of "+ oldBalance +" dollar(s). Woo Hoo!");
		else
			finalQueue.put(threadName + " was only able to borrow "+(oldBalance - balance)+" dollar(s). Boo Hoo!");

    }
   public void start () {
      if (t == null) {
         t = new Thread (this, threadName);
         t.start ();
      }
   }
}