package dana;

import java.io.StringReader;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import java.util.*;

/**
 * @author Christopher Heidelberg
 *
 * Represents an ArrayList of every node in the workflow with links to any inputs and outputs. There are also helper methods
 * for finding relationships between nodes.
 */
public class WorkflowJson {

	private JsonObject json;
	private ArrayList<WorkflowNode> workflows;
	
	public WorkflowJson(JsonObject json) {
		this.json = json;
		workflows = new ArrayList<WorkflowNode>();
		
		//Populate the workflows array list with all nodes from the workflow as well as gather
		//metadata on them from the json
		generateWorkflowNodesList();
	}
	
	
	/**
	 * Returns a full list of all nodes in this workflow
	 * @return list of workflow nodes
	 */
	public ArrayList<WorkflowNode> getWorkflowNode() {
		return workflows;
	}
	
	
	/**
	 * Returns outputs of the workflow. An output is any dataset node that does not have any outgoing links
	 * @return output datasets
	 */
	public ArrayList<WorkflowNode> getInputs() {
		
		ArrayList<WorkflowNode> inputNodes = new ArrayList<WorkflowNode>();
		boolean empty = true;
		for(WorkflowNode wn: workflows) {
			if(wn.getIncomingLinks() == null) {
				inputNodes.add(wn);
				empty = false;
			}
		}
		
		if(!empty)
			return inputNodes;
		else
			return null;
	}
	
	/**
	 * Returns outputs of the workflow. An output is any dataset node that does not have any outgoing links
	 * @return output datasets
	 */
	public ArrayList<WorkflowNode> getOutputs() {
		
		ArrayList<WorkflowNode> outputNodes = new ArrayList<WorkflowNode>();
		boolean empty = true;
		for(WorkflowNode wn: workflows) {
			if(wn.getOutgoingLinks() == null) {
				outputNodes.add(wn);
				empty = false;
			}
		}
		
		if(!empty)
			return outputNodes;
		else
			return null;
	}
	
	
	/**
	 * Returns a list of nodes that are either a parent or child of this node and all sub children and parents
	 * @param   node node to find full path of
	 * @return  list of nodes that are either children or parents of the given node
	 */
	public ArrayList<WorkflowNode> getFullWorkflowPathFromNode(WorkflowNode node) {
		ArrayList<WorkflowNode> fullPath = new ArrayList<WorkflowNode>();
		ArrayList<WorkflowNode> parents = getParents(node);
		ArrayList<WorkflowNode> children = getChildren(node);
		
		for(WorkflowNode p: parents) {
			if(!fullPath.contains(p)) {
				fullPath.add(p);
			}
		}
		
		for(WorkflowNode c: children) {
			if(!fullPath.contains(c)) {
				fullPath.add(c);
			}
		}
		
		if(fullPath.size() > 0) {
			return fullPath;
		} else {
			return null;
		}
		
	}
	
	
	/**
	 * Returns a list of nodes that are a parent of the given node. A parent node is any node above the given
	 * node in the workflow that eventually links to the given node.
	 * @param   node node to find parents of
	 * @return  list of nodes that are parents of the given node
	 */
	public ArrayList<WorkflowNode> getParents(WorkflowNode node) {
		
		ArrayList<WorkflowNode> parents = new ArrayList<WorkflowNode>();
		for(WorkflowNode p : node.getIncomingLinks()) {
			getParentsRecursivePopulate(p, parents);
		}
		
		if(parents.size() > 0) 
			return parents;
		else 
			return null;
	}
	
	
	/**
	 * Returns a list of nodes that are a children of the given node. A child node is any node above the given
	 * node in the workflow that the given node eventually links to.
	 * @param   node node to find parents of
	 * @return  list of nodes that are parents of the given node
	 */
	public ArrayList<WorkflowNode> getChildren(WorkflowNode node) {
		
		ArrayList<WorkflowNode> children = new ArrayList<WorkflowNode>();
		for(WorkflowNode c:node.getOutgoingLinks()) {
			getChildrenRecursivePopulate(c, children);
		}
		
		if(children.size() > 0)
			return children;
		else
			return null;
	}
	
	
	/**
	 * Use the json to populate the workflows array with nodes from the wings workflow. This populates with any dataset,
	 * parameter and step notes, as well as creating a relationship of input and output nodes for each node. 
	 */
	private void generateWorkflowNodesList() {
		
		addDatasets();   //Add all dataset nodes to the workflows array
		addSteps();      //Add all step nodes to the workflows array
		addInOutLinks(); //Add i/o links for every node in the workflows array
		
		// Print the workflows
		for(WorkflowNode wn:workflows) {
			System.out.println("------------");
			System.out.println(wn);
		}
	}
		
	
	/**
	 * Recursively populate the parentsList given the current node
	 * @param curr           current node
	 * @param parentsList    list of parents from the origin node
	 */
	private void getParentsRecursivePopulate(WorkflowNode curr, ArrayList<WorkflowNode> parentsList) {
		if(curr != null && !parentsList.contains(curr)) {
			parentsList.add(curr);
			if(curr.getIncomingLinks() != null) {
				for(WorkflowNode p : curr.getIncomingLinks()) {
					getParentsRecursivePopulate(p,parentsList);
				}
			}
		}
	}
	
	
	/**
	 * Recursively populate the parentsList given the current node
	 * @param curr           current node
	 * @param parentsList    list of parents from the origin node
	 */
	private void getChildrenRecursivePopulate(WorkflowNode curr, ArrayList<WorkflowNode> childList) {
		if(curr != null && !childList.contains(curr)) {
			childList.add(curr);
			if(curr.getOutgoingLinks() != null) {
				for(WorkflowNode c : curr.getOutgoingLinks()) {
					getChildrenRecursivePopulate(c,childList);
				}
			}
		}
	}
	
	
	/**
	 * Adds any dataset nodes from the json into the workflows array. This method must be called before
	 * the addInOutLinks() method so that the program can add a link to the WorkflowNode object when a
	 * link between two objects has been found
	 */
	private void addDatasets() {
		String[] arr = {"template","Variables"};
		JsonObject variables = getValue(json, arr);
		Set<String> variableKeys = variables.keySet();
		
		// Loop for every dataset in the workflow
		for(String key: variableKeys) {
			String[] keyArr = {"template","Variables",key};
			JsonObject curr = getValue(json, keyArr);
			
			// Get name from key string (Key is URL with the variable #name at end)
			String[] split = key.split("#");
			String name = split[split.length-1];
			
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
	 * Adds any step nodes from the json into the workflows array. This method must be called before the
	 * addInOutLinks() method so it can link any input and output links to the correct WorkflowNode object
	 */
	private void addSteps() {
		String[] arr = {"template","Nodes"};
		JsonObject nodes = getValue(json, arr);
		Set<String> nodeKeys = nodes.keySet();
		
		// Loop for every step in the workflow
		for(String key: nodeKeys) {
			
			// Get name from componentVariable binding (owl url)
			String id = key;
			String[] split = id.split("#");
			String name = split[split.length-1];
						
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
		
		String[] arr = {"template","Links"};
		JsonObject links = getValue(json, arr);
		Set<String> linkKeys = links.keySet();
		
		for(String key:linkKeys) {
			
			String[] keyArr = {"template","Links",key};
			JsonObject link = getValue(json, keyArr);
			Set<String> keySet = link.keySet();
			
			JsonObject variable = getValue(link,"variable");
			WorkflowNode curr = getWorkflowNode(variable.getString("id"));

			// loop over keys and assign any links to workflows inputs and outputs
			for(String args:keySet) {
				
				if(!args.equals("id")) {
					JsonObject cJson = getValue(link,args);
					
					if(args.equals("toNode")) {
						WorkflowNode toNode = getWorkflowNode(cJson.getString("id"));
						toNode.addIncomingLink(curr);
						curr.addOutgoingLink(toNode);
					}
					
					if(args.equals("fromNode")) {
						WorkflowNode fromNode = getWorkflowNode(cJson.getString("id"));
						fromNode.addOutgoingLink(curr);
						curr.addIncomingLink(fromNode);
					}
				}
			}
			
			
		}
	}
	
	
	/**
	 * Get a WorflowNode from the workflows array from the given id. id can either be the name of the 
	 * workflow or its id. If the workflow is not in the list then this function returns null.
	 * 
	 * @param id  Name or Id string of the node to search for
	 * @return    WorkflowNode with the given name/id or null if does not exist
	 */
	private WorkflowNode getWorkflowNode(String indicator) {
		
		// O(n)
		for(WorkflowNode wn:workflows) {
			String name = wn.getName();
			String id = wn.getId();
			
			// Return workflow if it has the same name
			if(name.equalsIgnoreCase(indicator)) {
				return wn;
			}
			
			// Return workflow if it has the same id
			if(id.equalsIgnoreCase(indicator)) {
				return wn;
			}
		}
		
		// Return null if nothing matched
		return null;
	}
	
	
	/**
	 * Returns a jsonObject from the given key. If one can't be found, returns null.
	 * Note: the value to return must be a jsonObject. This method cannot return a value and will return
	 * and empty jsonObject if the given key links to a value (ie: {"key": 1})
	 * 
	 * @param object json object
	 * @param key    key of desired json object value
	 * @return       json object from given key
	 */
	private JsonObject getValue(JsonObject object, String key) {
		try {
			return object.get(key).asJsonObject();
		} catch(Exception e) {
			System.out.println("Error getting key: " + key + " from object: " + object);
			 JsonReader jsonReader = Json.createReader(new StringReader("{}"));
			 return jsonReader.readObject();
		}
	}
	
	

	/**
	 * This method returns the json object after traversing through the array of keys. This is intended to make
	 * searching down larger json objects easier.
	 * Note: the value to return must be a jsonObject. This method cannot return a value and will return
	 * and empty jsonObject {} if the given key links to a value (ie: {"key": 1})
	 * 
	 * @param object input json object
	 * @param key    array of keys to search down
	 * @return       json object from given key list
	 */
	private JsonObject getValue(JsonObject object, String[] key) {
		JsonObject o;
		try {
			o = object.get(key[0]).asJsonObject();
		} catch(NullPointerException e) {
			System.out.println("Error getting key: " + key[0] + " from object: " + object);
			 JsonReader jsonReader = Json.createReader(new StringReader("{}"));
			 return jsonReader.readObject();
		}
		
		
		for (int i = 1; i < key.length; i++) {
			try {
				JsonValue tmp = o.get(key[i]);
				if(tmp.getValueType().toString().equalsIgnoreCase("object")) {
					o = tmp.asJsonObject();
				} else if(i != key.length -1) {
					System.out.println("Error getting key: " + key[i] + " from object: " + tmp + ". Too many keys; not enough json objects");
					 JsonReader jsonReader = Json.createReader(new StringReader("{}"));
					 return jsonReader.readObject();
				} else {
					System.out.println("Cannot get value from this method. It only returns JsonObjects. Try using one less key, then getting the value directly with var.get(key)");
					 JsonReader jsonReader = Json.createReader(new StringReader("{}"));
					 return jsonReader.readObject();
				}
			} catch(NullPointerException e) {
				System.out.println("Error getting key: " + key[i] + " from object: " + o);
				 JsonReader jsonReader = Json.createReader(new StringReader("{}"));
				 return jsonReader.readObject();
			}
		}
		
		return o;
		
	}
}
