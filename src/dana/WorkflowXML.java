package dana;

import java.util.ArrayList;
import javax.xml.*; 
import org.w3c.dom.*;

import com.sun.jmx.mbeanserver.NamedObject;

public class WorkflowXML {

	Document workflow;
	ArrayList<WorkflowNode> workflows;
	int count = 0;
	
	public WorkflowXML(Document doc) {
		workflow = doc;
		workflows = new ArrayList<WorkflowNode>();
	}
	
	
	public void generateWorkflowNodesList() {
		
			
		addInputLinksToWorkflowList();
		addInputOutputLinksToWorkflowList();
		addOutputLinksToWorkflowList();
		updateLinksForStepNodes();
		
		System.out.println("Count: " + count);
		for(WorkflowNode n : workflows) {
			System.out.println("-------------");
			System.out.println(n);
			count++;
		}
		
	}
	
	public WorkflowNode getWorkflowNodeByName(String name) {
		for(WorkflowNode n : workflows) {
			if(name.toLowerCase().equals(n.getName().toLowerCase())) {
				return n;
			}
		}
		return null;
	}
	
	//Get the nodes that are connected to the output of the given dataset
	private ArrayList<WorkflowNode> findDatasetOutputNodes(DatasetNode dataset) {
		NodeList linksList = workflow.getElementsByTagName("j.0:hasLink");
		ArrayList<WorkflowNode> workflowNodes = new ArrayList<WorkflowNode>();
		
		String name = dataset.getName().toLowerCase().replace("#", "");
		for(int i = 0; i < linksList.getLength(); i++) {
			Node curr = linksList.item(i);
			Node dataVariable = recursiveGetChildNodeFromName(curr,"j.0:InputLink");
			
			count++;
			if(dataVariable != null) {
				Node hasVariable = recursiveGetChildNodeFromName(dataVariable, "j.0:hasVariable");
				if (hasVariable != null) {
					String rdf = getAttribute(hasVariable,"rdf:resource").toLowerCase().replace("#", "");
					if(rdf.equals(name)) {
						Node destNode = recursiveGetChildNodeFromName(dataVariable, "j.0:hasDestinationNode");
						String stepNodeName = getAttribute(destNode,"rdf:resource").replace("#","");
						
						//Find the StepNode object, or create a new one and add it to outgoing links
						if (destNode != null) {
							WorkflowNode sn = getWorkflowNodeByName(stepNodeName);
							if(sn == null) {
								sn = new StepNode();
								sn.setName(stepNodeName);
								workflows.add(sn);
							} 
							workflowNodes.add(sn);
						} else {
							System.out.println("cant fine j.0:hasDestinationNode for dataset (name): " + dataset.getName());
						}
					}
				}
			}
		}
		
		return workflowNodes;
	}

	private String getAttribute(Node n, String name) {
		count++;
		if (n != null) {
			return n.getAttributes().getNamedItem(name).getNodeValue();
		}
		return "";
	}
	
	
	private void addInputLinksToWorkflowList() {
		NodeList inputList = workflow.getElementsByTagName("j.0:hasInputRole");
		
		for(int i = 0; i < inputList.getLength(); i++) {
			count++;
			DatasetNode inp = new DatasetNode();
			Node curr = inputList.item(i);
			
			Node dataVariable = recursiveGetChildNodeFromName(curr,"j.0:datavariable");
			if (dataVariable == null) {
				dataVariable = recursiveGetChildNodeFromName(curr,"j.0:ParameterVariable");
				inp.setIsParameter(true);
			}
			
			Node id = recursiveGetChildNodeFromName(curr,"j.0:hasRoleID");
			if (id != null) {
				inp.setId(id.getTextContent());
			} else {
				inp.setId("[not found!]");
			}
			
			if (dataVariable != null) {
				inp.setName(getAttribute(dataVariable, "rdf:ID"));
				
			} else {
				inp.setName("[not found!]");
			}
			
			inp.setOutgoingLinks(findDatasetOutputNodes(inp));
			
			workflows.add(inp);
		}
	}
	
	private void addOutputLinksToWorkflowList() {
		NodeList outputList = workflow.getElementsByTagName("j.0:hasOutputRole");
		
		for(int i = 0; i < outputList.getLength(); i++) {
			count++;
			DatasetNode inp = new DatasetNode();
			Node curr = outputList.item(i);
			
			Node role = recursiveGetChildNodeFromName(curr,"j.0:Role");

			
			Node id = recursiveGetChildNodeFromName(role,"j.0:hasRoleID");
			if (id != null) {
				inp.setId(id.getTextContent());
				inp.setName(id.getTextContent());
			} else {
				inp.setId("[not found!]");
				inp.setName("[not found!]");
			}

			inp.setIncomingLinks(findInputsFromOutputNode(inp.getName()));
			
			workflows.add(inp);
		}
	}
	
	private ArrayList<WorkflowNode> findInputsFromOutputNode(String name) {
		NodeList outputList = workflow.getElementsByTagName("j.0:hasLink");
		ArrayList<WorkflowNode> outNodes = new ArrayList<WorkflowNode>();
		
		for(int i = 0; i < outputList.getLength(); i++) {
			Node curr = outputList.item(i);
			Node outputLink = recursiveGetChildNodeFromName(curr, "j.0:OutputLink");
			if(outputLink != null && recursiveGetChildNodeFromName(curr, "j.0:DataVariable") != null) {
				NodeList tmp = curr.getChildNodes();
				if(tmp == null) {
					return null;
				}
				for(int j = 0; j < tmp.getLength(); j++) {
					count++;
					String incomingName = getAttribute(recursiveGetChildNodeFromName(tmp.item(j), "j.0:Node"), "rdf:ID");
					if(incomingName != "") {
						WorkflowNode sn = getWorkflowNodeByName(incomingName);
						if(sn == null) {
							sn = new StepNode();
							sn.setName(incomingName);
							workflows.add(sn);
						}
						outNodes.add(sn);
					}
				}
			}
		}
		return outNodes;
	}
	
	private void addInputOutputLinksToWorkflowList() {
		NodeList linksList = workflow.getElementsByTagName("j.0:hasLink");
		
		for(int i = 0; i < linksList.getLength(); i++) {
			count++;
			DatasetNode dn = new DatasetNode();
			Node curr = linksList.item(i);
			Node ioLink = recursiveGetChildNodeFromName(curr, "j.0:InOutLink");
			if(ioLink != null) {
				System.out.println("======(i/o:" + i + ")======");
				Node variable = recursiveGetChildNodeFromName(ioLink, "j.0:hasVariable");
				if(variable != null) {
					dn.setName(getAttribute(recursiveGetChildNodeFromName(ioLink, "j.0:DataVariable"),"rdf:ID"));
				}
				
				// Find origin node
				Node originNode = recursiveGetChildNodeFromName(curr, "j.0:hasOriginNode");
				if (originNode != null) {
					Node node = recursiveGetChildNodeFromName(curr, "j.0:Node");
					String originName = getAttribute(node, "rdf:ID");
					WorkflowNode sn = getWorkflowNodeByName(originName);
					if(sn == null) {
						sn = new StepNode();
						sn.setName(originName);
						workflows.add(sn);
					}
					dn.addIncomingLink(sn);	
				} else {
					System.out.println("inOut link \"" + dn.getName() + "\" doesnt have origin node!?");
				}
				
				// Find destination node
				Node destinationNode = recursiveGetChildNodeFromName(curr, "j.0:hasDestinationNode");
				if (destinationNode != null) {
					String originName = getAttribute(destinationNode, "rdf:resource");
					if(originName != null) {
						originName = originName.replace("#","");
						WorkflowNode sn = getWorkflowNodeByName(originName);
						if(sn == null) {
							sn = new StepNode();
							sn.setName(originName);
							workflows.add(sn);
						}
						dn.addOutgoingLink(sn);	
					} else {
						System.out.println("inOut link \"" + dn.getName() + "\" has no attribute \"rdf:resource\" for destination Node!");
					}

				} else {
					System.out.println("inOut link \"" + dn.getName() + "\" has no destination	 node!?");
				}
				
				workflows.add(dn);
			}
		}
	}
	
	private void updateLinksForStepNodes() {
		for(WorkflowNode n : workflows) {
			if (!n.isDataset()) {
				for(WorkflowNode curr : workflows) {
					ArrayList<WorkflowNode> tmp = curr.getIncomingLinks();
					if (tmp != null) {
						for(WorkflowNode incomingLink : tmp) {
							count++;
							if(incomingLink.getName().equalsIgnoreCase(n.getName())) {	
								n.addOutgoingLink(curr);
							}
						}
					}
					
					tmp = curr.getOutgoingLinks();
					if(tmp != null) {
						for(WorkflowNode outgoingLink : tmp) {
							count++;
							if(outgoingLink.getName().equalsIgnoreCase(n.getName())) {
								n.addIncomingLink(curr);
							}
						}
					}
				}
			}
		}
	}
	
	public Node recursiveGetChildNodeFromName(Node parent, String name) {
		
		if (parent.getNodeName().toLowerCase().equals(name.toLowerCase())) {
			return parent;
		}
		
		NodeList nl = parent.getChildNodes();

		for(int i = 0; i < nl.getLength(); i++) {
			count++;
			Node rec = recursiveGetChildNodeFromName(nl.item(i), name);
			if (rec != null) {
				return rec;
			}
		}
		
		return null;
	}
	
	public void printWorkflowNodes() {
		NodeList nl = workflow.getElementsByTagName("j.0:WorkflowTemplate");
		System.out.println("nl length: " + nl.getLength());
		
		System.out.println("TREE: \n\n" + recursivePrintTree(nl.item(0),""));
		//System.out.println(nl.item(0).getNodeName());
	}
	
	public String recursivePrintTree(Node node, String spaces) {
		String outp = node.getNodeName().substring(4) + "\n";
		NodeList nl = node.getChildNodes();
		int totalValid = countValidNodes(nl);
		for(int i = 0; i < nl.getLength(); i++) {
			count++;
			if(!nl.item(i).getNodeName().toLowerCase().equals("#text")) {
				totalValid--;
				String gap = "";
				if(spaces.length() > 0) 
					gap = "   ";
				
				if (totalValid == 0) {
					outp += spaces + gap +"└";
					gap = " " + gap;
				} else {
					outp += spaces + gap +"├";
					if(spaces.length() > 0)
						gap = "   │";
					else
						gap = "│";
				}
				outp += "───" + recursivePrintTree(nl.item(i), spaces + gap);
			}
		}
		return outp;
	}
	
	public int countValidNodes(NodeList nodelist) {
		int out = 0;
		for(int i = 0; i < nodelist.getLength(); i++) {
			count++;
			if(!nodelist.item(i).getNodeName().toLowerCase().equals("#text")) {
				out++;
			}
		}
		return out;
	}
}
