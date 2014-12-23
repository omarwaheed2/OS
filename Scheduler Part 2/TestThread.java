/**
 * TestThread.java
 * 
 * This thread is used to demonstrate how the scheduler operates.
 * This thread runs forever, periodically displaying its name.
 *
 * @author Greg Gagne, Peter Galvin, Avi Silberschatz
 * @version 1.0 - July 15, 1999.
 * Copyright 2000 by Greg Gagne, Peter Galvin, Avi Silberschatz
 * Applied Operating Systems Concepts - John Wiley and Sons, Inc.
 * 
 * Modified by: Zach Herman and Rehan Rasool
 * 
 */

// import libraries
import java.util.*;

class TestThread extends Thread
{   
    // import global variables
    private String name; // stores name
    private Random r; // generates random values
    private int burstTime; // stores burst time
    Scheduler CPUScheduler; // the scheduler
    boolean sleeping = true; // keeps track if its sleeping
    int priority; // stores the priority
    boolean done = false; // keeps track if current thread is done or not
    double ratio; // stores ratio. This is used in aging
    int wait = 0; // stores wait data
    int offset;

    /**
     * TestThread constructor
     * @param String id, int burstTime, Scheduler CPUScheduler
     * 
     */
    public TestThread(String id, int burstTime, Scheduler CPUScheduler) {
        name = id;
        r = new Random();
        this.burstTime = burstTime;
        this.burstTime = 50+r.nextInt(300);
        this.CPUScheduler = CPUScheduler;
        priority = 0;
        ratio = 0;
    }

    /**
     * this method returns name
     * @return String
     */
    public String toString () {
        return name;
    }

    /**
     * this method gets burst time
     * @return int
     */
    public int getBurst() {
        return burstTime;
    }
    
    /**
     * this method calculates ratio. (used for aging)
     * @return double
     */
    public double calculateRatio() {
        ratio = (this.burstTime + wait)/this.burstTime;
        return ratio;
    }

    /**
     * This is the run method.
     */
    public void run() {
        /** 
         * The thread does something
         **/
        while (true) {
            //System.out.println(this.name+ "\'s burst time: "+this.burstTime);
            
            for (int i = 0; i < burstTime; i++) {
                offset = i;
                wait = 0;
                while(sleeping){
                    wait++;
                }
                
                done=false;
                try {
                    //Prevent overusing CPU by sleeping briefly
                    Thread.sleep(100);
                } catch (InterruptedException e) 
                {}
                ;
                
                //System.out.println(this.name + " " +(this.burstTime-i) + " " + this.priority);
                if (CPUScheduler.stoptheProgram()){
                    i--;
                } else {
                    CPUScheduler.print(this,(this.burstTime-i));
                }
            }
            synchronized(CPUScheduler){
                done = true;
                sleeping=true;
                
               System.out.println(this.name + " " +(this.burstTime) + " " + this.priority);
               this.burstTime = 50+r.nextInt(300);
               CPUScheduler.print(this,burstTime);
               CPUScheduler.notify();
            }
            
        }
    
    }
}
