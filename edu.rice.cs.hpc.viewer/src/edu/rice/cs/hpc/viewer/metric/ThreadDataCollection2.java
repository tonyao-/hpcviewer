package edu.rice.cs.hpc.viewer.metric;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import edu.rice.cs.hpc.common.ui.Util;
import edu.rice.cs.hpc.data.experiment.Experiment;
import edu.rice.cs.hpc.data.experiment.metric.MetricRaw;
import edu.rice.cs.hpc.data.util.IProgressReport;
import edu.rice.cs.hpc.data.util.MergeDataFiles;

/******************************************************************
 * 
 * Class to manage a collection of thread data for database version 1 and 2.
 * Version 1 is when the database comprises of multiple files per thread,
 * while version 2 is when the files are merged into one mega file.
 *
 ******************************************************************/
public class ThreadDataCollection2 implements IThreadDataCollection 
{
	final private ThreadLevelDataCompatibility thread_data;
	private ThreadLevelDataFile data_file[];
	private File directory;
	private Experiment experiment;

	public ThreadDataCollection2(Experiment experiment)
	{
		this.experiment = experiment;
		thread_data 	= new ThreadLevelDataCompatibility();
		int num_metrics = experiment.getMetricRaw().length;
		data_file		= new ThreadLevelDataFile[num_metrics];
	}
	
	@Override
	public void open(String directory) throws IOException {
		File dir = new File(directory);
		if (dir.isFile())
			this.directory = dir.getParentFile();
		else
			this.directory = dir;
	}

	@Override
	public double[] getMetrics(long nodeIndex, int metricIndex, int numMetrics) 
			throws IOException {
		// check if the data already exists or not
		if (data_file != null) 
		{
			if (data_file[metricIndex] != null)
			{
				return data_file[metricIndex].getMetrics(nodeIndex, metricIndex, numMetrics);
			}
		}
		// data hasn't been created. Try to merge and open the file
		String file = thread_data.getMergedFile(directory, metricIndex);
		if (file != null)
		{
			data_file[metricIndex] = new ThreadLevelDataFile(Util.getActiveStatusLineManager());
			data_file[metricIndex].open(file);
			return data_file[metricIndex].getMetrics(nodeIndex, metricIndex, numMetrics);
		}
		return null;
	}

	@Override
	public boolean isAvailable() {
		return data_file != null && data_file.length>0;
	}

	
	@Override
	public double[] getRankLabels() {
		final int numLevels = data_file[0].getParallelismLevel();
		final String []labels = data_file[0].getRankLabels();
		
		double []rankLabels = new double[labels.length];
		
		// try to reorder the rank so that the threads can be evenly sparsed
		// for example if the ranks are 0.0, 0.1, 0.2, 1.0, 1.1, 1.2
		// we want to convert it into:  0.0, 0.3, 0.6, 1.0, 1.3, 1.6
		for(int i=0; i<labels.length; i++)
		{
			double rank_double = Double.parseDouble(labels[i]); 
			if (numLevels == 1)
			{
				rankLabels[i] = rank_double;
			} else {
				// for hybrid application we need to reorder the ranks
				int rank_first = (int) Math.floor(rank_double);
				int num_sibling = 0;
				int j;
				// compute the number of threads of this process
				for(j=i+1; j<labels.length; j++)
				{
					int next_rank = (int) Math.floor(Double.parseDouble(labels[j]));
					num_sibling++;
					if (next_rank != rank_first) {
						break;
					} else if (j==labels.length-1) {
						num_sibling++;
					}
				}
				// evenly sparse the thread number
				for(int k=0; k<num_sibling; k++)
				{
					rankLabels[i+k] = (double)rank_first + ((double)k/num_sibling);
				}
				i = j-1;
			}
		}
		
		return rankLabels;
	}

	@Override
	public int getParallelismLevel() {
		return data_file[0].getParallelismLevel();
	}

	@Override
	public String getRankTitle() {
		String title;
		if (getParallelismLevel() > 1)
		{
			title = "Process.Thread";
		} else {
			if (data_file[0].isMultiProcess())
				title = "Process";
			else 
				title = "Thread";
		}
		return title;
	}

	@Override
	public void dispose() {
		if (data_file != null)
		{
			for(ThreadLevelDataFile df : data_file)
			{
				if (df != null)
					df.dispose();
			}
		}
	}

	/**
	 * class to cache the name of merged thread-level data files. 
	 * We will ask A LOT the name of merged files, thus keeping in cache will avoid us to check to often
	 * if the merged file already exist or not
	 * 
	 * The class also check compatibility with the old version.
	 *
	 */
	private class ThreadLevelDataCompatibility {
		
		private HashMap<String, String> listOfFiles;
		
		public ThreadLevelDataCompatibility() {
			listOfFiles = new HashMap<String, String>();
		}
		
		/**
		 * method to find the name of file for a given metric ID. 
		 * If the files are not merged, it will be merged automatically
		 * 
		 * The name of the merge file will depend on the glob pattern
		 * 
		 * @param directory
		 * @param metric_raw_id
		 * @return
		 * @throws IOException
		 */
		public String getMergedFile(File directory, int metric_raw_id) throws IOException {
			
			final MetricRaw metric = experiment.getMetricRaw()[metric_raw_id];
			final String globInputFile = metric.getGlob();
			
			// assuming the number of merged experiments is less than 10
			final char experiment_char = globInputFile.charAt(0);
			int experiment_id = 1;
			
			if (experiment_char>='0' && experiment_char<='9') {
				experiment_id = experiment_char - '0';
			}
			
			// ------------------------------------------------------------------------------------
			// given the metric raw id, reconstruct the name of raw metric data file
			// for instance, if raw metric id = 1, then the file should be experiment-1.mdb
			// ------------------------------------------------------------------------------------
			final String outputFile = directory.getAbsolutePath() + File.separatorChar + 
					"experiment-" + experiment_id + ".mdb";

			// check if the file is already merged
			String cacheFileName = this.listOfFiles.get(outputFile);
			
			if (cacheFileName == null) {
				
				// ----------------------------------------------------------
				// the file doesn't exist, we need to merge metric-db files
				// ----------------------------------------------------------
				// check with the old version of thread level data
				this.checkOldVersionOfData(directory);
				
				final ProgressReport progress= new ProgressReport( Util.getActiveStatusLineManager() );
				
				// ------------------------------------------------------------------------------------
				// the compact method will return the name of the compacted files.
				// if the file doesn't exist, it will be created automatically
				// ------------------------------------------------------------------------------------
				MergeDataFiles.MergeDataAttribute att = MergeDataFiles.merge(directory, 
						globInputFile, outputFile, progress);
				
				if (att == MergeDataFiles.MergeDataAttribute.FAIL_NO_DATA) {
					// ------------------------------------------------------------------------------------
					// the data doesn't exist. Let's try to use experiment.mdb for compatibility with the old version
					// ------------------------------------------------------------------------------------
					cacheFileName =  directory.getAbsolutePath() + File.separatorChar + "experiment.mdb";
					att = MergeDataFiles.merge(directory, globInputFile, cacheFileName, progress);
					
					if (att == MergeDataFiles.MergeDataAttribute.FAIL_NO_DATA)
						return null;
				} else {
					cacheFileName = outputFile;
				}
				this.listOfFiles.put(outputFile, cacheFileName);

			}
			return cacheFileName;
		}
		
		private void checkOldVersionOfData(File directory) {
			
			String oldFile = directory.getAbsolutePath() + File.separatorChar + "experiment.mdb"; 
			File file = new File(oldFile);
			
			if (file.canRead()) {
				// old file already exist, needs to warn the user
				final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				MessageDialog.openWarning(window.getShell(), "Warning ! Old version of metric data file",
						"hpcviewer has detected the presence of an old version of metric data file:\n 'experiment.mdb'\n in the directory:\n "
						+ directory.getPath() + "\nIt is highly suggested to remove the file and replace it with the original *.metric-db files from hpcprof-mpi.");
			}
		}
	}

	
	
	/*******************
	 * Progress bar
	 * @author laksonoadhianto
	 *
	 */
	private class ProgressReport implements IProgressReport 
	{
		final private IStatusLineManager statusLine;

		public ProgressReport(IStatusLineManager statusMgr)
		{
			statusLine = statusMgr;
		}
		
		public void begin(String title, int num_tasks) {
			if (statusLine != null) {
				statusLine.setMessage(title);
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().update();
				statusLine.getProgressMonitor().beginTask(title, num_tasks);
			}
		}

		public void advance() {
			if (statusLine != null) 
				statusLine.getProgressMonitor().worked(1);
		}

		public void end() {
			if (statusLine != null) 
				statusLine.getProgressMonitor().done();
		}
		
	}


}
