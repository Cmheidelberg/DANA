package com.dana;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;

import org.json.JSONObject;

/**
 * @author Christopher Heidelberg
 *
 *         Represents an ArrayList of every node in the workflow with links to
 *         any inputs and outputs. There are also helper methods for finding
 *         relationships between nodes. This class stores all data on a workflow
 *         and any collected metadata. After the DANA json has been filled out,
 *         read it in with the readDanaJson() method to update each node with
 *         all gathered metadata.
 */
public class WorkflowJson {

	private JsonObject json;
	private String description = "";
	private String citation = "";
	private String name = "";
	private ArrayList<WorkflowNode> workflows; // This should be "workflow" but i'm too used to the original name now
	private ArrayList<Fragment> fragments;

	// Read in WINGS json file to populate workflow list with nodes
	public WorkflowJson(JsonObject json) {
		this.json = json;
		workflows = new ArrayList<WorkflowNode>();
		fragments = new ArrayList<Fragment>();

		// Populate the workflows array list with all nodes from the workflow as well as
		// gather metadata on them from the json.
		generateWorkflowNodesListFromWings();
	}

	// Returns workflow's description
	public String getDescription() {
		return this.description;
	}

	// Returns workflow's citation
	public String getCitation() {
		return this.citation;
	}

	// Returns workflow's name
	public String getName() {
		return this.name;
	}

	// Set workflow's name
	public void setName(String name) {
		this.name = name;
	}

	// Return a ArrayList of all fragments defined in the workflow
	public ArrayList<Fragment> getWorkflowFragments() {
		return fragments;
	}

	/**
	 * Get a specific fragment by name. Returns null if no such fragment exists
	 * 
	 * @param name | Name of fragment to search for. Case insensitive
	 * @return Fragment object if exists. Null otherwise
	 */
	public Fragment getFragment(String name) {
		for (Fragment f : fragments) {
			if (f.getName().equalsIgnoreCase(name)) {
				return f;
			}
		}
		return null;
	}

	/**
	 * @param fragment
	 * @return T/F if given fragment exists within the workflow
	 */
	public boolean hasFragment(Fragment fragment) {
		return fragments.contains(fragment);
	}

	/**
	 * @param fragment
	 * @return T/F if a fragment with the given name exists within the workflow.
	 *         Case insensitive
	 */
	public boolean hasFragment(String name) {
		for (Fragment f : fragments) {
			if (f.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Add a fragment to the workflow with the given name and description
	 * 
	 * @param name        | Name of fragment
	 * @param description | description of fragment
	 */
	public void addFragment(String name, String description) {
		if (!hasFragment(name)) {
			Fragment fragment = new Fragment(name, description);
			fragments.add(fragment);
		}
	}

	/**
	 * Add node to fragment's internal list of nodes. This also adds the fragment to
	 * the node's internal list of fragments. If the relation already exists then no
	 * change will be made. A warning will be printed to terminal if the fragment
	 * does not exists in the workflow.
	 * 
	 * @param fragmentName | Name of fragment
	 * @param node         | workflowNode object
	 */
	public void addFragmentNodeRelation(String fragmentName, WorkflowNode node) {
		// Sanity check input parameter
		if (fragmentName == null || fragmentName.equalsIgnoreCase("null"))
			return;

		if (hasFragment(fragmentName)) {
			getFragment(fragmentName).addNode(node);
		} else {
			System.out.println("Error adding fragment(" + fragmentName + ") node(" + node.getFullName()
					+ ") relation. There is no fragment with this name.");
		}
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
	 * Get a WorflowNode from the workflows array from the given id. Id can either
	 * be the name of the workflow or its id. If the workflow is not in the list
	 * then this function returns null.
	 * 
	 * @param indicator | id Name or Id string of the node to search for
	 * @return WorkflowNode with the given name/id or null if does not exist
	 */
	public WorkflowNode getWorkflowNode(String indicator) {

		for (WorkflowNode wn : workflows) {
			String name = wn.getFullName();
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
	 * Returns input datasets of the workflow. An input is any dataset node that
	 * does not have any incoming links and is not a parameter
	 * 
	 * @return input datasets
	 */
	public ArrayList<DatasetNode> getInputs() {

		ArrayList<DatasetNode> inputNodes = new ArrayList<DatasetNode>();
		for (WorkflowNode wn : workflows) {
			if (wn.getIncomingLinks() == null && !wn.isParameter()) {
				inputNodes.add((DatasetNode) wn);
			}
		}
		return inputNodes;
	}

	/**
	 * Returns an array list containing every parameter of the workflow
	 * 
	 * @return parameter datasets
	 */
	public ArrayList<DatasetNode> getParameters() {

		ArrayList<DatasetNode> parameterNodes = new ArrayList<DatasetNode>();
		for (WorkflowNode wn : workflows) {
			if (wn.getIncomingLinks() == null && wn.isParameter()) {
				parameterNodes.add((DatasetNode) wn);
			}
		}
		return parameterNodes;
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
	 * all sub children and parents. Currently this method has no use, but it is
	 * intended to be used with a diagram of the workflow so the path of the
	 * currently selected node can be hilighted.
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
	 * @param node | node to find parents of
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
	 * @param node | node to find parents of
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
	 * validate the provided json path against the DANA schema.json. Return true if
	 * given json is valid, prints the ValidationException otherwise. The DANA json
	 * (readData.json by default) needs to be validated before being reread since it
	 * contains user input information and a typo could crash the program in odd
	 * ways
	 * 
	 * @param path | path to the json needing validation
	 * @return
	 */
	public boolean validateJson(String path) {
		try {
			String schemaString = readFile("schema.json");
			JSONObject rawSchema = new JSONObject(schemaString);
			Schema schema = SchemaLoader.load(rawSchema);
			schema.validate(new JSONObject(readFile(path))); // throws a ValidationException if this object is invalid
		} catch (org.everit.json.schema.ValidationException e) {
			System.out.println("[ERROR] Invalid DANA JSON: " + e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Returns the longest path in the workflow. This is defined as the path that
	 * has the most steps and contains an input + output node. By default, this will
	 * be the path used while generating a workflow description since only a single
	 * path can be described in non-linear workflows. The path returned will include
	 * nodes that are within the max criticality threshold. This threshold is the
	 * highest supported criticality value (since lower criticality values mean the
	 * node is more critical).
	 * 
	 * This means that if a workflow has two parallel paths, one that is 5 steps
	 * long each with a criticality value of 2 and the other that is 3 steps long
	 * each with a criticality value of 1. Then if this method is called with a
	 * maxCriticalityThreshold=1, the path with 3 nodes will be returned. But if the
	 * method is called with maxCriticalityThreshold=2 then the path with 5 nodes
	 * will be returned.
	 * 
	 * @param maxCriticalityThreshold | maximum Criticality value allowed in the
	 *                                returned "longest path". Lower threshold means
	 *                                only more important nodes will be included in
	 *                                path
	 * @return longest path that meets the criticality threshold
	 */
	public ArrayList<WorkflowNode> getLongestPath(int maxCriticalityThreshold) {
		ArrayList<WorkflowNode> longestPath = new ArrayList<WorkflowNode>();
		ArrayList<DatasetNode> inputs = getInputs();

		for (DatasetNode i : inputs) {
			ArrayList<WorkflowNode> tmp = recursiveGetLongestPath(i, maxCriticalityThreshold);
			if (tmp.size() > longestPath.size()) {
				longestPath = tmp;
			}
		}
		return longestPath;
	}

	/**
	 * Returns whether a dataset has a step parent in the array that is within the
	 * minimum criticality. This is used as a helper function to getLongestPath() to
	 * decide if a dataset should be included in the path. If a step is not critical
	 * enough then its output also needs to not be included in the path
	 */
	private boolean datasetParentMeetsCriticality(WorkflowNode dataset, int maxCriticalityThreshold) {
		if (dataset.getIncomingLinks() == null) {
			return true;
		}
		for (WorkflowNode parent : dataset.getIncomingLinks()) {
			try {
				StepNode step = (StepNode) parent;
				if (step.getCriticality() <= maxCriticalityThreshold) {
					return true;
				}
			} catch (Exception e) {
				continue;
			}
		}
		return false;
	}

	/**
	 * Helper function for getLongestPath(). Recursively finds the longest sub-path
	 * for the given path
	 */
	private ArrayList<WorkflowNode> recursiveGetLongestPath(WorkflowNode node, int maxCriticalityThreshold) {
		ArrayList<WorkflowNode> outputs = node.getOutgoingLinks();
		ArrayList<WorkflowNode> largestSubPath = new ArrayList<WorkflowNode>();
		if (outputs == null || outputs.size() == 0) {
			largestSubPath.add(node);
			return largestSubPath;
		}

		for (WorkflowNode o : outputs) {
			ArrayList<WorkflowNode> tmp = recursiveGetLongestPath(o, maxCriticalityThreshold);

			if (node.isDataset() && datasetParentMeetsCriticality(node, maxCriticalityThreshold)) {
				tmp.add(0, node);

			} else if (!node.isDataset()) {
				try {
					StepNode s = (StepNode) node;
					if (s.getCriticality() <= maxCriticalityThreshold) {
						tmp.add(0, node);
					}
				} catch (Exception e) {
					System.out.println("WARNING: couldnt convert WorkflowNode to StepNode while finding longest path");
				}
			}

			if (tmp.size() > largestSubPath.size()) {
				largestSubPath = tmp;
			}
		}
		return largestSubPath;
	}

	/**
	 * Count how many time a node is used/appears in a workflow. This counts the
	 * nodes common name (name displayed in wings viewer).
	 * 
	 * @return count
	 */
	public int CountTimesUsedInWorkflow(WorkflowNode node) {
		int count = 0;
		for (WorkflowNode wn : workflows) {
			if (wn.getDisplayName().equalsIgnoreCase(node.getDisplayName())) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Read in the json generated by DANA after it has been manually filled out by
	 * the worfklow's author. This will populate the metadata fields for each of the
	 * workflow nodes
	 * 
	 * @param path | path to json (by default ./readData.json)
	 * @return true if json was successfully read and serialized; false otherwise
	 */
	public boolean readDanaJson(String path) {

		// Check json against schema
		if (!validateJson(path)) {
			System.out.println("Cannot read an invalid JSON");
			return false;
		}

		String danaJsonString = readFile(path);
		JsonReader jsonReader = Json.createReader(new StringReader(danaJsonString));
		JsonObject danaJson = jsonReader.readObject();
		jsonReader.close();

		// Add workflow metadata
		JsonObject metadata = getValue(danaJson, "metadata");
		this.description = metadata.get("description").toString();
		this.citation = metadata.get("citation").toString();

		JsonObject fragmentMetadata = getValue(metadata, "fragments");
		for (String key : fragmentMetadata.keySet()) {
			String description = readJsonValue("description", getValue(fragmentMetadata, key));
			addFragment(key, description);
		}

		// Add dataset metadata
		String[] datasetArr = { "nodes", "datasets" };
		JsonObject datasets = getValue(danaJson, datasetArr);
		Set<String> datasetsleKeys = datasets.keySet();
		for (String key : datasetsleKeys) {
			DatasetNode curr = (DatasetNode) getWorkflowNode(key);
			String[] currDatasetArr = { "nodes", "datasets", key };
			JsonObject currKeyJson = getValue(danaJson, currDatasetArr);

			String description = readJsonValue("description", currKeyJson);
			String license = readJsonValue("license", currKeyJson);
			String author = readJsonValue("author", currKeyJson);
			String doi = readJsonValue("doi", currKeyJson);
			String url = readJsonValue("url", currKeyJson);
			String type = readJsonValue("type", currKeyJson);
			String citation = readJsonValue("citation", currKeyJson);

			// Add fragments to current dataset object
			String fragmentsCsv = readJsonValue("fragments", currKeyJson);
			String[] fragments = fragmentsCsv.split(",");
			for (String f : fragments) {
				addFragmentNodeRelation(f, curr);
			}

			curr.setDescription(description);
			curr.setLicense(license);
			curr.setAuthor(author);
			curr.setDoi(doi);
			curr.setUrl(url);
			curr.setType(type);
			curr.setCitation(citation);
		}

		// Create parameter objects
		String[] parametersArr = { "nodes", "parameters" };
		JsonObject parameters = getValue(danaJson, parametersArr);
		Set<String> parametersKeys = parameters.keySet();
		for (String key : parametersKeys) {
			DatasetNode curr = (DatasetNode) getWorkflowNode(key);
			String[] currParameterArr = { "nodes", "parameters", key };
			JsonObject currKeyJson = getValue(danaJson, currParameterArr);

			String description = readJsonValue("description", currKeyJson);
			String type = readJsonValue("type", currKeyJson);

			// Add fragments to current parameter object
			String fragmentsCsv = readJsonValue("fragments", currKeyJson);
			String[] fragments = fragmentsCsv.split(",");
			for (String f : fragments) {
				addFragmentNodeRelation(f, curr);
			}

			curr.setDescription(description);
			curr.setType(type);
			curr.setCitation(citation);
		}

		// Create step objects
		String[] stepArr = { "nodes", "steps" };
		JsonObject steps = getValue(danaJson, stepArr);
		Set<String> stepsKeys = steps.keySet();
		for (String key : stepsKeys) {
			StepNode curr = (StepNode) getWorkflowNode(key);
			String[] currStepsArr = { "nodes", "steps", key };
			JsonObject currKeyJson = getValue(danaJson, currStepsArr);

			String shortDescription = readJsonValue("shortDescription", currKeyJson);
			String longDescription = readJsonValue("longDescription", currKeyJson);
			String gitHubUrl = readJsonValue("gitHubUrl", currKeyJson);
			String criticality = readJsonValue("criticality", currKeyJson);
			String citation = readJsonValue("citation", currKeyJson);
			String stepType = readJsonValue("stepType", currKeyJson);
			String website = readJsonValue("website", currKeyJson);
			String author = readJsonValue("author", currKeyJson);
			String license = readJsonValue("license", currKeyJson);
			String versionNumber = readJsonValue("versionNumber", currKeyJson);
			String dependencies = readJsonValue("dependencies", currKeyJson);
			String documentationLink = readJsonValue("documentationLink", currKeyJson);
			String commandLineInvocation = readJsonValue("commandLineInvocation", currKeyJson);

			// Add fragments to current parameter object
			String fragmentsCsv = readJsonValue("fragments", currKeyJson);
			String[] fragments = fragmentsCsv.split(",");
			for (String f : fragments) {
				addFragmentNodeRelation(f, curr);
			}

			curr.setShortDescription(shortDescription);
			curr.setLongDescription(longDescription);
			curr.setGitHubUrl(gitHubUrl);
			curr.setCriticality(criticality);
			curr.setCitation(citation);
			curr.setStepType(stepType);
			curr.setUrl(website);
			curr.setAuthor(author);
			curr.setLicense(license);
			curr.setVersionNumber(versionNumber);
			curr.setDependancies(dependencies);
			curr.setDocumentationLink(documentationLink);
			curr.setCommandLineInvocation(commandLineInvocation);
		}

		if (checkWorkflowLogicIsValid()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Test to make sure the workflow (and metadata entered) is logical. This
	 * includes checking that each node has a connection. Checking that each
	 * fragment is part of a connected subgraph. And validating the date entered is
	 * in a valid format.
	 * 
	 * This function is intended to be called after readDanaJson() to validate a
	 * logical narrative of this workflow can be generated
	 * 
	 */
	private boolean checkWorkflowLogicIsValid() {

		boolean isValid = true;

		try {

			// Validate each fragment is part of a connected subgraph
			for (Fragment f : fragments) {
				for (WorkflowNode wn : f.getNodes()) {
					if (!wn.hasFragment(f)) {
						System.out.println(
								"WorkflowNode(" + wn.getFullName() + ") is missing fragment(" + f.getName() + ")");
						isValid = false;
					}
				}

				// Check that the workflow has a connected subgraph
				if (f.getNodes().size() > 0) {
					ArrayList<WorkflowNode> subgraph = new ArrayList<WorkflowNode>();
					fragmentSubgraphIsConnected(f, f.getNodes().get(0), subgraph);
					if (subgraph.size() != f.getNodes().size()) {
						System.out.println("A fragment(" + f.getName()
								+ ") has a disconnected node. Please make sure all nodes wihtin a fragment are connected");
						isValid = false;
					}
				} else {
					// Throw a warning but does not need to invalidate the workflow
					System.out.println("Warning a fragment(" + f.getName() + ") has no nodes associated with it");
				}

				// TODO: check every node has correct fragment
				// TODO: check workflow is connected (subgraph from any node is length of all
				// nodes in workflow)
			}
			return isValid;
		} catch (Exception e) {
			System.out.println(
					"An exception has been thrown while validating the json's logic. Please check that the workflow is valid");
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Helper function for checkWorkflowLogicIsValid(). Workflow fragments must
	 * contain sub-graphs that are all connected. This function recursively finds
	 * all the nodes connected to the given node within the same fragment. If the
	 * returned graph is the same size as the list of all nodes in the given
	 * fragment then the subgraph of fragments is connected.
	 * 
	 */
	private void fragmentSubgraphIsConnected(Fragment fragment, WorkflowNode curr, ArrayList<WorkflowNode> subgraph) {

		ArrayList<WorkflowNode> incoming = curr.getIncomingLinks();
		ArrayList<WorkflowNode> outgoing = curr.getOutgoingLinks();

		if (!fragment.hasNode(curr) || subgraph.contains(curr)) {
			return;
		} else {
			subgraph.add(curr);
		}

		if (incoming != null) {
			for (WorkflowNode i : incoming) {
				fragmentSubgraphIsConnected(fragment, i, subgraph);
			}
		}

		if (outgoing != null) {
			for (WorkflowNode o : outgoing) {
				fragmentSubgraphIsConnected(fragment, o, subgraph);
			}
		}
	}

	/**
	 * Helper function for readDanaJson(). This function gets the value from the
	 * specified value of the given json and key. If there is an error reading in
	 * the value, it will return an empty string. Additionally, since the strings
	 * read in from the json have their string wrapper attached, this function
	 * removes the leading and trailing quotes (if applicable)
	 * 
	 * @param key
	 * @param json
	 * @return
	 */
	private String readJsonValue(String key, JsonObject json) {
		try {
			String read = json.get(key).toString();

			// String the string wrapper if it exists (leading and trailing ")
			char start = read.charAt(0);
			char end = read.charAt(read.length() - 1);
			int startIndex = 0;
			int endIndex = read.length();
			if (start == '\"' || start == '\'') {
				startIndex = +1;
			}
			if (end == '\"' || end == '\'') {
				endIndex -= 1;
			}

			return read.substring(startIndex, endIndex);
		} catch (NullPointerException npe) {
			return "";
		}
	}

	/**
	 * Use the wings json to populate the workflows array with nodes from the wings
	 * workflow. This populates with any dataset, parameter and step nodes, as well
	 * as creating a relationship of input and output nodes for each node. This does
	 * leave most of the metadata fields used by DANA empty. They will need to be
	 * manually entered by humans later
	 */
	private void generateWorkflowNodesListFromWings() {

		// Set the name of the workflow
		JsonObject t = getValue(json, "template");
		String[] url = t.getString("id").split("#");
		this.name = url[url.length - 1];

		addDatasets(); // Add all dataset nodes to the workflows array
		addSteps(); // Add all step nodes to the workflows array
		addInOutLinks(); // Add i/o links for every node in the workflows array

	}

	/**
	 * Recursively populate the parentsList given the current node. Helper function
	 * for getParents()
	 * 
	 * @param curr        | current node
	 * @param parentsList | list of parents from the origin node
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
	 * @param curr        | current node
	 * @param parentsList | list of parents from the origin node
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
	 * must be called before the addInOutLinks() method so that dana can add a link
	 * to the WorkflowNode object when a link between two objects has been found
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
			String fullName = split[split.length - 1];

			String[] bindingId = { "template", "Nodes", key, "componentVariable", "binding" };
			JsonObject binding = getValue(json, bindingId);
			String displayName;
			if (!binding.isEmpty()) {
				displayName = binding.getString("id");
				split = displayName.split("#");
				displayName = displayName.length() > 0 ? split[split.length - 1] : "";
			} else {
				displayName = fullName;
			}

			// Get the variable's type: 1=dataset, 2=parameter
			Boolean isParameter = Integer.parseInt(curr.get("type").toString()) == 2 ? true : false;

			// Set basic metadata
			DatasetNode dataset = new DatasetNode();
			dataset.setDisplayName(displayName);
			dataset.setFullName(fullName);
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
			String fullName = split[split.length - 1];

			String[] bindingId = { "template", "Nodes", key, "componentVariable", "binding" };
			JsonObject binding = getValue(json, bindingId);
			String displayName;
			if (!binding.isEmpty()) {
				displayName = binding.getString("id");
				split = displayName.split("#");
				displayName = displayName.length() > 0 ? split[split.length - 1] : "";
			} else {
				displayName = fullName;
			}

			// Set basic metadata
			StepNode step = new StepNode();
			step.setFullName(fullName);
			step.setDisplayName(displayName);
			step.setId(id);

			workflows.add(step);
		}
	}

	/**
	 * Add incoming and outgoing links to each node in the workflow. This allows
	 * each node to have an internal relationship to any nodes that connect in and
	 * out of it in the workflow
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
	 * @param object | json object
	 * @param key    | key of desired json object value
	 * @return json object from given key
	 */
	private JsonObject getValue(JsonObject object, String key) {
		try {
			return object.get(key).asJsonObject();
		} catch (Exception e) {
			// System.out.println("Error getting key: " + key + " from object: " + object);
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
	 * @param object | input json object
	 * @param key    | array of keys to search down
	 * @return json object from given key list
	 */
	private JsonObject getValue(JsonObject object, String[] key) {
		JsonObject o;
		try {
			o = object.get(key[0]).asJsonObject();
		} catch (NullPointerException e) {
			// System.out.println("Error getting key: " + key[0] + " from object: " +
			// object);
			JsonReader jsonReader = Json.createReader(new StringReader("{}"));
			return jsonReader.readObject();
		}

		for (int i = 1; i < key.length; i++) {
			try {
				JsonValue tmp = o.get(key[i]);
				if (tmp.getValueType().toString().equalsIgnoreCase("object")) {
					o = tmp.asJsonObject();
				} else if (i != key.length - 1) {
					// System.out.println("Error getting key: " + key[i] + " from object: " + tmp
					// + ". Too many keys; not enough json objects");
					JsonReader jsonReader = Json.createReader(new StringReader("{}"));
					return jsonReader.readObject();
				} else {
					System.out.println("Cannot get value from this method. It only returns JsonObjects. "
							+ "Try using one less key, then getting the value directly with var.get(key)");
					JsonReader jsonReader = Json.createReader(new StringReader("{}"));
					return jsonReader.readObject();
				}
			} catch (NullPointerException e) {
				// System.out.println("Error getting key: " + key[i] + " from object: " + o);
				JsonReader jsonReader = Json.createReader(new StringReader("{}"));
				return jsonReader.readObject();
			}
		}

		return o;

	}

	/**
	 * Read in a file from the given file path and return a string representation of
	 * that file. This function is used to get the string representation of a json
	 * file
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
