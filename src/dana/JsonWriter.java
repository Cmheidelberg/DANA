package dana;

import java.util.ArrayList;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.json.JsonObject;

/**
 * @author Chris Heidelberg
 * 
 *         Generate a json file used to enter in all the human entered data for
 *         DANA. This allows DANA to collect additional metadata fields it would
 *         be unable to assertain from the winds json alone
 */
public class JsonWriter {

	ArrayList<WorkflowNode> workflow;
	JsonObject json;

	public JsonWriter(ArrayList<WorkflowNode> workflow) {
		this.workflow = workflow;
	}

	/**
	 * Generate the JSON with all metadata fields. Any unknown data fields will be
	 * written with a value of null
	 */
	public void writeJson() {
		writeJson(".");
	}

	/**
	 * Generate the JSON with all metadata fields. Any unknown data fields will be
	 * written with a value of null
	 * 
	 * @param path
	 */
	public void writeJson(String path) {
		ArrayList<KeyValuePair> keys = new ArrayList<KeyValuePair>();

		keys.add(new KeyValuePair("metadata", metadata()));
		keys.add(new KeyValuePair("nodes", datasets()));
		String toWrite = prettyJson(keyValueStringFormatter(keys));

		// Write the json string to a file called "readData.json"
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("readData.json"));
			writer.write(toWrite);
			writer.close();
		} catch (IOException e) {
			System.out.println("Error writing json file:");
			e.printStackTrace();
		}

	}

	/**
	 * Generate a json object representing the workflow's metadata
	 * 
	 * @return String representation of the metadata json object
	 */
	private String metadata() {

		// Create an array of the names of each node.
		String[] nodes = new String[workflow.size()];
		for (int i = 0; i < workflow.size(); i++) {
			nodes[i] = workflow.get(i).getFullName();
		}

		ArrayList<KeyValuePair> keys = new ArrayList<KeyValuePair>();
		keys.add(new KeyValuePair("description"));
		keys.add(new KeyValuePair("citation"));
		keys.add(new KeyValuePair("author"));
		keys.add(new KeyValuePair("dateCreated"));
		keys.add(new KeyValuePair("nodes", arrayToCsv(nodes)));
		keys.add(new KeyValuePair("jsonCreatedBy", "DANA"));

		return keyValueStringFormatter(keys);
	}

	/**
	 * Generate a json object representing each of the workflow's nodes. This
	 * includes dataset nodes, parameter nodes and step nodes
	 * 
	 * @return String representation of the datasets json object
	 */
	private String datasets() {
		ArrayList<KeyValuePair> keys = new ArrayList<KeyValuePair>();
		ArrayList<KeyValuePair> datasetKeys = new ArrayList<KeyValuePair>();
		ArrayList<KeyValuePair> parameterKeys = new ArrayList<KeyValuePair>();

		// Add datasets and parameters
		for (WorkflowNode wn : workflow) {
			if (wn.isDataset()) {

				ArrayList<KeyValuePair> datasetMetadata = new ArrayList<KeyValuePair>();
				datasetMetadata.add(new KeyValuePair("description"));
				datasetMetadata.add(new KeyValuePair("license"));
				datasetMetadata.add(new KeyValuePair("author"));
				datasetMetadata.add(new KeyValuePair("citation"));
				datasetMetadata.add(new KeyValuePair("doi"));
				datasetMetadata.add(new KeyValuePair("url"));
				datasetMetadata.add(new KeyValuePair("type", "(not detected)"));
				datasetMetadata.add(new KeyValuePair("data", "(path to data should go here)"));
				datasetMetadata.add(new KeyValuePair("id", wn.getId()));

				//Reference to any nodes that point into the current node
				if (wn.getIncomingLinks() != null) {
					String[] inputLinks = new String[wn.getIncomingLinks().size()];
					ArrayList<WorkflowNode> incoming = wn.getIncomingLinks();

					for (int i = 0; i < incoming.size(); i++) {
						inputLinks[i] = incoming.get(i).getFullName();
					}
					datasetMetadata.add(new KeyValuePair("hasInput", arrayToCsv(inputLinks)));
				} else {
					datasetMetadata.add(new KeyValuePair("hasInput", null));
				}

				//Reference to any nodes that the current node points to
				if (wn.getOutgoingLinks() != null) {
					String[] outgoingLinks = new String[wn.getOutgoingLinks().size()];
					ArrayList<WorkflowNode> outgoing = wn.getOutgoingLinks();

					for (int i = 0; i < outgoing.size(); i++) {
						outgoingLinks[i] = outgoing.get(i).getFullName();
					}

					datasetMetadata.add(new KeyValuePair("hasOutput", arrayToCsv(outgoingLinks)));
				} else {
					datasetMetadata.add(new KeyValuePair("hasOutput", null));
				}

				//Keep separate objects for parameters and datasets
				KeyValuePair c = new KeyValuePair(wn.getFullName(), keyValueStringFormatter(datasetMetadata));
				if (wn.isParameter()) {
					parameterKeys.add(c);
				} else {
					datasetKeys.add(c);
				}
			}
		}

		keys.add(new KeyValuePair("datasets", keyValueStringFormatter(datasetKeys)));
		keys.add(new KeyValuePair("parameters", keyValueStringFormatter(parameterKeys)));
		keys.add(new KeyValuePair("steps", keyValueStringFormatter(step())));
		return keyValueStringFormatter(keys);
	}

	private ArrayList<KeyValuePair> step() {

		ArrayList<KeyValuePair> stepKeys = new ArrayList<KeyValuePair>();

		// Add step nodes
		for (WorkflowNode wn : workflow) {
			if (!wn.isDataset()) {

				//Add all metadata fields (these are all fields that a human will need to fill out)
				ArrayList<KeyValuePair> datasetMetadata = new ArrayList<KeyValuePair>();
				datasetMetadata.add(new KeyValuePair("fullName", wn.getFullName()));
				datasetMetadata.add(new KeyValuePair("displayName", wn.getDisplayName()));
				datasetMetadata.add(new KeyValuePair("shortDescription"));
				datasetMetadata.add(new KeyValuePair("longDescription"));
				datasetMetadata.add(new KeyValuePair("gitHubUrl"));
				datasetMetadata.add(new KeyValuePair("criticality",1));
				datasetMetadata.add(new KeyValuePair("stepType"));
				datasetMetadata.add(new KeyValuePair("website"));
				datasetMetadata.add(new KeyValuePair("citation"));
				datasetMetadata.add(new KeyValuePair("author"));
				datasetMetadata.add(new KeyValuePair("license"));
				datasetMetadata.add(new KeyValuePair("versionNumber"));
				datasetMetadata.add(new KeyValuePair("dependencies"));
				datasetMetadata.add(new KeyValuePair("documentationLink"));
				datasetMetadata.add(new KeyValuePair("commandLineInvocation"));
				datasetMetadata.add(new KeyValuePair("id", wn.getId()));
				
				//Reference to any nodes that point into the current step
				if (wn.getIncomingLinks() != null) {
					String[] inputLinks = new String[wn.getIncomingLinks().size()];
					ArrayList<WorkflowNode> incoming = wn.getIncomingLinks();

					for (int i = 0; i < incoming.size(); i++) {
						inputLinks[i] = incoming.get(i).getFullName();
					}
					datasetMetadata.add(new KeyValuePair("hasInput", arrayToCsv(inputLinks)));
				} else {
					datasetMetadata.add(new KeyValuePair("hasInput", null));
				}

				//Reference to any nodes that the current step points to
				if (wn.getOutgoingLinks() != null) {
					String[] outgoingLinks = new String[wn.getOutgoingLinks().size()];
					ArrayList<WorkflowNode> outgoing = wn.getOutgoingLinks();

					for (int i = 0; i < outgoing.size(); i++) {
						outgoingLinks[i] = outgoing.get(i).getFullName();
					}
					datasetMetadata.add(new KeyValuePair("hasOutput", arrayToCsv(outgoingLinks)));
				} else {
					datasetMetadata.add(new KeyValuePair("hasOutput", null));
				}
				KeyValuePair c = new KeyValuePair(wn.getFullName(), keyValueStringFormatter(datasetMetadata));
				stepKeys.add(c);
			}
		}
		
		return stepKeys;
	}

	/**
	 * Creates a (formatted) string representation of a json object from the given
	 * list of key/value pair objects
	 * 
	 * ie: { "key1": "value1", "key2": 3, "key3": { "key3.1": "value" } }
	 * 
	 * @param list of KeyValuePair objects to be added as json objects
	 * @return
	 */
	private String keyValueStringFormatter(ArrayList<KeyValuePair> keys) {
		if (keys == null) {
			return "{}";
		}

		String obj = "{\n";
		for (int i = 0; i < keys.size(); i++) {
			obj += keys.get(i);

			if (i != keys.size() - 1) {
				obj += ",\n";
			} else {
				obj += "\n";
			}
		}
		obj += "}";
		return obj;
	}

	/**
	 * Creates a csv string from the given array
	 * 
	 * @param arr input array
	 * @return comma separated values
	 */
	private String arrayToCsv(String[] arr) {
		String outp = "";
		for (int i = 0; i < arr.length; i++) {
			outp += arr[i];

			if (i != arr.length - 1) {
				outp += ",";
			}
		}
		return outp;
	}

	/**
	 * Given a string representation of a json, format with spaces for nested json
	 * objects. This will make it look nicer to read when printed to a file
	 * 
	 * @param json Json to format
	 * @return formatted json
	 */
	public String prettyJson(String json) {
		int tabLevel = 0;
		String out = "";
		String spacesPerTab = "   ";
		for (int i = 0; i < json.length(); i++) {
			String tabs;
			switch (json.charAt(i)) {
			case '{':
				tabLevel += 1;
				tabs = spacesPerTab.repeat(tabLevel);
				out += json.charAt(i) + tabs;
				break;

			case '}':
				tabLevel -= 1;
				tabs = spacesPerTab.repeat(tabLevel);
				out = out.substring(0, out.length() - 3);
				out += json.charAt(i);
				break;
			case '\n':
				tabs = spacesPerTab.repeat(tabLevel);
				out += json.charAt(i) + tabs;
				break;

			default:
				out += json.charAt(i);
				break;
			}
		}
		return out;
	}

}

/**
 * Store a relation of key, value pair. Sometimes the value will be empty since
 * much of the metadata will be entered from the user.
 */
class KeyValuePair {

	String key;
	Object value;

	KeyValuePair(String key, Object value) {
		this.key = key;
		this.value = value;
	}

	KeyValuePair(String key) {
		this.key = key;
		this.value = null;
	}

	public String getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}

	public String toString() {
		if (value != null) {
			if (value.getClass() == String.class && value.toString().length() > 0 && value.toString().charAt(0) != '{')
				return "\"" + key + "\": \"" + value + "\"";
			else
				return "\"" + key + "\": " + value;
		}

		return "\"" + key + "\": null";
	}
}
