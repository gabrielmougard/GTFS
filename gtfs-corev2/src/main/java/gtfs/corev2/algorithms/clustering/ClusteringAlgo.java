package gtfs.corev2.algorithms.clustering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.WeightedMultigraph;

import gtfs.corev2.GTFSVertex;
import gtfs.corev2.nio.util.Distance;
import gtfs.corev2.nio.util.Tuple;

public interface ClusteringAlgo<V> {

	Clustering<V> getClustering();
	
	interface Clustering<V> extends Iterable<Set<V>> {
		int getNumberClusters();
		
		List<Set<V>> getClusters();
	}
	
	class ClusteringImpl<V> implements Clustering<V>, Serializable {
		
		private static final long serialVersionUID = -73782410443848101L;
		private final List<Set<V>> clusters;
		private final Map<V, Set<V>> clustersMap;

	    /**
	     * Construct a new clustering
	     */
	    
	    public ClusteringImpl(Map<V, Set<V>> clustersMap) {
	    	this.clustersMap = clustersMap;
	    	this.clusters = new ArrayList<>(clustersMap.values());
	    }
    
	    @Override
	    public int getNumberClusters() {
	        return this.clusters.size();
	    }
	
	    @Override
	    public List<Set<V>> getClusters() {
	        return this.clusters;
	    }
	    
	    /*
	     * Compute the barycentric point (or centroid) of each cluster
	     */
	    private Tuple<Double, Double> getBarycenter(Set<V> cluster) {
	    		
	    	Double clusterBarycenterLat = 0.0;
	    	Double clusterBarycenterLon = 0.0;
	    		
	    	for (V vertex : cluster) {
	    		GTFSVertex gtfsVertex = (GTFSVertex)vertex;
	    		clusterBarycenterLat += gtfsVertex.getLat();
	    		clusterBarycenterLon += gtfsVertex.getLon();
	    	}
	    		
	    	clusterBarycenterLat /= cluster.size();
	    	clusterBarycenterLon /= cluster.size();
	    	return new Tuple<Double,Double>(clusterBarycenterLat,clusterBarycenterLon);
	    	
	    }
	    
	    public Graph<GTFSClusterVertex, GTFSClusterEdge> convertClustersAsGraph() {
	    	Graph<GTFSClusterVertex, GTFSClusterEdge> g = new WeightedMultigraph(GTFSClusterEdge.class);
	    	
	    	// create GTFSClusterVertex and add them to g
	    	Map<Set<GTFSVertex>, GTFSClusterVertex> clusterVertexMap = new HashMap<Set<GTFSVertex>, GTFSClusterVertex>();
	    	int clusterId = 0;
	    	for (Set<V> elements : this.clustersMap.values()) {
	    		//compute barycentric coordinates
	    		Tuple<Double, Double> barycenter = getBarycenter(elements);
	    		GTFSClusterVertex cv = new GTFSClusterVertex(clusterId, "cluster #"+clusterId, barycenter.first, barycenter.second, (Set<GTFSVertex>)elements);
	    		g.addVertex(cv);
	    		clusterVertexMap.put((Set<GTFSVertex>)elements,cv); // for later use when adding edges to graph
	    		clusterId++;
	    	}
	    	
	    	// create the GTFSClusterEdge and add them to g
	    	for (V key : this.clustersMap.keySet()) {
	    		//the key here is a vertex (in a cluster) linked to a different cluster
	    		
	    		//1) find in which cluster the key is
	    		GTFSClusterVertex keyCluster = null;
	    		for (Set<V> cluster : this.clustersMap.values()) {
	    			if (cluster.contains(key)) {
	    				keyCluster = clusterVertexMap.get((Set<GTFSVertex>)cluster);
	    				break;
	    			}
	    		}
	    		
	    		
	    		//2) find the cluster associated with the key in this.clusterMap
	    		GTFSClusterVertex targetCluster = clusterVertexMap.get(this.clustersMap.get(key));
	    		
	    		//3) Create the edge with the barycentric coordinates of the two GTFSClusterVertex found (use Distance.latlon() for the edge weight)
	    		if (keyCluster != null) {
	    			g.addEdge(
	    	    		keyCluster, 
	    	    		targetCluster, 
	    	    		new GTFSClusterEdge(
	    	    			Distance.latlon(
	    	    				keyCluster.getLat(), 
	    	    				keyCluster.getLon(), 
	    	    				targetCluster.getLat(), 
	    	    				targetCluster.getLon()
	    	    			)
	    	    		)
	    	    	);
	    		} else {
	    			System.out.println("Failed to create GTFSClusterEdge");
	    		}
	    		
	    	}
	    	
	    	return g;
	    }
	
	    @Override
	    public String toString() {
	    	StringBuilder str = new StringBuilder(); 
	    	str.append("Clustering : k = "+ clusters.size()+"\n");
	    	int i = 1;
	    	for (Set<V> cluster : clusters) {
	    		str.append("cluster #"+i+" size : "+cluster.size()+"\n");
	    		i++;
	    	}
	        return str.toString();
	    }
	
	    @Override
	    public Iterator<Set<V>> iterator() {
	        return clusters.iterator();
	    }
	}
	
}
