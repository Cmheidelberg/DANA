package dana;

import javax.swing.JFileChooser;
import java.io.*;
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class Dana {

	final static String workflowPath = "\\C:\\Users\\Admin\\Desktop\\Caesar_Cypher.json";

	public static void main(String[] args) {

		JsonObject workflowJson = openWorkflow();
		WorkflowJson workflow = new WorkflowJson(workflowJson);

		JsonWriter jw = new JsonWriter(workflow.getWorkflowNodes());
		//jw.writeJson();

		System.out.println("+++READ JSON+++");
		workflow.readDanaJson("./readData.json");
		for(WorkflowNode wf : workflow.getWorkflowNodes()) {
			System.out.println("\n" + wf.getName() + " description: ");
			System.out.println(workflow.generateNodeNarrative(wf.getName()));
		}
		
		System.out.println("\n");
		System.out.println("High level narrative: ");
		System.out.println(workflow.generateHighLevelNarrative());
		
		System.out.println("\n");
		System.out.println("Med level narrative (cv 1): ");
		System.out.println(workflow.generateMediumLevelNarrative(1));
		
		System.out.println("\n");
		System.out.println("Med level narrative (cv 2): ");
		System.out.println(workflow.generateMediumLevelNarrative(2));

	}

	public static JsonObject openWorkflow() {
		JsonObject doc = null;
		String path = "";

		// If the workflow path isnt hard coded prompt the user for a path
		if (workflowPath.length() == 0) {
			final JFileChooser fc = new JFileChooser();
			int returnVal = fc.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				path = fc.getSelectedFile().getPath();
			}
		} else {
			path = workflowPath;
		}

		String jsonString = readFile(workflowPath);
		JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
		JsonObject object = jsonReader.readObject();
		jsonReader.close();

		return object;
	}

	private static String readFile(String filePath) {
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

}
