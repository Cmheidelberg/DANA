package dana;

public class DatasetNode extends WorkflowNode {

	
	private boolean isParameter = false;
	
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
