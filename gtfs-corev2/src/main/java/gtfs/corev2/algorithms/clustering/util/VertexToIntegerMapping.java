package gtfs.corev2.algorithms.clustering.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VertexToIntegerMapping<V> {
	private final Map<V, Integer> vertexMap;
    private final List<V> indexList;
    
    public VertexToIntegerMapping(Set<V> vertices) {
        

        vertexMap = CollectionUtil.newHashMapWithExpectedSize(vertices.size());
        indexList = new ArrayList<>(vertices.size());

        for (V v : vertices) {
            vertexMap.put(v, vertexMap.size());
            indexList.add(v);
        }
    }
    
    public Map<V, Integer> getVertexMap() {
        return vertexMap;
    }

    /**
     * Get the indexList, a mapping from integers to vertices (i.e. the inverse of
     * vertexMap).
     *
     */
    public List<V> getIndexList() {
        return indexList;
    }

}
