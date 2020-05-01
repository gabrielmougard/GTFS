package com.algoadvcd.gtfs.core.graph;

public class WeightedEdge extends BaseWeightedEdge {
	private static final long serialVersionUID = 2L;

    protected Object getSource() {
        return source;
    }

    protected Object getTarget() {
        return target;
    }

    protected double getWeight() {
        return weight;
    }

    @Override
    public String toString()
    {
        return "(" + source + " : " + target + ")";
    }
}
