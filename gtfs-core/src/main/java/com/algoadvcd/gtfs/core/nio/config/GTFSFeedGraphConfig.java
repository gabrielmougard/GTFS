package com.algoadvcd.gtfs.core.nio.config;

import java.util.*;
import org.jgrapht.*;
import org.jgrapht.graph.DirectedMultigraph;

import tech.tablesaw.api.ColumnType;

public class GTFSFeedGraphConfig {
	public static Graph<GTFSFeedConfigVertex, GTFSFeedConfigEdge> defaultConfig() {
		Graph<GTFSFeedConfigVertex, GTFSFeedConfigEdge> g = new DirectedMultigraph(GTFSFeedConfigEdge.class);
		
		Map<String, GTFSFeedConfigVertex> verticesConfig = getVerticesConfig();
		g = buildGTFSFeedGraphEdges(g, verticesConfig);
		return g;
	}
	
	private static Map<String, GTFSFeedConfigVertex> getVerticesConfig() {
		Map<String, GTFSFeedConfigVertex> config = new HashMap<String, GTFSFeedConfigVertex>();
		
		// agency vertex
		Set<String> agencyRequiredColumns = new HashSet<String>();
		agencyRequiredColumns.add("agency_name");
		agencyRequiredColumns.add("agency_url");
		agencyRequiredColumns.add("agency_timezone");
		
		GTFSFeedConfigVertex agency = 
				new GTFSFeedConfigVertex
				(
						"agency.txt",
						Optional.empty(),
						agencyRequiredColumns

				);
		
			
		// calendar vertex
		Set<String> calendarRequiredColumns = new HashSet<String>();
		calendarRequiredColumns.add("service_id");
		calendarRequiredColumns.add("monday");
		calendarRequiredColumns.add("tuesday");
		calendarRequiredColumns.add("wednesday");
		calendarRequiredColumns.add("thursday");
		calendarRequiredColumns.add("friday");
		calendarRequiredColumns.add("saturday");
		calendarRequiredColumns.add("sunday");
		calendarRequiredColumns.add("start_date");
		calendarRequiredColumns.add("end_date");
			
		Map<String, VertexConverterOptions> calendarConverters = new HashMap<String, VertexConverterOptions>();
		calendarConverters.put("start_date", VertexConverterOptions.DATE_FORMAT);
		calendarConverters.put("end_date", VertexConverterOptions.DATE_FORMAT);
		calendarConverters.put("monday", VertexConverterOptions.NUMERIC_FORMAT);
		calendarConverters.put("tuesday", VertexConverterOptions.NUMERIC_FORMAT);
		calendarConverters.put("wednesday", VertexConverterOptions.NUMERIC_FORMAT);
		calendarConverters.put("thursday", VertexConverterOptions.NUMERIC_FORMAT);
		calendarConverters.put("friday", VertexConverterOptions.NUMERIC_FORMAT);
		calendarConverters.put("saturday", VertexConverterOptions.NUMERIC_FORMAT);
		calendarConverters.put("sunday", VertexConverterOptions.NUMERIC_FORMAT);
		Optional<Map<String, VertexConverterOptions>> calendarConvertersOpt = Optional.of(calendarConverters);
		
		GTFSFeedConfigVertex calendar = 
				new GTFSFeedConfigVertex
				(
						"calendar.txt",
						calendarConvertersOpt,
						calendarRequiredColumns

				);
		
		// calendar_dates vertex
		Set<String> calendarDatesRequiredColumns = new HashSet<String>();
		calendarRequiredColumns.add("service_id");
		calendarRequiredColumns.add("date");
		calendarRequiredColumns.add("exception_type");

			
		Map<String, VertexConverterOptions> calendarDatesConverters = new HashMap<String, VertexConverterOptions>();
		calendarConverters.put("date", VertexConverterOptions.DATE_FORMAT);
		calendarConverters.put("exception_type", VertexConverterOptions.NUMERIC_FORMAT);
		Optional<Map<String, VertexConverterOptions>> calendarDatesConvertersOpt = Optional.of(calendarDatesConverters);
		
		GTFSFeedConfigVertex calendarDates = 
				new GTFSFeedConfigVertex
				(
						"calendar_dates.txt",
						calendarDatesConvertersOpt,
						calendarDatesRequiredColumns

				);
		
		// fare_attributes vertex
		Set<String> fareAttributesRequiredColumns = new HashSet<String>();
		fareAttributesRequiredColumns.add("fare_id");
		fareAttributesRequiredColumns.add("price");
		fareAttributesRequiredColumns.add("currency_type");
		fareAttributesRequiredColumns.add("payment_method");
		fareAttributesRequiredColumns.add("transfers");
			
		Map<String, VertexConverterOptions> fareAttributesConverters = new HashMap<String, VertexConverterOptions>();
		fareAttributesConverters.put("price", VertexConverterOptions.NUMERIC_FORMAT);
		fareAttributesConverters.put("payment_method", VertexConverterOptions.NUMERIC_FORMAT);
		fareAttributesConverters.put("transfer_duration", VertexConverterOptions.NUMERIC_FORMAT);
		Optional<Map<String, VertexConverterOptions>> fareAttributesConvertersOpt = Optional.of(fareAttributesConverters);
		
		GTFSFeedConfigVertex fareAttributes = 
				new GTFSFeedConfigVertex
				(
						"fare_attributes.txt",
						fareAttributesConvertersOpt,
						fareAttributesRequiredColumns

				);
		
		// fare_rules vertex
		Set<String> fareRulesRequiredColumns = new HashSet<String>();
		fareRulesRequiredColumns.add("fare_id");
		
		GTFSFeedConfigVertex fareRules = 
				new GTFSFeedConfigVertex
				(
						"fare_rules.txt",
						Optional.empty(),
						fareRulesRequiredColumns

				);
		
		// feed_info vertex
		Set<String> feedInfoRequiredColumns = new HashSet<String>();
		feedInfoRequiredColumns.add("feed_publisher_name");
		feedInfoRequiredColumns.add("feed_publisher_url");
		feedInfoRequiredColumns.add("feed_lang");
		
		Map<String, VertexConverterOptions> feedInfoConverters = new HashMap<String, VertexConverterOptions>();
		feedInfoConverters.put("feed_start_date", VertexConverterOptions.DATE_FORMAT);
		feedInfoConverters.put("feed_end_date", VertexConverterOptions.DATE_FORMAT);
		Optional<Map<String, VertexConverterOptions>> feedInfoConvertersOpt = Optional.of(feedInfoConverters);

		GTFSFeedConfigVertex feedInfo = 
				new GTFSFeedConfigVertex
				(
						"feed_info.txt",
						feedInfoConvertersOpt,
						feedInfoRequiredColumns

				);
		
		// frequencies vertex
		Set<String> frequenciesRequiredColumns = new HashSet<String>();
		frequenciesRequiredColumns.add("trip_id");
		frequenciesRequiredColumns.add("start_time");
		frequenciesRequiredColumns.add("end_time");
		frequenciesRequiredColumns.add("headway_secs");
			
		Map<String, VertexConverterOptions> frequenciesConverters = new HashMap<String, VertexConverterOptions>();
		frequenciesConverters.put("headway_secs", VertexConverterOptions.NUMERIC_FORMAT);
		frequenciesConverters.put("exact_times", VertexConverterOptions.NUMERIC_FORMAT);
		frequenciesConverters.put("start_time", VertexConverterOptions.NUMERIC_FORMAT);
		frequenciesConverters.put("end_time", VertexConverterOptions.NUMERIC_FORMAT);		
		Optional<Map<String, VertexConverterOptions>> frequenciesConvertersOpt = Optional.of(frequenciesConverters);
		
		GTFSFeedConfigVertex frequencies = 
				new GTFSFeedConfigVertex
				(
						"frequencies.txt",
						frequenciesConvertersOpt,
						frequenciesRequiredColumns

				);
		
		// routes vertex
		
		Set<String> routesRequiredColumns = new HashSet<String>();
		routesRequiredColumns.add("route_id");
		routesRequiredColumns.add("route_short_name");
		routesRequiredColumns.add("route_long_name");
		routesRequiredColumns.add("route_type");
		
		Map<String, VertexConverterOptions> routesConverters = new HashMap<String, VertexConverterOptions>();
		routesConverters.put("route_type", VertexConverterOptions.NUMERIC_FORMAT);
		Optional<Map<String, VertexConverterOptions>> routesConvertersOpt = Optional.of(routesConverters);

		GTFSFeedConfigVertex routes = 
				new GTFSFeedConfigVertex
				(
						"routes.txt",
						routesConvertersOpt,
						routesRequiredColumns

				);
		
		// shapes vertex
		
		Set<String> shapesRequiredColumns = new HashSet<String>();
		shapesRequiredColumns.add("shape_id");
		shapesRequiredColumns.add("shape_pt_lat");
		shapesRequiredColumns.add("shape_pt_lon");
		shapesRequiredColumns.add("shape_pt_sequence");
		
		Map<String, VertexConverterOptions> shapesConverters = new HashMap<String, VertexConverterOptions>();
		shapesConverters.put("shape_id", VertexConverterOptions.NUMERIC_FORMAT);
		shapesConverters.put("shape_pt_lat", VertexConverterOptions.NUMERIC_FORMAT);
		shapesConverters.put("shape_pt_lon", VertexConverterOptions.NUMERIC_FORMAT);
		shapesConverters.put("shape_pt_sequence", VertexConverterOptions.NUMERIC_FORMAT);
		Optional<Map<String, VertexConverterOptions>> shapesConvertersOpt = Optional.of(shapesConverters);

		GTFSFeedConfigVertex shapes = 
				new GTFSFeedConfigVertex
				(
						"shapes.txt",
						routesConvertersOpt,
						shapesRequiredColumns

				);
		
		// stops vertex
		
		Set<String> stopsRequiredColumns = new HashSet<String>();
		stopsRequiredColumns.add("stop_id");
		stopsRequiredColumns.add("stop_name");
		stopsRequiredColumns.add("stop_lat");
		stopsRequiredColumns.add("stop_lon");
		
		Map<String, VertexConverterOptions> stopsConverters = new HashMap<String, VertexConverterOptions>();
		stopsConverters.put("stop_lat", VertexConverterOptions.NUMERIC_FORMAT);
		stopsConverters.put("stop_lon", VertexConverterOptions.NUMERIC_FORMAT);
		stopsConverters.put("location_type", VertexConverterOptions.NUMERIC_FORMAT);
		stopsConverters.put("wheelchair_boarding", VertexConverterOptions.NUMERIC_FORMAT);
		stopsConverters.put("pickup_type", VertexConverterOptions.NUMERIC_FORMAT);
		stopsConverters.put("drop_off_type", VertexConverterOptions.NUMERIC_FORMAT);
		stopsConverters.put("shape_dist_traveled", VertexConverterOptions.NUMERIC_FORMAT);
		stopsConverters.put("timepoint", VertexConverterOptions.NUMERIC_FORMAT);
		Optional<Map<String, VertexConverterOptions>> stopsConvertersOpt = Optional.of(stopsConverters);

		GTFSFeedConfigVertex stops = 
				new GTFSFeedConfigVertex
				(
						"stops.txt",
						stopsConvertersOpt,
						stopsRequiredColumns

				);
		
		// stop_times vertex
		
		Set<String> stopTimesRequiredColumns = new HashSet<String>();
		stopTimesRequiredColumns.add("trip_id");
		stopTimesRequiredColumns.add("arrival_time");
		stopTimesRequiredColumns.add("departure_time");
		stopTimesRequiredColumns.add("stop_id");
		stopTimesRequiredColumns.add("stop_sequence");

		Map<String, VertexConverterOptions> stopTimesConverters = new HashMap<String, VertexConverterOptions>();
		stopTimesConverters.put("arrival_time", VertexConverterOptions.TIME_FORMAT);
		stopTimesConverters.put("departure_time", VertexConverterOptions.TIME_FORMAT);
		stopTimesConverters.put("pickup_type", VertexConverterOptions.NUMERIC_FORMAT);
		stopTimesConverters.put("shape_dist_traveled", VertexConverterOptions.NUMERIC_FORMAT);
		stopTimesConverters.put("stop_sequence", VertexConverterOptions.NUMERIC_FORMAT);
		stopTimesConverters.put("timepoint", VertexConverterOptions.NUMERIC_FORMAT);
		Optional<Map<String, VertexConverterOptions>> stopTimesConvertersOpt = Optional.of(stopTimesConverters);

		GTFSFeedConfigVertex stopTimes = 
				new GTFSFeedConfigVertex
				(
						"stop_times.txt",
						stopTimesConvertersOpt,
						stopTimesRequiredColumns

				);
		
		// transfers vertex
		
		Set<String> transfersRequiredColumns = new HashSet<String>();
		transfersRequiredColumns.add("from_stop_id");
		transfersRequiredColumns.add("to_stop_id");
		transfersRequiredColumns.add("transfer_type");
		
		Map<String, VertexConverterOptions> transfersConverters = new HashMap<String, VertexConverterOptions>();
		transfersConverters.put("transfer_type", VertexConverterOptions.NUMERIC_FORMAT);
		transfersConverters.put("min_transfer_time", VertexConverterOptions.NUMERIC_FORMAT);
		Optional<Map<String, VertexConverterOptions>> transfersConvertersOpt = Optional.of(transfersConverters);

		GTFSFeedConfigVertex transfers = 
				new GTFSFeedConfigVertex
				(
						"transfers.txt",
						transfersConvertersOpt,
						transfersRequiredColumns

				);
		
		// trips vertex
		
		Set<String> tripsRequiredColumns = new HashSet<String>();
		tripsRequiredColumns.add("route_id");
		tripsRequiredColumns.add("service_id");
		tripsRequiredColumns.add("trip_id");
		
		Map<String, VertexConverterOptions> tripsConverters = new HashMap<String, VertexConverterOptions>();
		tripsConverters.put("direction_id", VertexConverterOptions.NUMERIC_FORMAT);
		tripsConverters.put("wheelchair_accessible", VertexConverterOptions.NUMERIC_FORMAT);
		tripsConverters.put("bikes_allowed", VertexConverterOptions.NUMERIC_FORMAT);
		Optional<Map<String, VertexConverterOptions>> tripsConvertersOpt = Optional.of(tripsConverters);

		GTFSFeedConfigVertex trips = 
				new GTFSFeedConfigVertex
				(
						"trips.txt",
						tripsConvertersOpt,
						tripsRequiredColumns

				);
		
		/////////////// update config and return
		config.put("agency.txt", agency);
		config.put("calendar.txt", calendar);
		config.put("calendar_dates.txt", calendarDates);
		config.put("fare_attributes.txt", fareAttributes);
		config.put("fare_rules.txt", fareRules);
		config.put("frequencies.txt", frequencies);
		config.put("routes.txt", routes);
		config.put("shapes.txt", shapes);
		config.put("stops.txt", stops);
		config.put("stop_times.txt", stopTimes);
		config.put("transfers.txt", transfers);
		config.put("trips.txt", trips);
		
		return config;
		
	}
	
	private static Graph<GTFSFeedConfigVertex, GTFSFeedConfigEdge> buildGTFSFeedGraphEdges(
			Graph<GTFSFeedConfigVertex, GTFSFeedConfigEdge> g,
			Map<String, GTFSFeedConfigVertex> verticesConfig) {
	
		
		//build the edges dependencies
		List<Map<String, String>> agency_routes_dependencies = new ArrayList<Map<String, String>>();
		Map<String, String> agency_routes_dependencies_hm1 = new HashMap<String, String>();
		agency_routes_dependencies_hm1.put("agency.txt", "agency_id");
		agency_routes_dependencies_hm1.put("routes.txt", "agency_id");
		agency_routes_dependencies.add(agency_routes_dependencies_hm1);
		GTFSFeedConfigEdge agency_routes_edge = new GTFSFeedConfigEdge(agency_routes_dependencies);
		
		List<Map<String, String>> calendar_trips_dependencies = new ArrayList<Map<String, String>>();
		Map<String, String> calendar_trips_dependencies_hm1 = new HashMap<String, String>();
		calendar_trips_dependencies_hm1.put("calendar.txt", "service_id");
		calendar_trips_dependencies_hm1.put("trips.txt", "service_id");
		calendar_trips_dependencies.add(calendar_trips_dependencies_hm1);
		GTFSFeedConfigEdge calendar_trips_edge = new GTFSFeedConfigEdge(calendar_trips_dependencies);
		
		List<Map<String, String>> calendar_dates_trips_dependencies = new ArrayList<Map<String, String>>();
		Map<String, String> calendar_dates_trips_dependencies_hm1 = new HashMap<String, String>();
		calendar_dates_trips_dependencies_hm1.put("calendar_dates.txt", "service_id");
		calendar_dates_trips_dependencies_hm1.put("trips.txt", "service_id");
		calendar_dates_trips_dependencies.add(calendar_dates_trips_dependencies_hm1);
		GTFSFeedConfigEdge calendar_dates_trips_edge = new GTFSFeedConfigEdge(calendar_dates_trips_dependencies);
		
		List<Map<String, String>> fare_attributes_fare_rules_dependencies = new ArrayList<Map<String, String>>();
		Map<String, String> fare_attributes_fare_rules_dependencies_hm1 = new HashMap<String, String>();
		fare_attributes_fare_rules_dependencies_hm1.put("fare_attributes.txt", "fare_id");
		fare_attributes_fare_rules_dependencies_hm1.put("fare_rules.txt", "fare_id");
		fare_attributes_fare_rules_dependencies.add(fare_attributes_fare_rules_dependencies_hm1);
		GTFSFeedConfigEdge fare_attributes_fare_rules_edge = new GTFSFeedConfigEdge(fare_attributes_fare_rules_dependencies);
		
		List<Map<String, String>> fare_rules_stops_dependencies = new ArrayList<Map<String, String>>();
		Map<String, String> fare_rules_stops_dependencies_hm1 = new HashMap<String, String>();
		Map<String, String> fare_rules_stops_dependencies_hm2 = new HashMap<String, String>();
		Map<String, String> fare_rules_stops_dependencies_hm3 = new HashMap<String, String>();
		fare_rules_stops_dependencies_hm1.put("fare_rules.txt", "origin_id");
		fare_rules_stops_dependencies_hm1.put("stops.txt", "zone_id");
		fare_rules_stops_dependencies_hm2.put("fare_rules.txt", "destination_id");
		fare_rules_stops_dependencies_hm2.put("stops.txt", "zone_id");
		fare_rules_stops_dependencies_hm3.put("fare_rules.txt", "contains_id");
		fare_rules_stops_dependencies_hm3.put("stops.txt", "zone_id");
		fare_rules_stops_dependencies.add(fare_rules_stops_dependencies_hm1);
		fare_rules_stops_dependencies.add(fare_rules_stops_dependencies_hm2);
		fare_rules_stops_dependencies.add(fare_rules_stops_dependencies_hm3);
		GTFSFeedConfigEdge fare_rules_stops_edge = new GTFSFeedConfigEdge(fare_rules_stops_dependencies);
		
		List<Map<String, String>> fare_rules_routes_dependencies = new ArrayList<Map<String, String>>();
		Map<String, String> fare_rules_routes_dependencies_hm1 = new HashMap<String, String>();
		fare_rules_routes_dependencies_hm1.put("fare_rules.txt", "route_id");
		fare_rules_routes_dependencies_hm1.put("routes.txt", "route_id");
		fare_rules_routes_dependencies.add(fare_rules_routes_dependencies_hm1);
		GTFSFeedConfigEdge fare_rules_routes_edge = new GTFSFeedConfigEdge(fare_rules_routes_dependencies);
		
		List<Map<String, String>> frequencies_trips_dependencies = new ArrayList<Map<String, String>>();
		Map<String, String> frequencies_trips_dependencies_hm1 = new HashMap<String, String>();
		frequencies_trips_dependencies_hm1.put("frequencies.txt", "trip_id");
		frequencies_trips_dependencies_hm1.put("trips.txt", "trip_id");
		frequencies_trips_dependencies.add(frequencies_trips_dependencies_hm1);
		GTFSFeedConfigEdge frequencies_trips_edge = new GTFSFeedConfigEdge(frequencies_trips_dependencies);
		
		List<Map<String, String>> routes_trips_dependencies = new ArrayList<Map<String, String>>();
		Map<String, String> routes_trips_dependencies_hm1 = new HashMap<String, String>();
		routes_trips_dependencies_hm1.put("routes.txt", "route_id");
		routes_trips_dependencies_hm1.put("trips.txt", "route_id");
		routes_trips_dependencies.add(routes_trips_dependencies_hm1);
		GTFSFeedConfigEdge routes_trips_edge = new GTFSFeedConfigEdge(routes_trips_dependencies);
		
		List<Map<String, String>> shapes_trips_dependencies = new ArrayList<Map<String, String>>();
		Map<String, String> shapes_trips_dependencies_hm1 = new HashMap<String, String>();
		shapes_trips_dependencies_hm1.put("shapes.txt", "shape_id");
		shapes_trips_dependencies_hm1.put("trips.txt", "shape_id");
		shapes_trips_dependencies.add(shapes_trips_dependencies_hm1);
		GTFSFeedConfigEdge shapes_trips_edge = new GTFSFeedConfigEdge(shapes_trips_dependencies);
		
		List<Map<String, String>> stops_stop_times_dependencies = new ArrayList<Map<String, String>>();
		Map<String, String> stops_stop_times_dependencies_hm1 = new HashMap<String, String>();
		stops_stop_times_dependencies_hm1.put("stops.txt", "stop_id");
		stops_stop_times_dependencies_hm1.put("stop_times.txt", "stop_id");
		stops_stop_times_dependencies.add(stops_stop_times_dependencies_hm1);
		GTFSFeedConfigEdge stops_stop_times_edge = new GTFSFeedConfigEdge(stops_stop_times_dependencies);
		
		List<Map<String, String>> stop_times_trips_dependencies = new ArrayList<Map<String, String>>();
		Map<String, String> stop_times_trips_dependencies_hm1 = new HashMap<String, String>();
		stop_times_trips_dependencies_hm1.put("stop_times.txt", "trip_id");
		stop_times_trips_dependencies_hm1.put("trips.txt", "trip_id");
		stop_times_trips_dependencies.add(stops_stop_times_dependencies_hm1);
		GTFSFeedConfigEdge stop_times_trips_edge = new GTFSFeedConfigEdge(stop_times_trips_dependencies);
		
		List<Map<String, String>> transfers_stops_dependencies = new ArrayList<Map<String, String>>();
		Map<String, String> transfers_stops_dependencies_hm1 = new HashMap<String, String>();
		Map<String, String> transfers_stops_dependencies_hm2 = new HashMap<String, String>();
		transfers_stops_dependencies_hm1.put("transfers.txt", "from_stop_id");
		transfers_stops_dependencies_hm1.put("stops.txt", "stop_id");
		transfers_stops_dependencies_hm2.put("transfers.txt", "to_stop_id");
		transfers_stops_dependencies_hm2.put("stops.txt", "stop_id");
		transfers_stops_dependencies.add(transfers_stops_dependencies_hm1);
		transfers_stops_dependencies.add(transfers_stops_dependencies_hm2);
		GTFSFeedConfigEdge transfers_stops_edge = new GTFSFeedConfigEdge(transfers_stops_dependencies);
		
		//add the edges
		g.addEdge(verticesConfig.get("agency.txt"), verticesConfig.get("routes.txt"), agency_routes_edge);
		g.addEdge(verticesConfig.get("calendar.txt"), verticesConfig.get("trips.txt"), calendar_trips_edge);
		g.addEdge(verticesConfig.get("calendar_dates.txt"), verticesConfig.get("trips.txt"), calendar_dates_trips_edge);
		g.addEdge(verticesConfig.get("fare_attributes.txt"), verticesConfig.get("fare_rules.txt"), fare_attributes_fare_rules_edge);
		g.addEdge(verticesConfig.get("fare_rules.txt"), verticesConfig.get("stops.txt"), fare_rules_stops_edge);
		g.addEdge(verticesConfig.get("fare_rules.txt"), verticesConfig.get("routes.txt"), fare_rules_routes_edge);
		g.addEdge(verticesConfig.get("frequencies.txt"), verticesConfig.get("trips.txt"), frequencies_trips_edge);
		g.addEdge(verticesConfig.get("routes.txt"), verticesConfig.get("trips.txt"), routes_trips_edge);
		g.addEdge(verticesConfig.get("shapes.txt"), verticesConfig.get("trips.txt"), shapes_trips_edge);
		g.addEdge(verticesConfig.get("stops.txt"), verticesConfig.get("stop_times.txt"), stops_stop_times_edge);
		g.addEdge(verticesConfig.get("stop_times.txt"), verticesConfig.get("trips.txt"), stop_times_trips_edge);
		g.addEdge(verticesConfig.get("transfers.txt"), verticesConfig.get("stops.txt"), transfers_stops_edge);

		//return graph
		return g;
	}
	
	public static ColumnType[] getColumnType(String tablename) {
		
		ColumnType[] types;
		
		switch(tablename) {
		case "agency":
			types = ColumnType[]{ColumnType., ColumnType.STRING};
			break;
			
		case "calendar":
			types = {};
			break;
			
		case "calendar_dates":
			types = {};
			break;
			
		case "fare_attributes":
			types = {};
			break;
			
		case "fare_rules":
			types = {};
			break;
			
		case "frequencies":
			types = {};
			break;
			
		case "routes":
			types = {};
			break;
			
		case "shapes":
			types = {};
			break;
			
		case "stops":
			types = {};
			break;
			
		case "stop_times":
			types = {};
			break;
			
		case "transfers":
			types = {};
			break;
			
		case "trips":
			types = {};
			break;
			
		case default:
			break
			
		}
		
		return types;
	}
}
