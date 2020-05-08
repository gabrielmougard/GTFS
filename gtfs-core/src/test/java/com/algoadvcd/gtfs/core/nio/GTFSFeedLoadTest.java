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
	
	@Test
	public void DateConversion() {
		DateFormat parser = new SimpleDateFormat("yyyyMMdd"); //ordinal format
		String isodate = "20190312";
		try {
			Integer dateInt = 20190312;
			System.out.println(dateInt.toString());
			System.out.println(parser.parse(isodate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
