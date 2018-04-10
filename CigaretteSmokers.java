import java.util.concurrent.Semaphore;
import java.util.Random;

public class CigaretteSmokers {
	static int time = 0, array[][] = new int[61][4];

	public static void main(String[] args) throws InterruptedException {
		
		Semaphore mutexS = new Semaphore(1);
		Semaphore full = new Semaphore(0);
		Semaphore empty = new Semaphore(1);
		Agent agent = new Agent(full, empty);
        agent.start();
        Smoker s[] = new Smoker[3];
        s[0] = new Smoker(agent, "Tabacco", "Smoker 1", mutexS, 1);
        s[0].start();
        s[1] = new Smoker(agent, "Paper", "Smoker 2", mutexS, 2);
        s[1].start();
        s[2] = new Smoker(agent, "Matches", "Smoker 3", mutexS, 3);
        s[2].start();
	}
	public static class Smoker extends Thread{
		
		private String myIngredient;
		private Agent agent;
		private String name;
		private Semaphore mutexS;
		private int id;
		
		public Smoker(Agent agent, String ingredient, String name, Semaphore mutexS, int id) {
			
			this.agent = agent;
			this.myIngredient = ingredient;
			this.name = name;
			this.mutexS = mutexS;
			this.id = id;
		}
		
		@Override
		public void run() {
			while(true) {
				//receive ingredient
				//make cigarette
				//smoke
				
				try {
					this.agent.full.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	
				try {
					this.mutexS.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if (this.agent.haveThisIngredient(this.myIngredient)) {
					this.mutexS.release();
					this.agent.full.release();
					continue;
				}
				
				Delay.delay();
				System.out.println("I am " + this.name + " and I have " + this.myIngredient + 
						". So I will make a cigarette with your " + this.agent.getIngredient1() + 
						" and " + this.agent.getIngredient2());
				Delay.delay();
				System.out.println("Making");
				Delay.delay();
				System.out.println("Smoking");
				Delay.delay();
				array[time][id]++;
				this.mutexS.release();
				this.agent.empty.release();	
			}
		}
	}
	public static class Agent extends Thread{
		
		private int randomIngredient;
		private Random random = new Random();
		private String ingredient1 = new String();
		private String ingredient2 = new String();
		public Semaphore full, empty;
		
		public Agent(Semaphore full, Semaphore empty) {
			this.full = full;
			this.empty = empty;
		}
		
		@Override
		public void run() {
			
			while(true) {
				try {
					this.empty.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				this.randomIngredient = this.random.nextInt(3);
				if (this.randomIngredient == 0) {
					this.ingredient1 = "Tabacco";
					this.ingredient2 = "Paper";
				} 
				else if (this.randomIngredient == 1) {
					this.ingredient1 = "Tabacco";
					this.ingredient2 = "Matches";
				} 
				else if (this.randomIngredient == 2) {
					this.ingredient1 = "Matches";
					this.ingredient2 = "Paper";
				}
				
				System.out.println("I am the agent and I offer " + this.ingredient1 + " and " + this.ingredient2);
				
				this.full.release();
				
			}
		}
		
		public boolean haveThisIngredient(String ing) {
			if(this.ingredient1.equals(ing) || this.ingredient2.equals(ing)) {
				return true;
			}
			return false;
		}
		
		public String getIngredient1() {
			return ingredient1;
		}

		public String getIngredient2() {
			return ingredient2;
		}
	}
	
	public static class Delay {
		
		public static void delay() {
        	try { Thread.sleep(1000); }
        	catch (InterruptedException e) {}
		}
	
	}
}