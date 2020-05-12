package gtfs.corev2.nio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedMultigraph;

import gtfs.corev2.*;
import gtfs.corev2.nio.util.Distance;
import gtfs.corev2.nio.util.Tuple;


public class GTFSParser {
	private Map<String, List<List<String>>> gtfsTables;
	private List<List<String>> edgeList;
	
	public GTFSParser(Map<String, List<List<String>>> gtfsTables) {
		this.gtfsTables = gtfsTables;
		this.edgeList = new ArrayList<List<String>>();
	}
	
	public GTFSParser(String pathToDataset, String jsonfile) {
		
	}
	
	public List<List<String>> getEdgeList() {
		return this.edgeList;
	}
	
	public Graph<GTFSVertex, GTFSEdge> build() {
		Graph<GTFSVertex, GTFSEdge> g = new DirectedMultigraph(GTFSEdge.class);
		
		
		//build the graph
		try {
			this.edgeList = buildEdgeList();
			buildGraph(g);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//return g
		return g;
	}
	
	
	private List<List<String>> buildEdgeList() throws Exception {
		if (this.gtfsTables.containsKey("transfers.txt") && this.gtfsTables.containsKey("stop_times.txt")) {
			Map<String, GTFSEdgeTemp> edges = new HashMap<String, GTFSEdgeTemp>();
			
			System.out.println("parsing stop_times.txt ...");
			parseStopTimes(edges);
			System.out.println("stop_times.txt parsed !");
			System.out.println("parsing transfers.txt ...");
			parseTransfers(edges);
			
			List<List<String>> res = convertEdgesMap2List(edges);
			
			// write res variable to resources to debug the output
			//writeToResources(res);
			//
			
			return res;
			
			
		} else {
			throw new Exception("transfers and stop_times data are not present.");
		}
	}
	
	private void parseStopTimes(Map<String, GTFSEdgeTemp> edges) {
		Iterator<List<String>> itStopTimes = this.gtfsTables.get("stop_times.txt").iterator();
		String tripId = "";
		String depTime = "";
		String toStopId = "";
		String lastDepTime = "";
		String fromStopId = "";
		String key = "";
		
		while (itStopTimes.hasNext()) {
			List<String> line = itStopTimes.next();
			
			//new trip begins
			if (!line.get(0).equals(tripId)) {
				tripId = line.get(0);
				depTime = "0";
				toStopId = "0";
				lastDepTime = "0";
				fromStopId = "0";
			}
			
			lastDepTime = depTime;
			String[] l = line.get(2).substring(0, 5).split(":");
			Integer hh = Integer.parseInt(l[0])*3600;
			Integer total = hh + Integer.parseInt(l[1])*60;
			depTime = total.toString();
			
			fromStopId = toStopId;
			toStopId = line.get(3);
			
			key = fromStopId+toStopId;
			
			if (!edges.containsKey(key)) {
				edges.put(key, createEdge(fromStopId, toStopId, EdgeType.METRO));
			}
				     
			updateEdge(edges.get(key), Integer.parseInt(lastDepTime), Integer.parseInt(depTime));
			
		}
		
	}
	
	private void parseTransfers(Map<String, GTFSEdgeTemp> edges) {
		Iterator<List<String>> itTransfers = this.gtfsTables.get("transfers.txt").iterator();
		String toStopId = "";
		String duration = "";
		String fromStopId = "";
		String key = "";

		while (itTransfers.hasNext()) {
			List<String> line = itTransfers.next();
			
			fromStopId = line.get(0);
			toStopId = line.get(1);
			try {
				duration = line.get(3);
			} catch (IndexOutOfBoundsException e) {
				duration = "0";
			}
			
			if (duration.equals("")) {
				duration = "0";
			}
			key = fromStopId+toStopId;
			edges.put(key, createEdge(fromStopId, toStopId, EdgeType.CORRESPONDANCE));
			updateEdge(edges.get(key), 0, Integer.parseInt(duration));
		}
	}
	
	private GTFSEdgeTemp createEdge(String fromStopId, String toStopId, EdgeType type) {
		return new GTFSEdgeTemp(fromStopId, toStopId, type, 0, 3600*24, 0, 0, 0, 0, 0);
	}
	
	private void updateEdge(GTFSEdgeTemp edge, int startTime, int endTime) {
		int delta = 0;
		int temp = 0;
		edge.setCounter(edge.getCounter()+1);
		delta = endTime - startTime;
		
		if (delta <= 0)
			delta = 60;
		
		edge.setDuration(edge.getDuration()+delta);
		
		if (edge.getType() == EdgeType.METRO) {
			
			if (edge.getDebut() > endTime && endTime > 3*3600)
				edge.setDebut(endTime);
			if (edge.getEnd() < endTime)
				edge.setEnd(endTime);
			
		} else {
			edge.setDebut(0);
			edge.setEnd(25*3600);
		}
		
		if (edge.getLastEncounter() != 0) {
			if (edge.getLastEncounter() < startTime) {
				temp = (startTime - edge.getLastEncounter());
				if (temp < 1200 && temp > 20) {
					edge.setEncountersAvg(edge.getEncountersAvg()+temp);
					edge.setEncounters(edge.getEncounters()+1);
				}
				edge.setLastEncounter(startTime);
			}
		} else {
			edge.setLastEncounter(startTime);
		}
	}
	
	private List<List<String>> convertEdgesMap2List(Map<String, GTFSEdgeTemp> edges) {
		List<List<String>> res = new ArrayList<List<String>>();
		for (String k : edges.keySet()) {
			List<String> l = new ArrayList<String>();
			l.add(edges.get(k).getFromStopId());
			l.add(edges.get(k).getToStopId());
			
			int averageDuration = 0;
			int startTime = 0;
			int endTime = 0;
			
			int counter = edges.get(k).getCounter();
			if (counter > 0)
				averageDuration = edges.get(k).getDuration() / counter;
			else
				averageDuration = 60;
			l.add(new Integer(averageDuration).toString());
		
			startTime = edges.get(k).getDebut();
			endTime = edges.get(k).getEnd();
			
			Integer hhStart = startTime/3600;
			Integer mmStart = (startTime%3600)/60;
			Integer hhEnd = endTime/3600;
			Integer mmEnd = (endTime%3600)/60;
			
			l.add(hhStart.toString()+"h"+mmStart.toString());
			l.add(hhEnd.toString()+"h"+mmEnd.toString());
			
			if (edges.get(k).getType() == EdgeType.METRO)
				l.add("1");
				if (edges.get(k).getEncounters() > 0 && edges.get(k).getEncountersAvg() != 0) {
					Integer ratio = edges.get(k).getEncountersAvg() / edges.get(k).getEncounters();
					l.add(ratio.toString());
				}
			else
				l.add("2");
			
			
			res.add(l);
		}
		return res;
	}
	
	private void buildGraph(Graph<GTFSVertex, GTFSEdge> g) {
		Map<String, List<String>> stops = readStops();
		//addRoutesInfosToStops(stops);
		parseEdges(stops, g);
		
	}
	
	private void writeToResources(List<List<String>> edgeList) {
		try {
			
			File fout = new File(this.getClass().getClassLoader().getResource("mbta/edges.txt").getFile());
			FileOutputStream fos = new FileOutputStream(fout);
			
			String buffer;
			int count = 0;
			for (List<String> list : edgeList) {
				System.out.println("line "+count+" : "+list.toString());
				buffer = list.toString()+"\n";
				byte[] mybytes = buffer.getBytes();
				try {
					fos.write(mybytes);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				count++;
			}
			try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private Map<String, List<String>> readStops()  {
		Iterator<List<String>> itStops = this.gtfsTables.get("stops.txt").iterator();
		Map<String, List<String>> res = new HashMap<String, List<String>>();
		while(itStops.hasNext()) {
			List<String> line = itStops.next();
			res.put(line.get(0), line.subList(1, line.size()));
		}
		return res;
	}
	
	private void addRoutesInfosToStops(Map<String, List<String>> stops) {
		//TODO : result of get_stops_for_line is Map<String, Map<String, String>>
		// get_stops_for_line
		Map<String, Map<String, String>> routes = new HashMap<String, Map<String, String>>();
		Map<String, Map<String, String>> trips = new HashMap<String, Map<String, String>>();
		Map<String, Map<String, String>> stopsForThisLine = new HashMap<String, Map<String, String>>();
		int nbStopsForLine = 0;
		
		//routes
		Iterator<List<String>> itRoutes = this.gtfsTables.get("routes.txt").iterator();
		while (itRoutes.hasNext()) {
			List<String> line = itRoutes.next();
			Map<String, String> m = new HashMap<String, String>();
			m.put("line",line.get(2));
			m.put("dir", line.get(3));
			m.put("type", line.get(5));
			routes.put(line.get(0), m);
		}
		
		//trips
		Iterator<List<String>> itTrips = this.gtfsTables.get("trips.txt").iterator();
		while (itTrips.hasNext()) {
			List<String> line = itTrips.next();
			String tripRouteId = line.get(0);
			String tripId = line.get(2);
			trips.put(tripId, routes.get(tripRouteId));
		}
		
		//stop_times
		Iterator<List<String>> itStops = this.gtfsTables.get("stops.txt").iterator();
		while (itStops.hasNext()) {
			nbStopsForLine++;
			itStops.next();
		}
		
		Iterator<List<String>> itStopTimes = this.gtfsTables.get("stop_times.txt").iterator();
		while (itStopTimes.hasNext()) {
			List<String> line = itStopTimes.next();
			if (stopsForThisLine.size() == nbStopsForLine) {
				break;
			}
			String stopTripId = line.get(0);
			String stopId = line.get(3);
			stopsForThisLine.put(stopId, trips.get(stopTripId));
		}
		
		for (Map.Entry<String, Map<String, String>> entry : stopsForThisLine.entrySet()) {
			//TODO
			
		}
		
		//
	}
	
	private void parseEdges(Map<String, List<String>> stops, Graph<GTFSVertex, GTFSEdge> g) {
		//use this.edgeList
		Iterator<List<String>> itEdgeList = this.edgeList.iterator();
		Set<String> added = new HashSet<String>();
		
		while (itEdgeList.hasNext()) {
			List<String> line = itEdgeList.next();
			
			String fromStopId = line.get(0);
			String toStopId = line.get(1);
			String duration = line.get(2);
			String beginTime = line.get(3);
			String endTime = line.get(4);
			String edgeType = line.get(5);
			String freq = "";
			if (line.size() == 7) {
				freq = line.get(6);
			}
			
			
			//map ids
			List<String> mappedFromStopId = stops.get(fromStopId);
			List<String> mappedToStopId = stops.get(toStopId);
			
			// create nodes
			try {
				if ((!stops.get(fromStopId).get(5).isEmpty() && 
					!stops.get(fromStopId).get(6).isEmpty()) &&
					(!stops.get(toStopId).get(5).isEmpty()) &&
					!stops.get(toStopId).get(6).isEmpty()){
					
					GTFSVertex v = new GTFSVertex(
							fromStopId,
							stops.get(fromStopId).get(1), //name
							stops.get(fromStopId).get(5), //lat 
							stops.get(fromStopId).get(6) //lon
					);
					
					GTFSVertex w = new GTFSVertex(
							toStopId,
							stops.get(toStopId).get(1), //name
							stops.get(toStopId).get(5), //lat 
							stops.get(toStopId).get(6) //lon
					);
					if (!g.containsVertex(v)) {
						g.addVertex(v);
					}
					if (!g.containsVertex(w)) {
						g.addVertex(w);
					}
					g.addEdge(v, w, new GTFSEdge(Distance.latlon(v.getLat(), v.getLon(), w.getLat(), w.getLon())));
					
					
				} else {
					System.out.println("empty lat or lon in v or w");
				}
				
			} catch( NullPointerException e) {
			
			}	
		}
	}
}
