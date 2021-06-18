package dana;

import java.util.ArrayList;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.json.JsonObject;

public class JsonWriter {

	ArrayList<WorkflowNode> workflow;
	JsonObject json;

	public JsonWriter(ArrayList<WorkflowNode> workflow) {
		this.workflow = workflow;
	}

	public void writeJson() {
		writeJson(".");
	}

	public void writeJson(String path) {
		ArrayList<KeyValuePair> keys = new ArrayList<KeyValuePair>();

		keys.add(new KeyValuePair("metadata", metadata()));
		keys.add(new KeyValuePair("nodes", datasets()));
		System.out.println(prettyJson(keyValueStringFormatter(keys)));
		String toWrite = prettyJson(keyValueStringFormatter(keys));

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("readData.json"));
			writer.write(toWrite);

			writer.close();
		} catch (IOException e) {
			System.out.println("Error writing json file:");
			e.printStackTrace();
		}

	}

	private String metadata() {

		// Create an array of the names of each node. This will come in handy when
		// reading in the json later
		String[] nodes = new String[workflow.size()];
		for (int i = 0; i < workflow.size(); i++) {
			nodes[i] = workflow.get(i).getName();
		}

		ArrayList<KeyValuePair> keys = new ArrayList<KeyValuePair>();
		keys.add(new KeyValuePair("author"));
		keys.add(new KeyValuePair("dateCreated"));
		keys.add(new KeyValuePair("nodes", arrayToCsv(nodes)));

		return keyValueStringFormatter(keys);
	}

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

				if (wn.getIncomingLinks() != null) {
					String[] inputLinks = new String[wn.getIncomingLinks().size()];
					ArrayList<WorkflowNode> incoming = wn.getIncomingLinks();

					for (int i = 0; i < incoming.size(); i++) {
						inputLinks[i] = incoming.get(i).getName();
					}

					datasetMetadata.add(new KeyValuePair("hasInput", arrayToCsv(inputLinks)));
				} else {
					datasetMetadata.add(new KeyValuePair("hasInput", null));
				}

				if (wn.getOutgoingLinks() != null) {
					String[] outgoingLinks = new String[wn.getOutgoingLinks().size()];
					ArrayList<WorkflowNode> outgoing = wn.getOutgoingLinks();

					for (int i = 0; i < outgoing.size(); i++) {
						outgoingLinks[i] = outgoing.get(i).getName();
					}

					datasetMetadata.add(new KeyValuePair("hasOutput", arrayToCsv(outgoingLinks)));
				} else {
					datasetMetadata.add(new KeyValuePair("hasOutput", null));
				}

				if (wn.isParameter()) {
					parameterKeys.add(new KeyValuePair(wn.getName(), keyValueStringFormatter(datasetMetadata)));
				} else {
					datasetKeys.add(new KeyValuePair(wn.getName(), keyValueStringFormatter(datasetMetadata)));
				}
			}
		}

		keys.add(new KeyValuePair("datasets", keyValueStringFormatter(datasetKeys)));
		keys.add(new KeyValuePair("patameters", keyValueStringFormatter(parameterKeys)));
		keys.add(new KeyValuePair("steps", keyValueStringFormatter(step())));
		return keyValueStringFormatter(keys);
	}

	private ArrayList<KeyValuePair> step() {

		ArrayList<KeyValuePair> stepKeys = new ArrayList<KeyValuePair>();

		// Add step nodes
		for (WorkflowNode wn : workflow) {
			if (!wn.isDataset()) {

				ArrayList<KeyValuePair> datasetMetadata = new ArrayList<KeyValuePair>();
				datasetMetadata.add(new KeyValuePair("shortDescription"));
				datasetMetadata.add(new KeyValuePair("longDescription"));
				datasetMetadata.add(new KeyValuePair("gitHubRepositoryLink"));
				datasetMetadata.add(new KeyValuePair("criticality"));
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

				if (wn.getIncomingLinks() != null) {
					String[] inputLinks = new String[wn.getIncomingLinks().size()];
					ArrayList<WorkflowNode> incoming = wn.getIncomingLinks();

					for (int i = 0; i < incoming.size(); i++) {
						inputLinks[i] = incoming.get(i).getName();
					}

					datasetMetadata.add(new KeyValuePair("hasInput", arrayToCsv(inputLinks)));
				} else {
					datasetMetadata.add(new KeyValuePair("hasInput", null));
				}

				if (wn.getOutgoingLinks() != null) {
					String[] outgoingLinks = new String[wn.getOutgoingLinks().size()];
					ArrayList<WorkflowNode> outgoing = wn.getOutgoingLinks();

					for (int i = 0; i < outgoing.size(); i++) {
						outgoingLinks[i] = outgoing.get(i).getName();
					}

					datasetMetadata.add(new KeyValuePair("hasOutput", arrayToCsv(outgoingLinks)));
				} else {
					datasetMetadata.add(new KeyValuePair("hasOutput", null));
				}

				if (wn.isParameter()) {
					stepKeys.add(new KeyValuePair(wn.getName(), keyValueStringFormatter(datasetMetadata)));
				} else {
					stepKeys.add(new KeyValuePair(wn.getName(), keyValueStringFormatter(datasetMetadata)));
				}
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
