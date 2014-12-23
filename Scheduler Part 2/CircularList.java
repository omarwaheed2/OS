/**
 * CircularList.java
 *
 * This class implements a circular list using the Vector class
 * note that elements in a vector with n elements are numbered 0 .. (n-1)
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

public class CircularList 
{
    // initialise global variables
    private Vector[] Lists; // Lists vector contains the 3 queues
    private int index; // index keeps track of the index data of the queue
    int size; // keeps track of list size
    public int currentLevel; // stores the level of the current queue
    Scheduler CPUScheduler; // CPU scheduler

    /**
     * constructor
     * @param Scheduler CPUScheduler
     */
    public CircularList(Scheduler CPUScheduler) {
        Lists = new Vector[3]; // initialised to 3 queues to store the 3 scheduling queues
        for(int i=0; i<Lists.length;i++)
            Lists[i] = new Vector(10);
        index = 0; 
        currentLevel = 0;
        this.CPUScheduler = CPUScheduler;
    }

    /**
     * getThread method to get the threads
     * @return TestThread[]
     */
    public TestThread[] getThreads() {
        TestThread[] t = new TestThread[6];
        int index = 0;
        // goes through the list and returns TestThread array with particular data
        for(int i =0; i<Lists.length;i++) {
            for(int j=0; j<Lists[i].size(); j++) {
                t[index++] = (TestThread)(Lists[i].elementAt(j));
            }
        }
        return t;
    }

    /**
     * this method returns the next element in the list.
     * @return Object
     */
    public Object getNext() {
        Object nextElement = null;
        int lastElement;
        if (!Lists[0].isEmpty() ) {
            nextElement = Lists[0].elementAt(findLowest(0));
            currentLevel = 0;
        }
        else if(!Lists[1].isEmpty()) {
            nextElement = Lists[1].elementAt(findLowest(1));
            currentLevel = 1;
        }
        else if(!Lists[2].isEmpty()) {
            nextElement = Lists[2].elementAt(0);
            currentLevel = 2;
        }
        return nextElement;
    }

    /**
     * method checks if list is empty
     * @return boolean
     */
    public boolean isEmpty ()
    {
        return Lists[0].isEmpty()&&Lists[1].isEmpty()&&Lists[2].isEmpty();
    }

    /**
     * method finds the lowest element in a particular queue
     * @param int level
     * @return int
     */
    public int findLowest(int level) {
        int min = 1000000000;
        int index = -1;
        for(int i=0; i<Lists[level].size(); i++) {
            TestThread t = (TestThread)Lists[level].elementAt(i);
            if(t.getBurst() < min) {
                min = t.getBurst();
                index = i;
            }
        }
        return index;
    }

    /**
     * this method adds an item to the list
     * @return void
     */
    public void addItem(Object t) {
        Lists[0].addElement(t);      
        size++;
    }

    /**
     * this method adds an item to the list
     * @param Object t, boolean q
     * @return void
     */
    public void addItem(Object t, boolean q) {
        if((( (!((TestThread)t).done) &&((TestThread)t).priority < 2)) &&  !q) {
            ((TestThread)t).priority++;
        }
        Lists[((TestThread)t).priority].addElement(t);      
        CPUScheduler.print(((TestThread)t),((TestThread)t).getBurst());
        size++;
    }

     /**
     * this method promotes the given object to a higher queue. This method is used in aging.
     * @param Object o
     * @return void
     */
    public void promote(Object o) {
        TestThread t = ((TestThread)o);
        if(t.priority!=0) {
            removeItem(t);
            t.priority--;
            addItem(t,true);
        }
    }

     /**
     * this method removes an item from the list
     * @param Object t
     * @return void
     */
    public void removeItem(Object t) {
        Lists[((TestThread)t).priority].remove(t);
        size--;
    }
    
     /**
     * this method reinitialises the Lists and its data
     * @return void
     */
    public void nuke() {
        for(int i=0; i<Lists.length;i++)
            Lists[i] = new Vector(10);
        index = 0;
        currentLevel = 0;
    }

     /**
     * this is a helper method to print the queues
     * @return void
     */
    public void printQueues() {
        for(int i=0;i<Lists.length;i++) {
            System.out.println("****QUEUE "+i+" ***************");
            for(int k=0; k<Lists[i].size();k++) {
                TestThread t = (TestThread)(Lists[i].elementAt(k));
                System.out.println(k+": "+t.toString()+" with a burst of: "+t.getBurst());
            }
            System.out.println("********************************");
        }
    }
}
