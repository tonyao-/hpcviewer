package edu.rice.cs.hpc.traceviewer.data.db;

import java.util.ArrayList;
import java.util.List;

public class TimelineDataSet {

	final private List<BaseDataVisualization> list;
	final private int height;
	final private int linenum;
	
	public TimelineDataSet( int linenum, int initSize, int height ) {
	 	
		list = new ArrayList<BaseDataVisualization>(initSize);
	 	this.height = height; 
	 	this.linenum = linenum;
	}
	
	public void add( BaseDataVisualization data ) {
		list.add(data);
	}
	
	public List<BaseDataVisualization> getList() {
		return list;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getLineNumber() {
		return linenum;
	}
}
