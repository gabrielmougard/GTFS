package com.algoadvcd.gtfs.core;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GTFSVertex implements Comparable<GTFSVertex>{
	int vertexId;
	List<String> data;
	
	public GTFSVertex(int vertexId) {
		this.vertexId = vertexId;
		data = new ArrayList<String>();
	}
	
	public GTFSVertex(int vertexId, List<String> data) {
		this.vertexId = vertexId;
		this.data = data;
	}
	
	public void setVertex(List<String> newVertex) {
		data = newVertex;
	}
	
	public List<String> getVertex() {
		return data;
	}
	
	@Override
	public int compareTo(GTFSVertex v) {
		if (v.vertexId == this.vertexId) {
			return 0;
		}
		return 1;
	}
	
	@Override
	public String toString() {
		return "GTFSVertex : {\n\t vertexId : "+vertexId+"\n\t data : "+data+"\n}\n";
	}
	
	public boolean equals(GTFSVertex v) {
		if (v.vertexId == this.vertexId) {
			return true;
		}
		return false;
	}
	
}
