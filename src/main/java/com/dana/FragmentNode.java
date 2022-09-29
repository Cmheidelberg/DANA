package main.java.com.dana;

import java.util.ArrayList;

public class FragmentNode extends WorkflowNode {

	private String description = "";
	private int criticality = 0;
	private String fragmentName = "";	
	private ArrayList<WorkflowNode> nodes = new ArrayList<WorkflowNode>();
	
	public FragmentNode(String name,String description,int criticality) {
		this.fragmentName = name;
		this.description = description;
		this.criticality = criticality;
	}
	//-------|
	//SETTERS|
	//-------|
	

	public void setCriticality(String criticality) {
		this.criticality = Integer.parseInt(criticality);
	}
	
	public void setCriticality(int criticality) {
		this.criticality = criticality;
	}
	
	public void setDescription(String longDescription) {
		this.description = longDescription;
	}
	
	public void setName(String fragmentName) {
		this.fragmentName = fragmentName;
	}
	
	//-------|
	//GETTERS|
	//-------|

	public int getCriticality() {
		return criticality;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getName() {
		return fragmentName;
	}
	
	public ArrayList<WorkflowNode> getNodes() {
		return nodes;
	}
	
	//-----|
	//OTHER|
	//-----|
	
	public void addNode(WorkflowNode node) {
		nodes.add(node);
		node.addFragment(this);
	}
	
	public boolean hasNode(String fullName) {
		for(WorkflowNode wn : nodes) {
			if (fullName.equals(wn.getFullName())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasNode(WorkflowNode node) {
		return nodes.contains(node);	
	}
	
	public boolean isDataset() {
		return false;
	}
	
	public boolean isParameter() {
		return false;
	}
	
	public boolean isFragment() {
		return true;
	}
	
	public String toString() {
		String outp = "Name: " + fragmentName + " |=| criticality: " + criticality;
		
		outp += "\nNodes:";
		for (WorkflowNode wn : getNodes()) {
			outp += wn.getFullName() + ",";
		}
		
		outp += "\nIncoming Links: ";
		for (WorkflowNode wn : getIncomingLinks()) {
			outp += wn.getFullName() + ",";
		}
		outp += "\nOutgoing Links: ";
		for (WorkflowNode wn : getOutgoingLinks()) {
			outp += wn.getFullName() + ",";
		}
		outp += "\n";
		return outp;
	}

}
