import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * @author Omar Waheed
 * description: This is a class for slot machine. It uses 3 objects from CounterWithThread which are running independently from each other.
 *
 */

public class Slots extends Frame {

	private final int NUM_OF_SLOTS = 3;
	private ArrayList<CounterWithThread> slots = new ArrayList<CounterWithThread>();
	private TextField textWindow1;
	private TextField textWindow2;
	private TextField textWindow3;
	private TextField winLoseWindow;

	private Button toggleButton1;
	private boolean stop;
	private CounterRunner runTheSlots = new CounterRunner();
	public static final int HEIGHT = 300;
	public static final int WIDTH = 300;

	public Slots() {
		stop = true;
		setSize(WIDTH, HEIGHT);
		setTitle("Slot Machine");
		setBackground(new Color (50,100,200));
		setLayout(new FlowLayout());
		addWindowListener(new WindowDestroyer());

		for (int i = 0; i < NUM_OF_SLOTS; i++) { //insert all the instance of CounterWiththread objects, which act as individual and independent slots
			System.out.println("adding slots");
			slots.add(new CounterWithThread());
		}

		textWindow1 = new TextField(2);
		add(textWindow1);
		textWindow1.setText("*");

		textWindow2 = new TextField(2);
		add(textWindow2);
		textWindow2.setText("*");

		textWindow3 = new TextField(2);
		add(textWindow3);
		textWindow3.setText("*");

		toggleButton1 = new Button("Play");
		toggleButton1.addActionListener(new ToggleButtonListener());
		add(toggleButton1);

		winLoseWindow = new TextField(20);
		add(winLoseWindow);
		winLoseWindow.setText("Starting");

		setVisible(true);
		

	}

	public static void main(String[] args) {
		System.out.println("Starting slot machine");
		Slots slotMachine = new Slots();

	}

	private class CounterRunner extends Thread {

		public void run() {
			while (true) {
				try {
					sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!stop) { // if not stop i.e game is running -> stop = false
					winLoseWindow.setText("Playing...");
					textWindow1.setText(Integer.toString(slots.get(0)
							.getCount()));
					textWindow2.setText(Integer.toString(slots.get(1)
							.getCount()));
					textWindow3.setText(Integer.toString(slots.get(2)
							.getCount()));

				} else {
					winLoseWindow.setText("Waiting");
					if (slots.get(0).isStopped() || slots.get(1).isStopped() || slots.get(2).isStopped()) {// until all three slots are stopped
						textWindow1.setText(Integer.toString(slots.get(0)
								.getCount()));
						textWindow2.setText(Integer.toString(slots.get(1)
								.getCount()));
						textWindow3.setText(Integer.toString(slots.get(2)
								.getCount()));
					} else { // when all three slots stop display the winning status of the player
						
						if (slots.get(0).getCount() == slots.get(1).getCount() && slots.get(0).getCount() == (slots.get(2).getCount())) {
							winLoseWindow.setText("You Win!!!");
						} else {
							winLoseWindow.setText("You Lose!!!");
						}
					}

				}
			}

		}

	}

	private class ToggleButtonListener implements ActionListener {
		Thread runIt = new Thread(runTheSlots);
		
		public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Stop")) {
				toggleButton1.setLabel("Play");

				stop = true; // if label is Play then game is stopped hence stop = true
				for (int i = 0; i < slots.size(); i++)
					slots.get(i).setStop(stop);

			} else if (e.getActionCommand().equals("Play")) {
				toggleButton1.setLabel("Stop");

				stop = false;// if label is Stop then game is being played hence stop = false
				for (int i = 0; i < slots.size(); i++)
					slots.get(i).setStop(stop);

			}
			if (!runIt.isAlive()) // just to prevent starting the thread again and again
				runIt.start();

		}// actionPerformed
	}

}
