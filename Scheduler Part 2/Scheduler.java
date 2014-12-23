// import libraries
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.concurrent.*;
/**
 * Scheduler.java
 *
 * This class is a simple round-robin scheduler.
 * The idea for this scheduler came from "Java Threads"
 * by Oaks and Wong (Oreilly, 1999).
 *
 * @author Greg Gagne, Peter Galvin, Avi Silberschatz
 * @version 1.0 - July 15, 1999.
 * Copyright 2000 by Greg Gagne, Peter Galvin, Avi Silberschatz
 * Applied Operating Systems Concepts - John Wiley and Sons, Inc.
 * 
 * Modified by: Zach Herman and Rehan Rasool
 * 
 */

public class Scheduler extends Thread implements ActionListener
{
    private CircularList queue; // declare CircularList called queue
    private int[] timeSlice = new int[3]; // timeSlice/quantam for different queues
    private static final int DEFAULT_TIME_SLICE = 50; // default time slice
    boolean sleeping; // keeps track if scheduler is sleeping
    private static JTextField[][] textfields; // used in GUI
    private JButton start; // GUI start button
    boolean alive; // keeps track if it is alive
    private boolean stopProgram; // boolean to stop the program

    /**
     * Constructor for scheduler
     */
    public Scheduler() {
        this.timeSlice[0] = DEFAULT_TIME_SLICE;
        this.timeSlice[1] = DEFAULT_TIME_SLICE*2;
        this.timeSlice[2] =  Integer.MAX_VALUE;
        queue = new CircularList(this);
        sleeping = false;
        alive = true;
        stopProgram = true;
    }

     /**
     * returns the boolean if the program needs to be stopped / button pressed
     * @return boolean
     */
    public boolean stoptheProgram(){
        return stopProgram;
    }

     /**
     * this method checks if scheduler is sleeping or not
     * @return boolean
     */
    public boolean isSleeping() {
        return sleeping;
    }

    /**
     * Constructor for scheduler 
     * @param int quantam
     */
    public Scheduler(int quantum) {
        this.timeSlice[0] = quantum;
        this.timeSlice[1] = quantum*2;
        this.timeSlice[2] = quantum*3;
        queue = new CircularList(this);
    }

    /**
     * adds a thread to the queue
     * @return void
     */
    public void addThread(Thread t) {
        queue.addItem(t);   
    }

    /**
     * this method puts the scheduler to sleep for a time quantum
     * @return void
     */
    private void schedulerSleep(int i) {
        try {
            synchronized(this)
            {
                sleeping = true;
                if(i==2) {
                    wait(this.timeSlice[i]);
                    
                }else {
                    wait(this.timeSlice[i]*100);
                }
                sleeping = false;
            }
        } catch (InterruptedException e) {};
    }

    /**
     * This method updates the ratios of each thread. This is used in aging.
     */
    public void updateRatios() {
        TestThread[] t = queue.getThreads();
        int limit = 2093890;
        for(int i = 0; i<t.length; i++) {
            t[i].calculateRatio();
            if(Math.abs(t[i].ratio) >= limit) {
                queue.promote(t[i]);
            }
        }
        updateGUI(t);
    }

    /**
     * This method updates the GUI
     */
    public void updateGUI(TestThread[] t) {
        for(int i=0;i<t.length;i++) {
            print(t[i],t[i].getBurst()-t[i].offset);
        }
    }

    /**
     * This method updates the GUI
     */
    public void updateGUI() {
        TestThread[] t = queue.getThreads();
        updateGUI(t);
    }

    /**
     * This is the run method. It runs the scheduler.
     */
    public void run() {

        TestThread current;
        int update = 0;
        // set the priority of the scheduler to the highest priority
        this.setPriority(6);
        while (!queue.isEmpty()) {
            try {
                if (!this.stoptheProgram()){
                    if(update==10) {
                        update = 0;
                        updateRatios();
                    }
                    current = (TestThread)queue.getNext();
                    if ( (current != null) && (current.isAlive()) ) {
                        System.out.println(" dispatching " + current);
                        current.sleeping = false;
                        current.setPriority(4);
                        int l = queue.currentLevel;

                        schedulerSleep(l);

                        current.sleeping = true;
                        queue.removeItem(current);
                        queue.addItem(current,false);
                        System.out.println("* * * Context Switch * * * ");
                        System.out.println(" preempting " + current);
                        current.setPriority(2);
                    } else if( !current.isAlive()) {
                        current.start();
                    }
                    update++;
                    updateGUI();
                }

            } catch (NullPointerException e3) { } ;
        }
    }

    /**
     * This method creates the GUI
     */
    public void createGUI() {
        JFrame f = new JFrame("Scheduler");
        f.setSize(700, 150);
        Container content = f.getContentPane();
        content.setBackground(Color.white);
        content.setLayout(new GridLayout(0,7)); 
        textfields = new JTextField[3][6];
        String thread_name = "Thread ";

        JTextField thread_title = new JTextField("Thread #");
        thread_title.setEditable(false);
        content.add(thread_title);

        JTextField[] labels = new JTextField[6];
        for(int z = 1; z<7; z++) {
            labels[z-1] = new JTextField(thread_name+z);
            labels[z-1].setEditable(false);
            content.add(labels[z-1]);
        }

        for(int i=0; i<3; i++) {
            JTextField cat = new JTextField("Priority "+i);
            cat.setEditable(false);
            content.add(cat);
            for(int j=0; j<6;j++) {
                textfields[i][j] = new JTextField();
                textfields[i][j].setEditable(false);
                content.add(textfields[i][j]);
            }
        }
        JTextField Q0 = new JTextField("Q0: "+this.timeSlice[0]);
        Q0.setEditable(false);
        JTextField Q1 = new JTextField("Q1: "+this.timeSlice[1]);
        Q1.setEditable(false);
        start = new JButton("START");
        content.add(Q0);
        content.add(Q1);
        content.add(start);
        start.addActionListener(this);
        f.setVisible(true);
    }

    /**
     * This method is used for the start/stop button
     * @param ActionEvent evt
     */
    public void actionPerformed(ActionEvent evt) {
        Object src = evt.getSource();
        if (src == start) {
            if(alive) {
                stopProgram = false;
                start.setText("STOP");
                //System.out.println("ALIVE");
            }
            else {
                stopProgram = true;
                start.setText("START");
                //System.out.println("NOT");

            }
            alive = !alive;
        }
    }

    /**
     * This method prints the data to the GUI
     */
    public void print(TestThread t,int time) {
        for(int i = 0; i<3; i++) {
            textfields[i][(Integer.parseInt(t.toString())-1)].setText("");
        }
        textfields[t.priority][(Integer.parseInt(t.toString())-1)].setText(time+"");
        //System.out.println(time);
    }

}

