package com.algoadvcd.gtfs.core.nio;

import tech.tablesaw.api.Table;

public interface GTFSFeed {
	
	public Table get(String filename);
	public void set(String filename, Table dataframe);
	
}
