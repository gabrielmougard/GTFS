package gtfs.corev2.nio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jgrapht.Graph;

import org.jgrapht.nio.*;
import org.jgrapht.nio.json.JSONExporter;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.Storage.BucketGetOption;

import gtfs.corev2.GTFSEdge;
import gtfs.corev2.GTFSVertex;
import gtfs.corev2.nio.GTFSLoader.SynchronizedLocalLoader;

public class GraphSerializerBuilder {
	private String pathToDataset;
	private Target target;
	
	public GraphSerializerBuilder(String pathToDataset) {
		this.pathToDataset = pathToDataset;
		this.target = Target.LOCAL;
	}
	
	public GraphSerializerBuilder remoteSerializer() {
		this.target = Target.REMOTE;
		return this;
	}
	
	public GraphSerializerBuilder localSerializer() {
		this.target = Target.LOCAL;
		return this;
	}
	
	public GraphSerializerBuilder remoteAndLocalSerializer() {
		this.target = Target.LOCAL_AND_REMOTE;
		return this;
	}
	
	public GraphSerializer build() {
		return new GraphSerializer(this.pathToDataset, this.target);
	}
	
	public class GraphSerializer {
		private String pathToDataset;
		private Target target;
		
		private GraphSerializer(String pathToDataset, Target target) {
			this.pathToDataset = pathToDataset;
			this.target = target;
		}
		
		public void serialize(Graph<GTFSVertex, GTFSEdge> g) {
			if (this.target == Target.LOCAL) {
				SynchronizedSerializer serializer = new SynchronizedSerializer(g, this.pathToDataset);
				serializer.serializeLocal();
			} else if (this.target == Target.REMOTE) {
				SynchronizedSerializer serializer = new SynchronizedSerializer(g, this.pathToDataset);
				serializer.serializeRemote();
			} else {
				ExecutorService executorService = Executors.newFixedThreadPool(2);
				SynchronizedSerializer serializer = new SynchronizedSerializer(g, this.pathToDataset);
				
				executorService.submit(() -> serializer.serializeLocal());
				executorService.submit(() -> serializer.serializeRemote());
				
				executorService.shutdown();
		        try {
					executorService.awaitTermination(20, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		/*
		public Graph<GTFSVertex, GTFSEdge> unserialize() {
			if (this.target == Target.LOCAL) {
				SynchronizedSerializer serializer = new SynchronizedSerializer(this.pathToDataset);
				serializer.unserializeLocal();
			} else if (this.target == Target.REMOTE) {
				SynchronizedSerializer serializer = new SynchronizedSerializer(this.pathToDataset);
				serializer.unserializeRemote();
			} else {
				SynchronizedSerializer serializer = new SynchronizedSerializer(this.pathToDataset);
				serializer.unserializeRemote();
			}
		}
		*/
	}
	
	
	class SynchronizedSerializer {
		private String pathToDataset;
		private Graph<GTFSVertex, GTFSEdge> g;
		
		public SynchronizedSerializer(Graph<GTFSVertex, GTFSEdge> g, String pathToDataset) {
			this.g = g;
			this.pathToDataset = pathToDataset;
			
		}
		
		public SynchronizedSerializer(String pathToDataset) {
			this.pathToDataset = pathToDataset;
		}
		
		public synchronized void serializeLocal() {
			
			
			URL u = this.getClass().getResource(this.pathToDataset+"/"+this.pathToDataset+"_graph.json");
			GraphExporter<GTFSVertex, GTFSEdge> ge = new JSONExporter<GTFSVertex, GTFSEdge>();
			
			if (u == null) {
				try {
					Path source = Paths.get(this.getClass().getResource("/"+this.pathToDataset).getPath());
			        Path newFile = Paths.get(source.toAbsolutePath() +"/"+this.pathToDataset+"_graph.json");
			        Files.createFile(newFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
			
			File file = new File(
				getClass()
				.getClassLoader()
				.getResource(this.pathToDataset+"/"+this.pathToDataset+"_graph.json")
				.getFile()
			);
				
			ge.exportGraph(g, file);
				
		}
		
		public synchronized void serializeRemote() {
			
			GraphExporter<GTFSVertex, GTFSEdge> ge = new JSONExporter<GTFSVertex, GTFSEdge>();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ge.exportGraph(g, out);
			InputStream decodedSerializedGraph = new ByteArrayInputStream(out.toByteArray());
			//setup GCP connection
			try {
				Credentials credentials = GoogleCredentials.fromStream(
						getClass()
						.getClassLoader()
						.getResourceAsStream(BucketConfiguration.BUCKET_CREDENTIALS)
				);
				Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
				String bucketName = BucketConfiguration.BUCKET_NAME;
				
				// setup the BLOB and send it
				BlobId blobId = BlobId.of(bucketName, this.pathToDataset+"/"+this.pathToDataset+"_graph.json");
			    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("application/json").build();
			    storage.create(blobInfo, decodedSerializedGraph);
				//		
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
		}
		
		/*
		public synchronized Graph<GTFSVertex, GTFSEdge> unserializeLocal() {
			
		}
		
		public synchronized Graph<GTFSVertex, GTFSEdge> unserializeRemote() {
			
		}
		*/
		
	}
	
}
