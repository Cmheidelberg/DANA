package com.dana;

import java.io.*;
import java.io.StringReader;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.WingsConnection;

import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.JFileChooser;

public class Dana {

	// This path is used so a path does not have to be entered each time while
	// developing. It represents the json from wings
	static String workflowPath = "examples/Caesar_Cypher.json";

	public static void main(String[] args) {
		StringBuilder contentBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader("password"))) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				contentBuilder.append(sCurrentLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String password = contentBuilder.toString();
		WingsConnection wings = new WingsConnection(password);

		// Read in
		JsonObject workflowJson = openWorkflow(false);
		WorkflowJson workflow = new WorkflowJson(workflowJson);

		JsonWriter jw = new JsonWriter(workflow);
		//jw.writeJson(); //comment out to prevent rewriting of readData.json every run
		NarrativeGenerator ng = new NarrativeGenerator(workflow);
		
		if (workflow.readDanaJson("readData.json")) {

			// Print workflows debug tostring
			System.out.println("NODE METADATA DEBUG: ");
			for (WorkflowNode wn : workflow.getWorkflowNodes()) {
				System.out.println("====" + wn.getFullName() + "====");
				System.out.println(wn);
			}

			System.out.println("DATA NARRATIVES FOR EACH STEP: ");
			for (WorkflowNode s : workflow.getWorkflowNodes()) {
				System.out.println("+++" + s.getDisplayName() + "+++");
				System.out.println(ng.getNodeNarrative(s));
				System.out.println("\n");
			}

			//Debug print of citations when multiple node narratives are printed
			for (String s : ng.getAllCitations()) {
				System.out.println(s);
			}

			
			System.out.println("\n[Workflow Fragments:]");
			for (Fragment f: workflow.getWorkflowFragments()) {
				System.out.println("Name: " + f.getName() + "| Description: " + f.getDescription());
				System.out.print("Associated Node(s): ");
				for(WorkflowNode n : f.getNodes()) {
					System.out.print(n.getFullName() + ", ");
				}
				System.out.print("\n");
			}
			
			System.out.println("\nWorkflow Narrative debug info:");
			//Work in progress: Workflow Narrative generation
			NarrativeGenerator ngWorkflow = new NarrativeGenerator(workflow);

			String workflowNarrative = ngWorkflow.getWorkflowNarrative(1);
			System.out.println(workflowNarrative);

			for (String s : ngWorkflow.getAllCitations()) {
				System.out.println(s);
			}
		}
		// danaCliMenu();

	}

	/**
	 * Open a json file as a JsonObject. If the class variable workflowPath is
	 * defined (and overrideWorkflowPath is false) it will open the json from that
	 * path. Otherwise, a file selection menu will appear asking the user to provide
	 * a json file. Note: in testing this menu does not have default focus for
	 * windows, so it might appear behind your IDE or terminal.
	 * 
	 * @param overrideWorkflowPath boolean flag representing if method should prompt
	 *                             user for a path to json file.
	 * @return JsonObject for file provided in path
	 */
	public static JsonObject openWorkflow(boolean overrideWorkflowPath) {
		JsonObject doc = null;
		String path = "";

		// If the workflow path isnt hard-coded prompt the user for a path
		if (workflowPath.length() == 0 || overrideWorkflowPath) {
			final JFileChooser fc = new JFileChooser();
			
			FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON files", "json");
			fc.addChoosableFileFilter(filter);
			//fc.setCurrentDirectory(new File("C:\\Users\\Admin\\Desktop"));
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
		NarrativeGenerator ng = null;
		if (workflow.readDanaJson("readData.json")) {
			ng = new NarrativeGenerator(workflow);
		} else {
			System.out.println("Please read in a new Wings JSON");
		}

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
				JsonWriter jw = new JsonWriter(workflow);
				jw.writeJson();

				System.out
						.println("Please enter in additional metadata to readData.json and hit any button to continue");
				scan.next();

				if (workflow.readDanaJson("readData.json")) {
					ng = new NarrativeGenerator(workflow);
				} else {
					System.out.println("Error. Invalid metadata entered!");
				}

				System.out.println("Succesfully generated new narrative");
				break;
			case 1:
				if (!ng.equals(null)) {
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
				} else {
					System.out.println(
							"Workflow was never serialized! Please read in a new wings json and enter the appropriate metadata");
				}
				break;
			case 2:
				System.out.println("Enter a criticality value (1-5)");

				try {
					int cv = Integer.parseInt(scan.nextLine());
					ng.getWorkflowNarrative(cv);
					// System.out.println(ng.getWorkflowNarrative(cv));

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
