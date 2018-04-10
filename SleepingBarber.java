import java.util.concurrent.*;

public class SleepingBarber extends Thread {

  /* PREREQUISITES */


  /* we create the semaphores. First there are no customers and 
   the barber is asleep so we call the constructor with parameter
   0 thus creating semaphores with zero initial permits. 
   Semaphore(1) constructs a binary semaphore, as desired. */
  
    public static Semaphore customers = new Semaphore(0);
    public static Semaphore barber = new Semaphore(1);
    public static Semaphore accessSeats = new Semaphore(1);
    public static Semaphore gettingHairCut = new Semaphore(0);
    public static Semaphore gettingHairCut2 = new Semaphore(1);
    
    public static int customerLeft = 0;

  /* we denote that the number of chairs in this barbershop is 5. */

    public static int CHAIRS = 5, CUSTOMERS = 15;
    public static int numberOfFreeSeats = CHAIRS;

  /* we create the integer numberOfFreeSeats so that the customers
   can either sit on a free seat or leave the barbershop if there
   are no seats available */


   
/* THE CUSTOMER THREAD */

static class Customer extends Thread {
  
  /* we create the integer iD which is a unique ID number for every customer
     and a boolean notCut which is used in the Customer waiting loop */
  
  int iD;
  boolean notCut=true;

  /* Constructor for the Customer */
    
  public Customer(int i) {
    iD = i;
  }

  public void run() {   
    while (notCut) {  // as long as the customer is not cut 
      try {
      accessSeats.acquire();  //tries to get access to the chairs
      if (numberOfFreeSeats > 0) {  //if there are any free seats
        System.out.println("Customer " + this.iD + " just sat down.");
        numberOfFreeSeats--;  //sitting down on a chair
        customers.release();  //notify the barber that there is a customer
        accessSeats.release();  // don't need to lock the chairs anymore  
        try {
	    barber.acquire();  // now it's this customers turn but we have to wait if the barber is busy
	    gettingHairCut.acquire();
	    this.get_haircut();  //cutting...
	    gettingHairCut2.release();
        notCut = false;  // this customer will now leave after the procedure
        barber.release();
        } catch (InterruptedException ex) {}
      }   
      else  {  // there are no free seats
        System.out.println("There are no free seats. Customer " + this.iD + " has left the barbershop.");
        customerLeft++;
        System.out.println("Count of customers thet left without haircut is " + customerLeft);
        accessSeats.release();  //release the lock on the seats
        notCut=false; // the customer will leave since there are no spots in the queue left.
      }
     }
      catch (InterruptedException ex) {}
    }
  }

  /* this method will simulate getting a hair-cut */
  
  public void get_haircut() throws InterruptedException{
    System.out.println("Customer " + this.iD + " is getting his hair cut");
    try {
    sleep(0);
    } catch (InterruptedException ex) {}
    System.out.println("Customer " + this.iD + " left as he got his hair cut");
    
  }

}

 
/* THE BARBER THREAD */


static class Barber extends Thread {
  
  public Barber() {}
  
  public void run() {
    while(true) {  // runs in an infinite loop
      try {
      customers.acquire(); // tries to acquire a customer - if none is available he goes to sleep
      accessSeats.acquire(); // at this time he has been awaken -> want to modify the number of available seats
      numberOfFreeSeats++; // one chair gets free
      accessSeats.release(); // we don't need the lock on the chairs anymore
      gettingHairCut2.acquire();
      this.cutHair();  //cutting...
      gettingHairCut.release();
    } catch (InterruptedException ex) {}
    }
  }

    /* this method will simulate cutting hair */
   
  public void cutHair(){
    try {
      sleep(1000);
    } catch (InterruptedException ex){ }
  }
}       
  
  /* main method */

  public static void main(String args[]) throws InterruptedException {
    
    SleepingBarber barberShop = new SleepingBarber();  //Creates a new barbershop
    barberShop.start();  // Let the simulation begin
    Barber mybarber = new Barber();  //Giovanni is the best barber ever 
    mybarber.start();  //Ready for another day of work
    /* This method will create new customers for a while */
    	    Customer c[] = new Customer[CUSTOMERS]; 
    	    for (int i=0; i<CUSTOMERS; i++) {
    	      c[i] = new Customer(i);
    	      c[i].start();
    	      try {
    	        sleep(500);
    	      } catch(InterruptedException ex) {};
    	    }
    	    for(int i = 0; i<CUSTOMERS; i++) {
    	    	c[i].join();
    	    }
  }
}