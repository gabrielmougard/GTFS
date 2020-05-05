package com.algoadvcd.gtfs.core.nio;

import tech.tablesaw.api.Table;

public class GTFSFeedLoader {
	private String datasetname;
	private Target target;
	
	private GTFSFeedLoader(GTFSFeedLoaderBuilder builder) {
		this.datasetname = builder.datasetname;
		this.target = builder.target;
	}
	
	public GTFSFeed getFeed() {
		if (target == Target.LOCAL) {
			GTFSFeed feed = new LocalGTFSFeed(datasetname);
			return feed;
		} else {
			GTFSFeed feed = new RemoteGTFSFeed(datasetname);
		}
	}
	public static class GTFSFeedLoaderBuilder {
		private String datasetname;
		private Target target;
		
		public GTFSFeedLoaderBuilder(String datasetname) {
			this.datasetname = datasetname;
		}
		
		public GTFSFeedLoaderBuilder localDataset() {
			this.target = Target.LOCAL;
			return this;
		}
		
		public GTFSFeedLoaderBuilder remoteDataset() {
			this.target = Target.REMOTE;
			return this;
			
		}
		
		public GTFSFeedLoader build() {
			return new GTFSFeedLoader(this);
		}
	}
}
