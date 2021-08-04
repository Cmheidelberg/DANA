package dana;

import java.io.*;
import java.io.StringReader;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.JFileChooser;

public class Dana {

	static String workflowPath = "\\C:\\Users\\Admin\\Desktop\\Caesar_Cypher.json";

	public static void main(String[] args) {

		JsonObject workflowJson = openWorkflow(false);
		WorkflowJson workflow = new WorkflowJson(workflowJson);

		JsonWriter jw = new JsonWriter(workflow.getWorkflowNodes());
		//jw.writeJson(); //comment out to prevent rewriting of readData.json every run
		NarrativeGenerator ng = new NarrativeGenerator(workflow);
		
		workflow.readDanaJson("C:\\Users\\Admin\\eclipse-workspace\\DANA\\readData.json");
			
		for(WorkflowNode s : workflow.getWorkflowNodes()) {
			System.out.println("+++" + s.getDisplayName() + "+++");
			System.out.println(ng.getNodeNarrative(s));
			System.out.println("\n");
		}
		
		for(String s : ng.getAllCitations()) {
			System.out.println(s);
		}
		
		NarrativeGenerator ngWorkflow = new NarrativeGenerator(workflow);
		
		String workflowNarrative = ngWorkflow.getWorkflowNarrative(1);
		System.out.println(workflowNarrative);
		
		for(String s : ngWorkflow.getAllCitations()) {
			System.out.println(s);
		}
		//danaCliMenu();

	}

	public static JsonObject openWorkflow(boolean overrideWorkflowPath) {
		JsonObject doc = null;
		String path = "";

		// If the workflow path isnt hard coded prompt the user for a path
		if (workflowPath.length() == 0 || overrideWorkflowPath) {
			final JFileChooser fc = new JFileChooser();

			FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON files", "json");
			fc.addChoosableFileFilter(filter);
			fc.setCurrentDirectory(new File("C:\\Users\\Admin\\Desktop"));
			fc.setFileFilter(filter);
			int returnVal = fc.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				path = fc.getSelectedFile().getPath();
			}
		} else {
			path = workflowPath;
		}
		String jsonString = readFile(path);
		JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
		JsonObject object = jsonReader.readObject();
		jsonReader.close();

		return object;
	}

	/**
	 * Helper function for openWorkflow(). Returns a string representation of the
	 * provided filePath
	 */
	private static String readFile(String filePath) {
		workflowPath = filePath;
		StringBuilder contentBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				contentBuilder.append(sCurrentLine).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return contentBuilder.toString();
	}
	
	
	/**
	 * Cli menu for DANA. This is the command line interface representation of DANA.
	 * This is designed for debugging and is not a representation of the final
	 * product.
	 */
	public static void danaCliMenu() {
		JsonObject workflowJson = openWorkflow(false);
		WorkflowJson workflow = new WorkflowJson(workflowJson);
		workflow.readDanaJson("C:\\Users\\Admin\\eclipse-workspace\\DANA\\readData.json");
		NarrativeGenerator ng = new NarrativeGenerator(workflow);
		Scanner scan = new Scanner(System.in);
		String question = "Please select an action (" + workflowPath + ")";
		String[] choices = { "Read Wings JSON", "Get all node descriptions", "Get workflow description", "Quit" };
		boolean cont = true;

		while (cont) {
			int choice = simpleMenu(question, choices);
			System.out.println("");
			switch (choice) {
			case 0:

				// Ask before overwriting existing file
				System.out.println("This will overwrite the existing readData.json. Are you sure? [y/n]");
				String sureCheck = scan.nextLine();
				if (!sureCheck.equalsIgnoreCase("y")) {
					System.out.println("Returning to main menu");
					break;
				}

				
				workflowJson = openWorkflow(true);
				System.out.println(workflowJson);
				workflow = new WorkflowJson(workflowJson);
				JsonWriter jw = new JsonWriter(workflow.getWorkflowNodes());
				jw.writeJson();

				System.out
						.println("Please enter in additional metadata to readData.json and hit any button to continue");
				scan.next();

				workflow.readDanaJson("C:\\Users\\Admin\\eclipse-workspace\\DANA\\readData.json");
				ng = new NarrativeGenerator(workflow);

				System.out.println("Succesfully generated new narrative");
				break;
			case 1:
				System.out.println("Printing " + workflow.getWorkflowNodes().size() + " narratives:\n");

				for (WorkflowNode s : workflow.getWorkflowNodes()) {
					System.out.println("+++" + s.getDisplayName() + "+++");
					System.out.println(ng.getNodeNarrative(s));
					System.out.println("");
				}

				if (ng.getAllCitations().size() > 0) {
					System.out.println("Citations:");
					for (String s : ng.getAllCitations()) {
						System.out.println(s);
					}
				}

				System.out.println("Hit any button to continue");
				scan.nextLine();

				break;
			case 2:
				System.out.println("Enter a criticality value (1-5)");

				try {
					int cv = Integer.parseInt(scan.nextLine());
					ng.getWorkflowNarrative(cv);
					//System.out.println(ng.getWorkflowNarrative(cv));

//					if (ng.getAllCitations().size() > 0) {
//						System.out.println("Citations:");
//						for (String s : ng.getAllCitations()) {
//							System.out.println(s);
//						}
//					}

				} catch (NumberFormatException nfe) {
					System.out.println("Input must be a number");
				}

				break;
			case 3:
				System.out.println("Quitting!");
				cont = false;
				break;
			}
		}
		scan.close();
	}
	
	

	/**
	 * Simple menu to prompt the user with a list of choices. This method will ask
	 * the user to input a number to perform a corresponding action and return the
	 * choice selected.
	 * 
	 * @param question: question to prompt user with
	 * @param options: string array of options user can select
	 * @return value: int representing the index of the option selected
	 */
	private static int simpleMenu(String question, String[] options) {
		Scanner scan = new Scanner(System.in);
		boolean lastError = false;
		while (true) {

			if (lastError) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException ie) {
					System.out.println("Thread interrupted");
					ie.printStackTrace();
				}
			}

			lastError = false;
			System.out.println("\n" + question);
			for (int i = 0; i < options.length; i++) {
				System.out.println((i + 1) + ". " + options[i]);
			}

			String inp = scan.nextLine();
			try {
				int choice = Integer.parseInt(inp);
				choice = choice - 1;
				if (choice >= 0 && choice < options.length) {
					return choice;
				} else {
					System.out.println("Invalid Range");
					lastError = true;
				}
			} catch (NumberFormatException nfe) {
				System.out.println("Invalid Input Value");
				lastError = true;
			}
		}
	}


}
