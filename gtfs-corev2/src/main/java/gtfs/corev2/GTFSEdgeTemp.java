package gtfs.corev2;

import org.jgrapht.graph.DefaultEdge;

public class GTFSEdgeTemp extends DefaultEdge {
	private String toStopId;
	private String fromStopId;
	private int duration;
	private int debut;
	private int lastEncounter;
	private int encountersAvg;
	private int encounters;
	private int end;
	private int counter;
	private EdgeType type;
	
	
	public GTFSEdgeTemp(
			String toStopId,
			String fromStopId,
			EdgeType type,
			int duration,
			int debut,
			int lastEncounter,
			int encountersAvg,
			int encounters,
			int end,
			int counter) {
		
		this.toStopId = toStopId;
		this.fromStopId = fromStopId;
		this.duration = duration;
		this.debut = debut;
		this.lastEncounter = lastEncounter;
		this.encountersAvg = encountersAvg;
		this.encounters = encounters;
		this.end = end;
		this.type = type;
		
	}
	
	public String getToStopId() {
		return toStopId;
	}
	public void setToStopId(String toStopId) {
		this.toStopId = toStopId;
	}
	public String getFromStopId() {
		return fromStopId;
	}
	public void setFromStopId(String fromStopId) {
		this.fromStopId = fromStopId;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public int getDebut() {
		return debut;
	}
	public void setDebut(int debut) {
		this.debut = debut;
	}
	public int getLastEncounter() {
		return lastEncounter;
	}
	public void setLastEncounter(int lastEncounter) {
		this.lastEncounter = lastEncounter;
	}
	public int getEncountersAvg() {
		return encountersAvg;
	}
	public void setEncountersAvg(int encountersAvg) {
		this.encountersAvg = encountersAvg;
	}
	public int getEncounters() {
		return encounters;
	}
	public void setEncounters(int encounters) {
		this.encounters = encounters;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}
	public EdgeType getType() {
		return type;
	}
	public void setType(EdgeType type) {
		this.type = type;
	}
	
	@Override
	public GTFSEdgeTemp getSource() {
		return getSource(); 
	}
	
	@Override
	public GTFSEdgeTemp getTarget() {
		return getTarget(); 
	}
	
}
