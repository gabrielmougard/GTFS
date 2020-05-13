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
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.HashMap;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.io.ComponentAttributeProvider;
import org.jgrapht.nio.*;
import org.jgrapht.nio.json.JSONExporter;
import org.jgrapht.nio.json.JSONImporter;
import org.jgrapht.util.*;

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
			JSONExporter<GTFSVertex, GTFSEdge> ge = new JSONExporter<GTFSVertex, GTFSEdge>();
			Function<GTFSVertex, Map<String, Attribute>> vertexAttibuteExporterFn = 
				(GTFSVertex v) -> {
					Map<String, Attribute> map = new HashMap<String, Attribute>();
					Attribute vertexId = new DefaultAttribute(v.getVertexId(), AttributeType.STRING);
					Attribute name = new DefaultAttribute(v.getName(), AttributeType.STRING);
					Attribute lat = new DefaultAttribute(v.getLat(), AttributeType.DOUBLE);
					Attribute lon = new DefaultAttribute(v.getLon(), AttributeType.DOUBLE);
					map.put("vertexId", vertexId);
					map.put("name", name);
					map.put("lat", lat);
					map.put("lon", lon);
					return map;
			};
			Function<GTFSEdge, Map<String, Attribute>> edgeAttributeExporterFn =
				(GTFSEdge e) -> {
					Map<String, Attribute> map = new HashMap<String, Attribute>();
					Attribute weight = new DefaultAttribute(e.getWeight(), AttributeType.DOUBLE);
					if (!e.getSource().getName().equals(e.getTarget().getName())) {
						map.put("distance",weight);
					} else {
						if (e.getSource().getLat().equals(e.getTarget().getLat()) && e.getSource().getLon().equals(e.getTarget().getLon())) {
							Attribute zeroWeight = new DefaultAttribute(0.0, AttributeType.DOUBLE);
							map.put("distance",zeroWeight);
						} else {
							map.put("distance",weight);
						}
					}
						
					
					return map;
			};
			
			ge.setEdgeAttributeProvider(edgeAttributeExporterFn);
			ge.setVertexAttributeProvider(vertexAttibuteExporterFn);
			
			
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
			
			JSONExporter<GTFSVertex, GTFSEdge> ge = new JSONExporter<GTFSVertex, GTFSEdge>();
			Function<GTFSVertex, Map<String, Attribute>> vertexAttibuteExporterFn = 
					(GTFSVertex v) -> {
						Map<String, Attribute> map = new HashMap<String, Attribute>();
						Attribute vertexId = new DefaultAttribute(v.getVertexId(), AttributeType.STRING);
						Attribute name = new DefaultAttribute(v.getName(), AttributeType.STRING);
						Attribute lat = new DefaultAttribute(v.getLat(), AttributeType.DOUBLE);
						Attribute lon = new DefaultAttribute(v.getLon(), AttributeType.DOUBLE);
						map.put("vertexId", vertexId);
						map.put("name", name);
						map.put("lat", lat);
						map.put("lon", lon);
						return map;
				};
				Function<GTFSEdge, Map<String, Attribute>> edgeAttributeExporterFn =
					(GTFSEdge e) -> {
						Map<String, Attribute> map = new HashMap<String, Attribute>();
						Attribute weight = new DefaultAttribute(e.getWeight(), AttributeType.DOUBLE);
						if (!e.getSource().getName().equals(e.getTarget().getName())) {
							map.put("distance",weight);
						} else {
							if (e.getSource().getLat().equals(e.getTarget().getLat()) && e.getSource().getLon().equals(e.getTarget().getLon())) {
								Attribute zeroWeight = new DefaultAttribute(0.0, AttributeType.DOUBLE);
								map.put("distance",zeroWeight);
							} else {
								map.put("distance",weight);
							}
						}
							
						
						return map;
				};
				
				ge.setEdgeAttributeProvider(edgeAttributeExporterFn);
				ge.setVertexAttributeProvider(vertexAttibuteExporterFn);
				
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
		
		
		public synchronized Graph<GTFSVertex, GTFSEdge> unserializeLocal() {
			JSONImporter<GTFSVertex, GTFSEdge> ji = new JSONImporter<GTFSVertex, GTFSEdge>();
			Graph<GTFSVertex, GTFSEdge> g = new DirectedMultigraph(GTFSEdge.class);
			
			//setup the Vertex and Edges BiConsummer
			ji.addVertexAttributeConsumer((p, attrValue) -> {
				GTFSVertex v = p.getFirst();
	            String attrName = p.getSecond();
	            
	            if (attrName.equals("vertexId")) {
	            	v.setVertexId(attrValue.getValue());
	            } else if (attrName.equals("lat")) {
	            	v.setLat(Double.parseDouble(attrValue.getValue()));
	            } else if (attrName.equals("lon")) {
	            	v.setLon(Double.parseDouble(attrValue.getValue()));
	            } else if (attrName.equals("name")){
	            	v.setName(attrValue.getValue());
	            } else {
	            	System.out.println("Error while decoding file for vertices : wrong attribute name : "+attrName);
	            }
			});
			
			ji.addEdgeAttributeConsumer((p, attrValue) -> {
				GTFSEdge e = p.getFirst();
	            String attrName = p.getSecond();
	            
	            if (attrName.equals("distance")) {
	            	e.setWeight(Double.parseDouble(attrValue.getValue()));
	            } else {
	            	System.out.println("Error while decoding file for edges : wrong attribute name : "+attrName);
	            }
			});
			//
			
			try {
				
				File graphFile = new File(
						getClass()
						.getClassLoader()
						.getResource(this.pathToDataset+"/"+this.pathToDataset+"_graph.json")
						.getFile()
				);
				
				ji.importGraph(g, graphFile);
				return g;
				
			} catch (NullPointerException e) { // if file not present
				e.printStackTrace();
				return null;
			}
			
		}
		
		
		public synchronized Graph<GTFSVertex, GTFSEdge> unserializeRemote() {
			JSONImporter<GTFSVertex, GTFSEdge> ji = new JSONImporter<GTFSVertex, GTFSEdge>();
			Graph<GTFSVertex, GTFSEdge> g = new DirectedMultigraph(GTFSEdge.class);
			
			//setup the Vertex and Edges BiConsummer
			ji.addVertexAttributeConsumer((p, attrValue) -> {
				GTFSVertex v = p.getFirst();
	            String attrName = p.getSecond();
	            
	            if (attrName.equals("vertexId")) {
	            	v.setVertexId(attrValue.getValue());
	            } else if (attrName.equals("lat")) {
	            	v.setLat(Double.parseDouble(attrValue.getValue()));
	            } else if (attrName.equals("lon")) {
	            	v.setLon(Double.parseDouble(attrValue.getValue()));
	            } else if (attrName.equals("name")){
	            	v.setName(attrValue.getValue());
	            } else {
	            	System.out.println("Error while decoding file for vertices : wrong attribute name : "+attrName);
	            }
			});
			
			ji.addEdgeAttributeConsumer((p, attrValue) -> {
				GTFSEdge e = p.getFirst();
	            String attrName = p.getSecond();
	            
	            if (attrName.equals("distance")) {
	            	e.setWeight(Double.parseDouble(attrValue.getValue()));
	            } else {
	            	System.out.println("Error while decoding file for edges : wrong attribute name : "+attrName);
	            }
			});
			//
			
			//TODO : download the .json file (if it exists) from the GCP as an outputStream Object
			//then run the GraphImporter and return g
			try {
				
				
				return g;
			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		
		
	}
	
}
