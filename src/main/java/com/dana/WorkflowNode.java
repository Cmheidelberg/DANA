package main.java.com.dana;

import java.util.ArrayList;

/**
 * WorkflowNode is an abstract class representation of each node on the
 * workflow. This class has two children classes that inherit its properties:
 * StepNode and DatasetNode. Step node represents a node on the workflow that
 * contains executable code. A dataset node represents either a dataset
 * (input/output file) or a parameter.
 */
public abstract class WorkflowNode {

	// Automatically generated metadata
	private String fullName = "";
	private String displayName = "";
	private String id;
	private int criticality = 0;
	private ArrayList<WorkflowNode> outgoingLinks = new ArrayList<WorkflowNode>();
	private ArrayList<WorkflowNode> incomingLinks = new ArrayList<WorkflowNode>();

	// Manually entered metadata
	private String author = "";
	private String license = "";
	private String url = "";
	private String citation = "";
	private ArrayList<FragmentNode> fragments;

	abstract boolean isDataset();

	abstract boolean isParameter();

	abstract boolean isFragment();

	// -------|
	// SETTERS|
	// -------|

	public void setCriticality(String criticality) {
		this.criticality = Integer.parseInt(criticality);
	}

	public void setFragments(ArrayList<FragmentNode> fragments) {
		this.fragments = fragments;
	}

	public void setFullName(String name) {
		this.fullName = name;
	}

	public void setCitation(String citation) {
		this.citation = citation;
	}

	public void setId(String roleId) {
		this.id = roleId;
	}

	public void setDisplayName(String name) {
		this.displayName = name;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setOutgoingLinks(ArrayList<WorkflowNode> outgoingLinks) {
		this.outgoingLinks = outgoingLinks;
	}

	public void setIncomingLinks(ArrayList<WorkflowNode> incomingLinks) {
		this.incomingLinks = incomingLinks;
	}

	// -------|
	// GETTERS|
	// -------|

	public ArrayList<FragmentNode> getFragments() {
		return fragments;
	}

	public int getCriticality() {
		return this.criticality;
	}

	public String getFullName() {
		if (fullName.length() > 0) {
			return fullName;
		} else {
			return displayName;
		}
	}

	public String getCitation() {
		return citation;
	}

	public String getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getAuthor() {
		return author;
	}

	public String getLicense() {
		return license;
	}

	public String getUrl() {
		return url;
	}

	public ArrayList<WorkflowNode> getOutgoingLinks() {
		return outgoingLinks;
	}

	public ArrayList<WorkflowNode> getIncomingLinks() {
		return incomingLinks;
	}

	// --------------|
	// MUTATORS/OTHER|
	// --------------|

	public void addFragment(FragmentNode fragment) {
		if (fragments == null) {
			fragments = new ArrayList<FragmentNode>();
		}

		// Validate the given fragment doesnt already exist in the fragments array
		// before adding it. Only unique fragments should exist in the fragments array
		for (FragmentNode f : fragments) {
			if (f.getName().equalsIgnoreCase(fragment.getName()))
				return;
		}

		fragments.add(fragment);
	}

	/**
	 * Returns true if this node is within the given fragment
	 */
	public boolean hasFragment(FragmentNode fragment) {
		if (fragments == null)
			return false;

		for (FragmentNode s : fragments) {
			if (fragment.equals(s)) {
				return true;
			}
		}

		return false;
	}

	public void addOutgoingLink(WorkflowNode outgoingLink) {
		if (outgoingLinks == null) {
			outgoingLinks = new ArrayList<WorkflowNode>();
		}

		outgoingLinks.add(outgoingLink);
	}

	public void addIncomingLink(WorkflowNode incomingLink) {
		if (incomingLinks == null) {
			incomingLinks = new ArrayList<WorkflowNode>();
		}

		incomingLinks.add(incomingLink);
	}

	/**
	 * Function returning whether or not the node has incoming links
	 * 
	 * @return true/false if the given node has any inputs or not
	 */
	public boolean hasInputs() {
		return getIncomingLinks() == null || getIncomingLinks().size() == 0;
	}

	/**
	 * Function returning whether or not the node has outgoing links
	 * 
	 * @return true/false if the given node has any outputs or not
	 */
	public boolean hasOutputs() {
		return getOutgoingLinks() == null || getOutgoingLinks().size() == 0;
	}

	// toString provides general debug information
	public String toString() {
		String outp = "DisplayName: " + displayName + " |=| fullName: " + fullName + " |=| isDataset: " + isDataset()
				+ " | isParameter: " + isParameter() + " \n";

		outp += "Incoming Link Names: ";
		if (incomingLinks != null) {
			for (WorkflowNode a : incomingLinks) {
				outp += a.getFullName() + ",";
			}
		} else {
			outp += "null";
		}

		outp += "\nOutgoing Link Names: ";
		if (outgoingLinks != null) {
			for (WorkflowNode a : outgoingLinks) {
				outp += a.getFullName() + ",";
			}
		} else {
			outp += "null";
		}

		outp += "\nFragments: ";
		if (fragments != null) {
			for (FragmentNode s : fragments) {
				outp += s.equals(fragments.get(fragments.size() - 1)) ? s.getName() : s.getName() + ",";
			}
		} else {
			outp += "null";
		}
		return outp + "\n";

	}
}
