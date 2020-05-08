package com.algoadvcd.gtfs.core.nio;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.Graph;

import com.algoadvcd.gtfs.core.nio.GTFSFeedLoader.GTFSFeedLoaderBuilder;
import com.algoadvcd.gtfs.core.nio.config.GTFSFeedConfigEdge;
import com.algoadvcd.gtfs.core.nio.config.GTFSFeedConfigVertex;
import com.algoadvcd.gtfs.core.nio.config.GTFSFeedGraphConfig;

import it.unimi.dsi.fastutil.ints.IntIterator;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.strings.StringPredicates;
import tech.tablesaw.selection.Selection;

public class GTFSReader {
	private static Logger logger = LogManager.getLogger();
	private Target target;
	private String datasetname;
	private static Set<String> DAY_NAMES;
	
	private GTFSReader(GTFSReaderBuilder builder) {
		this.target = builder.target;
		this.datasetname = builder.datasetname;
		String[] days = new String[]{"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
		this.DAY_NAMES = new HashSet<String>(Arrays.asList(days));
	}
	
	
	private GTFSFeed loadFeed(String path) {
		if (this.target == Target.LOCAL) {
			Graph<GTFSFeedConfigVertex, GTFSFeedConfigEdge> config = GTFSFeedGraphConfig.defaultConfig();
			GTFSFeed feed = 
					new GTFSFeedLoaderBuilder(path)
					.localDataset()
					.build()
					.getFeed();
			return feed;
			
		} else {
			
		}
	}
	
	public Map<String, Set<String>> readServiceIdsByDate(String path) {
		GTFSFeed feed = loadFeed(path);
		Map<Date, Set<String>> results = new HashMap<Date, Set<String>>();
		Map<Date, Set<String>> removals = new HashMap<Date, Set<String>>();
		
		List<String> service_ids = (List<String>) feed.get("trips").column("service_id").asList();
		Table calendar = feed.get("calendar");
		Table caldates = feed.get("calendar_dates");
		
		//1) only consider calendar rows with applicable trips
		if (!calendar.isEmpty()) {
			int[] caledar_indexes = new int[service_ids.size()];
			List<String> calendar_service_id = (List<String>) calendar.column("service_id").asList();
			
			int j = 0;
			for (int i = 0; i < service_ids.size(); i++) {
				if (calendar_service_id.contains(service_ids.get(i))) {
					caledar_indexes[j] = i;
					j++;
				}
			}
			
			calendar = calendar.where(Selection.with(caledar_indexes)).copy();
			logger.info("calendar service_id filtered");
		}
		
		//2) only consider calendar_dates rows with applicable trips
		if (!caldates.isEmpty()) {
			int[] caldates_indexes = new int[service_ids.size()];
			List<String> caldates_service_id = (List<String>) caldates.column("service_id").asList();
			
			int j = 0;
			for (int i = 0; i < service_ids.size(); i++) {
				if (caldates_service_id.contains(service_ids.get(i))) {
					caldates_indexes[j] = i;
					j++;
				}
			}
			
			caldates = caldates.where(Selection.with(caldates_indexes)).copy();
			logger.info("calendar_dates service_id filtered");
		}
		
		//3) Parse dates and Build up results dict from calendar ranges
		if (!calendar.isEmpty()) {
			Column startDate = calendar.column("start_date");
			Column endDate = calendar.column("end_date");
			
			for (Row row : calendar) {
				int start = row.getInt("start_date"); //the data are already formatted with ISO format (YYYYMMDD)
				int end = row.getInt("end_date");
				
				int[] dow = new int[this.DAY_NAMES.size()];
				int count = 0;
				
				for (String day : this.DAY_NAMES) {
					dow[count] = row.getInt(day);
					count++;
				}
				
				DateFormat dateParser = new SimpleDateFormat("yyyyMMdd"); //ordinal format
				for (int s = start; s < end+1; s++) {
					Integer dateInt = s;
					String isodate = dateInt.toString();
					try {
						Date date = dateParser.parse(isodate);
						@SuppressWarnings("deprecation")
						int weekDay = date.getDay();
						if (dow[weekDay] == 1) {
							if (results.containsKey(date)) {
								results.get(date).add(row.getString("service_id"));
							} else {
								Set<String> serviceIdSet = new HashSet<String>();
								serviceIdSet.add(row.getString("service_id"));
								results.put(date, serviceIdSet);
							}
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		//4) Parse dates again and Split out additions and removals
		// Add to results by date
		// Collect removals and finally, process removals by date
		// Drop the key from results if no service present
		if (!calendar.isEmpty()) {
			Column dates = caldates.column("date");
			List<Integer> exceptionTypes = (List<Integer>) caldates.column("exception_type").asList();
			
			//Split out additions and removals
			int[] datesAdded = new int[exceptionTypes.size()];
			int[] datesRemoved = new int[exceptionTypes.size()];
			
			int j = 0;
			for (int i = 0; i < exceptionTypes.size(); i++) {
				if (exceptionTypes.get(i) == 1) {
					datesAdded[j] = i;
					j++;
				}
			}
			j = 0;
			for (int i = 0; i < exceptionTypes.size(); i++) {
				if (exceptionTypes.get(i) == 2) {
					datesRemoved[j] = i;
					j++;
				}
			}
			
			Table cdadd = caldates.where(Selection.with(datesAdded)).copy();
			logger.info("caldates exception type = 1 filtered");
			Table cdrem = caldates.where(Selection.with(datesRemoved)).copy();
			logger.info("caldates exception type = 2 filtered");
			
			// Add to results by date
			for (Row row : cdadd) {
				
			}
			
			// Collect removals
			for (Row row : cdrem) {
				
			}
			
			// Finally, process removals by date
			
			
		}
		
		// Python (return {k: frozenset(v) for k, v in results.items()})
		
		
	}
	
	public Map<String, Integer> readTripCountsByDate(String path) {
		
	}
	
	public GTFSFeed loadRawFeed(String path) {
		
	}
	
	
	private GTFSFeed _loadFeed(String path, Graph<GTFSFeedConfigVertex, GTFSFeedConfigEdge> config) {
		
	}
	
	private Map<String, Set<String>> serviceIdsByDate(GTFSFeed feed) {
		
	}
	
	public static class GTFSReaderBuilder {
		private String datasetname;
		private Target target;
		
		public GTFSReaderBuilder(String datasetname) {
			this.datasetname = datasetname;
		}
		
		public GTFSReaderBuilder localReader() {
			this.target = Target.LOCAL;
			return this;
		}
		
		public GTFSReaderBuilder remoteReader() {
			this.target = Target.REMOTE;
			return this;
			
		}
		
		public GTFSReader build() {
			return new GTFSReader(this);
		}
	}
	
}
