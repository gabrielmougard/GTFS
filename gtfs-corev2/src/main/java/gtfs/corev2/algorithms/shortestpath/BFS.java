package gtfs.corev2.algorithms.shortestpath;

import org.jgrapht.Graph;

import gtfs.corev2.GTFSEdge;
import gtfs.corev2.GTFSVertex;

import java.util.*;

public class BFS {
	private Graph<GTFSVertex, GTFSEdge> graph;
	private GTFSVertex src;
	private GTFSVertex dest;
	
	public BFS(Graph<GTFSVertex, GTFSEdge> graph, GTFSVertex src, GTFSVertex dest) {
        this.graph = graph;
        this.src = src;
        this.dest = dest;
    }
	
	public List<GTFSVertex> getPath() {
		//Initialization
		Map<GTFSVertex, GTFSVertex> nextVertexMap = new HashMap<GTFSVertex, GTFSVertex>();
		GTFSVertex currentVertex = this.src;
		GTFSVertex previousVertex = this.src;
		
		//Queue
		Queue<GTFSVertex> queue = new LinkedList<GTFSVertex>();
		queue.add(currentVertex);
		
		Set<GTFSVertex> visitedVertices = new HashSet<GTFSVertex>();
		visitedVertices.add(currentVertex);
		
		Set<GTFSVertex> neighbors = new HashSet<GTFSVertex>();
		while(!queue.isEmpty()) {
			currentVertex = queue.remove();
			if (currentVertex.equals(this.dest)) {
				if (!previousVertex.equals(currentVertex)) {
					nextVertexMap.put(previousVertex, currentVertex);
			    }
			    break;
			} else {
				
				try {
					neighbors = getNeighbors(currentVertex);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				for (GTFSVertex nextVertex : neighbors) {
					
					if (!visitedVertices.contains(nextVertex)) {
						queue.add(nextVertex);
						visitedVertices.add(nextVertex);
						
						//Look up of next vertex instead of previous
						nextVertexMap.put(currentVertex,  nextVertex);
						previousVertex = currentVertex;
					}
				}
			}
		}
		
		if (!currentVertex.equals(this.dest)) {
	        throw new RuntimeException("No feasible path.");
	    }
		
		//Reconstruct the path
		List<GTFSVertex> path = new LinkedList<GTFSVertex>();
		for (GTFSVertex v = this.src; v != null; v = nextVertexMap.get(v)) {
			path.add(v);
		}
		
		return path;
	}
	
	private Set<GTFSVertex> getNeighbors(GTFSVertex start) throws Exception {
		Set<GTFSVertex> res = new HashSet<GTFSVertex>();
		for (GTFSEdge e: this.graph.edgesOf(start)) {
			GTFSVertex source = e.getSource();
			GTFSVertex target = e.getTarget();
			GTFSVertex toBeAdded;
			// the graph is directed so source may be not equals to start
			if (source.equals(start)) {
				toBeAdded = target;
			} else if (target.equals(start)){
				toBeAdded = source;
			} else {
				//we have an error here...
				throw new Exception("We have an error on the edge.");
			}
			res.add(toBeAdded);
		}
		return res;
	}
	
	public ArrayList<GTFSVertex> doBFSShortestPath() throws Exception {
		ArrayList<GTFSVertex> shortestPathList = new ArrayList<GTFSVertex>();
		HashMap<GTFSVertex, Boolean> visited = new HashMap<GTFSVertex, Boolean>();
		
		if (this.src == this.dest)
			return null;
		Queue<GTFSVertex> queue = new LinkedList<GTFSVertex>();
		Stack<GTFSVertex> pathStack = new Stack<GTFSVertex>();

		queue.add(this.src);
		pathStack.add(this.src);
		visited.put(this.src, true);
		
		while(!queue.isEmpty()) {
			GTFSVertex u = queue.poll();

			Set<GTFSEdge> edges = this.graph.outgoingEdgesOf(u);
			List<GTFSVertex> neighbors = new ArrayList<GTFSVertex>();
			for (GTFSEdge e : edges) {
				neighbors.add(e.getTarget());
			}
			for(GTFSVertex v : neighbors) {
				if(!visited.containsKey(v)) {
					queue.add(v);
					visited.put(v, true);
					pathStack.add(v);
					if(u == this.dest) {
						break;
					}	
				}
			}
		}
		
		//find the path
		GTFSVertex node, currentSrc=this.dest;
		shortestPathList.add(this.dest);
		while(!pathStack.isEmpty()) {
			node = pathStack.pop();
			if (this.graph.containsEdge(currentSrc, node)) { //if node is a neighbor of currentSrc
				shortestPathList.add(node);
				currentSrc = node;
				if(node == this.src) {
					break;	
				}
			}
			
		}
		
		
		return shortestPathList;
	}
}