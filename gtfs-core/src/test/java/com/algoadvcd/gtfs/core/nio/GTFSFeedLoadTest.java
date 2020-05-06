package com.algoadvcd.gtfs.core.nio;

import org.junit.*;

import tech.tablesaw.api.Table;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class GTFSFeedLoadTest {
	@Test
	public void TableLoad() {
		InputStream fileStream = 
				getClass()
				.getClassLoader()
				.getResourceAsStream("calendar.txt");
		
		try {
			Table t = Table.read().csv(fileStream, "calendar");
			System.out.println(t.column("start_date").toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
