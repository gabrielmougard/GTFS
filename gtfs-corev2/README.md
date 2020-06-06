# gtfs-core documentation

## How to build a GTFS graph from local resources 
```java
import org.jgrapht.Graph;
import gtfs.corev2.GTFSVertex;
import gtfs.corev2.GTFSEdge;

Graph<GTFSVertex, GTFSEdge> g = 
    new GTFSGraphBuilder("<DATASET_FOLDER>") 
    .localDataset()
    .build();
```

For example, if your project is like this :

```
src/
|   main/
|   |   java/...  
|   |   resources/
|   |   |   mbta/
|   |   |   | edges.txt        (MANDATORY)
|   |   |   | routes.txt       (MANDATORY)
|   |   |   | stop_times.txt   (MANDATORY)
|   |   |   | stops.txt        (MANDATORY)
|   |   |   | transfers.txt    (MANDATORY)
|   |   |   | trips.txt        (MANDATORY)
|   |   |   ratp/
|   |   |   |   .
|   |   |   |   .
|   |   |   |   .
    
```

you can replace `<DATASET_FOLDER>` by `mbta` and it will compute the static GTFS graph (a simplified version).

## How to build a GTFS graph from remote resources

In our case we have set up a Google Cloud Platform (GCP) bucket with the same filesystem structure than above. If you want to use your own bucket, put your `.json` access file at the root of the resources folder. Please rename it `gtfs-bucket-access.json`

Example : 

```
src/
|   main/
|   |   java/...  
|   |   resources/
|   |   |   mbta/
|   |   |   ratp/
|   |   |   |   .
|   |   |   |   .
|   |   |   |   .
|   |   |   gtfs-bucket-access.json
```

The `.json`should look like this :

```json
{
  "type": ...,
  "project_id": ...,
  "private_key_id": ...,
  "private_key": ...,
  "client_email": ...,
  "client_id": ...,
  "auth_uri": ...,
  "token_uri": ...,
  "auth_provider_x509_cert_url": ...,
  "client_x509_cert_url": ...
}
```

Then you can load your GTFS feed graph like this :
```java
Graph<GTFSVertex, GTFSEdge> g = 
    new GTFSGraphBuilder("<DATASET_FOLDER>") 
    .remoteDataset()
    .build();
```

## Serialization/Unserialization

Loading a GTFS graph with the `GTFSGraphBuilder` can be quite time consuming (especially the remote option since the dataset can be several dozens or hundreds of Mo).

We then implemented a way of serializing the graph under a `.json` format for the next time it has to be computed. 

```java
// get the graph from local raw dataset (around 3 to 4 seconds)
Graph<GTFSVertex, GTFSEdge> g = 
    new GTFSGraphBuilder("<DATASET_FOLDER>") 
    .localDataset()
    .build();

// serialize it to local resources (some microseconds)
GraphSerializer gs = 
    new GraphSerializerBuilder("mbta")
    .localSerializer()
    .build();        
gs.serialize(g);

// serialize it to GCP bucket(some milliseconds to one second according to your internet upload speed)
gs = new GraphSerializerBuilder("mbta")
    .remoteSerializer()
    .build();
gs.serialize(g);

/*
 * Now that the data has been serialized if we want to compute 
 * the graph after the runtime, we can unserialize it.
 * 
 * in local : we go from 4 seconds to microseconds
 * in remote : we go from several minutes (for a big dataset like mbta) to 1 second ! 
 */

//local unserialization
gs = new GraphSerializerBuilder("mbta")
    .localSerializer()
    .build();
Graph<GTFSVertex, GTFSEdge> g2 = gs.unserialize();

//remote unserialization
gs = new GraphSerializerBuilder("mbta")
    .remoteSerializer()
    .build();
Graph<GTFSVertex, GTFSEdge> g3 = gs.unserialize();
```

## Computing BFS/Dijkstra shortest paths

```java
import gtfs.corev2.algorithms.shortestpath.BFS;
import gtfs.corev2.algorithms.shortestpath.Dijkstra;

//BFS
BFS<GTFSVertex, GTFSEdge> bfs = new BFS<GTFSVertex, GTFSEdge>(g);
List<GTFSVertex> path = bfs.getPath(<START_GTFS_VERTEX>, <TARGET_GTFS_VERTEX>).getVertexList();

//DIJKSTRA
Dijkstra<GTFSVertex, GTFSEdge> dijkstra = new Dijkstra<GTFSVertex, GTFSEdge>(g);
List<GTFSVertex> path = dijkstra.getPath(<START_GTFS_VERTEX>, <TARGET_GTFS_VERTEX>).getVertexList();
```

go look at `gtfs.corev2.BFSFrame.java` and `gtfs.corev2.DijkstraFrame.java` to see how the shortest paths are computed and shown in Swing JFrames.

## Clustering

If a given graph is too large, maybe it's time to clusterize it ! Here is how it works :

```java
import org.jgrapht.Graph;

import gtfs.corev2.algorithms.clustering.ClusteringAlgo.Clustering;
import gtfs.corev2.algorithms.clustering.GTFSClusterEdge;
import gtfs.corev2.algorithms.clustering.GTFSClusterVertex;
import gtfs.corev2.algorithms.clustering.KSpanningTreeClustering;

Graph<GTFSVertex, GTFSEdge> g = 
    new GTFSGraphBuilder("mbta")
    .localDataset()
    .build();
        
//We want to convert the graph into its 20 most significant clusters.
//The number of clusters, let's call it k should respect the following condition : 1 <= k <= g.vertexSet().size()
KSpanningTreeClustering c = new KSpanningTreeClustering(g, 20);
Clustering<GTFSVertex> clusters = c.getClustering();
System.out.println(clusters.toString());
		
//convert the clusters in disjoint-sets as a graph 
Graph<GTFSClusterVertex, GTFSClusterEdge> gCluster = clusters.convertClustersAsGraph();
		
//check graph information
System.out.println("The cluster graph has : "+gCluster.vertexSet().size()+" vertices.");
System.out.println("The cluster graph has : "+gCluster.edgeSet().size()+" edges.");
```


## Launch the demo

Launch `gtfs.corev2.PathVisualizer.java` to see the two JFrames.

If you want to look at the complte GTFS graph (the mbta network in our case), uncomment the `MainFrame` object in `PathVisualizer.java`. This operation takes a lot of times since the graph is huge (8.5K vertices for 11K edges)