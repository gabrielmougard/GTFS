# GTFS-visualizer

A very simple overview on how to extract interesting data from `stops.txt` (from the GTFS standard specification).

The gathered data is a map of the MBTA (Massachusetts Bay Transportation Authority).

## Run the script

`pip3 install networkx` and `pip3 install matplotlib` if not already installed.
then `python3 visualizer.py` 

## Further resources

* https://github.com/rovaniemi/osm-graph-parser (java osm xml reader in JSON to ingest data)
* https://www.researchgate.net/profile/Catherine_Morency/publication/310446593_Innovative_GTFS_Data_Application_for_Transit_Network_Analysis_Using_a_Graph-Oriented_Method/links/59518088aca272a343d7fdcc/Innovative-GTFS-Data-Application-for-Transit-Network-Analysis-Using-a-Graph-Oriented-Method.pdf

* https://github.com/remix/partridge (convert GTFS dataset to unique dataframe) -> use of `tablesaw` java lib instead of python pandas for dataframe
* https://github.com/kuanb/peartree (convert GTFS dataframe into graph datastructure)