package com.dana;

public class DatasetNode extends WorkflowNode {

	
	private boolean isParameter = false;
	private String type;
	private String pathToData;
	private String description;
	private String doi;
	
	//-------|
	//SETTERS|
	//-------|
	
	public void setParameter(boolean isParameter) {
		this.isParameter = isParameter;
	}

	public void setType(String type) {
		this.type = type.equalsIgnoreCase("null") ? "" : type;
	}

	public void setPathToData(String pathToData) {
		this.pathToData = pathToData;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}
	
	//-------|
	//GETTERS|
	//-------|
	
	public String getType() {
		return type;
	}

	public String getPathToData() {
		return pathToData;
	}

	public String getDescription() {
		return description;
	}

	public String getDoi() {
		return doi;
	}
	

	//-----|
	//OTHER|
	//-----|

	public boolean isDataset() {
		return true;
	}
	
	public boolean isParameter() {
		return isParameter;
	}
	
	public void setIsParameter(boolean isParameter) {
		this.isParameter = isParameter;
	}
	
	public String toString() {
		String out = super.toString();
		if (description != null && description != "") {
			out += "\nDescription: " + description; 
		}
		if (doi != null && doi != "" && !doi.equalsIgnoreCase("null")) {
			out += "\ndoi: " + doi; 
		}
		if (type != null && type != "" && !type.equalsIgnoreCase("null")) {
			out += "\ntype: " + type; 
		}
		out += "\n";
		return out;
	}
}
