package com.algoadvcd.gtfs.core.nio.gtfs2feed;

import tech.tablesaw.api.Table;

public interface GTFSFeed {
	
	public Table get(String tablename);
	
	public void fetch();
	
}
