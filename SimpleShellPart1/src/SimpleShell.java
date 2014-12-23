import java.io.*;
import java.util.ArrayList;

/**
 * 
 * @author Omar 
 *
 *Description: this is my version of Java based shell. It works on all the commands specified by the assignment.
 *
 */
public class SimpleShell {
	private static final String HOME_DIR = System.getProperty("user.dir"); // contains home directory path
	private static ProcessBuilder pb; 
	private static File currentDir = new File(HOME_DIR); // File to home directory
	private static File changeFileDir; 
	private static ArrayList<Integer> subDirAt = new ArrayList<Integer>();
	private static ArrayList<File> currentDirectory = new ArrayList<File>();
	private static int currentDirIndex = 0;
	private static int commandCounter = 0;
	private static int indexofsubDirArray = 0;
	private static ArrayList<String> userCommandHistory = new ArrayList<String>(); // used to store user history
	private static ArrayList<String> currentCommand = new ArrayList<String>(); // used to store current user command
	private static char[] initialDir = HOME_DIR.toCharArray(); // this is used to store the initial path of home

	

	public static void start() throws IOException {
		String commandLine;
		File changeFileDir;

		currentDirectory.add(currentDirIndex, currentDir);

		for (int i = 0; i < initialDir.length; i++) {
			System.out.print(initialDir[i]);
		}

		for (int i = 0; i < initialDir.length; i++) {
			if (initialDir[i] == '/') {
				subDirAt.add(indexofsubDirArray, i); // adds the '/' at ith position of directory at indexofSubArray position of the subDirAt array
				indexofsubDirArray++;
			}
		}

		BufferedReader console = new BufferedReader(new InputStreamReader(
				System.in));
		// we break out with <control><C>
		while (true) {
			// read what they entered
			System.out.print("jsh>");

			commandLine = console.readLine();

			// if they entered a return, just loop again
			if (commandLine.equals(""))
				continue;
			if (commandLine.equals("exit")) {
				break; // break the while loop and exit the program.
			}
			
			userCommandHistory.add(commandLine);// add the current command to history
			
			/* this was a solution to a big bug. That
			 was happening in for loop
			 below
			 */
			int currentCommandSize = currentCommand.size() - 1; 
			
			/* empty currentCommand so no command
			 from previous run are in the
			 array
			 */
			for (int i = currentCommandSize; i >= 0; i--)
				currentCommand.remove(i);
			
			/*
			 *  Separate the current command into different parts
			 *   such as "cd directoryname" into "cd" and "directoryname"
			 */
			for (int i = 0; i < commandLine.split(" ").length; i++)
				currentCommand.add(i, commandLine.split(" ")[i]);

			if (currentCommand.size() > 1) {
				pb = new ProcessBuilder(currentCommand.subList(0,
						currentCommand.size()));
			} else {
				pb = new ProcessBuilder(currentCommand.subList(0, 1));
			}
			pb = pb.directory(currentDir); // set directory to current directory
			
			switch (currentCommand.get(0)) {

			case "pwd":
				pwd();
				continue;
			case "history":
				history();
				continue;
			case "cd":
				// Set directory to home directory
				if (currentCommand.get(0).equals("cd")
						&& currentCommand.size() == 1) {
					changeToHomeDir();
					continue;

				} else if (currentCommand.get(1).contains("..")) {
					if(!OnlyDotsAndSlahes(currentCommand.get(1))){
						System.out.println("Invalid command");
						commandCounter++;
						continue;
					}
					String[] dots = currentCommand.get(1).split("/"); // String array that contains all the dots
																		
					goUpTheFolder(dots, pb, 0); // use iterative method to go up n folders where n is # of dots
					continue;

				} else {// cd some_dir
					File boolDir = new File(pb.directory() + "/"
							+ currentCommand.get(1));

					if (boolDir.exists()) {

						String realCommand = currentCommand.get(1); // real part

						changeFileDir = new File(currentDir + "/" + realCommand); // create
						subDirAt.add(currentDir.toString().length());
						pb = pb.directory(changeFileDir); // change the dir here
						System.out.println("current Dir = " + pb.directory());
						commandCounter++;

						currentDir = pb.directory();
						currentDirIndex++;
						currentDirectory.add(currentDirIndex, currentDir);
						continue;
					} else {
						String[] invalidDir = userCommandHistory.get(
								commandCounter).split("cd");
						for (int i = 0; i < invalidDir.length; i++) {
							System.out.print(invalidDir[i] + " ");

						}
						System.out
								.println(" is an invalid directory or does not exist");

						commandCounter++;
						continue;
					}
				}
			default:
				try {
					Process process = pb.start();

					InputStream is = process.getInputStream();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String line;
					while ((line = br.readLine()) != null)
						System.out.println(line);
					br.close();
					commandCounter++;// gets the current command
				} catch (Exception e) {
					System.out.println(e + " !");
					commandCounter++;
				}
			}

			/**
			 * The steps are: (1) parse the input to obtain the command and any
			 * parameters (2) create a ProcessBuilder object (3) start the
			 * process (4) obtain the output stream (5) output the contents
			 * returned by the command
			 */
		}
		System.out
				.println("\n\t***********COMMAND SHELL CLOSING******************");
		System.out
				.println("\t***********Thank you for using my command Shell program******************");
		System.out
				.println("\t***********Author: Omar Waheed***************************\n");
	}


	private static boolean OnlyDotsAndSlahes(String dotsAndSlash){
		boolean correct = false;
		for(int i = 0 ; i < dotsAndSlash.length();i++){
			if(dotsAndSlash.charAt(i) == '.' || dotsAndSlash.charAt(i) == '/'){
				correct = true;
			}else{
				return false;
			}
			
		}
		return correct;
	}
	

	private static void history() {
		for (int i = 0; i < userCommandHistory.size(); i++) {
			System.out.println(userCommandHistory.get(i));
		}
		commandCounter++;
	}

	private static void pwd() {
		indexofsubDirArray = 0;
		for (int i = 0; i < initialDir.length; i++) {
			if (initialDir[i] == '/') {
				subDirAt.add(indexofsubDirArray, i); // adds the '/' at ith position of directory at indexofSubArray position of the subDirAt array
				indexofsubDirArray++;
			}
		}
		System.out.println(pb.directory());
	}

	private static void changeToHomeDir(ProcessBuilder pbArg) {
		pb = pbArg.directory(new File(HOME_DIR));
		currentDir = pb.directory();
	}

	private static void changeToHomeDir() { // if command is cd
		changeToHomeDir(pb);
		System.out.println("current Dir = " + pb.directory());
		commandCounter++;
	}

	private static void goUpTheFolder(String[] dots, ProcessBuilder pbArg,
			int start) {
		if (start >= dots.length)// should be a multiple of 2 e.g .., ...., ...... after we get an array of chars that is splitted at regex '/'
		{
			return;
		}

		int end = dots.length;
		ArrayList<Integer> tempSubdir = new ArrayList<Integer>();
		while (start < end) {
			try {
				changeFileDir = new File(currentDir.toString().substring(0,
						subDirAt.get(subDirAt.size() - 1)));
				pb = pbArg.directory(changeFileDir); // now in previous
														// directory
				currentDir = pb.directory();
				if (subDirAt.size() > 0)
					subDirAt.remove(subDirAt.size() - 1);
				start++;
			} catch (Exception e) {
				System.out.println("no more directories below!");
				commandCounter++;
				return;
			}
		}

	}
	
	public static void main(String[] args) throws java.io.IOException {
		SimpleShell myShell = new SimpleShell();
		myShell.start();
	}
}