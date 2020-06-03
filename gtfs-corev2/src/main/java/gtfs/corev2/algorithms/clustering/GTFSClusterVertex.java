package gtfs.corev2.algorithms.clustering;

import java.io.Serializable;
import java.util.Set;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import gtfs.corev2.GTFSVertex;

public class GTFSClusterVertex implements Serializable {
	
	private static final long serialVersionUID = 354875774844L;
	private int clusterId;
	private String name;
	private Double lat;
	private Double lon;
	private Set<GTFSVertex> elements;
	private Set<GTFSVertex> boundaries;
	
	public GTFSClusterVertex(int clusterId, String name, Double lat, Double lon, Set<GTFSVertex> elements) {
		this.clusterId = clusterId;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.elements = elements;
	}
	
	public int getClusterId() {
		return clusterId;
	}
	public void setClusterId(int clusterId) {
		this.clusterId = clusterId;
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
	public Set<GTFSVertex> getElements() {
		return elements;
	}
	public void setElements(Set<GTFSVertex> elements) {
		this.elements = elements;
	}
	public Set<GTFSVertex> getBoundaries() {
		return boundaries;
	}
	public void setBoundaries(Set<GTFSVertex> boundaries) {
		this.boundaries = boundaries;
	}
	
	@Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
            append(clusterId).
            toHashCode();
    }
	
	@Override
	public String toString() {
		return name+"\nNb. of elements : "+this.elements.size()+"\nlat : "+this.lat+"\nlon : "+this.lon+"\n";
	}
	
}
