package dana;

import java.util.ArrayList;

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

	abstract boolean isDataset();

	abstract boolean isParameter();

	// -------|
	// SETTERS|
	// -------|

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

	public String toString() {
		String outp = "DisplayName: " + displayName + " |=| fullName: " + fullName + " |=| isDataset: " + isDataset() + " | isParameter: " + isParameter()
				+ " \n";
		outp += "Id: " + id + "\n";

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
			outp += "\n";
		} else {
			outp += "null\n";
		}

		return outp;

	}
}
