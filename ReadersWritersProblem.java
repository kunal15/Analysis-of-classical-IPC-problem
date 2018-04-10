import java.util.concurrent.Semaphore;

public class ReadersWritersProblem {
    
    
    static Semaphore readLock = new Semaphore(1);
    static Semaphore writeLock = new Semaphore(1);
    static Semaphore readerTry = new Semaphore(1);
    static int readCount = 0;
    
    static int x, n_readers = 8, n_writers = 8;
    
    static class Read extends Thread {
        @Override
        public void run() {
            try {
                //Acquire Section
                readerTry.acquire();
                readLock.acquire();
                readerTry.release();
                readCount++;
                if (readCount == 1) {
                    writeLock.acquire();
                }
                readLock.release();
                
                //Reading section
                System.out.println("Thread "+Thread.currentThread().getName() + " is READING");
                Thread.sleep(100);
                System.out.println("Thread "+Thread.currentThread().getName() + " has FINISHED READING");

                //Releasing section
                readLock.acquire();
                readCount--;
                if(readCount == 0) {
                    writeLock.release();
                }
                readLock.release();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    static class Write extends Thread {
        @Override
        public void run() {
            try {
                readerTry.acquire();
                writeLock.acquire();
                System.out.println("Thread "+Thread.currentThread().getName() + " is WRITING");
                Thread.sleep(100);
                System.out.println("Thread "+Thread.currentThread().getName() + " has finished WRITING");
                writeLock.release();
                readerTry.release();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    public static class Readers extends Thread{
        public void run() {
            Read r[] = new Read[n_readers];
            for(int i = 0;i<n_readers;i++) {
                 r[i] = new Read();
                 r[i].setName("Reader : " +i);
                 r[i].start();
            }
            for(int i = 0; i< n_readers; i++) {
                try {
                    r[i].join();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    public static class Writers extends Thread{
        public void run() {
            Write w[] = new Write[n_writers];
            for(int i = 0;i<n_writers;i++) {
                w[i] = new Write();
                    w[i].setName("Writer : " +i);
                    w[i].start();
            }
            for(int i = 0; i< n_writers; i++) {
                try {
                    w[i].join();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    public static void main(String[] args) throws InterruptedException {
        // TODO Auto-generated method stub
            Readers r = new Readers();
                    r.start();
        
            Writers w = new Writers();
                    w.start();
            r.join();
            w.join();
        
    }
    
}