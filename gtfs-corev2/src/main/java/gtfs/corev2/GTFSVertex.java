package gtfs.corev2;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class GTFSVertex implements Serializable {
	private static final long serialVersionUID = 354999994054L;
	private String vertexId;
	private String name;
	private Double lat;
	private Double lon;
	
	public GTFSVertex() {
		this.setVertexId("");
		this.setName("");
		this.setLat(0.0);
		this.setLon(0.0);
	}
	
	public GTFSVertex(String vertexId, String name, String lat, String lon) {
		this.setVertexId(vertexId);
		this.setName(name);
		this.setLat(Double.parseDouble(lat));
		this.setLon(Double.parseDouble(lon));
	}

	public String getVertexId() {
		return vertexId;
	}

	public void setVertexId(String vertexId) {
		this.vertexId = vertexId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}
	
	@Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
            append(vertexId).
            toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
       if (!(obj instanceof GTFSVertex))
            return false;
        if (obj == this)
            return true;

        GTFSVertex rhs = (GTFSVertex) obj;
        return new EqualsBuilder().
            append(vertexId, rhs.getVertexId()).
            isEquals();
    }
    
    @Override
    public String toString() {
    	return "{ VertexId : "+vertexId+"\n"+"Name : "+name+"\n"+"lat : "+lat+"\n"+"lon : "+lon+"\n}";
    }
}
