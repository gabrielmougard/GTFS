package com.algoadvcd.gtfs.core.nio.config;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class GTFSFeedConfigVertex {
	
	private String filename;
	private Optional<Map<String, VertexConverterOptions>> converters;
	private Set<String> requiredColumns;
	
	public GTFSFeedConfigVertex(String filename, 
			Optional<Map<String, VertexConverterOptions>> converters, 
			Set<String> requiredColumns) {
		
		this.filename = filename;
		this.converters = converters;
		this.requiredColumns = requiredColumns;
		
	}
	
	public String getFilename() {
		return this.filename;
	}
	
	public Optional<Map<String, VertexConverterOptions>> getConverters() {
		if (this.converters.isPresent()) {
			return this.converters;
		} else {
			Optional<Map<String, VertexConverterOptions>> empty = Optional.empty();
			return empty;
		}
	}

	public Set<String> getRequiredColumns() {
		return this.requiredColumns;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public void getConverters(Optional<Map<String, VertexConverterOptions>> converters) {
		this.converters = converters;
	}

	public void setRequiredColumns(Set<String> requiredColumns) {
		this.requiredColumns = requiredColumns;
	}
}
