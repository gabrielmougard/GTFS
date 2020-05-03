import csv
import networkx as nx
from station import Station
import itertools
import matplotlib.pyplot as plt 

def create_stations(filename='data/stops.txt'):
    """
    Reads in lines of stops GTFS file and creates stops objects
    parameters: 
        filename - string, the filename of the GTFS stops file
    returns:
        list of stations in the file, as Station objects
    """
    with open(filename, newline='') as stops:
        station_info = csv.reader(stops) 
        # skip first line, which is just attribute names
        next(station_info)
        # empty list to hold stations
        stations = []
        # parse all the lines and create stations
        for row in station_info:
            # assign each attribute of Station based on line in file

            (stop_id, stop_code, stop_name, stop_desc, platform_code, platform_name,\
                stop_lat, stop_lon, zone_id, stop_address, stop_url, level_id,\
                location_type, parent_station, wheelchair_boarding, municipality, on_street, at_street,vehicle_type) = tuple(row)
            # create Station instance with those attributes
            try:
                stations.append(Station(stop_id, stop_code, stop_name, stop_desc, \
                    platform_code, platform_name, stop_lat, stop_lon, stop_address, \
                    zone_id, stop_url, level_id, location_type, parent_station,\
                    wheelchair_boarding, municipality, on_street, at_street,vehicle_type))
            except ValueError:
                print("Value error at row : "+str(row)+"\n")
                
    return stations

def build_station_network(filename='data/edges.txt'):
    """
    Builds network of T stations
    parameters:
        filename, edge info file name (string)
    returns: 
        networkx graph of stations
    """
    # get lon, lat locations
    locations = {} # location dictionary
    for station in create_stations():
        locations[station.get_id()] = station.get_coordinates()
    
    # initialize networkx graph
    t_map = nx.Graph()
    
    # load in edges from 't_edges.txt'
    with open(filename) as t:
         # read in all lines 
         edges = t.readlines()
         # iterate over edges and add all nodes and edges to graph
         for line in edges:
             (source, destination, time, color) = tuple(line.split(','))
             # get rid of newline char at end of color string
             color = color.replace('\n','') 
             # add edge to graph with color attribute and time as weight
             t_map.add_edge(source, destination, weight=float(time), color=color) 
    
    # return graph
    return t_map

def draw_network(G, edges, color='black', new_plot=True, title='Map of the network', label=None):
    '''
    Creates plot of network with geographically accurate station positions
    parameters:
        G, the networkx graph of the network
        color, dict of colors to use for edges (black by default)
        new_plot, True if starting a new plot, False if adding to existing plot
        title, the title to display on the plot ('Map of T' is default)
        label, dict of labels to use for nodes (default is no labeling)
    returns plot
    '''
    # get lon, lat locations
    locations = {} # location dictionary
    nodes = list(itertools.chain(*edges)) # get list of nodes
    for station in create_stations():
        locations[station.get_id()] = station.get_coordinates()
    
    # initialize new plot if specified
    if new_plot:
        plt.figure()
        plt.title(title)    
    # add labels if specified
    if label is not None:
        nx.draw_networkx_labels(G, pos=locations, labels=label, font_size=6)
    # draw edges with geographic positions and color corresponding to line
    nx.draw_networkx_edges(G, pos=locations, edge_color=color, edgelist=edges)
    # draw nodes based on geo positions
    nx.draw_networkx_nodes(G, pos=locations, node_color='black',\
                           node_size=20, nodelist=nodes)

    plt.show()

# graph of all stations and tracks between them                 
G_map = build_station_network()
# draw map of the T with colors corresponding to lines

edges_map,colors = zip(*nx.get_edge_attributes(G_map,'color').items())

draw_network(G_map,edges_map,color=colors)