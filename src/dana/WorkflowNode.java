package dana;

import java.util.ArrayList;

public abstract class WorkflowNode {
	
	//Automatically generated metadata 
	private String name;
	private String id;
	private ArrayList<WorkflowNode> outgoingLinks;
	private ArrayList<WorkflowNode> incomingLinks;
	
	//Manually entered metadata
	private String author = "";
	private String license = "";
	private String url = "";
	private String citation = "";
	
	abstract boolean isDataset();
	abstract boolean isParameter();
			
	//-------|
	//SETTERS|
	//-------|
	
	public void setId(String roleId) {
		this.id = roleId;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setOutgoingLinks(ArrayList<WorkflowNode> outgoingLinks) {
		this.outgoingLinks = outgoingLinks;
	}
	
	public void setIncomingLinks(ArrayList<WorkflowNode> incomingLinks) {
		this.incomingLinks = incomingLinks;
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
	
	public void setCitation(String citation) {
		this.citation = citation;
	}
	
	
	//-------|
	//GETTERS|
	//-------|
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<WorkflowNode> getOutgoingLinks() {
		return outgoingLinks;
	}
	
	public ArrayList<WorkflowNode> getIncomingLinks() {
		return incomingLinks;
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
	
	public String getCitation() {
		return citation;
	}
	
	//--------------|
	//MUTATORS/OTHER|
	//--------------|
	
	public void addOutgoingLink(WorkflowNode outgoingLink) {
		if(outgoingLinks == null) {
			outgoingLinks = new ArrayList<WorkflowNode>();
		}
		
		outgoingLinks.add(outgoingLink);
	}
	
	public void addIncomingLink(WorkflowNode incomingLink) {
		if(incomingLinks == null) {
			incomingLinks = new ArrayList<WorkflowNode>();
		}
		
		incomingLinks.add(incomingLink);
	}
	
	public String toString() {
		String outp = "Name: " + name + " |=| isDataset: " + isDataset() + " | isParameter: " + isParameter() +" \n";
		outp += "Id: " + id + "\n";
		
		outp += "Incoming Link Names: ";
		if(incomingLinks != null) {
			for(WorkflowNode a : incomingLinks) {
				outp += a.name + ",";
			}
		} else {
			outp += "null";
		}
		
		outp += "\nOutgoing Link Names: ";
		if(outgoingLinks != null) {
			for(WorkflowNode a : outgoingLinks) {
				outp += a.name + ",";
			}
			outp += "\n";
		} else {
			outp += "null\n";
		}
		
		return outp;
		
	}
}
