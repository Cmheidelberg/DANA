package dana;

public class StepNode extends WorkflowNode{

	private String shortDescription = "";
	private String longDescription = "";
	private String gitHubDescription = "";
	private String gitHubUrl = "";
	private int criticality = 0;
	private String stepType = "";
	private String versionNumber = "";
	private String dependancies = "";
	private String documentationLink = "";
	private String commandLineInvocation = "";
	
	
	//-------|
	//SETTERS|
	//-------|
	
	public void setGitHubDescription(String gitHubDescription) {
		this.gitHubDescription = gitHubDescription;
	}

	public void setCriticality(String criticality) {
		this.criticality = Integer.parseInt(criticality);
	}
	
	public void setCriticality(int criticality) {
		this.criticality = criticality;
	}

	public void setStepType(String stepType) {
		this.stepType = stepType;
	}

	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	public void setDependancies(String dependancies) {
		this.dependancies = dependancies;
	}

	public void setDocumentationLink(String documentationLink) {
		this.documentationLink = documentationLink;
	}

	public void setCommandLineInvocation(String commandLineInvocation) {
		this.commandLineInvocation = commandLineInvocation;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	
	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}
	
	public void setGitHubUrl(String gitHubUrl) {
		this.gitHubUrl = gitHubUrl;
	}
	
	//-------|
	//GETTERS|
	//-------|
	
	public String getShortDescription() {
		return shortDescription;
	}
	
	public String getLongDescription() {
		if (longDescription.length() == 0) {
			return shortDescription;
		}else {
			return longDescription;
		}
	}
	
	public String getGitHubDescription() {
		return gitHubDescription;
	}

	public int getCriticality() {
		return criticality;
	}

	public String getStepType() {
		return stepType;
	}

	public String getVersionNumber() {
		return versionNumber;
	}

	public String getDependancies() {
		return dependancies;
	}

	public String getDocumentationLink() {
		return documentationLink;
	}

	public String getCommandLineInvocation() {
		return commandLineInvocation;
	}
	
	public String getGitHubUrl() {
		return gitHubUrl;
	}
	
	//-----|
	//OTHER|
	//-----|
	
	public boolean isDataset() {
		return false;
	}
	
	public boolean isParameter() {
		return false;
	}
	
	public Boolean hasLongDescription() {
		return longDescription.length() > 0;
	}
}
