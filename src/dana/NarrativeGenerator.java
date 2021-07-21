package dana;

import java.util.ArrayList;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import com.sun.org.apache.bcel.internal.generic.NEW;

public class NarrativeGenerator {

	WorkflowJson workflow;

	public NarrativeGenerator(WorkflowJson workflow) {
		this.workflow = workflow;
	}

	public String getStepNarrative(StepNode step) {
		StepNarrative sn = new StepNarrative(workflow, step);
		return sn.getNarrative();
	}

	public String getDatasetNarrative(DatasetNode dataset) {
		return getNodeNarrative(dataset);
	}

	public String getNodeNarrative(WorkflowNode node) {

		if (node.isDataset()) {
			try {
				DatasetNode dn = (DatasetNode) node;
				return getDatasetNarrative(dn);

			} catch (ClassCastException cce) {
				System.out.println("Error: cannot cast \"" + node.getName() + "\" as a DatasetNode");
				cce.printStackTrace();
				return null;
			}
		} else {
			try {
				StepNode sn = (StepNode) node;
				return getStepNarrative(sn);
			} catch (ClassCastException cce) {
				System.out.println("Error: cannot cast \"" + node.getName() + "\" as a StepNode");
				cce.printStackTrace();
				return null;
			}
		}
	}

}

class StepNarrative {

	StepNode step;
	WorkflowJson workflow;
	int remainingDP;
	int discussionPoints;

	// Supporting fields
	String[] stepReference;
	int stepReferenceCount = 0;

	public StepNarrative(WorkflowJson workflow, StepNode step) {
		this.step = step;
		this.workflow = workflow;
		String[] tmp = { step.getName(), "it", "this step", "it" };
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
		outp += getNextStepReference() + " takes in " + getInputDescription() + getVerbLink() + " producing "
				+ getOutputDescription() + ".";

		if (step.getLongDescription().length() > 0 && !step.getLongDescription().equalsIgnoreCase("null")) {
			outp += " " + step.getName() + " " + step.getLongDescription() + ".";
		}

		int count = workflow.CountTimesUsedInWorkflow(step);
		String times = count > 1 ? "times" : "time";

		outp += " Overall, this step is used " + Num2Word.convert(count) + " " + times + " in the workflow.";

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

		// Separate Datset inputs from parameter inputs
		for (WorkflowNode i : inputs) {
			if (i.isDataset()) {
				DatasetNode tmp = (DatasetNode) i;
				if (tmp.isParameter()) {
					parameterInputs.add(tmp);
				} else {
					datasetInputs.add(tmp);
				}
			} else {
				System.out.println("WARNING: found input to step (" + step.getName() + ") that is not a dataset: " + i);
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

		// Generate description for eqch unique type of input
		for (String ctype : types) {
			ctype = ctype.toLowerCase();
			ArrayList<DatasetNode> currDatasets = getDatasetsWithType(ctype, datasetInputs);

			if (currDatasets.size() == 1) {
				outp += "a " + ctype + " file[" + datasetInputs.get(0).getName() + "]";
				remainingDP--;

			} else if (currDatasets.size() > 1) {
				outp += Num2Word.convert(currDatasets.size()) + " " + ctype + "files[";
				for (int i = 0; i < currDatasets.size(); i++) {
					outp += i == currDatasets.size() - 1 ? currDatasets.get(i).getName()
							: currDatasets.get(i).getName() + ",";
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
					outp += ", ";
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
				outp += "a parameter[" + parameterInputs.get(0).getName() + "],";
			} else if (parameterInputs.size() > 1) {
				outp += Num2Word.convert(parameterInputs.size()) + " parameters[";
				for (int i = 0; i < parameterInputs.size(); i++) {
					outp += i == parameterInputs.size() - 1 ? parameterInputs.get(i).getName()
							: parameterInputs.get(i).getName() + ",";
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
		if (discussionPoints > 2) {
			outp += " then " + step.getStepType() + " them";
		} else if (discussionPoints == 2) {
			outp += " " + step.getStepType() + " them,";
		} else {
			outp += step.getStepType() + " it,";
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
				System.out.println("WARNING: found input to step (" + step.getName() + ") that is not a dataset: " + i);
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
				System.out.print("WARNING: exceptin when converting outgoing link " + w.getName() + " as dataset node");
			}
		}

		// Descriptions for each unique type of output
		if (step.getOutgoingLinks().size() == 1) {
			String currType = datasetOutputs.get(0).getType().toLowerCase();
			String reference = "";
			if (datasetOutputs.get(0).getType() == null) {
				if (types.get(0).equalsIgnoreCase("null")) {
					reference = "another ";
				} else {
					reference = "a ";
				}
			} else if (types.size() == 1 && datasetOutputs.get(0).getType().equalsIgnoreCase(types.get(0))) {
				reference = "another ";
			} else {
				reference = "a ";
			}
			outp += reference + currType + " file[" + datasetOutputs.get(0).getName() + "]";
		} else {
			ArrayList<String> uniqueOutputTypes = getUniqueTypes(datasetOutputs);
			discussionPoints = uniqueOutputTypes.size();
			remainingDP = discussionPoints;

			for (String s : uniqueOutputTypes) {
				ArrayList<DatasetNode> currDatasets = getDatasetsWithType(s, datasetOutputs);

				if (currDatasets.size() == 1) {
					outp += "a " + s + " file[" + datasetInputs.get(0).getName() + "]";
					remainingDP--;

				} else if (currDatasets.size() > 1) {
					outp += Num2Word.convert(currDatasets.size()) + " " + s + "files[";
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
