package dana;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import com.sun.tools.javac.comp.Todo;

import java.util.*;

/**
 * @author Christopher Heidelberg
 *
 *         Represents an ArrayList of every node in the workflow with links to
 *         any inputs and outputs. There are also helper methods for finding
 *         relationships between nodes.
 */
public class WorkflowJson {

	private JsonObject json;
	private String workflowDescription = "will encript the unput document using a caesar cypher algorithm";
	private ArrayList<WorkflowNode> workflows;

	public WorkflowJson(JsonObject json) {
		this.json = json;
		workflows = new ArrayList<WorkflowNode>();

		// Populate the workflows array list with all nodes from the workflow as well as
		// gather metadata on them from the json.
		generateWorkflowNodesListFromWings();
	}

	/**
	 * Returns a full list of all nodes in this workflow
	 * 
	 * @return list of workflow nodes
	 */
	public ArrayList<WorkflowNode> getWorkflowNodes() {
		return workflows;
	}

	/**
	 * Get a WorflowNode from the workflows array from the given id. id can either
	 * be the name of the workflow or its id. If the workflow is not in the list
	 * then this function returns null.
	 * 
	 * @param id Name or Id string of the node to search for
	 * @return WorkflowNode with the given name/id or null if does not exist
	 */
	public WorkflowNode getWorkflowNode(String indicator) {

		// O(n)
		for (WorkflowNode wn : workflows) {
			String name = wn.getName();
			String id = wn.getId();

			// Return workflow if it has the same name
			if (name.equalsIgnoreCase(indicator)) {
				return wn;
			}

			// Return workflow if it has the same id
			if (id.equalsIgnoreCase(indicator)) {
				return wn;
			}
		}

		// Return null if nothing matched
		return null;
	}

	/**
	 * Returns outputs of the workflow. An output is any dataset node that does not
	 * have any outgoing links
	 * 
	 * @return output datasets
	 */
	public ArrayList<DatasetNode> getInputs() {

		ArrayList<DatasetNode> inputNodes = new ArrayList<DatasetNode>();
		for (WorkflowNode wn : workflows) {
			if (wn.getIncomingLinks() == null) {
				inputNodes.add((DatasetNode) wn);
			}
		}
		return inputNodes;

	}

	/**
	 * Returns outputs of the workflow. An output is any dataset node that does not
	 * have any outgoing links
	 * 
	 * @return output datasets
	 */
	public ArrayList<DatasetNode> getOutputs() {

		ArrayList<DatasetNode> outputNodes = new ArrayList<DatasetNode>();
		for (WorkflowNode wn : workflows) {
			if (wn.getOutgoingLinks() == null) {
				outputNodes.add((DatasetNode) wn);
			}
		}
		return outputNodes;

	}

	/**
	 * Returns a list of every step node in the workflow. These are the nodes that
	 * run code
	 * 
	 * @return array list of step nodes
	 */
	public ArrayList<StepNode> getSteps() {

		ArrayList<StepNode> stepNodes = new ArrayList<StepNode>();
		for (WorkflowNode wn : workflows) {
			if (!wn.isDataset()) {
				stepNodes.add((StepNode) wn);
			}
		}
		return stepNodes;
	}

	/**
	 * Returns a list of nodes that are either a parent or child of this node and
	 * all sub children and parents
	 * 
	 * @param node node to find full path of
	 * @return list of nodes that are either children or parents of the given node
	 */
	public ArrayList<WorkflowNode> getFullWorkflowPathFromNode(WorkflowNode node) {
		ArrayList<WorkflowNode> fullPath = new ArrayList<WorkflowNode>();
		ArrayList<WorkflowNode> parents = getParents(node);
		ArrayList<WorkflowNode> children = getChildren(node);

		if (parents != null) {
			for (WorkflowNode p : parents) {
				if (!fullPath.contains(p)) {
					fullPath.add(p);
				}
			}
		}

		fullPath.add(node);

		if (children != null) {
			for (WorkflowNode c : children) {
				if (!fullPath.contains(c)) {
					fullPath.add(c);
				}
			}
		}

		if (fullPath.size() > 0) {
			return fullPath;
		} else {
			return null;
		}
	}

	/**
	 * Returns a list of nodes that are a parent of the given node. A parent node is
	 * any node above the given node in the workflow that eventually links to the
	 * given node.
	 * 
	 * @param node node to find parents of
	 * @return list of nodes that are parents of the given node
	 */
	public ArrayList<WorkflowNode> getParents(WorkflowNode node) {

		if (node.getIncomingLinks() == null) {
			return null;
		}

		ArrayList<WorkflowNode> parents = new ArrayList<WorkflowNode>();
		for (WorkflowNode p : node.getIncomingLinks()) {
			getParentsRecursivePopulate(p, parents);
		}

		if (parents.size() > 0)
			return parents;
		else
			return null;
	}

	/**
	 * Returns a list of nodes that are a children of the given node. A child node
	 * is any node above the given node in the workflow that the given node
	 * eventually links to.
	 * 
	 * @param node node to find parents of
	 * @return list of nodes that are parents of the given node
	 */
	public ArrayList<WorkflowNode> getChildren(WorkflowNode node) {

		if (node.getOutgoingLinks() == null) {
			return null;
		}

		ArrayList<WorkflowNode> children = new ArrayList<WorkflowNode>();
		for (WorkflowNode c : node.getOutgoingLinks()) {
			getChildrenRecursivePopulate(c, children);
		}

		if (children.size() > 0)
			return children;
		else
			return null;
	}

	/**
	 * Read in the json generated by DANA after it has been manually filled out.
	 * This will populate the metadata fields for each of the workflow nodes
	 * 
	 * @param path to json
	 */
	public void readDanaJson(String path) {
		String danaJsonString = readFile(path);
		JsonReader jsonReader = Json.createReader(new StringReader(danaJsonString));
		JsonObject danaJson = jsonReader.readObject();
		jsonReader.close();

		// Add dataset metadata
		String[] datasetArr = { "nodes", "datasets" };
		JsonObject datasets = getValue(danaJson, datasetArr);
		Set<String> datasetsleKeys = datasets.keySet();
		for (String key : datasetsleKeys) {
			DatasetNode curr = (DatasetNode) getWorkflowNode(key);
			String[] currDatasetArr = { "nodes", "datasets", key };
			JsonObject currKeyJson = getValue(danaJson, currDatasetArr);

			String description = currKeyJson.get("description").toString();
			String license = currKeyJson.get("license").toString();
			String author = currKeyJson.get("author").toString();
			String doi = currKeyJson.get("doi").toString();
			String url = currKeyJson.get("url").toString();
			String type = currKeyJson.get("type").toString();

			curr.setDescription(description);
			curr.setLicense(license);
			curr.setAuthor(author);
			curr.setDoi(doi);
			curr.setUrl(url);
			curr.setType(type);
		}

		String[] parametersArr = { "nodes", "parameters" };
		JsonObject parameters = getValue(danaJson, parametersArr);
		Set<String> parametersKeys = parameters.keySet();
		for (String key : parametersKeys) {
			DatasetNode curr = (DatasetNode) getWorkflowNode(key);
			String[] currParameterArr = { "nodes", "parameters", key };
			JsonObject currKeyJson = getValue(danaJson, currParameterArr);

			String description = currKeyJson.get("description").toString();
			String license = currKeyJson.get("license").toString();
			String author = currKeyJson.get("author").toString();
			String doi = currKeyJson.get("doi").toString();
			String url = currKeyJson.get("url").toString();
			String type = currKeyJson.get("type").toString();

			curr.setDescription(description);
			curr.setLicense(license);
			curr.setAuthor(author);
			curr.setDoi(doi);
			curr.setUrl(url);
			curr.setType(type);
		}

		String[] stepArr = { "nodes", "steps" };
		JsonObject steps = getValue(danaJson, stepArr);
		Set<String> stepsKeys = steps.keySet();
		for (String key : stepsKeys) {
			StepNode curr = (StepNode) getWorkflowNode(key);
			String[] currStepsArr = { "nodes", "steps", key };
			JsonObject currKeyJson = getValue(danaJson, currStepsArr);

			String shortDescription = currKeyJson.get("shortDescription").toString();
			String longDescription = currKeyJson.get("longDescription").toString();
			String gitHubUrl = currKeyJson.get("gitHubUrl").toString();
			String criticality = currKeyJson.get("criticality").toString();
			String stepType = currKeyJson.get("stepType").toString();
			String website = currKeyJson.get("website").toString();
			String author = currKeyJson.get("author").toString();
			String license = currKeyJson.get("license").toString();
			String versionNumber = currKeyJson.get("versionNumber").toString();
			String dependencies = currKeyJson.get("dependencies").toString();
			String documentationLink = currKeyJson.get("documentationLink").toString();
			String commandLineInvocation = currKeyJson.get("commandLineInvocation").toString();

			curr.setShortDescription(shortDescription);
			curr.setLongDescription(longDescription);
			curr.setGitHubUrl(gitHubUrl);
			curr.setCriticality(criticality);
			curr.setStepType(stepType);
			curr.setWebsite(website);
			curr.setAuthor(author);
			curr.setLicense(license);
			curr.setVersionNumber(versionNumber);
			curr.setDependancies(dependencies);
			curr.setDocumentationLink(documentationLink);
			curr.setCommandLineInvocation(commandLineInvocation);
		}

	}

	/**
	 * Generate the data narrative for the workflow node with a matching name as the
	 * given string. Returns empty string if a workflow node with the given name
	 * cannot be found. The returned string is in markdown format (to provide links
	 * to other nodes/urls)
	 * 
	 * @param workflowNodeName name of the node to generate narrative for
	 * @return data narrative
	 */
	public String generateNodeNarrative(String workflowNodeName) {

		WorkflowNode node = getWorkflowNode(workflowNodeName);
		if (node.isDataset()) {
			try {
				DatasetNode dn = (DatasetNode) node;
				return generateDatasetNarrative(dn);

			} catch (ClassCastException cce) {
				System.out.println("Error: cannot cast \"" + workflowNodeName + "\" as a DatasetNode");
				cce.printStackTrace();
				return null;
			}
		} else {
			try {
				StepNode sn = (StepNode) node;
				return generateStepNarrative(sn);
			} catch (ClassCastException cce) {
				System.out.println("Error: cannot cast \"" + workflowNodeName + "\" as a StepNode");
				cce.printStackTrace();
				return null;
			}
		}

	}

	public String generateHighLevelNarrative() {
		return workflowDescription;
	}

	public String generateMediumLevelNarrative(int minCriticality) {

		// TODO: this will not work for non-linear workflows (workflows with more than
		// one i/o)

		ArrayList<DatasetNode> inputs = getInputs();
		ArrayList<DatasetNode> outputs = getOutputs();
		
		//TODO: get a better way to sort steps for instances with more than one input/output
		ArrayList<StepNode> steps = new ArrayList<StepNode>();
		ArrayList<WorkflowNode> tmp = getChildren(inputs.get(0));
		for(WorkflowNode w : tmp) {
			if(!w.isDataset()) {
				steps.add((StepNode)w);
			}
		}

		String outp = "";

		outp += "The workflow takes in " + Num2Word.convert(inputs.size()) + " inputs and produces "
				+ Num2Word.convert(outputs.size()) + " outputs. ";
		outp += "This workflow has a total of " + Num2Word.convert(steps.size()) + " steps. ";

		StepNode lastStep = null;
		for (StepNode s : steps) {
			if (Integer.parseInt(s.getCriticality()) <= minCriticality) {
				if (lastStep == null) {
					outp += "First, " + inputs.get(0).getName() + " is passed into a " + s.getStepType() + "[" + s.getName() + "] step. ";
					outp += "This step " + s.getShortDescription() + ". ";
				} else {
					outp += "The output from " + lastStep.getName() + " is then passed into a " + s.getStepType() + "["
							+ s.getName() + "] step. ";
					outp += "This step " + s.getShortDescription() + ". ";
				}
				lastStep = s;
			}
		}

		if (lastStep != null) {
			outp += "Finally, " + lastStep.getName() + " produces one text document[" + outputs.get(0).getName()
					+ "] as the workflows output. ";
		} else {
			System.out.println(
					"Error: generateMediumLevelNarrative needs a min criticality that includes at least one step to generate a narrative");
			return "";
		}
		return outp;

	}

	public String generateLowLevelNarrative(int minCriticality) {
		return null;
	}

	private String generateDatasetNarrative(DatasetNode dn) {
		String outp = "";
		String nameType = dn.isParameter() ? "parameter" : "document";
		int inc = 0;

		String[] references = { dn.getName(), "This " + nameType, "It" };

		if (!dn.getType().equalsIgnoreCase("null")) {
			outp += references[inc] + " is a " + dn.getType() + " file. ";
			inc = inc == 0 ? 1 : (inc % 2) + 1;
		}

		if (!dn.getDescription().equalsIgnoreCase("null")) {
			outp += references[inc] + " " + dn.getDescription() + ". ";
			inc = inc == 0 ? 1 : (inc % 2) + 1;
		}

		if (!dn.getLicense().equalsIgnoreCase("null")) {
			outp += references[inc] + " has license " + dn.getLicense() + ". ";
			inc = inc == 0 ? 1 : (inc % 2) + 1;
		}

		if (!dn.getAuthor().equalsIgnoreCase("null")) {
			outp += references[inc] + " has author " + dn.getAuthor() + ". ";
			inc = inc == 0 ? 1 : (inc % 2) + 1;
		}

		if (!dn.getDoi().equalsIgnoreCase("null")) {
			outp += references[inc] + " has doi " + dn.getDoi() + ". ";
			inc = inc == 0 ? 1 : (inc % 2) + 1;
		}

		if (!dn.getUrl().equalsIgnoreCase("null")) {
			outp += "More information on " + references[inc] + " can be found at " + dn.getUrl() + ". ";
			inc = inc == 0 ? 1 : (inc % 2) + 1;
		}

		outp += "\n";
		ArrayList<WorkflowNode> path = getFullWorkflowPathFromNode(dn);
		for (WorkflowNode wn : path) {
			outp += wn.getName().equalsIgnoreCase(dn.getName()) ? " (" + wn.getName() + ") ->" : wn.getName() + " ->";
		}

		return outp;
	}

	private String generateStepNarrative(StepNode sn) {
		String outp = "";
		int inc = 0;

		String[] references = { sn.getName(), "This step", "It" };

		// Website
		if (!sn.getWebsite().equalsIgnoreCase("null")) {
			outp += "The website for " + references[inc] + " can be found at " + sn.getWebsite() + ". ";
			inc = inc == 0 ? 1 : (inc % 2) + 1;
		}

		// Documentation
		if (!sn.getDocumentationLink().equalsIgnoreCase("null")) {

			outp += "Documentation for " + references[inc] + " can be found at " + sn.getDocumentationLink() + ". ";
			inc = inc == 0 ? 1 : (inc % 2) + 1;
		}

		// GitHub url
		if (!sn.getGitHubUrl().equalsIgnoreCase("null")) {
			if (inc != 0) {
				outp += "The GitHub repository can be found at " + sn.getGitHubUrl() + ". ";
			} else {
				outp += "The GitHub repository for " + references[inc] + " can be found at " + sn.getGitHubUrl() + ". ";
			}
			inc = inc == 0 ? 1 : (inc % 2) + 1;
		}

		// Version number
		if (!sn.getVersionNumber().equalsIgnoreCase("null")) {
			outp += "The version of " + sn.getName() + " used was " + sn.getVersionNumber() + ". ";
			inc = inc == 0 ? 1 : (inc % 2) + 1;
		}

		// Human Descriptions
		if (!sn.getLongDescription().equalsIgnoreCase("null") || !sn.getShortDescription().equalsIgnoreCase("null")) {
			if (!sn.getLongDescription().equalsIgnoreCase("null")) {
				outp += references[inc] + " " + sn.getLongDescription() + ". ";
			} else {
				outp += references[inc] + " " + sn.getShortDescription() + ". ";
			}
		}

		// incoming/outgoing links
		ArrayList<WorkflowNode> incoming = sn.getIncomingLinks();
		ArrayList<WorkflowNode> outgoing = sn.getOutgoingLinks();
		int inSize = incoming.size();
		int outSize = outgoing.size();

		if (inSize > 0) {
			outp += references[inc] + " has " + Num2Word.convert(inSize) + " incoming[";
			for (WorkflowNode wn : incoming) {
				outp += wn != incoming.get(inSize - 1) ? wn.getName() + "," : wn.getName();
			}
			if (outSize > 0) {
				outp += "] links and produces " + Num2Word.convert(outSize) + " output[";
				for (WorkflowNode wn : outgoing) {
					outp += wn != outgoing.get(outSize - 1) ? wn.getName() + "," : wn.getName();
				}
			}
			outp += "] link. ";
		}
		return outp;
	}

	/**
	 * Use the wings json to populate the workflows array with nodes from the wings
	 * workflow. This populates with any dataset, parameter and step nodes, as well
	 * as creating a relationship of input and output nodes for each node. This does
	 * leave most of the metadata fields used by DANA empty. They will need to be
	 * manually entered by humans later
	 */
	private void generateWorkflowNodesListFromWings() {

		addDatasets(); // Add all dataset nodes to the workflows array
		addSteps(); // Add all step nodes to the workflows array
		addInOutLinks(); // Add i/o links for every node in the workflows array

		// Print the workflows
		for (WorkflowNode wn : workflows) {
			System.out.println("------------");
			System.out.println(wn);
		}
	}

	/**
	 * Recursively populate the parentsList given the current node. Helper function
	 * for getParents()
	 * 
	 * @param curr        current node
	 * @param parentsList list of parents from the origin node
	 */
	private void getParentsRecursivePopulate(WorkflowNode curr, ArrayList<WorkflowNode> parentsList) {
		if (curr != null && !parentsList.contains(curr)) {
			parentsList.add(0, curr);
			if (curr.getIncomingLinks() != null) {
				for (WorkflowNode p : curr.getIncomingLinks()) {
					getParentsRecursivePopulate(p, parentsList);
				}
			}
		}
	}

	/**
	 * Recursively populate the childList given the current node. Helper function
	 * for getChildren()
	 * 
	 * @param curr        current node
	 * @param parentsList list of parents from the origin node
	 */
	private void getChildrenRecursivePopulate(WorkflowNode curr, ArrayList<WorkflowNode> childList) {
		if (curr != null && !childList.contains(curr)) {
			childList.add(curr);
			if (curr.getOutgoingLinks() != null) {
				for (WorkflowNode c : curr.getOutgoingLinks()) {
					getChildrenRecursivePopulate(c, childList);
				}
			}
		}
	}

	/**
	 * Adds any dataset nodes from the json into the workflows array. This method
	 * must be called before the addInOutLinks() method so that the program can add
	 * a link to the WorkflowNode object when a link between two objects has been
	 * found
	 */
	private void addDatasets() {
		String[] arr = { "template", "Variables" };
		JsonObject variables = getValue(json, arr);
		Set<String> variableKeys = variables.keySet();

		// Loop for every dataset in the workflow
		for (String key : variableKeys) {
			String[] keyArr = { "template", "Variables", key };
			JsonObject curr = getValue(json, keyArr);

			// Get name from key string (Key is URL with the variable #name at end)
			String[] split = key.split("#");
			String name = split[split.length - 1];

			// Get the variable's type: 1=dataset, 2=parameter
			Boolean isParameter = Integer.parseInt(curr.get("type").toString()) == 2 ? true : false;

			// Set basic metadata
			DatasetNode dataset = new DatasetNode();
			dataset.setName(name);
			dataset.setIsParameter(isParameter);
			dataset.setId(key);

			workflows.add(dataset);
		}

	}

	/**
	 * Adds any step nodes from the json into the workflows array. This method must
	 * be called before the addInOutLinks() method so it can link any input and
	 * output links to the correct WorkflowNode object
	 */
	private void addSteps() {
		String[] arr = { "template", "Nodes" };
		JsonObject nodes = getValue(json, arr);
		Set<String> nodeKeys = nodes.keySet();

		// Loop for every step in the workflow
		for (String key : nodeKeys) {

			// Get name from componentVariable binding (owl url)
			String id = key;
			String[] split = id.split("#");
			String name = split[split.length - 1];

			// Set basic metadata
			StepNode step = new StepNode();
			step.setName(name);
			step.setId(id);

			workflows.add(step);
		}
	}

	/**
	 * Add incoming and outgoing links to each node in the workflow.
	 */
	private void addInOutLinks() {

		String[] arr = { "template", "Links" };
		JsonObject links = getValue(json, arr);
		Set<String> linkKeys = links.keySet();

		for (String key : linkKeys) {

			String[] keyArr = { "template", "Links", key };
			JsonObject link = getValue(json, keyArr);
			Set<String> keySet = link.keySet();

			JsonObject variable = getValue(link, "variable");
			WorkflowNode curr = getWorkflowNode(variable.getString("id"));

			// loop over keys and assign any links to workflows inputs and outputs
			for (String args : keySet) {

				if (!args.equals("id")) {
					JsonObject cJson = getValue(link, args);

					if (args.equals("toNode")) {
						WorkflowNode toNode = getWorkflowNode(cJson.getString("id"));
						toNode.addIncomingLink(curr);
						curr.addOutgoingLink(toNode);
					}

					if (args.equals("fromNode")) {
						WorkflowNode fromNode = getWorkflowNode(cJson.getString("id"));
						fromNode.addOutgoingLink(curr);
						curr.addIncomingLink(fromNode);
					}
				}
			}

		}
	}

	/**
	 * Returns a jsonObject from the given key. If one can't be found, returns null.
	 * Note: the value to return must be a jsonObject. This method cannot return a
	 * value and will return and empty jsonObject if the given key links to a value
	 * (ie: {"key": 1})
	 * 
	 * @param object json object
	 * @param key    key of desired json object value
	 * @return json object from given key
	 */
	private JsonObject getValue(JsonObject object, String key) {
		try {
			return object.get(key).asJsonObject();
		} catch (Exception e) {
			System.out.println("Error getting key: " + key + " from object: " + object);
			JsonReader jsonReader = Json.createReader(new StringReader("{}"));
			return jsonReader.readObject();
		}
	}

	/**
	 * This method returns the json object after traversing through the array of
	 * keys. This is intended to make searching down larger json objects easier.
	 * Note: the value to return must be a jsonObject. This method cannot return a
	 * value and will return and empty jsonObject {} if the given key links to a
	 * value (ie: {"key": 1})
	 * 
	 * @param object input json object
	 * @param key    array of keys to search down
	 * @return json object from given key list
	 */
	private JsonObject getValue(JsonObject object, String[] key) {
		JsonObject o;
		try {
			o = object.get(key[0]).asJsonObject();
		} catch (NullPointerException e) {
			System.out.println("Error getting key: " + key[0] + " from object: " + object);
			JsonReader jsonReader = Json.createReader(new StringReader("{}"));
			return jsonReader.readObject();
		}

		for (int i = 1; i < key.length; i++) {
			try {
				JsonValue tmp = o.get(key[i]);
				if (tmp.getValueType().toString().equalsIgnoreCase("object")) {
					o = tmp.asJsonObject();
				} else if (i != key.length - 1) {
					System.out.println("Error getting key: " + key[i] + " from object: " + tmp
							+ ". Too many keys; not enough json objects");
					JsonReader jsonReader = Json.createReader(new StringReader("{}"));
					return jsonReader.readObject();
				} else {
					System.out.println("Cannot get value from this method. It only returns JsonObjects. "
							+ "Try using one less key, then getting the value directly with var.get(key)");
					JsonReader jsonReader = Json.createReader(new StringReader("{}"));
					return jsonReader.readObject();
				}
			} catch (NullPointerException e) {
				System.out.println("Error getting key: " + key[i] + " from object: " + o);
				JsonReader jsonReader = Json.createReader(new StringReader("{}"));
				return jsonReader.readObject();
			}
		}

		return o;

	}

	/**
	 * Read in a file from the given file path and return a string representation of
	 * that file
	 * 
	 * @param filePath path of file to read
	 * @return String of files contents
	 */
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

/**
 * Convert integer into the English word equivalent.
 *
 */
class Num2Word {

	public static final String[] units = { "", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
			"ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen",
			"nineteen" };

	public static final String[] tens = { "", // 0
			"", // 1
			"twenty", // 2
			"thirty", // 3
			"forty", // 4
			"fifty", // 5
			"sixty", // 6
			"seventy", // 7
			"eighty", // 8
			"ninety" // 9
	};

	/**
	 * Convert input number n into English word equivalent. For example 104 would
	 * return one hundred and four
	 * 
	 * @param n number to convert
	 * @return English string equivalent
	 */
	public static String convert(final int n) {
		if (n < 0) {
			return "minus " + convert(-n);
		}

		if (n < 20) {
			return units[n];
		}

		if (n < 100) {
			return tens[n / 10] + ((n % 10 != 0) ? " " : "") + units[n % 10];
		}

		if (n < 1000) {
			return units[n / 100] + " hundred" + ((n % 100 != 0) ? " " : "") + convert(n % 100);
		}

		if (n < 1000000) {
			return convert(n / 1000) + " thousand" + ((n % 1000 != 0) ? " " : "") + convert(n % 1000);
		}

		if (n < 1000000000) {
			return convert(n / 1000000) + " million" + ((n % 1000000 != 0) ? " " : "") + convert(n % 1000000);
		}

		return convert(n / 1000000000) + " billion" + ((n % 1000000000 != 0) ? " " : "") + convert(n % 1000000000);
	}
}
