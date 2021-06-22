package dana;

public class StepNode extends WorkflowNode{

	private String shortDescription = "";
	private String longDescription = "";
	private String gitHubDescription = "";
	private String criticality = "";
	private String stepType = "";
	private String website = "";
	private String versionNumber = "";
	private String dependancies = "";
	private String documentationLink = "";
	private String commandLineInvocation = "";
	
	
	//-------|
	//SETTERS|
	//-------|
	
	public String getGitHubDescription() {
		return gitHubDescription;
	}

	public String getCriticality() {
		return criticality;
	}

	public String getStepType() {
		return stepType;
	}

	public String getWebsite() {
		return website;
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

	public void setGitHubDescription(String gitHubDescription) {
		this.gitHubDescription = gitHubDescription;
	}

	public void setCriticality(String criticality) {
		this.criticality = criticality;
	}

	public void setStepType(String stepType) {
		this.stepType = stepType;
	}

	public void setWebsite(String website) {
		this.website = website;
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
	
	//-------|
	//GETTERS|
	//-------|
	
	public String getShortDescription() {
		return shortDescription;
	}

	public String getLongDescription() {
		return longDescription;
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
}
