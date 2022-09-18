package dana;

import java.util.ArrayList;

public class Fragment {
	private String name;
	private String description;
	private ArrayList<WorkflowNode> nodes = new ArrayList<WorkflowNode>();
	
	public Fragment(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	public void addNode(WorkflowNode node) {
		nodes.add(node);
		node.addFragment(this);
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public ArrayList<WorkflowNode> getNodes() {
		return nodes;
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
}
