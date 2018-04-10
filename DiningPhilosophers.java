import java.util.concurrent.Semaphore;

public class DiningPhilosophers {
	static Semaphore mutexS = new Semaphore(1);
	final static int thinking = 0, hungry = 1, eating = 2;
	static Philosopher[] philosophers;
	static int number_of_philosophers;
	static Semaphore s[];

	public static void main(String[] args) throws InterruptedException {
		number_of_philosophers = 5;
		philosophers = new Philosopher[number_of_philosophers];
		s = new Semaphore[number_of_philosophers];
		for (int i = 0; i < number_of_philosophers; i++)
			s[i] = new Semaphore(0);
		for (int i = 0; i < philosophers.length; i++) {
			philosophers[i] = new Philosopher(i, "P" + i);
		}
		for (int i = 0; i < philosophers.length; i++) {
			Thread t = new Thread(philosophers[i]);
			t.start();
		}
		for (int i = 0; i < number_of_philosophers; i++) {
			philosophers[i].join();
		}
	}

	public static class Philosopher extends Thread {
		private String name;
		private int id, state;
		private int food = 1;

		public Philosopher(int id, String name) {
			this.name = name;
			this.id = id;
			this.state = 0;
		}

		public void takeForks() throws InterruptedException {
			mutexS.acquire();
			this.state = hungry;
			test(id);
			mutexS.release();
			s[id].acquire();
		}

		public void putForks() throws InterruptedException {
			mutexS.acquire();
			this.state = thinking;
			think();
			test((id - 1 + number_of_philosophers) % number_of_philosophers);
			test((id + 1) % number_of_philosophers);
			mutexS.release();
		}

		public void test(int i) {
			try {
				if ((philosophers[i].state == hungry)
						&& (philosophers[(i - 1 + number_of_philosophers) % number_of_philosophers].state != eating)
						&& (philosophers[(i + 1) % number_of_philosophers].state != eating)) {
					philosophers[i].state = eating;
					s[i].release();
				}
			} catch (Exception e) {

			}
		}

		public void think() {
			Log.msg(name + ": Thinking");
			Log.Delay(1000);
		}

		public void eat() {
			Log.msg(name + ": Eating " + food + "/3");
			Log.Delay(1000);
		}

		public void run() {
			while (food <= 3) {
				try {
					takeForks();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				eat();
				try {
					putForks();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				this.food++;
			}
		}
	}

	static class Log {
		public static void msg(String msg) {
			 System.out.println(msg);
		}

		public static void Delay(int ms) {
			try {
				Thread.sleep(ms);
			} catch (InterruptedException ex) {
			}
		}
	}
}