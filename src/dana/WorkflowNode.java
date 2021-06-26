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
	
	abstract boolean isDataset();
	abstract boolean isParameter();
			
	//-------|
	//SETTERS|
	//-------|
	
	public void setId(String roleId) {
		this.id = stripStringWrapper(roleId);
	}
	
	public void setName(String name) {
		this.name = stripStringWrapper(name);
	}
	
	public void setAuthor(String author) {
		this.author = stripStringWrapper(author);
	}
	
	public void setLicense(String license) {
		this.license = stripStringWrapper(license);
	}
	
	public void setUrl(String url) {
		this.url = stripStringWrapper(url);
	}
	
	public void setOutgoingLinks(ArrayList<WorkflowNode> outgoingLinks) {
		this.outgoingLinks = outgoingLinks;
	}
	
	public void setIncomingLinks(ArrayList<WorkflowNode> incomingLinks) {
		this.incomingLinks = incomingLinks;
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
	
	/**
	 * Strips the " symbols from the front and back of a string. For example if the
	 * input is: \"hello world\" then hello world is returned as the contents. This
	 * is used to remove the string symbols from the readData.json.
	 * 
	 * @param input string
	 * @return string without starting and ending quotationmarks
	 */
	protected String stripStringWrapper(String input) {
		char start = input.charAt(0);
		char end = input.charAt(input.length() - 1);
		int startIndex = 0;
		int endIndex = input.length();

		if (start == '\"' || start == '\'') {
			startIndex = +1;
		}

		if (end == '\"' || end == '\'') {
			endIndex -= 1;
		}

		return input.substring(startIndex, endIndex);
	}
}
