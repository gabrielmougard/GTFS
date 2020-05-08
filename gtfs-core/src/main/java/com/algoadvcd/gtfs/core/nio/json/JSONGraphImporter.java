package com.algoadvcd.gtfs.core.nio.json;

import java.io.*;
import java.nio.ByteBuffer;

import com.algoadvcd.gtfs.core.Graph;
import com.algoadvcd.gtfs.core.nio.gtfs2feed.BucketConfiguration;
import com.algoadvcd.gtfs.core.nio.gtfs2feed.GraphImporter;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class JSONGraphImporter<GTFSVertex> implements GraphImporter<GTFSVertex> {
		
	/*
	 * the target is meant to be on the resources
	 */
	public void importGraphLocal(Graph<GTFSVertex> g, String target) {
		InputStream inputStream = 
				getClass()
				.getClassLoader()
				.getResourceAsStream(target);
		
		JsonObjectIterator iterator = new JsonObjectIterator(inputStream);
		while(iterator.hasNext()) {
			// TODO
			//add to the graph
		}
		
	}

	/*
	 * the remote access is google cloud bucket
	 */
	public void importGraphRemote(Graph<GTFSVertex> g, String target) {
		//bucket connection
		// TODO : set the environment variable 
		// ex : export GOOGLE_APPLICATION_CREDENTIALS="/home/user/Downloads/[FILE_NAME].json"
		
		Storage storage = StorageOptions.getDefaultInstance().getService();
		String bucketName = BucketConfiguration.BUCKET_NAME;
		Blob blob = storage.get(BlobId.of(bucketName, target));
		 
		ReadChannel reader = blob.reader();
		byte[] decoded = null;
        ByteBuffer bytes = ByteBuffer.allocate(64 * 1024);
        try {
        	while (reader.read(bytes) > 0) {
        		bytes.flip();
			    decoded = bytes.array();
			    bytes.clear();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        InputStream decodedStream = new ByteArrayInputStream(decoded);
        JsonObjectIterator iterator = new JsonObjectIterator(decodedStream);
		
        while(iterator.hasNext()) {
			// TODO
			//add to the graph
		}
		 
	}
	
}
