package gtfs.corev2.algorithms.clustering;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gtfs.corev2.GTFSVertex;
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

	    /**
	     * Construct a new clustering.
	     *
	     * @param clusters clusters
	     */
	    public ClusteringImpl(List<Set<V>> clusters) {
	        this.clusters = clusters;
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
	    public Map<Set<V>, Tuple<Double, Double>> getBarycentersMap() {
	    	Map<Set<V>, Tuple<Double, Double>> barycentersMap = new HashMap<Set<V>, Tuple<Double, Double>>();
	    	
	    	for (Set<V> cluster : this.clusters) {
	    		
	    		Double clusterBarycenterLat = 0.0;
	    		Double clusterBarycenterLon = 0.0;
	    		
	    		for (V vertex : cluster) {
	    			GTFSVertex gtfsVertex = (GTFSVertex)vertex;
	    			clusterBarycenterLat += gtfsVertex.getLat();
	    			clusterBarycenterLon += gtfsVertex.getLon();
	    		}
	    		
	    		clusterBarycenterLat /= cluster.size();
	    		clusterBarycenterLon /= cluster.size();
	    		barycentersMap.put(cluster, new Tuple<Double,Double>(clusterBarycenterLat,clusterBarycenterLon));
	    	}
	    	
	    	
	    	return barycentersMap;
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
