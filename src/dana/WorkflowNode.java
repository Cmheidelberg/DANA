package dana;

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
	private ArrayList<WorkflowNode> outgoingLinks;
	private ArrayList<WorkflowNode> incomingLinks;

	// Manually entered metadata
	private String author = "";
	private String license = "";
	private String url = "";
	private String citation = "";
	private ArrayList<Fragment> fragments;

	abstract boolean isDataset();

	abstract boolean isParameter();

	// -------|
	// SETTERS|
	// -------|

	public void setFragments(ArrayList<Fragment> fragments) {
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

	public ArrayList<Fragment> getFragments() {
		return fragments;
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

	public void addFragment(Fragment fragment) {
		if (fragments == null) {
			fragments = new ArrayList<Fragment>();
		}

		// Validate the given fragment doesnt already exist in the fragments array
		// before adding it. Only unique fragments should exist in the fragments array
		for (Fragment f : fragments) {
			if (f.getName().equalsIgnoreCase(fragment.getName()))
				return;
		}

		fragments.add(fragment);
	}

	/**
	 * Returns true if this node is within the given fragment
	 */
	public boolean hasFragment(Fragment fragment) {
		if (fragments == null)
			return false;

		for (Fragment s : fragments) {
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
			for (Fragment s : fragments) {
				outp += s.equals(fragments.get(fragments.size() - 1)) ? s.getName() : s.getName() + ",";
			}
		} else {
			outp += "null";
		}
		return outp + "\n";

	}
}
