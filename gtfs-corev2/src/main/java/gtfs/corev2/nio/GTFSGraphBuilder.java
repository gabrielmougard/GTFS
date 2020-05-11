package gtfs.corev2.nio;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.Graph;

import gtfs.corev2.*;

public class GTFSGraphBuilder {
	
	private String pathToDataset;
	private Target target;
	private boolean fromJSON;
	private String jsonFilename;
	private static Logger logger = LogManager.getLogger();
	
	public GTFSGraphBuilder(String pathToDataset) {
		this.pathToDataset = pathToDataset;
		this.target = null;
		this.fromJSON = false;
		this.jsonFilename = "";
	}
	
	public GTFSGraphBuilder localDataset() {
		this.target = Target.LOCAL;
		return this;
	}
	
	public GTFSGraphBuilder remoteDataset() {
		this.target = Target.REMOTE;
		return this;
	}
	
	public GTFSGraphBuilder fromJSON(String jsonFilename) {
		this.fromJSON = true;
		this.jsonFilename = jsonFilename;
		return this;
	}
	
	
	public Graph<GTFSVertex, GTFSEdge> build() {
		
		if (this.target == null) {
			logger.info("The target dataset has not been set. We then chose 'local' by default.");
			this.target = Target.LOCAL;
		} 
		
		if (!this.fromJSON) {
			Map<String, List<List<String>>> tables = new GTFSLoader(this.pathToDataset, this.target).load();
			return new GTFSParser(tables).build();
		} else {
			return new GTFSParser(this.pathToDataset, this.jsonFilename).build();
		} 
		
	}
}
