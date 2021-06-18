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
		this.type = type;
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
}
