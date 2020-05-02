package com.algoadvcd.gtfs.core;

import org.junit.*;
import java.util.*;
import java.security.SecureRandom;

public class GTFSGraphTest {
	
	
	@Test
	public void graphCreation() {
		Graph<GTFSVertex> g = new GTFSGraph<GTFSVertex>();
		
		List<GTFSVertex> vertices = generateRandomGTFSVertices();
		int[] indexes = new int[vertices.size()];
		for(int i = 0; i < indexes.length; i++) {
			indexes[i] = i;
		}
		indexes = randomizeArray(indexes);
		
		if (vertices.size() % 2 == 0) {
			for (int i = 0; i < indexes.length-1; i++) {
				GTFSVertex v = vertices.get(indexes[i]);
				GTFSVertex w = vertices.get(indexes[i+1]);
				g.addEdge(v, w);
			}
		} else {
			for (int i = 0; i < indexes.length-2; i++) {
				GTFSVertex v = vertices.get(indexes[i]);
				GTFSVertex w = vertices.get(indexes[i+1]);
				g.addEdge(v, w);
			}
		}
	}
	
	private String randomString() {
		
		String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
	    String CHAR_UPPER = CHAR_LOWER.toUpperCase();
	    String NUMBER = "0123456789";
	    String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
	    SecureRandom random = new SecureRandom();

		int stringLength = 50;
		StringBuilder sb = new StringBuilder(stringLength);
        for (int i = 0; i < stringLength; i++) {

			// 0-62 (exclusive), random returns 0-61
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);
            sb.append(rndChar);
        }
        
        return sb.toString();
	}
	
	private List<GTFSVertex> generateRandomGTFSVertices() {
		SecureRandom random = new SecureRandom();
		int nbVertices = random.nextInt(100) + 4; //minimum 4 vertices
		int nbDataFields = random.nextInt(10) + 2; //minimum 2 data fields
		List<List<String>> verticesData = new ArrayList<List<String>>();
		for (int i = 0; i < nbVertices; i++) {
			List<String> vertexData = new ArrayList<String>();
			for (int j = 0; j < nbDataFields; j++) {
				vertexData.add(randomString());
			}
			verticesData.add(vertexData);
		}
		
		List<GTFSVertex> res = new ArrayList<GTFSVertex>();
		int count = 0;
		for (List<String> data : verticesData) {
			res.add(new GTFSVertex(count, data));
			count++;
		}
		return res;
	}
	
	private int[] randomizeArray(int[] array) {
		Random rgen = new Random();  // Random number generator			
		 
		for (int i=0; i<array.length; i++) {
		    int randomPosition = rgen.nextInt(array.length);
		    int temp = array[i];
		    array[i] = array[randomPosition];
		    array[randomPosition] = temp;
		}
 
		return array;
	}
}
