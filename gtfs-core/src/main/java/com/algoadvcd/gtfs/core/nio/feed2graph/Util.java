package com.algoadvcd.gtfs.core.nio.feed2graph;

import java.security.SecureRandom;

public class Util {
	
	public static String generateRamdomName() {
		int stringLength = 5;
		String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
	    String CHAR_UPPER = CHAR_LOWER.toUpperCase();
	    String NUMBER = "0123456789";
	    String DATA_FOR_RANDOM_STRING = CHAR_UPPER + NUMBER;
	    SecureRandom random = new SecureRandom();
		StringBuilder sb = new StringBuilder(stringLength);
        for (int i = 0; i < stringLength; i++) {
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);
            sb.append(rndChar);
        } 
        return sb.toString();
	}
	
	public static getNearestNodes() {
		
	}
	
	public static greatCircleVec() {
		
	}
	
	public static generateGraphNodeDataframe() {
		
	}
}
