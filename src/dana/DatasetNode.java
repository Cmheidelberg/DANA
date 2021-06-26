package dana;

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
		this.type = super.stripStringWrapper(type);
	}

	public void setPathToData(String pathToData) {
		this.pathToData = super.stripStringWrapper(pathToData);
	}

	public void setDescription(String description) {
		this.description = super.stripStringWrapper(description);
	}

	public void setDoi(String doi) {
		this.doi = super.stripStringWrapper(doi);
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
		if (doi != null && doi != "") {
			out += "\ndoi: " + doi; 
		}
		out += "\n";
		return out;
	}
}
