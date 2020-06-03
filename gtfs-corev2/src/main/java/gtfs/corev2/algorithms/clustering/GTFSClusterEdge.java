package gtfs.corev2.algorithms.clustering;

import java.io.Serializable;

import org.jgrapht.graph.DefaultWeightedEdge;

public class GTFSClusterEdge extends DefaultWeightedEdge implements Serializable {
	
	private static final long serialVersionUID = 35407675625654L;
	private Double weight;

	
	public GTFSClusterEdge(Double weight) {
		this.weight = weight;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	
	@Override
	public GTFSClusterVertex getSource() {
		return (GTFSClusterVertex) super.getSource(); 
	}
	
	@Override
	public GTFSClusterVertex getTarget() {
		return (GTFSClusterVertex) super.getTarget(); 
	}
	
	@Override
	public String toString() {
		return "Source : "+getSource().toString()+"\nTarget : "+getTarget().toString()+"\nDistance (in km) : "+this.weight+"\n";
	}

}
