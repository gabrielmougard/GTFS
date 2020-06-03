package gtfs.corev2.algorithms.clustering;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/*
 * Union-Find is a disjoint-set data structure. It supports two operations:
 * finding the set a specific element is in, and merging two sets. The implementation uses union by
 * rank and path compression to achieve an amortized cost of $O(alpha(n))$ per operation where
 * alpha is the inverse Ackermann function. UnionFind uses the hashCode and equals method of the
 * elements it operates on.
 */

public class UnionFind<V> {
	private final Map<V, V> parentMap;
	private final Map<V, Integer> rankMap;
	private int count; //numlber of components
	
	/*
	 * Creates a Union-Find instance with all the elements in separate sets
	 */
	public UnionFind(Set<V> elements) {
		parentMap = new LinkedHashMap<>();
		rankMap = new HashMap<>();
		for (V element : elements) {
			parentMap.put(element, element);
			rankMap.put(element, 0);
		}
		count = elements.size();
	}
	
	/*
	 * Returns the representative element of the set that element is in.
	 */
	public V find(final V element) {
		if (!parentMap.containsKey(element)) {
			throw new IllegalArgumentException(
	                "element is not contained in this UnionFind data structure: " + element);
		}
		
		V current = element;
		while(true) {
			V parent = parentMap.get(current);
			if (parent.equals(current)) {
				break;
			}
			current = parent;
		}
		final V root = current;
		
		current = element;
		while (!current.equals(root)) {
			V parent = parentMap.get(current);
			parentMap.put(current, root);
			current = parent;
		}
		
		return root;
	}
	
	public void union(V element1, V element2) {
		if (!parentMap.containsKey(element1) || !parentMap.containsKey(element2)) {
            throw new IllegalArgumentException("elements must be contained in given set");
        }

        V parent1 = find(element1);
        V parent2 = find(element2);

        // check if the elements are already in the same set
        if (parent1.equals(parent2)) {
            return;
        }

        int rank1 = rankMap.get(parent1);
        int rank2 = rankMap.get(parent2);
        if (rank1 > rank2) {
            parentMap.put(parent2, parent1);
        } else if (rank1 < rank2) {
            parentMap.put(parent1, parent2);
        } else {
            parentMap.put(parent2, parent1);
            rankMap.put(parent1, rank1 + 1);
        }
        count--;
	}
	
	public int numberOfSets() {
		assert count >= 1 && count <= parentMap.keySet().size();
        return count;
	}
}
