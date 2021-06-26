package dana;

public class StepNode extends WorkflowNode{

	private String shortDescription = "";
	private String longDescription = "";
	private String gitHubDescription = "";
	private String gitHubUrl = "";
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


	public void setGitHubDescription(String gitHubDescription) {
		this.gitHubDescription = super.stripStringWrapper(gitHubDescription);
	}

	public void setCriticality(String criticality) {
		this.criticality = super.stripStringWrapper(criticality);
	}

	public void setStepType(String stepType) {
		this.stepType = super.stripStringWrapper(stepType);
	}

	public void setWebsite(String website) {
		this.website = super.stripStringWrapper(website);
	}

	public void setVersionNumber(String versionNumber) {
		this.versionNumber = super.stripStringWrapper(versionNumber);
	}

	public void setDependancies(String dependancies) {
		this.dependancies = super.stripStringWrapper(dependancies);
	}

	public void setDocumentationLink(String documentationLink) {
		this.documentationLink = super.stripStringWrapper(documentationLink);
	}

	public void setCommandLineInvocation(String commandLineInvocation) {
		this.commandLineInvocation = super.stripStringWrapper(commandLineInvocation);
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = super.stripStringWrapper(shortDescription);
	}
	
	public void setLongDescription(String longDescription) {
		this.longDescription = super.stripStringWrapper(longDescription);
	}
	
	public void setGitHubUrl(String gitHubUrl) {
		this.gitHubUrl = super.stripStringWrapper(gitHubUrl);
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
}
