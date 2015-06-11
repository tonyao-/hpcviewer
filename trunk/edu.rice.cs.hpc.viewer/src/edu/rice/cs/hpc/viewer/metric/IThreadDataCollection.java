package edu.rice.cs.hpc.viewer.metric;

import java.io.IOException;

public interface IThreadDataCollection 
{
	public void 	open(String directory) throws IOException;
	
	public boolean 	isAvailable();
	public double[]	getRankLabels();	
	public int 		getParallelismLevel();
	public String   getRankTitle();
	
	public double[] getMetrics(long nodeIndex, int metricIndex, int numMetrics) 
			throws IOException;

	public void		dispose();
}
