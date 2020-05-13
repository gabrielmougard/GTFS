package gtfs.corev2;

import java.io.Serializable;

import org.jgrapht.graph.DefaultEdge;

public class GTFSEdge extends DefaultEdge implements Serializable{

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
	
	@Override
	public String toString() {
		return "Source : "+super.getSource().toString()+"\nTarget : "+super.getTarget().toString()+"\nDistance (in km) : "+this.weight+"\n";
	}
}
