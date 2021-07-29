package dana;

import javax.swing.JFileChooser;
import java.io.*;
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class Dana {

	final static String workflowPath = "\\C:\\Users\\Admin\\Desktop\\tmp.json";

	public static void main(String[] args) {

		JsonObject workflowJson = openWorkflow();
		WorkflowJson workflow = new WorkflowJson(workflowJson);

		JsonWriter jw = new JsonWriter(workflow.getWorkflowNodes());
		//jw.writeJson(); //comment out to prevent rewriting of readData.json every run
		NarrativeGenerator ng = new NarrativeGenerator(workflow);
		
		workflow.readDanaJson("C:\\Users\\Admin\\eclipse-workspace\\DANA\\readData.json");
			
		for(WorkflowNode s : workflow.getWorkflowNodes()) {
			System.out.println("+++" + s.getName() + "+++");
			System.out.println(ng.getNodeNarrative(s));
			System.out.println("\n");
		}
		
		for(String s : ng.getAllCitations()) {
			System.out.println(s);
		}
		
		NarrativeGenerator ngWorkflow = new NarrativeGenerator(workflow);
		
		String workflowNarrative = ngWorkflow.getWorkflowNarrative();
		System.out.println(workflowNarrative);
		System.out.println(ngWorkflow.getCitation("workflow"));
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
