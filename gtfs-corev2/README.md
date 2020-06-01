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

go look at `gtfs.corev2.BFSFrame.java` and `gtfs.corev2.DijkstraFrame.java` to see how the shortest paths are computed and shown in Swing JFrames.

## Launch the demo

Launch `gtfs.corev2.PathVisualizer.java` to see the two JFrames.

If you want to look at the complte GTFS graph (the mbta network in our case), uncomment the `MainFrame` object in `PathVisualizer.java`. This operation takes a lot of times since the graph is huge (8.5K vertices for 11K edges)