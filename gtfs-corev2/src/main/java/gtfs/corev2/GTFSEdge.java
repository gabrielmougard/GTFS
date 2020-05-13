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
		return (GTFSVertex) super.getSource(); 
	}
	
	@Override
	public GTFSVertex getTarget() {
		return (GTFSVertex) super.getTarget(); 
	}
	
	@Override
	public String toString() {
		return "Source : "+getSource().toString()+"\nTarget : "+getTarget().toString()+"\nDistance (in km) : "+this.weight+"\n";
	}
}
