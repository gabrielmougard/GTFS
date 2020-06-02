package gtfs.corev2.algorithms.clustering;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
	        return clusters.size();
	    }
	
	    @Override
	    public List<Set<V>> getClusters() {
	        return clusters;
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
