package gtfs.corev2;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

public class GTFSEdge extends DefaultWeightedEdge implements Serializable{

	private static final long serialVersionUID = 354054054054L;
	private Double weight;
	
	public GTFSEdge(Double weight) {
		this.weight = weight;
	}

	
	public double getWeight() {
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
	
	@Override
    public boolean equals(Object obj) {
       if (!(obj instanceof GTFSEdge))
            return false;
        if (obj == this)
            return true;

        GTFSEdge rhs = (GTFSEdge) obj;
        return new EqualsBuilder().
            append(this.toString(), rhs.toString()).
            isEquals();
    }
}
