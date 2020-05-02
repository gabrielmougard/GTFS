package com.algoadvcd.gtfs.core;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GTFSVertex implements Comparable<GTFSVertex>{
	String vertexId;
	List<String> data;
	
	public GTFSVertex() {
		vertexId = UUID.randomUUID().toString();
		data = new ArrayList<String>();
	}
	
	public GTFSVertex(List<String> data) {
		vertexId = UUID.randomUUID().toString();
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
	
}
