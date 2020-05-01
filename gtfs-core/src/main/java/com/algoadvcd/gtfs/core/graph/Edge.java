package com.algoadvcd.gtfs.core.graph;

import com.algoadvcd.gtfs.core.*;

public class Edge extends BaseEdge{
	
    private static final long serialVersionUID = 1L;

	protected Object getSource() {
        return source;
    }

    protected Object getTarget() {
        return target;
    }

    @Override
    public String toString()
    {
        return "(" + source + " : " + target + ")";
    }
}
