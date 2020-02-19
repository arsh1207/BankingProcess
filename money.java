import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
public class money {
    
	public static void main(String[] args) {
	
		try{
		BufferedReader br_cust = new BufferedReader(new FileReader("customers.txt"));
		BufferedReader br_banks = new BufferedReader(new FileReader("banks.txt"));
		
		Semaphore sem = new Semaphore(1); 
		
		BlockingQueue<String> reqQueue = new LinkedBlockingQueue<>(10);
		BlockingQueue<String> resQueue = new LinkedBlockingQueue<>(10);
		BlockingQueue<String> resultQueue = new LinkedBlockingQueue<>(10);
		BlockingQueue<String> finalQueue = new LinkedBlockingQueue<>(10);
		
		BankThread bank_obj;
		String banks;
		System.out.println("** Banks and financial resources **");
		while ((banks = br_banks.readLine()) != null)
        {
			
			String input[] = banks.split(",");
			System.out.println(input[0].substring(1,(input[0].length()))+": "+ Integer.parseInt(input[1].substring(0,(input[1].length()-2))));
			bank_obj = new BankThread(input[0].substring(1,(input[0].length())), Integer.parseInt(input[1].substring(0,(input[1].length()-2))), reqQueue, resQueue, resultQueue, finalQueue );
			bank_obj.start();
			
        }
		System.out.println();
		
		String customers; 
		CustomerThread cust_obj;
		System.out.println("** Customers and loan objectives **");
		 while ((customers = br_cust.readLine()) != null)
        {
			
			String input[] = customers.split(",");
			System.out.println(input[0].substring(1,(input[0].length()))+": "+ Integer.parseInt(input[1].substring(0,(input[1].length()-2))));
			cust_obj = new CustomerThread(input[0].substring(1,(input[0].length())), Integer.parseInt(input[1].substring(0,(input[1].length()-2))),  reqQueue, resQueue, resultQueue, finalQueue, sem);
			cust_obj.start();
        }
		System.out.println();
		String take = "", take1 = "";
		while(take != null){
			 take = resultQueue.poll(1000, TimeUnit.MILLISECONDS); 
			 if(take != null)
				System.out.println(take);
		}
		System.out.println();
		while(take1 != null){
			 take1 = finalQueue.poll(1000, TimeUnit.MILLISECONDS); 
			 if(take1 != null)
				System.out.println(take1);
		}
			
		
		}
		catch(Exception e){ e.printStackTrace();}
		
    }
}

