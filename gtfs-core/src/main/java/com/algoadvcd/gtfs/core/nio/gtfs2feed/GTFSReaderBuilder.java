package com.algoadvcd.gtfs.core.nio.gtfs2feed;

public class GTFSReaderBuilder {
	private String datasetname;
	private Target target;
	
	public GTFSReaderBuilder(String datasetname) {
		this.datasetname = datasetname;
	}
	
	public GTFSReaderBuilder localReader() {
		this.target = Target.LOCAL;
		return this;
	}
	
	public GTFSReaderBuilder remoteReader() {
		this.target = Target.REMOTE;
		return this;
		
	}
	
	public GTFSReader build() {
		return new GTFSReader(this);
	}
	
	public Target getTarget() {
		return this.target;
	}
	
	public String getDatasetName() {
		return this.datasetname;
	}
}
