package gtfs.corev2.algorithms.shortestpath;

public interface ShortestPath<V, E> {

	GTFSPath<V, E> getPath(V source, V target);
	
	double getPathWeight(V source, V target);
	
	Paths<V, E> getPaths(V source);
	
	interface Paths<V, E> {
        
        /**
         * Returns the source vertex.
         */
        V getSourceVertex();

        /**
         * Return the weight of the path from the source vertex to the target vertex. 
         */
        double getWeight(V target);

        /**
         * Return the path from the source vertex to the target vertex.
         */
        GTFSPath<V, E> getPath(V target);
    }
}
