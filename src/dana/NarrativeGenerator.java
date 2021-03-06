package dana;

import java.util.ArrayList;

public class NarrativeGenerator {

	WorkflowJson workflow;
	int citationReference = 1;
	ArrayList<Narrative> citations = new ArrayList<Narrative>();

	public NarrativeGenerator(WorkflowJson workflow) {
		this.workflow = workflow;
	}

	/**
	 * Returns the data narrative for a given step in the workflow. Returns a string
	 * of the description.
	 */
	public String getStepNarrative(StepNode step) {
		StepNarrative sn = new StepNarrative(workflow, step, citationReference);

		if (!step.getCitation().equalsIgnoreCase("null") && !step.getCitation().equals("")) {
			updateCitations(sn);
		}

		return sn.getNarrative();
	}

	/**
	 * Returns the data narrative for a given dataset in the workflow. Returns a
	 * string of the description. This method will also automatically increment the
	 * citatinReference variable if the dataset has a citation in it. The citation
	 * reference is the citation number when the
	 * 
	 * TODO: update this description
	 */
	public String getDatasetNarrative(DatasetNode dataset) {
		DatasetNarrative dn = new DatasetNarrative(workflow, dataset, citationReference);

		if (!dataset.getCitation().equalsIgnoreCase("null") && !dataset.getCitation().equals("")) {
			updateCitations(dn);
		}
		return dn.getNarrative();
	}

	/**
	 * Calls the appropriate narrative generating function for a given WorkflowNode.
	 * This is to make it easier to generate data narratives while looping over
	 * every node in a workflow. Returns a string narrative for the given node.
	 */
	public String getNodeNarrative(WorkflowNode node) {

		if (node.isDataset()) {
			try {
				DatasetNode dn = (DatasetNode) node;
				return getDatasetNarrative(dn);

			} catch (ClassCastException cce) {
				System.out.println("Error: cannot cast \"" + node.getFullName() + "\" as a DatasetNode");
				cce.printStackTrace();
				return null;
			}
		} else {
			try {
				StepNode sn = (StepNode) node;
				return getStepNarrative(sn);
			} catch (ClassCastException cce) {
				System.out.println("Error: cannot cast \"" + node.getFullName() + "\" as a StepNode");
				cce.printStackTrace();
				return null;
			}
		}
	}

	public String getWorkflowNarrative(int minCriticality) {
		return getWorkflowNarrative(true, minCriticality);
	}

	/**
	 * Warning: Calling this function resets the citationReference counter.
	 */
	public String getWorkflowNarrative(boolean trackCitations, int minCriticality) {
		resetCitationReference();
		WorkflowNarrative wn = new WorkflowNarrative(workflow, citationReference, minCriticality);
		if (!workflow.getCitation().equalsIgnoreCase("null") && !workflow.getCitation().equals("")) {
			updateCitations(wn);
		}
		return wn.getNarrative();
	}

	private void updateCitations(Narrative narrative) {
		citations.add(narrative);
		citationReference += 1;
	}

	public void resetCitationReference() {
		citationReference = 1;
		citations.clear();
	}

	public String getCitation(String nodeName) {
		for (Narrative n : citations) {
			if (n.getName().equalsIgnoreCase(nodeName)) {
				return n.getCitation();
			}
		}
		return null;
	}

	public ArrayList<String> getAllCitations() {
		ArrayList<String> out = new ArrayList<String>();

		for (Narrative n : citations) {
			out.add(n.getCitation());
		}

		return out;
	}
}

interface Narrative {
	public String getNarrative();

	public String getCitation();

	public String getName();
}

class WorkflowNarrative implements Narrative {
	WorkflowJson workflow;
	boolean hasCitation;
	int citationReference;
	int minCriticality;

	WorkflowNarrative(WorkflowJson workflow, int citationReference, int minCriticality) {
		this.workflow = workflow;
		this.hasCitation = !workflow.getCitation().equalsIgnoreCase("null") && !workflow.getCitation().equals("");
		this.citationReference = citationReference;
		this.minCriticality = minCriticality;
	}

	public String getNarrative() {
		String outp = "";

		int numInputs = workflow.getInputs().size();
		int numOutputs = workflow.getOutputs().size();
		int numParameters = workflow.getParameters().size();

		boolean hasDescription = workflow.getDescription() != null
				&& !workflow.getDescription().equalsIgnoreCase("null") && !workflow.getDescription().equals("");
		// Workflow metadata
		String citationReferenceString = "";
		if (hasCitation) {
			citationReferenceString = " [" + citationReference + "] ";
		}

		if (hasDescription) {
			outp += "This workflow" + citationReferenceString + workflow.getDescription() + ".\n";
		}
		outp += "\n";

		// Workflow link descriptions

		outp += hasDescription ? "[THIS WORKFLOW] " : "[THIS WORKFLOW]" + citationReferenceString;
		if (numInputs > 0) {
			String inputs = numInputs == 1 ? "input" : "inputs";
			outp += "takes in " + Num2Word.convert(numInputs) + " " + inputs;
			if (numParameters > 0) {
				String parameters = numParameters == 1 ? "parameter" : "parameters";
				outp += ", " + Num2Word.convert(numParameters) + " " + parameters + ",";
			}

			String outputs = numOutputs == 1 ? "output" : "outputs";
			outp += " and produces " + Num2Word.convert(numOutputs) + " " + outputs + ".";

		} else if (numParameters > 0) {
			String parameters = numParameters == 1 ? "parameter" : "parameters";
			outp += "takes in " + Num2Word.convert(numParameters) + " " + parameters;

			String outputs = numOutputs == 1 ? "output" : "outputs";
			outp += " and produces " + Num2Word.convert(numOutputs) + " " + outputs + ".";

		} else {
			String outputs = numOutputs == 1 ? "output" : "outputs";
			outp += " produces " + Num2Word.convert(numOutputs) + " " + outputs + ".";
			outp += " There are no inputs for this workflow.";
		}

		ArrayList<WorkflowNode> longestPath = workflow.getLongestPath(minCriticality);
		outp += "\n";
		for (WorkflowNode wn : longestPath) {
			String line = wn.equals(longestPath.get(longestPath.size() - 1)) ? wn.getDisplayName() + "\n"
					: wn.getDisplayName() + " -> ";
			System.out.print(line);
			outp += line; // TODO replace this with the actual description
		}

		return outp;
	}

	public String getCitation() {
		if (hasCitation) {
			return "[" + citationReference + "] " + workflow.getCitation();
		} else {
			return "";
		}
	}

	public String getName() {
		return "workflow";
	}
}

class DatasetNarrative implements Narrative {

	DatasetNode dataset;
	WorkflowJson workflow;
	boolean hasCitation;
	int citationReference;

	DatasetNarrative(WorkflowJson workflow, DatasetNode dataset, int citationReference) {
		this.workflow = workflow;
		this.dataset = dataset;
		this.hasCitation = !dataset.getCitation().equalsIgnoreCase("null") && !dataset.getCitation().equals("");
		this.citationReference = citationReference;
	}

	public String getNarrative() {
		String outp = "";
		String citationReferenceString = "";

		boolean hasFileType = dataset.getType() != null && !dataset.getType().equalsIgnoreCase("null")
				&& !dataset.getType().equals("");
		boolean hasDescription = dataset.getDescription() != null && !dataset.getDescription().equalsIgnoreCase("null")
				&& !dataset.getDescription().equals("");

		if (hasCitation) {
			citationReferenceString = " [" + citationReference + "]";
		}

		String fileType = "";
		if (dataset.isParameter()) {
			fileType = "parameter";
		} else {
			if (hasFileType) {
				fileType = dataset.getType() + " file";
			} else {
				fileType = "dataset";
			}
		}
		outp += dataset.getDisplayName() + citationReferenceString + " is a " + fileType + ".";

		if (hasDescription) {
			String descriptionNoun = "";
			if (dataset.isParameter()) {
				descriptionNoun = " This parameter";
			} else if (hasFileType) {
				descriptionNoun = " This dataset";
			} else {
				descriptionNoun = " It";
			}
			outp += descriptionNoun + " " + dataset.getDescription() + ".";
		}

		if (isInput()) {
			int inputLen = workflow.getInputs().size();
			if (inputLen > 1) {
				outp += " It is one of " + Num2Word.convert(inputLen) + " inputs for this workflow.";
			} else {
				outp += " It is the workflows input datset.";
			}
		}

		if (isOutput()) {
			int outLen = workflow.getOutputs().size();
			if (outLen > 1) {
				outp += " It is one of " + Num2Word.convert(outLen) + " outputs for this workflow.";
			} else {
				outp += " It is the workflows output datset.";
			}
		}

		return outp;
	}

	public String getCitation() {
		if (hasCitation) {
			return "[" + citationReference + "] " + dataset.getCitation();
		} else {
			return "";
		}
	}

	public String getName() {
		return dataset.getDisplayName();
	}

	private boolean isInput() {
		ArrayList<DatasetNode> inputs = workflow.getInputs();

		for (DatasetNode dn : inputs) {
			if (dataset.equals(dn)) {
				return true;
			}
		}
		return false;
	}

	private boolean isOutput() {
		ArrayList<DatasetNode> outputs = workflow.getOutputs();

		for (DatasetNode dn : outputs) {
			if (dataset.equals(dn)) {
				return true;
			}
		}
		return false;
	}
}

class StepNarrative implements Narrative {

	StepNode step;
	WorkflowJson workflow;
	int remainingDP;
	int discussionPoints;
	boolean hasCitation;
	Boolean hasStepType;
	int citationReference;

	// Supporting fields
	String[] stepReference;
	int stepReferenceCount = 0;

	public StepNarrative(WorkflowJson workflow, StepNode step, int citationReference) {
		this.step = step;
		this.workflow = workflow;
		this.citationReference = citationReference;
		this.hasCitation = !step.getCitation().equalsIgnoreCase("null") && !step.getCitation().equals("");
		this.hasStepType = !step.getStepType().equalsIgnoreCase("null") && !step.getStepType().equals("");
		String[] tmp = { step.getDisplayName(), "it", "this step", "it" };

		stepReference = tmp;
	}

	/**
	 * Generate a narrative for the step. A narrative is a human readable
	 * description for the metadata associated with the step.
	 * 
	 * @return narrative
	 */
	public String getNarrative() {
		String outp = "";

		String citationReferenceString = "";
		if (hasCitation) {
			citationReferenceString = " [" + citationReference + "]";
		}

		// ==MAIN DESCRIPTION==
		if (hasStepType) {
			outp += getNextStepReference() + citationReferenceString + " takes in " + getInputDescription()
					+ getVerbLink() + " producing " + getOutputDescription() + ".";
		} else {
			outp += getNextStepReference() + citationReferenceString + " takes in " + getInputDescription()
					+ " and produces " + getOutputDescription() + ".";
		}

		if (step.getLongDescription().length() > 0 && !step.getLongDescription().equalsIgnoreCase("null")) {
			outp += " " + step.getDisplayName() + " " + step.getLongDescription() + ".";
		}

		int count = workflow.CountTimesUsedInWorkflow(step);
		String times = count > 1 ? "times" : "time";

		if (count > 1) {
			outp += " Overall, this step is used " + Num2Word.convert(count) + " " + times + " in the workflow.";
		}

		// ==ADDITIONAL INFORMATION
		outp += getAdditionalInformation();
//		outp += "\n\n";
//
//		// CREDITS
//		outp += getCitation();
		return outp;
	}

	public String getCitation() {
		if (hasCitation) {
			return "[" + citationReference + "] " + step.getCitation();
		} else {
			return "";
		}
	}

	public String getName() {
		return step.getDisplayName();
	}

	private String getAdditionalInformation() {
		String outp = "";

		boolean hasUrl = !step.getUrl().equals("") && !step.getUrl().equalsIgnoreCase("null");
		boolean hasGitHubRepo = !step.getGitHubUrl().equals("") && !step.getGitHubUrl().equalsIgnoreCase("null");
		boolean hasDocumentationLink = !step.getDocumentationLink().equals("")
				&& !step.getDocumentationLink().equalsIgnoreCase("null");
		boolean hasVersion = !step.getVersionNumber().equals("") && !step.getVersionNumber().equalsIgnoreCase("null");

		if (hasUrl || hasGitHubRepo || hasDocumentationLink) {
			outp += "\n\n";
			outp += "For more information about the software implemented in this step,";

			if (hasUrl) {
				outp += " the website can be found at " + step.getUrl() + ".";
				if (hasGitHubRepo) {
					outp += " Additionally, a repository exists at " + step.getGitHubUrl();
					if (hasVersion)
						outp += ", this workflow used version " + step.getVersionNumber() + ".";
					else
						outp += ".";
				} else if (hasVersion) {
					outp += " This workflow used version " + step.getVersionNumber() + ".";
				}

				if (hasDocumentationLink) {
					outp += " For implementation details, see the documentation at " + step.getDocumentationLink()
							+ ".";
				}
			} else if (hasDocumentationLink) {
				outp += " see the documentation at " + step.getDocumentationLink() + ".";
				if (hasGitHubRepo) {
					outp += " Additionally, a repository exists at " + step.getGitHubUrl();
					if (hasVersion)
						outp += ", this workflow used version " + step.getVersionNumber() + ".";
					else
						outp += ".";
				} else if (hasVersion) {
					outp += " This workflow used version " + step.getVersionNumber() + ".";
				}
			} else if (hasGitHubRepo) {
				outp += " a repository can be found at " + step.getGitHubUrl();
				if (hasVersion)
					outp += ", this workflow used version " + step.getVersionNumber() + ".";
				else
					outp += ".";
			}
		} else if (hasVersion) {
			outp += "This workflow used version " + step.getVersionNumber() + ".";
		}

		return outp;
	}

	/**
	 * Writes the input description for the main step description. This description
	 * discusses what file types/parameters go into the current step as well as how
	 * many.
	 * 
	 * @return
	 */
	private String getInputDescription() {
		ArrayList<WorkflowNode> inputs = step.getIncomingLinks();
		ArrayList<DatasetNode> datasetInputs = new ArrayList<DatasetNode>();
		ArrayList<DatasetNode> parameterInputs = new ArrayList<DatasetNode>();
		
		// Separate Dataset inputs from parameter inputs
		for (WorkflowNode i : inputs) {
			if (i.isDataset()) {
				DatasetNode tmp = (DatasetNode) i;
				if (tmp.isParameter()) {
					parameterInputs.add(tmp);
				} else {
					datasetInputs.add(tmp);
				}
			} else {
				System.out.println(
						"WARNING: found input to step (" + step.getFullName() + ") that is not a dataset: " + i);
				System.out.println("Ignoring warning");
			}
		}

		String outp = "";

		ArrayList<String> types = getUniqueTypes(datasetInputs);
		discussionPoints = types.size();

		if (parameterInputs.size() > 0) {
			discussionPoints += 1;
		}

		remainingDP = discussionPoints;
		String ctypeSpaced;
		// Generate description for each unique type of input
		for (String ctype : types) {
			ctype = ctype.toLowerCase();
			ctypeSpaced = ctype.length() > 0 ? " " + ctype : ctype;

			ArrayList<DatasetNode> currDatasets = getDatasetsWithType(ctype, datasetInputs);
			if (currDatasets.size() == 1) {
				outp += "a" + ctypeSpaced + " file[" + datasetInputs.get(0).getDisplayName() + "]";
				remainingDP--;

			} else if (currDatasets.size() > 1) {
				outp += Num2Word.convert(currDatasets.size()) + ctypeSpaced + "files[";
				for (int i = 0; i < currDatasets.size(); i++) {
					outp += i == currDatasets.size() - 1 ? currDatasets.get(i).getDisplayName()
							: currDatasets.get(i).getDisplayName() + ",";
				}
				outp += "]";
				remainingDP--;
			}

			if (parameterInputs.size() == 0) {
				if (discussionPoints >= 2 && remainingDP > 1 && !ctype.equalsIgnoreCase(types.get(0))) {
					outp += ", ";
				} else if (remainingDP == 1 && discussionPoints > 1) {
					outp += "and ";
				} else {
					outp += ",";
				}
			}
		}

		// Parameter inputs are treated as one type of input
		if (parameterInputs.size() > 0) {

			if (discussionPoints >= 2 && remainingDP > 1) {
				outp += ", ";
			} else if (remainingDP == 1 && discussionPoints > 1) {
				outp += " and ";
			} else {
				outp += " ";
			}

			if (parameterInputs.size() == 1) {
				outp += "a parameter[" + parameterInputs.get(0).getDisplayName() + "],";
			} else if (parameterInputs.size() > 1) {
				outp += Num2Word.convert(parameterInputs.size()) + " parameters[";
				for (int i = 0; i < parameterInputs.size(); i++) {
					outp += i == parameterInputs.size() - 1 ? parameterInputs.get(i).getDisplayName()
							: parameterInputs.get(i).getDisplayName() + ",";
				}
				outp += "],";
			}
		}
		return outp;
	}

	/**
	 * Links together the InputDescription and outputDescription. This transition
	 * makes it a little less robotic to read the step descriptions
	 */
	private String getVerbLink() {

		String outp = "";

		if (hasStepType) {
			if (discussionPoints > 2) {
				outp += " then " + step.getStepType() + " them";
			} else if (discussionPoints == 2) {
				outp += " " + step.getStepType() + " them,";
			} else {
				outp += step.getStepType() + " it,";
			}
		}
		return outp;
	}

	/**
	 * Generates a description for any output datasets that are produced by this
	 * step.
	 */
	private String getOutputDescription() {
		ArrayList<DatasetNode> datasetOutputs = new ArrayList<DatasetNode>();
		String outp = "";

		ArrayList<DatasetNode> datasetInputs = new ArrayList<DatasetNode>();

		// Separate Dataset inputs from parameter inputs
		for (WorkflowNode i : step.getIncomingLinks()) {
			if (i.isDataset()) {
				DatasetNode tmp = (DatasetNode) i;
				if (!tmp.isParameter()) {
					datasetInputs.add(tmp);
				}
			} else {
				System.out.println(
						"WARNING: found input to step (" + step.getFullName() + ") that is not a dataset: " + i);
				System.out.println("Ignoring warning");
			}
		}

		// Get a string list of every unique type of file output from this step ie:
		// {text,jpg,mp3}
		ArrayList<String> types = getUniqueTypes(datasetInputs);

		// Create a list of dataset objects from the output of this step
		for (WorkflowNode w : step.getOutgoingLinks()) {
			try {
				datasetOutputs.add((DatasetNode) w);
			} catch (Exception e) {
				System.out.print(
						"WARNING: exceptin when converting outgoing link " + w.getFullName() + " as dataset node");
			}
		}

		// Descriptions for each unique type of output
		if (step.getOutgoingLinks().size() == 1) {
			String currType = datasetOutputs.get(0).getType().toLowerCase();
			currType = currType.length() > 0 ? " " + currType : currType;
			String reference = "";
			if (datasetOutputs.get(0).getType() == null) {
				if (types.get(0).equalsIgnoreCase("null")) {
					reference = "another";
				} else {
					reference = "a";
				}
			} else if (types.size() == 1 && datasetOutputs.get(0).getType().equalsIgnoreCase(types.get(0))) {
				reference = "another";
			} else {
				reference = "a";
			}
			outp += reference + currType + " file[" + datasetOutputs.get(0).getDisplayName() + "]";
		} else {
			ArrayList<String> uniqueOutputTypes = getUniqueTypes(datasetOutputs);
			discussionPoints = uniqueOutputTypes.size();
			remainingDP = discussionPoints;

			for (String s : uniqueOutputTypes) {
				ArrayList<DatasetNode> currDatasets = getDatasetsWithType(s, datasetOutputs);

				if (currDatasets.size() == 1) {
					outp += "a" + s + " file[" + datasetInputs.get(0).getDisplayName() + "]";
					remainingDP--;

				} else if (currDatasets.size() > 1) {
					outp += Num2Word.convert(currDatasets.size()) + s + "files[";
					for (int i = 0; i < currDatasets.size(); i++) {
						outp += i == currDatasets.size() - 1 ? currDatasets.get(i) : currDatasets.get(i) + ",";
					}
					outp += "]";
					remainingDP--;
				}

				if (discussionPoints > 2 && remainingDP >= 1 && !s.equalsIgnoreCase(types.get(0))) {
					outp += ", ";
				} else if (remainingDP == 0 && discussionPoints > 1) {
					outp += "and ";
				} else {
					outp += " ";
				}
			}

		}

		return outp;
	}

	/**
	 * Return an noun to to reference the current step. Using different nouns makes
	 * the descriptions a little less robotic to read. This function returns a
	 * different noun reference every time it is called until it gets to the end of
	 * the stepReference list, then it repeats from the beginning.
	 * 
	 * @return noun
	 */
	private String getNextStepReference() {
		String tmp = stepReference[stepReferenceCount];
		stepReferenceCount = stepReferenceCount >= stepReference.length ? 0 : stepReferenceCount++;
		return tmp;
	}

	/**
	 * Reset the step reference counter. This will force the next step reference to
	 * be the steps name. This is helpful when references are made between complex
	 * sentences and the original step name needs to be reiterated.
	 */
	private void resetStepReference() {
		stepReferenceCount = 0;
	}

	/**
	 * Return a list of all the types within the given inputs arraylist
	 */
	private ArrayList<String> getUniqueTypes(ArrayList<DatasetNode> inputs) {
		ArrayList<String> out = new ArrayList<String>();

		for (DatasetNode d : inputs) {
			if (d.getType() != null && !out.contains(d.getType().toLowerCase())) {
				out.add(d.getType().toLowerCase());
			}

			if (d.getType() == null) {
				out.add("null");
			}
		}
		return out;
	}

	/**
	 * Return an array list with all the datasets from the given input list that
	 * contain the given type.
	 */
	private ArrayList<DatasetNode> getDatasetsWithType(String type, ArrayList<DatasetNode> inputs) {
		ArrayList<DatasetNode> out = new ArrayList<DatasetNode>();
		for (DatasetNode d : inputs) {
			if (d.getType() != null && d.getType().equalsIgnoreCase(type)) {
				out.add(d);
			}

			if (d.getType() == null) {
				if (type.equalsIgnoreCase("null")) {
					out.add(d);
				}
			}
		}
		return out;
	}
}

/**
 * Convert integer into the English word equivalent. This class works for all
 * numbers -10 billion < num < 10 billion. If there is a workflow with more than
 * 10 billion nodes, DANA is probably not going to work very well regardless of
 * how large a number it can convert into English.
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
			return "negative " + convert(-n);
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
