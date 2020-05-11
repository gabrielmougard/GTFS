package gtfs.corev2;

import org.jgrapht.graph.DefaultEdge;

public class GTFSEdge extends DefaultEdge {

	private Double weight;
	
	public GTFSEdge(Double weight) {
		this.weight = weight;
	}

	
	public Double getWeight() {
		return weight;
	}
	
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	
	@Override
	public GTFSVertex getSource() {
		return getSource(); 
	}
	
	@Override
	public GTFSVertex getTarget() {
		return getTarget(); 
	}
}
