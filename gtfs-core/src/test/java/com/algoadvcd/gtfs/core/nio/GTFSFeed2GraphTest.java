package com.algoadvcd.gtfs.core.nio;

import org.junit.*;

import tech.tablesaw.api.Table;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.lang.Object;

public class GTFSFeed2GraphTest {

	@Test
	public void ListSubstractTest() {
		List<Double> l1 = new ArrayList<Double>(Arrays.asList(2.3,4.6,3.8,9.66));
		List<Double> l2 = new ArrayList<Double>(Arrays.asList(1.5,4.5,3.8,10.66));
		Iterator<Double> it1 = l1.iterator();
		Iterator<Double> it2 = l2.iterator();
		List<Double> res = new ArrayList<Double>();
		
		while(it1.hasNext() && it2.hasNext()) {
			res.add(it1.next()-it2.next());
		}
		System.out.println(res.toString());
		
		List<Double> first = l1.subList(1, l1.size());
		List<Double> second = l1.subList(0, l1.size()-1);
		it1 = first.iterator();
		it2 = second.iterator();
		res = new ArrayList<Double>();
		
		while(it1.hasNext() && it2.hasNext()) {
			res.add(it1.next()-it2.next());
		}
		System.out.println(res.toString());
		res.removeIf(s -> s < 0);
		System.out.println(res.toString());
	}
}
