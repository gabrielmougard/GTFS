package gtfs.corev2.nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;


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
		
		public SynchronizedRemoteLoader(Map<String, List<List<String>>> gtfsTables, String pathToDataset) {
			this.gtfsTables = gtfsTables;
			this.pathToDataset = pathToDataset;
		}
		
		public synchronized void load(String file) {
			//TODO
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
