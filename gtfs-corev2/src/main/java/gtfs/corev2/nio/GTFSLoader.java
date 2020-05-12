package gtfs.corev2.nio;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.api.services.storage.Storage.Channels;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BucketGetOption;
import com.google.cloud.storage.StorageOptions;
import com.google.common.primitives.Bytes;


public class GTFSLoader {

	private String pathToDataset;
	private Target target;
	private Map<String, List<List<String>>> gtfsTables;
	private String[] datasetFiles = new String[] {"routes.txt", "trips.txt", "stop_times.txt", "stops.txt", "transfers.txt"};
	public GTFSLoader(String pathToDataset, Target target) {
		this.pathToDataset = pathToDataset;
		this.target = target;
		this.gtfsTables = new HashMap<String, List<List<String>>>();
		
	}
	
	public Map<String, List<List<String>>> load() {
		if (this.target == Target.LOCAL) {
			
			ExecutorService executorService = Executors.newFixedThreadPool(this.datasetFiles.length);
			SynchronizedLocalLoader loader = new SynchronizedLocalLoader(this.gtfsTables, this.pathToDataset);
			for (String file : this.datasetFiles) {
				executorService.submit(() -> loader.load(file));
				System.out.println("ComputeLocal task created for : "+file);
			}
			executorService.shutdown();
	        try {
				executorService.awaitTermination(5, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        this.gtfsTables = loader.getTables();
	        
			return this.gtfsTables;
			
		} else {
			
			ExecutorService executorService = Executors.newFixedThreadPool(this.datasetFiles.length);
			SynchronizedRemoteLoader loader = new SynchronizedRemoteLoader(this.gtfsTables, this.pathToDataset);
			for (String file : this.datasetFiles) {
				executorService.submit(() -> loader.load(file));
				System.out.println("ComputeRemote task created for : "+file);
			}
			executorService.shutdown();
	        try {
				executorService.awaitTermination(200, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        this.gtfsTables = loader.getTables();
	        	        
			return this.gtfsTables;
			
		}
	}
	
	class SynchronizedLocalLoader {
		
		private Map<String, List<List<String>>> gtfsTables;
		private String pathToDataset;
		
		public SynchronizedLocalLoader(Map<String, List<List<String>>> gtfsTables, String pathToDataset) {
			this.gtfsTables = gtfsTables;
			this.pathToDataset = pathToDataset;
		}
		
		public synchronized void load(String file) {
			InputStream fileStream = 
					getClass()
					.getClassLoader()
					.getResourceAsStream(this.pathToDataset+"/"+file);
			
			System.out.println("Converting "+file+" ...");
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(fileStream));
				// skip the header of the csv
			    List<List<String>> inputList = br.lines().skip(1).map(mapToItem).collect(Collectors.toList());
			    br.close();
				this.gtfsTables.put(file, inputList);
				System.out.println("ComputeLocal task ended for : "+file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public Map<String, List<List<String>>> getTables() {
			return this.gtfsTables;
		}
		
		private Function<String, List<String>> mapToItem = (String line) -> {
			  String[] p = line.split(",");// a CSV has comma separated lines
			  List<String> item = new ArrayList<String>();
			  for (String elt : p) {
				  item.add(elt);
			  }
			  //more initialization goes here
			  return item;
		};
	}
	
	
	class SynchronizedRemoteLoader {
		
		private Map<String, List<List<String>>> gtfsTables;
		private String pathToDataset;
		private Storage storage;
		private String bucketName;
		
		public SynchronizedRemoteLoader(Map<String, List<List<String>>> gtfsTables, String pathToDataset) {
			this.gtfsTables = gtfsTables;
			this.pathToDataset = pathToDataset;
			
			try {
				Credentials credentials = GoogleCredentials.fromStream(
						getClass()
						.getClassLoader()
						.getResourceAsStream(BucketConfiguration.BUCKET_CREDENTIALS)
				);
				this.storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			this.bucketName = BucketConfiguration.BUCKET_NAME;
			
			Bucket bucket = this.storage.get(this.bucketName, BucketGetOption.fields(Storage.BucketField.values()));

		    // Print bucket metadata
		    System.out.println("BucketName: " + bucket.getName());
		    System.out.println("DefaultEventBasedHold: " + bucket.getDefaultEventBasedHold());
		    System.out.println("DefaultKmsKeyName: " + bucket.getDefaultKmsKeyName());
		    System.out.println("Id: " + bucket.getGeneratedId());
		    System.out.println("IndexPage: " + bucket.getIndexPage());
		    System.out.println("Location: " + bucket.getLocation());
		    System.out.println("LocationType: " + bucket.getLocationType());
		    System.out.println("Metageneration: " + bucket.getMetageneration());
		    System.out.println("NotFoundPage: " + bucket.getNotFoundPage());
		    System.out.println("RetentionEffectiveTime: " + bucket.getRetentionEffectiveTime());
		    System.out.println("RetentionPeriod: " + bucket.getRetentionPeriod());
		    System.out.println("RetentionPolicyIsLocked: " + bucket.retentionPolicyIsLocked());
		    System.out.println("RequesterPays: " + bucket.requesterPays());
		    System.out.println("SelfLink: " + bucket.getSelfLink());
		    System.out.println("StorageClass: " + bucket.getStorageClass().name());
		    System.out.println("TimeCreated: " + bucket.getCreateTime());
		    System.out.println("VersioningEnabled: " + bucket.versioningEnabled());
		    System.out.println("");
		}
		
		public synchronized void load(String file) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Blob blob = this.storage.get(BlobId.of(this.bucketName, this.pathToDataset+"/"+file));
			blob.downloadTo(out, Blob.BlobSourceOption.generationMatch());
	        InputStream decodedStream = new ByteArrayInputStream(out.toByteArray());
	        System.out.println("[REMOTE] Converting "+file+" ...");
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(decodedStream));
				// skip the header of the csv
			    List<List<String>> inputList = br.lines().skip(1).map(mapToItem).collect(Collectors.toList());
			    br.close();
				this.gtfsTables.put(file, inputList);
				System.out.println("ComputeRemote task ended for : "+file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public Map<String, List<List<String>>> getTables() {
			return this.gtfsTables;
		}
		
		private Function<String, List<String>> mapToItem = (String line) -> {
			  String[] p = line.split(",");// a CSV has comma separated lines
			  List<String> item = new ArrayList<String>();
			  for (String elt : p) {
				  item.add(elt);
			  }
			  //more initialization goes here
			  return item;
		};
	}
	
	private Function<String, List<String>> mapToItem = (String line) -> {
		  String[] p = line.split(",");// a CSV has comma separated lines
		  List<String> item = new ArrayList<String>();
		  for (String elt : p) {
			  item.add(elt);
		  }
		  //more initialization goes here
		  return item;
	};
	
	
}
