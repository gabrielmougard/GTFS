package gtfs.corev2.algorithms.clustering.util;

import java.util.HashMap;
import java.util.HashSet;

public class CollectionUtil {
	private CollectionUtil() {}
	
	public static <E> HashSet<E> newHashSetWithExpectedSize(int expectedSize) {
        return new HashSet<>(capacityForSize(expectedSize));
    }
	
	public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(int expectedSize) {
        return new HashMap<>(capacityForSize(expectedSize));
    }
	

    private static int capacityForSize(int size) { // consider default load factor 0.75f of (Linked)HashMap
        return (int) (size / 0.75f + 1.0f); // let (Linked)HashMap limit it if it's too large
    }
}
