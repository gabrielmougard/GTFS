package gtfs.corev2.nio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import java.util.function.Function;
import java.util.HashMap;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.WeightedMultigraph;
import org.jgrapht.nio.*;
import org.jgrapht.nio.json.JSONExporter;
import org.jgrapht.util.*;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import gtfs.corev2.GTFSEdge;
import gtfs.corev2.GTFSVertex;

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
	
		public Graph<GTFSVertex, GTFSEdge> unserialize() {
			if (this.target == Target.LOCAL) {
				SynchronizedSerializer serializer = new SynchronizedSerializer(this.pathToDataset);
				return serializer.unserializeLocal();
			} else if (this.target == Target.REMOTE) {
				SynchronizedSerializer serializer = new SynchronizedSerializer(this.pathToDataset);
				return serializer.unserializeRemote();
			} else {
				SynchronizedSerializer serializer = new SynchronizedSerializer(this.pathToDataset);
				return serializer.unserializeRemote();
			}
		}
	
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
			Graph<GTFSVertex, GTFSEdge> g = new WeightedMultigraph(GTFSEdge.class);
			
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream is = classloader.getResourceAsStream(this.pathToDataset+"/"+this.pathToDataset+"_graph.json");
	        JsonParser parser = new JsonParser();
			String json = null;
			
			try {
			    int size = is.available();
			    byte[] buffer = new byte[size];
			    is.read(buffer);
			    is.close();
			    json = new String(buffer, "UTF-8");

			} catch (IOException e) {
			    e.printStackTrace();
			}
			
			JsonObject jsonObject = parser.parse(json).getAsJsonObject();
			JsonArray nodes = jsonObject.getAsJsonArray("nodes");
			JsonArray edges = jsonObject.getAsJsonArray("edges");
			Map<String, String> id2VertexId = new HashMap<String, String>();
			Map<String, GTFSVertex> vertexId2Vertex = new HashMap<String, GTFSVertex>();
			
			for (JsonElement node : nodes) {
			    JsonObject nodeObj = node.getAsJsonObject();
			    
			    String id = nodeObj.get("id").getAsString();
			    String vertexId = nodeObj.get("vertexId").getAsString();
			    id2VertexId.put(id, vertexId);
			    String name = nodeObj.get("name").getAsString();
			    String lat = nodeObj.get("lat").getAsString();
			    String lon = nodeObj.get("lon").getAsString();
			    
			    GTFSVertex v = new GTFSVertex(vertexId, name, lat, lon);
			    
			    g.addVertex(v);
			    vertexId2Vertex.put(vertexId, v);
			}
			
			for (JsonElement edge : edges) {
				JsonObject edgeObj = edge.getAsJsonObject();
				
				String source = edgeObj.get("source").getAsString();
				String target = edgeObj.get("target").getAsString();
				String distance = edgeObj.get("distance").getAsString();
				String vertexIdSource = id2VertexId.get(source);
				String vertexIdTarget = id2VertexId.get(target);
				

				g.addEdge(
					vertexId2Vertex.get(vertexIdSource), 
					vertexId2Vertex.get(vertexIdTarget), 
					new GTFSEdge(Double.parseDouble(distance))
				);
				
				
			}
			
			return g;
			
		}
		
		
		public synchronized Graph<GTFSVertex, GTFSEdge> unserializeRemote() {
	
			Graph<GTFSVertex, GTFSEdge> g = new DirectedMultigraph(GTFSEdge.class);
			JsonParser parser = new JsonParser();
			String json = null;

			try {
				//initialize GCP client
				Storage storage;
				try {
					Credentials credentials = GoogleCredentials.fromStream(
							getClass()
							.getClassLoader()
							.getResourceAsStream(BucketConfiguration.BUCKET_CREDENTIALS)
					);
					storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}

				String bucketName = BucketConfiguration.BUCKET_NAME;				
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				Blob blob = storage.get(BlobId.of(bucketName, this.pathToDataset+"/"+this.pathToDataset+"_graph.json"));
				blob.downloadTo(out, Blob.BlobSourceOption.generationMatch());
		        InputStream decodedStream = new ByteArrayInputStream(out.toByteArray());
		        
		        try {
				    int size = decodedStream.available();
				    byte[] buffer = new byte[size];
				    decodedStream.read(buffer);
				    decodedStream.close();
				    json = new String(buffer, "UTF-8");

				} catch (IOException e) {
				    e.printStackTrace();
				}
				
				JsonObject jsonObject = parser.parse(json).getAsJsonObject();
				JsonArray nodes = jsonObject.getAsJsonArray("nodes");
				JsonArray edges = jsonObject.getAsJsonArray("edges");
				Map<String, String> id2VertexId = new HashMap<String, String>();
				Map<String, GTFSVertex> vertexId2Vertex = new HashMap<String, GTFSVertex>();
				
				for (JsonElement node : nodes) {
				    JsonObject nodeObj = node.getAsJsonObject();
				    
				    String id = nodeObj.get("id").getAsString();
				    String vertexId = nodeObj.get("vertexId").getAsString();
				    id2VertexId.put(id, vertexId);
				    String name = nodeObj.get("name").getAsString();
				    String lat = nodeObj.get("lat").getAsString();
				    String lon = nodeObj.get("lon").getAsString();
				    
				    GTFSVertex v = new GTFSVertex(vertexId, name, lat, lon);
				    
				    g.addVertex(v);
				    vertexId2Vertex.put(vertexId, v);
				}
				
				for (JsonElement edge : edges) {
					JsonObject edgeObj = edge.getAsJsonObject();
					
					String source = edgeObj.get("source").getAsString();
					String target = edgeObj.get("target").getAsString();
					String distance = edgeObj.get("distance").getAsString();
					String vertexIdSource = id2VertexId.get(source);
					String vertexIdTarget = id2VertexId.get(target);
					

					g.addEdge(
						vertexId2Vertex.get(vertexIdSource), 
						vertexId2Vertex.get(vertexIdTarget), 
						new GTFSEdge(Double.parseDouble(distance))
					);
					
				}
		        
			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}
			
			return g;
		}
		
		
		
	}
	
}
