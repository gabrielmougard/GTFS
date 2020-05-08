package com.algoadvcd.gtfs.core.nio.gtfs2feed;

import java.io.*;
import java.nio.charset.*;

import com.algoadvcd.gtfs.core.Graph;

public interface GraphImporter<GTFSVertex> {
	
	void importGraphLocal(Graph<GTFSVertex> g, String target);

	void importGraphRemote(Graph<GTFSVertex> g, String target);

}
