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

	// Supporting fields
	String[] stepReference = { step.getName(), "it", "this step", "it" };
	int stepReferenceCount = 0;

	public StepNarrative(StepNode step) {
		this.step = step;
	}

	public String getNarrative() {
		String outp = "";
		outp += getMainDescription();
	}

	public String getMainDescription() {
		return getNextStepReference() + " takes in " + getInputDescriptions() + getVerbLink() + getOutputDescription;
	}

	/**
	 * Generates a description for the inputs that come into this step. This is part
	 * of the main description.
	 * 
	 * For example, if the main description is "CaesarNode takes in a text
	 * file[DocumentWithLineBreaks] and a parameter[ShiftKey] it then encrypts them
	 * producing another text file[EncryptedDocument]" 
	 * 
	 * Then the input description is: "a text file[DocumentWithLineBreaks] and a 
	 * parameter[ShiftKey]"
	 * 
	 */
	private String getInputDescriptions() {
		ArrayList<WorkflowNode> inputs = step.getIncomingLinks();
		ArrayList<DatasetNode> datasetInputs = new ArrayList<DatasetNode>();
		ArrayList<DatasetNode> parameterInputs = new ArrayList <DatasetNode>();
		
		//Separate Datset inputs from parameter inputs
		for(WorkflowNode i:inputs) {
			if (i.isDataset()) {
				DatasetNode tmp = (DatasetNode)i;
				if(tmp.isParameter()) {
					parameterInputs.add(tmp);
				}else {
					datasetInputs.add(tmp);
				}
			}else {
				System.out.println("WARNING: found input to step (" + step.getName() + ") that is not a dataset: " + i);
				System.out.println("Ignoring warning");
			}
		}
		
		String outp = "";
		
		//TODO: for each type of input add a description
		//TODO: description for parameter
		//TODO: track complexity
//		if(datasetInputs.size() == 1) {
//			outp += "a " + step.getStepType() + " file[" + datasetInputs.get(0).getName() + "] ";
//			
//		} else if (datasetInputs.size() > 1) {
//			
//		}
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
	private ArrayList<String> getInputTypes(ArrayList<DatasetNode> inputs) {
		ArrayList<String> out = new ArrayList<String>();
		for (DatasetNode d : inputs) {
			if(!out.contains(d.getType().toLowerCase())) {
				out.add(d.getType().toLowerCase());
			}
		}
		return out;
	}
	
	/**
	 * Return an array list with all the datasets from the given input list that contain the given type. 
	 */
	private ArrayList<DatasetNode> getDatasetsWithType(String type, ArrayList<DatasetNode> inputs) {
		
		ArrayList<DatasetNode> out = new ArrayList<DatasetNode>();
		for (DatasetNode d : inputs) {
			if(d.getType().equalsIgnoreCase(type)) {
				out.add(d);
			}
		}
		return out;
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
