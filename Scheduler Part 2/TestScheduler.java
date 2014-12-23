 /** 
 * Class to Test the Scheduler
 * 
 * Modified by: Zach Herman and Rehan Rasool
 * 
 * 
 * */

public class TestScheduler  
{
    public static void main(String args[]) {
        /**
         * This must run at the highest priority to ensure that
         * it can create the scheduler and the example threads.
         * If it did not run at the highest priority, it is possible
         * that the scheduler could preempt this and not allow it to
         * create the example threads.
         */
        
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        Scheduler CPUScheduler = new Scheduler(); // initialise the scheduler

        TestThread[] t = new TestThread[6]; // initialise testthreads
        
        for(int i = 0; i<t.length; i++) {
            t[i] = new TestThread((i+1)+"", 200,CPUScheduler);
            CPUScheduler.addThread(t[i]);
        }
        
        CPUScheduler.createGUI();
        
        for(int i = 0; i<t.length; i++) {
            CPUScheduler.print(t[i],t[i].getBurst());
        }
        
        CPUScheduler.start();
    }
}
