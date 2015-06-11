package edu.rice.cs.hpc.viewer.metric;

import java.io.IOException;

import edu.rice.cs.hpc.data.experiment.Experiment;
import edu.rice.cs.hpc.data.experiment.metric.MetricRaw;

/***
 * manager class to handle raw metrics and its read to file
 * All access to raw metrics (aka thread level data) has to use this file
 * 
 *
 */
public class ThreadLevelDataManager {

	private IThreadDataCollection data_file;
	private Experiment experiment;

	public ThreadLevelDataManager(Experiment exp) throws IOException 
	{
		final MetricRaw []metrics = exp.getMetricRaw();
		if (metrics!=null) {
			int version = exp.getMajorVersion();
			String directory = exp.getDefaultDirectory().getAbsolutePath();
			
			switch(version)
			{
			case 1:
			case 2:
				data_file = new ThreadDataCollection2(exp);
				data_file.open(directory);
				break;
			case 3:
				data_file = new ThreadDataCollection3();
				data_file.open(directory);
				break;
			default:
				data_file = null;
				break;
			}
		}
		this.experiment = exp;
	}
	
	
	//==============================================================================================
	// PUBLIC METHODS
	//==============================================================================================

	/**
	 * check data availability
	 * @return true if the data is ready and available
	 */
	public boolean isDataAvailable() {
		return (data_file != null && data_file.isAvailable());
	}
	
	
	
	/**
	 * thread level data may contain some experiment instances. 
	 * This will retrieve the name of all instances
	 * @return
	 */
	public String[] getSeriesName() {
		MetricRaw []metrics_raw = experiment.getMetricRaw();

		if (metrics_raw == null)
			return null;
		
		String keys[] = new String[metrics_raw.length];
		for (int i=0; i<metrics_raw.length; i++)
			keys[i] = metrics_raw[i].getDisplayName();
		
		return keys;
	}
	
	
	
	
	/**
	 * get the list of processor IDs. The ID has to a number. Otherwise it throws an exception 
	 * 
	 * @param metric_raw_id
	 * @return
	 * @throws NumberFormatException (in case the processor ID is not a number)
	 */
	public double[] getProcessIDsDouble(int metric_raw_id) throws NumberFormatException {
		
		return data_file.getRankLabels();
	}


	/**
	 * retrieve an array of raw metric value of a given node and raw metric
	 * @param metric: raw metric
	 * @param node_index: normalized node index
	 * 
	 * @return array of doubles of metric value
	 */
	public double[] getMetrics(MetricRaw metric, long node_index)
			throws IOException {
		if (this.data_file == null)
			return null;
				
		return data_file.getMetrics(node_index, metric.getRawID(), metric.getSize());
	}

	
	public int getParallelismLevel()
	{
		return data_file.getParallelismLevel();
	}
	
	public String getRankTitle()
	{
		return data_file.getRankTitle();
	}
	
	public void dispose() {
		if (data_file != null) {
			data_file.dispose();
		}
	}
	
	
	
	//==============================================================================================
	// PRIVATE METHODS
	//==============================================================================================
} 
	