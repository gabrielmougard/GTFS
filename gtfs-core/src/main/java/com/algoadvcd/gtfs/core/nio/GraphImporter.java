package com.algoadvcd.gtfs.core.nio;

import java.io.*;
import java.nio.charset.*;

import com.algoadvcd.gtfs.core.Graph;

public interface GraphImporter<GTFSVertex> {
	
	void importGraphLocal(Graph<GTFSVertex> g, String target);

	void importGraphRemote(Graph<GTFSVertex> g, String target);

}
